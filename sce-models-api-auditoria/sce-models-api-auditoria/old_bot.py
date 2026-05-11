import sys
import pika
import config
import json
from modules import digitization, control
import traceback
import time
import threading
from db.progress import update_det_parametro_by_nombre, get_det_parametro_valor
from collections import defaultdict

from logger_config import logger
AMQD = 'amq.direct'

SYSTEM_STATUS = {
    "is_active": None
}

PARAM_NAME = "p_cola_modelo_procesamiento"
SYSTEM_USER = "sce_admin"

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

QUEUES = [
    "sce-queue-new-acta",
    "sce-queue-new-acta-celeste",
    "sce-queue-process-acta",
    "sce-queue-process-acta-stae",
    "sce-queue-process-lista-electores",
    "sce-queue-process-miembros_mesa"
]

queue_state = QueueStateManager(QUEUES)

def on_connection_open(connection):
    logger.info("Conexión establecida con RabbitMQ")
    connection.channel(on_open_callback=on_channel_open)


def on_channel_open(channel):
    logger.info("Canal abierto, declarando colas...")

    queues = [
        "sce-queue-new-acta",
        "sce-queue-new-acta-celeste",
        "sce-queue-process-acta",
        "sce-queue-process-acta-stae",
        "sce-queue-process-lista-electores",
        "sce-queue-process-miembros_mesa"
    ]

    for queue in queues:
        channel.queue_declare(queue=queue, durable=True)
        channel.queue_bind(exchange=AMQD, queue=queue, routing_key=f"{queue}-rt")
        channel.queue_declare(queue=f"{queue}-dlq", durable=True)
        channel.queue_bind(exchange=AMQD, queue=f"{queue}-dlq", routing_key=f"{queue}-dlq-rt")

    channel.basic_qos(prefetch_count=1)

    channel.basic_consume(queue="sce-queue-new-acta", on_message_callback=new_acta_callback, auto_ack=False)
    channel.basic_consume(queue="sce-queue-new-acta-celeste", on_message_callback=new_acta_celeste_callback, auto_ack=False)
    channel.basic_consume(queue="sce-queue-process-acta", on_message_callback=process_acta_callback, auto_ack=False)
    channel.basic_consume(queue="sce-queue-process-acta-stae", on_message_callback=process_acta_stae_callback, auto_ack=False)
    channel.basic_consume(queue="sce-queue-process-lista-electores", on_message_callback=process_lista_electores_callback, auto_ack=False)
    channel.basic_consume(queue="sce-queue-process-miembros_mesa", on_message_callback=process_miembros_mesa_callback, auto_ack=False)

    logger.info(' [*] Esperando mensajes. Para salir presiona CTRL+C')

def new_acta_callback(ch, method, properties, body):
    threading.Thread(target=handle_new_acta, args=(ch, method, properties, body)).start()

def new_acta_celeste_callback(ch, method, properties, body):
    threading.Thread(target=handle_new_acta_celeste, args=(ch, method, properties, body)).start()

def process_acta_callback(ch, method, properties, body):
    threading.Thread(target=handle_process_acta, args=(ch, method, properties, body)).start()

def process_acta_stae_callback(ch, method, properties, body):
    threading.Thread(target=handle_process_acta_stae, args=(ch, method, properties, body)).start()

def process_lista_electores_callback(ch, method, properties, body):
    threading.Thread(target=handle_process_lista_electores, args=(ch, method, properties, body)).start()

def process_miembros_mesa_callback(ch, method, properties, body):
    threading.Thread(target=handle_process_miembros_mesa, args=(ch, method, properties, body)).start()

def handle_new_acta(ch, method, properties, body):
    queue_name = "sce-queue-new-acta"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        digitization.validate_mesa(body['actaId'], body['fileId'], body['type'], body['usuario'], body['codigocc'], body['abrevProceso'])
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_new_acta_celeste(ch, method, properties, body):
    queue_name = "sce-queue-new-acta-celeste"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        digitization.validate_acta_celeste(
            body['actaId'], body['fileId'], body['type'],
            body['usuario'], body['codigocc'], body['abrevProceso']
        )
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_process_acta(ch, method, properties, body):
    queue_name = "sce-queue-process-acta"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        control.process_acta(body['actaId'], body['fileId1'], body['fileId2'], body['codUsuario'], body['codCentroComputo'])
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_process_lista_electores(ch, method, properties, body):
    queue_name = "sce-queue-process-lista-electores"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        control.process_lista_electores(body['mesaId'], body['abrevDocumento'], body['codUsuario'], body['codCentroComputo'])
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_process_miembros_mesa(ch, method, properties, body):
    queue_name = "sce-queue-process-miembros_mesa"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        control.process_miembros_mesa(body['mesaId'], body['abrevDocumento'], body['codUsuario'], body['codCentroComputo'])
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_process_acta_stae(ch, method, properties, body):
    queue_name = "sce-queue-process-acta-stae"
    queue_state.start_processing(queue_name)
    body = json.loads(body)
    try:
        control.process_acta_stae_vd(body['actaId'], body['codUsuario'], body['codCentroComputo'])
    except Exception:
        handle_error(ch, properties, body, queue_name)
    finally:
        ch.basic_ack(delivery_tag=method.delivery_tag)
        queue_state.finish_processing(queue_name)

