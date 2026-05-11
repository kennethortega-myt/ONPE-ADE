import sys
import signal
import pika
import config
import json
import psycopg
from pathlib import Path
from datetime import datetime
from modules import digitization, control
import time
import threading
import multiprocessing
from multiprocessing import Manager, Event as MPEvent
from db.progress import update_det_parametro_by_nombre, get_det_parametro_valor
from collections import defaultdict
from util import constantes
import traceback
from logger_config import logger
import shutil
import os
import psutil
from logger_config import set_worker_context
from db.model_integrity import new_verify_model_weights

logger.info(
    f"Configuración cargada: "
    f"API_URL_CENTRO_COMPUTO={config.API_URL_CENTRO_COMPUTO}, "
    f"RABBITMQ_HOST={config.RABBITMQ_HOST}, "
    f"RABBITMQ_PORT={config.RABBITMQ_PORT}, "
    f"POSTGRE_HOST={config.POSTGRE_HOST}, "
    f"POSTGRE_DATABASE={config.POSTGRE_DATABASE}, "
    f"POSTGRE_PORT={config.POSTGRE_PORT}, "
    f"POSTGRE_DEFAULT_SCHEMA={config.POSTGRE_DEFAULT_SCHEMA}, "
    f"IMAGES_DIR={config.IMAGES_DIR}, "
    f"WORKERS_PROCESS={config.PROCESS_WORKERS_NUM},"
    f"LE_PROCESS_WORKERS={config.LE_PROCESS_WORKERS_NUM},"
    f"WORKERS_PAGINA_LE={config.WORKERS_PAGINA_LE},"
    f"CUT_AND_PREDICT={config.CUT_AND_PREDICT},"
    f"ENABLE_DETECTOR={config.ENABLE_DETECTOR},"
    f"ENABLE_DUAL_PREDICTION={config.ENABLE_DUAL_PREDICTION},"
    f"WORKERS_PAGINA_LE={config.WORKERS_PAGINA_LE},"
    f"LE_PROCESS_WORKERS_NUM={config.LE_PROCESS_WORKERS_NUM}"
)

_mp_manager = None
_mp_shutdown = None
_mp_process_count = None
_mp_le_process_count = None

_shutdown              = threading.Event()
_active_connections    = {}
_active_connections_lock = threading.Lock()

_worker_local = threading.local()
_current_worker_id: str = "unknown"

def _log_reconnect_attempt(prefix: str, conn_key: str, disconnected_at) -> None:
    if disconnected_at is not None:
        elapsed = time.monotonic() - disconnected_at
        logger.info(f"[{prefix}] {conn_key} reconectando al broker (desconectado hace {elapsed:.1f}s)")


def _log_reconnect_success(prefix: str, conn_key: str, disconnected_at):
    if disconnected_at is not None:
        elapsed = time.monotonic() - disconnected_at
        logger.info(f"[{prefix}] {conn_key} reconexión exitosa tras {elapsed:.1f}s de interrupción")
        return None
    return disconnected_at


def _log_broker_closed(prefix: str, conn_key: str, exc: pika.exceptions.ConnectionClosedByBroker) -> None:
    reply_code = exc.args[0]
    reply_text = exc.args[1] if len(exc.args) > 1 else ""
    if reply_code == 320:
        logger.error(
            f"[{prefix}] {conn_key}: broker RabbitMQ apagado forzosamente "
            f"(código={reply_code}, motivo='{reply_text}'). "
            f"Posibles causas: disco lleno, OOM, reinicio del servidor. Reintentando en 5s..."
        )
    else:
        logger.error(
            f"[{prefix}] {conn_key}: broker cerró conexión "
            f"(código={reply_code}, motivo='{reply_text}'). Reintentando en 5s..."
        )


def _safe_close_connection(connection) -> None:
    if connection and not connection.is_closed:
        try:
            connection.close()
        except Exception:
            pass


def _process_acta_worker_main(
    worker_index:    int,
    total_workers:   int,
    mp_shutdown,
    mp_process_count,
):
    """
    Punto de entrada de cada proceso hijo para sce-queue-process-acta.
    Cada proceso tiene su propio GIL, su propio TF, sus propios threads.
    """
    global _current_worker_id
    _current_worker_id = f"sce-queue-process-acta[{worker_index}]"
    # 1. Señales — el hijo maneja su propio SIGTERM
    signal.signal(signal.SIGINT,  signal.SIG_IGN)   # el padre gestiona CTRL+C
    signal.signal(signal.SIGTERM, lambda s, f: mp_shutdown.set())

    # 2. Configurar HILOS PARA LOS MODELOS
    _configure_tf_for_process(worker_index, total_workers)

    # 3. Warmup del modelo en este proceso
    _warmup_models_in_process()

    # 4. Contexto de worker
    set_worker_context(worker_index, threads_budget=1)

    queue_name = "sce-queue-process-acta"
    conn_key   = f"{queue_name}[{worker_index}]"

    _disconnected_at = None

    while not mp_shutdown.is_set():
        connection = None
        try:
            _log_reconnect_attempt("WORKER-PROC", conn_key, _disconnected_at)
            logger.info(f"[WORKER-PROC] Iniciando consumidor {conn_key}")
            connection, channel = create_channel(queue_name)
            _disconnected_at = _log_reconnect_success("WORKER-PROC", conn_key, _disconnected_at)
            channel.basic_consume(
                queue=queue_name,
                on_message_callback=_make_process_acta_handler(mp_process_count),
                auto_ack=False,
            )
            logger.info(f"[WORKER-PROC] {conn_key} esperando mensajes")
            channel.start_consuming()
        except pika.exceptions.ConnectionClosedByBroker as exc:
            if mp_shutdown.is_set():
                break
            _disconnected_at = _disconnected_at or time.monotonic()
            _log_broker_closed("WORKER-PROC", conn_key, exc)
            time.sleep(5)
        except Exception as exc:
            if mp_shutdown.is_set():
                break
            _disconnected_at = _disconnected_at or time.monotonic()
            logger.exception(f"[WORKER-PROC] Error en {conn_key}: {exc}. Reintentando en 5s...")
            time.sleep(5)
        finally:
            _safe_close_connection(connection)

    logger.info(f"[WORKER-PROC] {conn_key} terminado")

