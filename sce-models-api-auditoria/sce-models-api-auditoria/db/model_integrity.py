from pathlib import Path
from logger_config import logger
from db.progress import get_all_active_model_hashes
from util import constantes
from db.model_integrity_state import set_integrity_failure
from util.hash_util import sha256_file
import threading
_verify_state = threading.local()

def reset_weights_verification():
    """Llamar una vez al inicio de cada mensaje — resetea solo el thread actual."""
    _verify_state.verified = False

def new_verify_model_weights(
    models,
    usuario,
    raise_on_error=True,
    log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
):

    if getattr(_verify_state, "verified", False):
        return True
    logger.info(f"Usuario: {usuario} | Iniciando verificación de integridad de {len(models)} modelo(s)",queue=log_queue)

    integrity_ok = True
    try:
        valid_hashes = get_all_active_model_hashes(log_queue=log_queue)
        if not valid_hashes:
            msg = "No existen hashes activos en BD para validación"
            logger.critical(msg, queue=log_queue)
            return False

        for model in models:
            model_path = Path(model["model_path"])

            logger.info(
                f"Usuario: {usuario} | Verificando archivo {model_path}",
                queue=log_queue
            )

            if not model_path.exists():
                msg = (f"Usuario: {usuario} | Archivo de modelo no encontrado: {model_path}")
                logger.error(msg, queue=log_queue)
                set_integrity_failure()
                integrity_ok = False
                continue

            current_hash = sha256_file(model_path)

            if current_hash not in valid_hashes:
                msg = (f"Usuario: {usuario} | HASH INVALIDO\n Archivo : {model_path}\n No existe coincidencia en BD")
                logger.critical(msg, queue=log_queue)
                set_integrity_failure()
                integrity_ok = False
                continue

            logger.info(
                f"Usuario: {usuario} | Integridad OK | {model_path.name}",
                queue=log_queue
            )

        if integrity_ok:
            logger.info(
                f"Usuario: {usuario} | Integridad verificada correctamente para todos los modelos",
                queue=log_queue
            )
            _verify_state.verified = True
        else:
            logger.warning(
                f"Usuario: {usuario} | Verificación finalizada con ERRORES de integridad",
                queue=log_queue
            )

        return integrity_ok

    except Exception as e:
        logger.exception(
            f"Usuario: {usuario} | Fallo inesperado en verificación de integridad: {e}",
            queue=log_queue
        )
        set_integrity_failure()

        if raise_on_error:
            raise
        return False