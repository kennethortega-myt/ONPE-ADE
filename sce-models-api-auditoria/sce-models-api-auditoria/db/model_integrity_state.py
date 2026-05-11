from db.progress import get_det_parametro_valor
from logger_config import logger

HAY_FALLO_INTEGRIDAD_MODELOS = False

def set_integrity_failure():
    global HAY_FALLO_INTEGRIDAD_MODELOS
    HAY_FALLO_INTEGRIDAD_MODELOS = True


def reset_integrity_failure(log_queue="PROCESS"):
    global HAY_FALLO_INTEGRIDAD_MODELOS

    try:
        valor_parametro = get_det_parametro_valor(
            "p_modelo_prediccion",
            log_queue=log_queue
        )

        if valor_parametro is None:
            logger.warning("No se pudo obtener p_modelo_prediccion desde BD",queue=log_queue)
            return

        if valor_parametro.lower() == "false":
            logger.warning("p_modelo_prediccion=false en BD => Forzando HAY_FALLO_INTEGRIDAD_MODELOS = True",queue=log_queue)

        # Solo si BD dice TRUE
        HAY_FALLO_INTEGRIDAD_MODELOS = False
        logger.info("p_modelo_prediccion=true en BD => HAY_FALLO_INTEGRIDAD_MODELOS = False",queue=log_queue)

    except Exception as e:
        logger.exception(f"Error en reset_integrity_failure(), se mantiene fallo por seguridad: {e}",queue=log_queue)
        HAY_FALLO_INTEGRIDAD_MODELOS = True