def _configure_tf_for_process(worker_index: int, total_workers: int):
    """
    Configura TF una sola vez por proceso.
    Con procesos aislados, intra=2 aplica SOLO a este proceso.
    """
    try:
        import tensorflow as tf
        total_cores = os.cpu_count() or 4

        # Afinidad de cores
        cores_per_worker = max(1, total_cores // total_workers)
        start_core = (worker_index * cores_per_worker) % total_cores
        assigned_cores = list(range(
            start_core,
            min(start_core + cores_per_worker, total_cores)
        ))
        try:
            proc = psutil.Process()
            proc.cpu_affinity(assigned_cores)
        except Exception:
            pass  # Windows sin soporte, no crítico

        # TF respeta la afinidad — intra=2 es el valor óptimo
        intra = min(2, cores_per_worker)
        inter = 1
        tf.config.threading.set_intra_op_parallelism_threads(intra)
        tf.config.threading.set_inter_op_parallelism_threads(inter)

        logger.info(
            f"[TF-PROC] worker={worker_index} | "
            f"cores={assigned_cores} | intra={intra} inter={inter}",
            queue="default"
        )
    except Exception:
        logger.exception("[TF-PROC] Error configurando TF")
    
    if config.ENABLE_DETECTOR == 1 or config.ENABLE_DUAL_PREDICTION == 1:
            try:
                import torch
                torch_threads = min(2, cores_per_worker)
                torch.set_num_threads(torch_threads)
                torch.set_num_interop_threads(max(1, torch_threads // 2))
                logger.info(
                    f"[TORCH-PROC] worker={worker_index} | "
                    f"num_threads={torch_threads}",
                    queue="default"
                )
            except Exception:
                logger.exception("[TORCH-PROC] Error configurando PyTorch")

def _warmup_models_in_process():
    """Cada proceso carga sus propios modelos en memoria."""
    try:
        logger.info("[WARMUP-PROC] Cargando modelos...", queue="default")
        from models.mnistmodel.model import load_model
        from models.detectormodel.new_evaluate_image import load_rfdetr_model
        from models.binarymodel.valid_trazo_classification import load_multiclass_model
        from models.binarymodel.valid_trazo_classification import MOBILENETV3_PATH, SPINALVGG_PATH
        from models.mnistmodel.model import MODEL_PATH
        from models.detectormodel.new_evaluate_image import RFDET_PATH

        new_verify_model_weights(
            models=[
                {"model_path": MODEL_PATH},
                {"model_path": MOBILENETV3_PATH},
                {"model_path": SPINALVGG_PATH},
                {"model_path": RFDET_PATH},
            ],
            usuario=SYSTEM_USER,
            raise_on_error=True,
            log_queue="default"
        )
        loaded_models = []
        skipped_models = []
        load_model()
        loaded_models.append("EfficientCapsNet")
        if config.ENABLE_DUAL_PREDICTION == 1:
            load_multiclass_model()
            loaded_models.append("MobileNetV3 + SpinalVGG")
        else:
            skipped_models.append("MobileNetV3 + SpinalVGG")
        if config.ENABLE_DETECTOR == 1:
            load_rfdetr_model()
            loaded_models.append("RF-DETR")
        else:
            skipped_models.append("RF-DETR")
        logger.info(
            f"[WARMUP-PROC] Modelos cargados={loaded_models} | omitidos={skipped_models}",
            queue="default"
        )
        logger.info("[WARMUP-PROC] Modelos listos.", queue="default")
    except Exception:
        logger.exception("[WARMUP-PROC] Error cargando modelos")
        raise

def _make_process_acta_handler(mp_process_count):
    """
    Fabrica el handler con referencia al contador compartido entre procesos.
    """
    def handle_process_acta(ch, method, properties, body_bytes):
        t_start = time.perf_counter()
        body = json.loads(body_bytes)
        workspace = _create_workspace(
            body["actaId"], queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )

        def process(body):
            logger.info(f"[WORKSPACE] {workspace}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            control.process_acta(
                body["actaId"], body["fileId1"], body["fileId2"],
                body["codUsuario"], body["codCentroComputo"],
                workspace=str(workspace),
            )
            logger.info(
                f"[SUCCESS] Procesamiento OK | workspace={workspace}",
                queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
            )

        # Contador compartido para el monitor
        try:
            with mp_process_count.get_lock():
                mp_process_count.value += 1
        except Exception:
            pass

        try:
            _dispatch(
                ch, method, properties, body_bytes,
                queue_name="sce-queue-process-acta",
                process_fn=process,
                log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS,
            )
        finally:
            elapsed = time.perf_counter() - t_start
            logger.info(
                f"[TIMER] handle_process_acta | actaId={body['actaId']} | total={elapsed:.2f}s",
                queue=constantes.QUEUE_LOGGER_VALUE_PROCESS,
            )
            _delete_workspace(workspace)
            try:
                with mp_process_count.get_lock():
                    mp_process_count.value = max(0, mp_process_count.value - 1)
            except Exception:
                pass

    return handle_process_acta

def _process_lista_electores_worker_main(
    worker_index:    int,
    total_workers:   int,
    mp_shutdown,
    mp_le_process_count,
):
    """
    Punto de entrada de cada proceso hijo para sce-queue-process-lista-electores.
    No usa TF/PyTorch — procesa imágenes con OpenCV usando un ThreadPoolExecutor
    interno (WORKERS_PAGINA_LE hilos por proceso).
    """
    global _current_worker_id
    _current_worker_id = f"sce-queue-process-lista-electores[{worker_index}]"
    # El padre gestiona CTRL+C; el hijo escucha su propio SIGTERM
    signal.signal(signal.SIGINT,  signal.SIG_IGN)
    signal.signal(signal.SIGTERM, lambda s, f: mp_shutdown.set())

    # Contexto de worker — el presupuesto de hilos coincide con WORKERS_PAGINA_LE
    set_worker_context(worker_index, threads_budget=config.WORKERS_PAGINA_LE)

    queue_name = "sce-queue-process-lista-electores"
    conn_key   = f"{queue_name}[{worker_index}]"

    while not mp_shutdown.is_set():
        connection = None
        try:
            logger.info(f"[WORKER-PROC] Iniciando consumidor {conn_key}")
            connection, channel = create_channel(queue_name)
            channel.basic_consume(
                queue=queue_name,
                on_message_callback=_make_process_lista_electores_handler(mp_le_process_count),
                auto_ack=False,
            )
            logger.info(f"[WORKER-PROC] {conn_key} esperando mensajes")
            channel.start_consuming()
        except Exception as exc:
            if mp_shutdown.is_set():
                break
            logger.exception(f"[WORKER-PROC] Error en {conn_key}: {exc}. Reintentando en 5s...")
            time.sleep(5)
        finally:
            if connection and not connection.is_closed:
                try:
                    connection.close()
                except Exception:
                    pass

    logger.info(f"[WORKER-PROC] {conn_key} terminado")


def _make_process_lista_electores_handler(mp_le_process_count):
    """
    Fabrica el handler con referencia al contador compartido entre procesos.
    """
    def handle_process_lista_electores(ch, method, properties, body_bytes):
        t_start = time.perf_counter()
        body = json.loads(body_bytes)

        def process(body):
            control.process_lista_electores(
                body["mesaId"], body["abrevDocumento"],
                body["codUsuario"], body["codCentroComputo"],
            )
            logger.info(
                "[SUCCESS] Lista de electores OK",
                queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
            )

        # Contador compartido para el monitor
        try:
            with mp_le_process_count.get_lock():
                mp_le_process_count.value += 1
        except Exception:
            pass

        try:
            _dispatch(
                ch, method, properties, body_bytes,
                queue_name="sce-queue-process-lista-electores",
                process_fn=process,
                log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES,
            )
        finally:
            elapsed = time.perf_counter() - t_start
            logger.info(
                f"[TIMER] handle_process_lista_electores | mesaId={body['mesaId']} | total={elapsed:.2f}s",
                queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES,
            )
            try:
                with mp_le_process_count.get_lock():
                    mp_le_process_count.value = max(0, mp_le_process_count.value - 1)
            except Exception:
                pass

    return handle_process_lista_electores

#------------------------------------------



AMQD = "amq.direct"
SYSTEM_STATUS = {"is_active": None}
PARAM_NAME = "p_cola_modelo_procesamiento"
SYSTEM_USER = "sce_admin"

PROJECT_ROOT = Path(__file__).resolve().parent
WORK_ROOT = PROJECT_ROOT / "SCE_WORKSPACES"
WORK_ROOT.mkdir(exist_ok=True)

DLQ_DRAIN_INTERVAL = 2

PROCESS_WORKERS_NUM    = config.PROCESS_WORKERS_NUM
LE_PROCESS_WORKERS_NUM = config.LE_PROCESS_WORKERS_NUM

QUEUE_WORKERS = {
    "sce-queue-new-acta":                1,
    "sce-queue-new-acta-celeste":        1,
    "sce-queue-process-acta":            PROCESS_WORKERS_NUM,
    "sce-queue-process-acta-stae":       1,
    "sce-queue-process-lista-electores": LE_PROCESS_WORKERS_NUM,
    "sce-queue-process-miembros_mesa":   1,
}

QUEUES = list(QUEUE_WORKERS.keys())

_shutdown = threading.Event()
_active_connections: dict[str, pika.BlockingConnection] = {}
_active_connections_lock = threading.Lock()

def log_system_resources() -> None:
    try:
        proc = psutil.Process(os.getpid())
        mem_rss = proc.memory_info().rss / 1024 / 1024
        mem_pct = proc.memory_percent()
        cpu_pct = proc.cpu_percent(interval=1) / psutil.cpu_count()
        threads = proc.num_threads()
        ram_libre = psutil.virtual_memory().available / 1024 / 1024
        logger.info(
            f"[RESOURCES] "
            f"RAM={mem_rss:.1f}MB ({mem_pct:.1f}%) | "
            f"CPU={cpu_pct:.1f}% | "
            f"Threads={threads} | "
            f"RAM libre={ram_libre:.0f}MB"
        )
    except Exception:
        logger.exception("[RESOURCES] Error leyendo métricas del proceso")

def _register_connection(conn_key: str, connection: pika.BlockingConnection):
    with _active_connections_lock:
        _active_connections[conn_key] = connection

def _unregister_connection(conn_key: str):
    with _active_connections_lock:
        _active_connections.pop(conn_key, None)

def shutdown():
    if _shutdown.is_set():
        return
    logger.info("[SHUTDOWN] Señal recibida — cerrando workers...")
    _shutdown.set()
    with _active_connections_lock:
        connections_snapshot = dict(_active_connections)
    for conn_key, conn in connections_snapshot.items():
        try:
            if not conn.is_closed:
                conn.close()
                logger.info(f"[SHUTDOWN] Conexión cerrada para {conn_key}")
        except Exception:
            pass

def _signal_handler(sig, frame):
    shutdown()

class QueueStateManager:
    def __init__(self, queue_names):
        self.lock = threading.Lock()
        self.processing_count = defaultdict(int)
        self.queue_names = queue_names

    def start_processing(self, queue_name):
        with self.lock:
            self.processing_count[queue_name] += 1

    def finish_processing(self, queue_name):
        with self.lock:
            self.processing_count[queue_name] -= 1

    def total_processing(self):
        with self.lock:
            return sum(self.processing_count.values())

    def get_processing_snapshot(self):
        with self.lock:
            return dict(self.processing_count)

queue_state = QueueStateManager(QUEUES)

def build_connection_params():
    return pika.ConnectionParameters(
        host=config.RABBITMQ_HOST,
        port=config.RABBITMQ_PORT,
        credentials=pika.PlainCredentials(
            username=config.RABBITMQ_USER,
            password=config.RABBITMQ_PASSWORD,
        ),
        connection_attempts=10,
        retry_delay=2,
        blocked_connection_timeout=30,
        heartbeat=600,
    )

def create_channel(queue_name: str) -> tuple:
    connection = pika.BlockingConnection(build_connection_params())
    channel = connection.channel()
    try:
        channel.queue_declare(queue=queue_name, durable=True, arguments={"x-max-priority": 10})
    except pika.exceptions.ChannelClosedByBroker as e:
        if e.reply_code == 406:
            # La cola existe en el broker sin x-max-priority, reconectar y declarar
            connection = pika.BlockingConnection(build_connection_params())
            channel = connection.channel()
            channel.queue_declare(queue=queue_name, durable=True, passive=True)
        else:
            raise
    channel.queue_bind(exchange=AMQD, queue=queue_name, routing_key=f"{queue_name}-rt")
    dlq = f"{queue_name}-dlq"
    channel.queue_declare(queue=dlq, durable=True)
    channel.queue_bind(exchange=AMQD, queue=dlq, routing_key=f"{dlq}-rt")
    channel.basic_qos(prefetch_count=1)
    return connection, channel


def handle_error(ch, properties, body, queue_name):
    tb = traceback.format_exc()
    worker_id = getattr(_worker_local, "worker_id", None) or _current_worker_id
    existing_headers = dict(properties.headers or {})
    retry_count = existing_headers.get("x-retry-count", 0)
    existing_headers["x-error-traceback"] = tb[:4000]  # RabbitMQ limita tamaño de headers
    existing_headers["x-retry-count"] = retry_count
    existing_headers["x-worker-id"] = worker_id
    existing_headers["x-error-type"] = type(sys.exc_info()[1]).__name__
    ch.basic_publish(
        exchange=AMQD,
        routing_key=f"{queue_name}-dlq-rt",
        body=json.dumps(body).encode("utf-8"),
        properties=pika.BasicProperties(
            delivery_mode=2,
            headers=existing_headers,
        ),
    )

def _dispatch(ch, method, properties, body_bytes, queue_name, process_fn, log_queue):
    queue_state.start_processing(queue_name)
    body = json.loads(body_bytes)
    if log_queue == constantes.QUEUE_LOGGER_VALUE_PROCESS_PREDICT:
        body_to_log = {
            "actaId":              body.get("actaId"),
            "nombreUsuario":       body.get("nombreUsuario"),
            "codigoCentroComputo": body.get("codigoCentroComputo"),
            "abrevProceso":        body.get("abrevProceso"),
        }
    else:
        body_to_log = body

    try:
        logger.info(f"[START] Mensaje recibido en {queue_name}", queue=log_queue)
        logger.info(f"[BODY] {body_to_log}", queue=log_queue)
        process_fn(body)
        # Flujo optimo
        ch.basic_ack(delivery_tag=method.delivery_tag)
        logger.info(f"[ACK] tag={method.delivery_tag}", queue=log_queue)
    except Exception:
        # Flujo falló - intentar enviar a DLQ
        logger.exception(
            f"[ERROR] Fallo procesando mensaje | queue={queue_name} | body={body}",
            queue="default"
        )
        try:
            handle_error(ch, properties, body, queue_name)
            ch.basic_ack(delivery_tag=method.delivery_tag)
            logger.info(f"[ACK→DLQ] tag={method.delivery_tag}", queue=log_queue)
        except Exception:
            # Error critico DLQ tambien fallo (canal roto, rabbit caido)
            logger.exception(
                f"[NACK] handle_error falló — descartando mensaje sin DLQ | "
                f"queue={queue_name} | body={body}",
                queue="default"
            )
            try:
                # El mensaje se descarta pero el traceback queda en el log
                ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
            except Exception:
                # Canal completamente muerto.
                # Al cerrar la conexión RabbitMQ re-encola el mensaje automáticamente.
                logger.exception(
                    f"[NACK-FAILED] Canal muerto, RabbitMQ re-encolará | queue={queue_name}",
                    queue="default"
                )
    finally:
        # Finalmente solo cuenta el procesamiento de las colas
        queue_state.finish_processing(queue_name)

def _create_workspace(acta_id, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS) -> Path:
    """
    Crea y retorna un directorio único para el procesamiento del mensaje.
    Unicidad garantizada por actaId + timestamp con microsegundos.
    """
    now         = datetime.now()
    folder_name = f"{queue}_{acta_id}_{now.strftime('%Y_%m_%d')}_{now.strftime('%H_%M_%S_%f')}"
    workspace   = WORK_ROOT / folder_name
    workspace.mkdir(parents=True, exist_ok=True)
    return workspace

def _delete_workspace(workspace: Path) -> None:
    """Elimina el workspace al finalizar el procesamiento (éxito o fallo)."""
    try:
        if workspace and workspace.exists():
            shutil.rmtree(workspace)
            logger.info(
                f"[WORKSPACE] Eliminado {workspace}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    except Exception:
        logger.exception(f"[WORKSPACE] Error eliminando {workspace}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

def handle_new_acta(ch, method, properties, body_bytes):
    def process(body):
        digitization.validate_mesa(
            body["actaId"], body["fileId"], body["type"],
            body["usuario"], body["codigocc"], body["abrevProceso"],
        )
        logger.info("[SUCCESS] validate_mesa OK", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    _dispatch(
        ch, method, properties, body_bytes,
        queue_name="sce-queue-new-acta",
        process_fn=process,
        log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE,
    )


def handle_new_acta_celeste(ch, method, properties, body_bytes):
    def process(body):
        digitization.validate_acta_celeste(
            body["actaId"], body["fileId"], body["type"],
            body["usuario"], body["codigocc"], body["abrevProceso"],
        )
        logger.info("[SUCCESS] validate_mesa_celeste OK", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    _dispatch(
        ch, method, properties, body_bytes,
        queue_name="sce-queue-new-acta-celeste",
        process_fn=process,
        log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE,
    )


def handle_process_acta(ch, method, properties, body_bytes):
    t_start = time.perf_counter()
    body = json.loads(body_bytes)
    workspace = _create_workspace(body["actaId"], queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    def process(body):
        logger.info(f"[WORKSPACE] {workspace}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        control.process_acta(
            body["actaId"],
            body["fileId1"],
            body["fileId2"],
            body["codUsuario"],
            body["codCentroComputo"],
            workspace=str(workspace),
        )
        logger.info(
            f"[SUCCESS] Procesamiento OK | workspace={workspace}",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
    try:
        _dispatch(
            ch, method, properties, body_bytes,
            queue_name="sce-queue-process-acta",
            process_fn=process,
            log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS,
        )
    finally:
        elapsed = time.perf_counter() - t_start
        logger.info(
            f"[TIMER] handle_process_acta | actaId={body['actaId']} | total={elapsed:.2f}s",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS,
        )
        _delete_workspace(workspace)

def handle_process_acta_stae(ch, method, properties, body_bytes):
    t_start = time.perf_counter()
    body = json.loads(body_bytes)
    def process(body):
        control.process_acta_stae_vd(
            body["actaId"], body["codUsuario"], body["codCentroComputo"],
        )
        logger.info("[SUCCESS] Stae OK", queue=constantes.QUEUE_LOGGER_VALUE_STAE)

    try:
        _dispatch(
            ch, method, properties, body_bytes,
            queue_name="sce-queue-process-acta-stae",
            process_fn=process,
            log_queue=constantes.QUEUE_LOGGER_VALUE_STAE,
        )
    finally:
        elapsed = time.perf_counter() - t_start
        logger.info(
            f"[TIMER] handle_process_acta_stae | actaId={body['actaId']} | total={elapsed:.2f}s",
            queue=constantes.QUEUE_LOGGER_VALUE_STAE,
        )


def handle_process_lista_electores(ch, method, properties, body_bytes):
    t_start = time.perf_counter()
    body = json.loads(body_bytes)
    def process(body):
        control.process_lista_electores(
            body["mesaId"], body["abrevDocumento"],
            body["codUsuario"], body["codCentroComputo"],
        )
        logger.info("[SUCCESS] Lista de electores OK", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)

    try:
        _dispatch(
            ch, method, properties, body_bytes,
            queue_name="sce-queue-process-lista-electores",
            process_fn=process,
            log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES,
        )
    finally:
        elapsed = time.perf_counter() - t_start
        logger.info(
            f"[TIMER] handle_process_lista_electores | mesaId={body['mesaId']} | total={elapsed:.2f}s",
            queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES,
        )

def handle_process_miembros_mesa(ch, method, properties, body_bytes):
    t_start = time.perf_counter()
    body = json.loads(body_bytes)
    def process(body):
        control.process_miembros_mesa(
            body["mesaId"], body["abrevDocumento"],
            body["codUsuario"], body["codCentroComputo"],
        )
        logger.info("[SUCCESS] Miembros de mesa OK", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

    try:
        _dispatch(
            ch, method, properties, body_bytes,
            queue_name="sce-queue-process-miembros_mesa",
            process_fn=process,
            log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA,
        )
    finally:
        elapsed = time.perf_counter() - t_start
        logger.info(
            f"[TIMER] handle_process_miembros_mesa | mesaId={body['mesaId']} | total={elapsed:.2f}s",
            queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA,
        )


QUEUE_HANDLERS = {
    "sce-queue-new-acta":                handle_new_acta,
    "sce-queue-new-acta-celeste":        handle_new_acta_celeste,
    "sce-queue-process-acta":            handle_process_acta,
    "sce-queue-process-acta-stae":       handle_process_acta_stae,
    "sce-queue-process-lista-electores": handle_process_lista_electores,
    "sce-queue-process-miembros_mesa":   handle_process_miembros_mesa,
}

def extract_last_error(traceback_str: str) -> str:
    if not traceback_str:
        return "traceback no encontrado"

    lines = traceback_str.strip().splitlines()

    last_lines = []
    for line in reversed(lines):
        if line.strip():
            last_lines.append(line)
        if len(last_lines) >= 3:
            break

    return "\n".join(reversed(last_lines))

_DLQ_MAX_RETRIES = 3
_DLQ_BACKOFF     = [1, 5, 10]
_RECOVERABLE_ERRORS = {
    # Errores criticos de conexion
    "OperationalError",
    "AdminShutdown",
    "PoolTimeout",
    "TooManyRequests",
    "UndefinedTable",

    # Concurrencia fallida
    "DeadlockDetected",
    "SerializationFailure",
    "LockNotAvailable",

    # Otros errores comunes
    "InterfaceError",
    "ConnectionException",
    "QueryCanceled",
    
    "ReadOnlySqlTransaction",

    # Errores personalizados
    "ActaNotFoundError",
    "FTPFileNotFoundError",
    "LEFallbackError",
    "MMFallbackError",
    "ProcessActaFallbackError",
}

def handle_dlq_message(channel, method, properties, body_bytes):
    dlq_name = f"{method.routing_key.replace('-dlq-rt', '')}-dlq"
    origin   = dlq_name.replace("-dlq", "")
    try:
        body = json.loads(body_bytes)
    except Exception:
        body = body_bytes.decode("utf-8", errors="replace")

    headers = dict(properties.headers or {})
    retry_count = headers.get("x-retry-count", 0)

    error_type = headers.get("x-error-type", "")
    tb = headers.get("x-error-traceback", "traceback no disponible")
    worker_id = headers.get("x-worker-id", "desconocido")

    logger.info(
        f"[DLQ] Mensaje fallido | dlq={dlq_name} | origin={origin} | "
        f"worker={worker_id} | error_type={error_type} | retry={retry_count} | body={body}\n"
        f"{extract_last_error(tb)}",
        queue="default"
    )

    is_recoverable = error_type in _RECOVERABLE_ERRORS

    if not is_recoverable:
        logger.error(
            f"[DLQ-DROP] Error no recuperable ({error_type}), descartando | body={body}\n{tb}",
            queue="default"
        )
        channel.basic_ack(delivery_tag=method.delivery_tag)
        return

    if retry_count < _DLQ_MAX_RETRIES:
        wait = _DLQ_BACKOFF[min(retry_count, len(_DLQ_BACKOFF) - 1)]
        headers["x-retry-count"] = retry_count + 1
        logger.warning(
            f"[DLQ-RETRY] Esperando {wait}s antes de reencolar | "
            f"origin={origin} | retry={retry_count + 1}/{_DLQ_MAX_RETRIES} | body={body}",
            queue="default"
        )
        time.sleep(wait)
        try:
            channel.basic_publish(
                exchange=AMQD,
                routing_key=f"{origin}-rt",
                body=json.dumps(body).encode("utf-8"),
                properties=pika.BasicProperties(delivery_mode=2, headers=headers, priority=10),
            )
        except Exception:
            logger.exception("[DLQ-RETRY] Error reencolando mensaje", queue="default")
    else:
        logger.error(
            f"[DLQ-DROP] Descartado tras {_DLQ_MAX_RETRIES} reintentos | "
            f"origin={origin} | body={body}\n{tb}",
            queue="default"
        )
    channel.basic_ack(delivery_tag=method.delivery_tag)


def _dlq_idle_wait(poll_interval: int) -> None:
    """Espera poll_interval segundos en tramos cortos para reaccionar al shutdown."""
    for _ in range(poll_interval * 2):
        if _shutdown.is_set():
            return
        time.sleep(0.5)


def _dlq_drain_loop(channel, dlq_name: str, poll_interval: int) -> None:
    """Drena mensajes de la DLQ hasta shutdown o error de conexión."""
    while not _shutdown.is_set():
        method, properties, body = channel.basic_get(queue=dlq_name, auto_ack=False)
        if method is None:
            _dlq_idle_wait(poll_interval)
        else:
            handle_dlq_message(channel, method, properties, body)


def _dlq_close_connection(connection, dlq_name: str) -> None:
    """Cierra la conexión de forma segura."""
    _unregister_connection(f"dlq:{dlq_name}")
    if connection and not connection.is_closed:
        try:
            connection.close()
        except Exception:
            pass


def dlq_worker(origin_queue: str) -> None:
    """
    Worker dedicado a una DLQ. Usa basic_get (polling) para poder
    respetar el DLQ_DRAIN_INTERVAL entre mensajes y reaccionar al shutdown.
    """
    dlq_name      = f"{origin_queue}-dlq"
    poll_interval = 30
    while not _shutdown.is_set():
        connection = None
        try:
            connection = pika.BlockingConnection(build_connection_params())
            channel    = connection.channel()
            channel.queue_declare(queue=dlq_name, durable=True)
            _register_connection(f"dlq:{dlq_name}", connection)
            logger.info(f"[DLQ-WORKER] Conectado a {dlq_name}")
            _dlq_drain_loop(channel, dlq_name, poll_interval)
        except Exception as exc:
            if _shutdown.is_set():
                break
            logger.exception(f"[DLQ-WORKER] Error en {dlq_name}: {exc}. Reintentando en 10s...")
            time.sleep(10)
        finally:
            _dlq_close_connection(connection, dlq_name)
    logger.info(f"[DLQ-WORKER] {dlq_name} → terminado")

def _configure_worker_threads(total_workers: int):
    """Solo calcula el presupuesto de hilos Python para el ThreadPoolExecutor."""
    total_cores = os.cpu_count() or 4
    threads_per_worker = max(1, total_cores // total_workers)
    logger.info(f"ASIGNACION DE CORES || Cores detectados = {total_cores} | Hilos asignados a cada workers = {threads_per_worker} ", queue = "default")

    return threads_per_worker

def queue_worker(queue_name: str, worker_index: int, total_workers: int = PROCESS_WORKERS_NUM):  # ← añadir total_workers
    handler  = QUEUE_HANDLERS[queue_name]
    conn_key = f"{queue_name}[{worker_index}]"

    _worker_local.worker_id = conn_key
    
    threads_budget = _configure_worker_threads(total_workers)
    set_worker_context(worker_index, threads_budget=threads_budget)  # pasar al contexto

    _disconnected_at = None

    while not _shutdown.is_set():
        connection = None
        try:
            _log_reconnect_attempt("WORKER", conn_key, _disconnected_at)
            logger.info(f"[WORKER] Iniciando consumidor {conn_key}")
            connection, channel = create_channel(queue_name)
            _register_connection(conn_key, connection)
            _disconnected_at = _log_reconnect_success("WORKER", conn_key, _disconnected_at)
            channel.basic_consume(
                queue=queue_name,
                on_message_callback=handler,
                auto_ack=False,
            )
            logger.info(f"[WORKER] {conn_key} = esperando mensajes")
            channel.start_consuming()
        except pika.exceptions.ConnectionClosedByBroker as exc:
            if _shutdown.is_set():
                break
            _disconnected_at = _disconnected_at or time.monotonic()
            _log_broker_closed("WORKER", conn_key, exc)
            time.sleep(5)
        except Exception as exc:
            if _shutdown.is_set():
                break
            _disconnected_at = _disconnected_at or time.monotonic()
            logger.exception(f"[WORKER] Error en {conn_key}: {exc}. Reintentando en 5s...")
            time.sleep(5)
        finally:
            _unregister_connection(conn_key)
            _safe_close_connection(connection)

    logger.info(f"[WORKER] {conn_key} = terminado")

def get_total_enqueued_messages():
    total = 0
    try:
        connection = pika.BlockingConnection(build_connection_params())
        channel = connection.channel()
        for queue in QUEUES:
            q = channel.queue_declare(queue=queue, passive=True)
            total += q.method.message_count
        connection.close()
    except Exception as e:
        logger.exception(f"Error consultando colas: {e}")
    return total

def _read_mp_count(mp_count) -> int:
    """Lee un multiprocessing.Value de forma segura; devuelve 0 si no disponible."""
    if mp_count is None:
        return 0
    try:
        with mp_count.get_lock():
            return mp_count.value
    except Exception:
        return 0

def _apply_active_transition(is_active: bool, active_interval: int, idle_interval: int) -> int:
    """Actualiza SYSTEM_STATUS, dispara set_system_status y devuelve el nuevo intervalo."""
    SYSTEM_STATUS["is_active"] = is_active
    if is_active:
        logger.info("Sistema pasó a estado ACTIVO")
        set_system_status(True)
        return active_interval
    logger.info("Sistema pasó a estado IDLE")
    set_system_status(False)
    return idle_interval

def monitor_queues():
    check_interval_active = 10
    check_interval_idle   = 30
    current_interval      = check_interval_active
    while not _shutdown.is_set():
        _dlq_idle_wait(current_interval)
        if _shutdown.is_set():
            return
        thread_processing = queue_state.total_processing()
        acta_proc_processing = _read_mp_count(_mp_process_count)
        le_proc_processing = _read_mp_count(_mp_le_process_count)
        proc_processing  = acta_proc_processing + le_proc_processing
        total_processing = thread_processing + proc_processing
        total_enqueued   = get_total_enqueued_messages()
        is_active_now    = total_processing > 0 or total_enqueued > 0
        if is_active_now:
            logger.info(
                f"Estado sistema => "
                f"Procesando: {total_processing} "
                f"(acta={acta_proc_processing} | le={le_proc_processing} | threads={thread_processing}) | "
                f"Encolados: {total_enqueued}"
            )
            log_system_resources()
        if SYSTEM_STATUS["is_active"] != is_active_now:
            current_interval = _apply_active_transition(
                is_active_now, check_interval_active, check_interval_idle
            )

RETRY_INTERVAL = 5
MAX_RETRY_TIME = 60

def set_system_status(is_active: bool) -> bool:
    new_value  = "true" if is_active else "false"
    start_time = time.time()
    attempt    = 0

    while True:
        attempt += 1
        try:
            current_value = get_det_parametro_valor(PARAM_NAME)
            if current_value is None:
                logger.error(f"[SYSTEM-STATUS] Parámetro {PARAM_NAME} no existe", queue="default")
                return False
            if current_value.lower() == new_value:
                logger.info(f"[SYSTEM-STATUS] Ya está en '{new_value}' (attempt={attempt})", queue="default")
                return True
            logger.info(f"[SYSTEM-STATUS] Actualizando '{PARAM_NAME}': '{current_value}' → '{new_value}' (attempt={attempt})", queue="default")
            ok = update_det_parametro_by_nombre(
                cod_usuario=SYSTEM_USER, c_nombre=PARAM_NAME,
                nuevo_valor=new_value, log_queue="default",
            )
            if ok:
                return True
            logger.warning(f"[SYSTEM-STATUS] Update falló sin excepción (attempt={attempt})", queue="default")
        except psycopg.OperationalError as e:
            logger.warning(f"[SYSTEM-STATUS] DB DOWN (attempt={attempt}): {e}", queue="default")
        except Exception as e:
            logger.exception(f"[SYSTEM-STATUS] Error no recuperable: {e}", queue="default")
            return False

        if time.time() - start_time >= MAX_RETRY_TIME:
            logger.critical(f"[SYSTEM-STATUS] Timeout. No se pudo actualizar {PARAM_NAME}", queue="default")
            return False

        logger.info(f"[SYSTEM-STATUS] Reintentando en {RETRY_INTERVAL}s...", queue="default")
        time.sleep(RETRY_INTERVAL)


def main():
    global _mp_manager, _mp_shutdown, _mp_process_count, _mp_le_process_count

    signal.signal(signal.SIGINT,  _signal_handler)
    signal.signal(signal.SIGTERM, _signal_handler)

    # Inicializar shared state entre procesos
    _mp_manager          = Manager()
    _mp_shutdown         = MPEvent()
    _mp_process_count    = multiprocessing.Value("i", 0)
    _mp_le_process_count = multiprocessing.Value("i", 0)

    threading.Thread(target=monitor_queues, daemon=True, name="monitor").start()

    workers     = []
    child_procs = []

    # Procesos para sce-queue-process-acta
    total_process_workers = QUEUE_WORKERS["sce-queue-process-acta"]
    for i in range(total_process_workers):
        p = multiprocessing.Process(
            target=_process_acta_worker_main,
            args=(i, total_process_workers, _mp_shutdown, _mp_process_count),
            name=f"proc-worker-process-acta-{i}",
            daemon=True,
        )
        p.start()
        child_procs.append(p)
        logger.info(f"[MAIN] Proceso hijo iniciado: proc-worker-process-acta-{i} (pid={p.pid})")

    # Procesos para sce-queue-process-lista-electores
    total_le_workers = QUEUE_WORKERS["sce-queue-process-lista-electores"]
    for i in range(total_le_workers):
        p = multiprocessing.Process(
            target=_process_lista_electores_worker_main,
            args=(i, total_le_workers, _mp_shutdown, _mp_le_process_count),
            name=f"proc-worker-process-lista-electores-{i}",
            daemon=True,
        )
        p.start()
        child_procs.append(p)
        logger.info(f"[MAIN] Proceso hijo iniciado: proc-worker-process-lista-electores-{i} (pid={p.pid})")

    # Threads para el resto de colas (sin TF/multiprocessing)
    _proc_queues = {"sce-queue-process-acta", "sce-queue-process-lista-electores"}
    thread_queues = {k: v for k, v in QUEUE_WORKERS.items() if k not in _proc_queues}
    for queue_name, count in thread_queues.items():
        for i in range(count):
            t = threading.Thread(
                target=queue_worker,
                args=(queue_name, i, count),
                daemon=True,
                name=f"worker-{queue_name}-{i}",
            )
            t.start()
            workers.append(t)

    # Workers DLQ (threads)
    for queue_name in QUEUES:
        t = threading.Thread(
            target=dlq_worker,
            args=(queue_name,),
            daemon=True,
            name=f"dlq-worker-{queue_name}",
        )
        t.start()
        workers.append(t)

    logger.info(
        f"[MAIN] {total_process_workers} procesos (process-acta) + "
        f"{total_le_workers} procesos (process-lista-electores) + "
        f"{len(workers)} threads (otras colas) iniciados."
    )

    # Esperar shutdown
    while not _shutdown.is_set():
        time.sleep(0.5)

    # Propagar shutdown a procesos hijos
    _mp_shutdown.set()

    logger.info("[MAIN] Esperando que los procesos terminen...")
    for p in child_procs:
        p.join(timeout=30)
        if p.is_alive():
            logger.warning(f"[MAIN] Proceso {p.name} no terminó, forzando...")
            p.terminate()

    for t in workers:
        t.join(timeout=15)

    _mp_manager.shutdown()
    logger.info("[MAIN] Proceso terminado limpiamente.")
    sys.exit(0)


if __name__ == "__main__":
    main()