def handle_error(ch, properties, body, queue_name):
    logger.error(f"Error processing {body}", file=sys.stderr)
    traceback.print_exc()
    ch.basic_publish(
        exchange=AMQD,
        routing_key=f"{queue_name}-dlq-rt",
        body=json.dumps(body).encode('utf-8'),
        properties=pika.BasicProperties(
            delivery_mode=2,
            headers=properties.headers
        )
    )

def get_total_enqueued_messages():
    total = 0
    try:
        connection = pika.BlockingConnection(pika.ConnectionParameters(
            host=config.RABBITMQ_HOST,
            port=config.RABBITMQ_PORT,
            credentials=pika.PlainCredentials(
                username=config.RABBITMQ_USER,
                password=config.RABBITMQ_PASSWORD
            )
        ))

        channel = connection.channel()
        for queue in QUEUES:
            q = channel.queue_declare(queue=queue, passive=True)
            total += q.method.message_count
        connection.close()

    except Exception as e:
        logger.error(f"Error consultando colas: {e}")
    return total

def monitor_queues():

    check_interval_active = 10
    check_interval_idle = 30

    current_interval = check_interval_active

    while True:

        time.sleep(current_interval)

        total_processing = queue_state.total_processing()
        total_enqueued = get_total_enqueued_messages()

        is_active_now = (total_processing > 0 or total_enqueued > 0)

        if is_active_now:
            logger.info(f"Estado sistema => Procesando: {total_processing} | Encolados: {total_enqueued}")

        if SYSTEM_STATUS["is_active"] != is_active_now:

            SYSTEM_STATUS["is_active"] = is_active_now

            if is_active_now:
                logger.info("Sistema pasó a estado ACTIVO")
                set_system_status(True)
                current_interval = check_interval_active
            else:
                logger.info("Sistema pasó a estado IDLE")
                set_system_status(False)
                current_interval = check_interval_idle


def set_system_status(is_active: bool):
    """
    Actualiza parámetro en BD solo si cambia el valor real.
    """
    new_value = "true" if is_active else "false"
    current_value = get_det_parametro_valor(PARAM_NAME)

    if current_value is None:
        logger.error(f"No se pudo obtener valor actual de {PARAM_NAME}")
        return

    if current_value.lower() == new_value:
        logger.info(f"No se actualiza '{PARAM_NAME}' porque ya está en '{new_value}'")
        return

    logger.info(f"Actualizando '{PARAM_NAME}' de '{current_value}' a '{new_value}'")

    update_det_parametro_by_nombre(
        cod_usuario=SYSTEM_USER,
        c_nombre=PARAM_NAME,
        nuevo_valor=new_value,
        log_queue="default"
    )

def main():
    parameters = pika.ConnectionParameters(
        host=config.RABBITMQ_HOST,
        port=config.RABBITMQ_PORT,
        credentials=pika.PlainCredentials(
            username=config.RABBITMQ_USER,
            password=config.RABBITMQ_PASSWORD
        ),
        connection_attempts=10,
        retry_delay=2,
        blocked_connection_timeout=30,
        heartbeat=600
    )

    connection = pika.SelectConnection(parameters, on_open_callback=on_connection_open)
    monitor_thread = threading.Thread(target=monitor_queues, daemon=True)
    monitor_thread.start()

    try:
        connection.ioloop.start()
    except KeyboardInterrupt:
        logger.error("Shutting down...")
        connection.close()
        connection.ioloop.start()
        sys.exit(0)
    except Exception as e:
        logger.error(f"Error en la conexión: {str(e)}", file=sys.stderr)
        traceback.print_exc()
        logger.info("Reintentando conexión en 5 segundos...")
        time.sleep(5)
        main()


if __name__ == "__main__":
    main()
