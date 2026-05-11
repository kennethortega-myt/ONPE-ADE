import numpy as np
import os
from datetime import datetime
import config
import requests
from db.progress import get_data_cab_acta, get_data_cab_acta_celeste, update_cab_acta, update_cab_acta_celeste, insert_det_acta_accion,select_det_acta_accion,get_guid_by_archivo_pk, download_file_from_dir, get_control_automatico
from db.progress import get_cantidad_agrupaciones_politicas
from util.modules_utils import determinar_tipo_acta, obtener_cantidad_candidatos, obtener_contexto, ajustar_acta_por_convencional, determinar_acta_instalacion_sufragio
from util.modules_utils import validate_acta
from util.exceptions import ActaNotFoundError, FTPFileNotFoundError
from logger_config import logger
from util import constantes
import cv2

def create_det_acta_accion(acta_id, usuario, codigo_cc, p_fecha_modificacion):

  insert_doc = {}
  insert_doc['id_cab_acta'] = acta_id
  insert_doc['c_accion'] = 'RECIBIDA'
  insert_doc['c_tiempo'] = 'FIN'
  insert_doc['n_orden'] = 2
  nueva_iteracion = 1
  results = None
  search_criteria = {
  'c_accion': 'RECIBIDA',             # Valor de c_accion
  'n_acta':  acta_id ,    # Valor de id_cab_acta (usualmente un ObjectId)
  'c_tiempo': 'FIN'                   # Valor de c_tiempo
  }
  results = select_det_acta_accion(search_criteria, constantes.QUEUE_LOGGER_VALUE_VALIDATE)
  if results:
    last_result = results[-1]
    if 'n_iteracion' in last_result:
        n_iteracion = last_result['n_iteracion']
        if isinstance(n_iteracion, (int, float)):
          nueva_iteracion = n_iteracion + 1

  insert_doc['n_iteracion'] = nueva_iteracion
  insert_doc['c_usuario_accion'] = usuario
  insert_doc['d_fecha_accion'] = p_fecha_modificacion
  insert_doc['n_activo'] = 1
  insert_doc['c_aud_usuario_creacion'] = usuario
  insert_doc['c_codigo_centro_computo'] = codigo_cc
  insert_doc['d_aud_fecha_creacion'] = datetime.now()
  insert_det_acta_accion(insert_doc, constantes.QUEUE_LOGGER_VALUE_VALIDATE)

def validate_mesa(acta_id: int, file_id: int, acta_type: int, usuario: str, codigo_cc: str, abrev_proceso: str):
    """
    Valida la mesa de la acta
    Args:
        acta_id: int
        file_id: int
        acta_type: int
        usuario: str
        codigo_cc: str
        abrev_proceso: str
    Returns:
        None
    """
    logger.info(f"validate_mesa, acta_id: {acta_id}, file_id: {file_id}, acta_type: {acta_type}, usuario: {usuario}, codigo_cc: {codigo_cc}, abrev_proceso: {abrev_proceso}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    FILE_ID = " file_id: "
    acta = get_data_cab_acta(acta_id, constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    if acta is None:
        logger.error(f"acta no encontrado en la base de datos {acta_id}{FILE_ID}{file_id}", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        raise ActaNotFoundError(f"acta_id={acta_id} file_id={file_id}")
    logger.info(f"Acta iniciando la validacion \nid_acta = {acta.get('n_acta_pk')}, \nc_estado_digitalizacion = {acta.get('c_estado_digitalizacion')}, \nn_digitalizacion_escrutinio = {acta.get('n_digitalizacion_escrutinio')}, \nn_digitalizacion_instalacion_sufragio = {acta.get('n_digitalizacion_instalacion_sufragio')}, \nc_observacion_digitalizacion_escrutinio = {acta.get('c_observacion_digitalizacion_escrutinio')}, \nc_observacion_digitalizacion_instalacion_sufragio = {acta.get('c_observacion_digitalizacion_instalacion_sufragio')}, \nd_aud_fecha_modificacion = {acta.get('d_aud_fecha_modificacion')} \n", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    ftp_id = get_guid_by_archivo_pk(file_id, constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    logger.info(f"ftp_id: {ftp_id} , file_id: {file_id}" ,queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    file_path = download_file_from_dir(ftp_id, constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    if file_path is None:
        logger.error(f"El archivo: {ftp_id} con fileid: {file_id} no existe en el directorio fuente.", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        raise FTPFileNotFoundError(f"ftp_id={ftp_id} file_id={file_id}")

    eleccion, ubigeo, tab_mesa, tipo_hoja_stae = obtener_contexto(acta, log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    is_stae = tab_mesa['n_solucion_tecnologica'] == constantes.SOLUCION_TECNOLOGICA_STAE
    codigo_eleccion = eleccion['c_codigo']
    is_convencional = constantes.FLUJO_CONVENCIONAL
    if ubigeo["c_ubigeo"][0] == "9": is_convencional=constantes.FLUJO_EXTRANJERO
    logger.debug(f"is_convencional: {is_convencional}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    
    cantidad_candidatos = get_cantidad_agrupaciones_politicas(int(acta_id), constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    cantidad_columnas_candidatos = obtener_cantidad_candidatos(eleccion, ubigeo)

    update_doc = {}
    mensaje_ok = False
    if acta_type == 2:
        if acta['n_archivo_escrutinio'] != file_id:
            return f"archivo escrutinio no encontrado en la base de datos {acta['n_archivo_escrutinio']}{FILE_ID}{file_id}"
        acta_tipo_disenio = determinar_tipo_acta(codigo_eleccion, is_stae, tipo_hoja_stae, cantidad_columnas_candidatos)
        acta_tipo_disenio = ajustar_acta_por_convencional(acta_tipo_disenio, is_convencional)
        mensaje_ok = validar_documento(
            acta_id, acta, file_path, eleccion['c_codigo'], acta_type, cantidad_candidatos, acta_tipo_disenio,
            campo_digital='n_digitalizacion_escrutinio',
            campo_obs='c_observacion_digitalizacion_escrutinio',
            update_doc=update_doc,
            is_convencional=is_convencional,
            tab_mesa = tab_mesa
        )

    elif acta_type == 3:
        if acta['n_archivo_instalacion_sufragio'] != file_id:
            return f"archivo instalacion sufragio no encontrado en la base de datos {acta['n_archivo_instalacion_sufragio']}{FILE_ID}{file_id}"
        tipo_acta_instalacion = determinar_acta_instalacion_sufragio(is_convencional)
        mensaje_ok = validar_documento(
            acta_id, acta, file_path, eleccion['c_codigo'], acta_type, cantidad_candidatos, tipo_acta_instalacion,
            campo_digital='n_digitalizacion_instalacion_sufragio',
            campo_obs='c_observacion_digitalizacion_instalacion_sufragio',
            update_doc=update_doc,
            is_convencional=is_convencional,
            tab_mesa = tab_mesa
        )

    logger.info(file_path, queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    if os.path.exists(file_path):
        os.remove(file_path)
        logger.info(f"El archivo {file_path} ha sido eliminado.", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    else:
        logger.info(f"El archivo {file_path} no existe.", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    logger.info("Valor del mensaje por procesamiento del modelo = %s", mensaje_ok, queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    acta = get_data_cab_acta(acta_id, constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    logger.info(f"Validacion de acta antes de ingresar a servicio de validacion automatica \nid_acta = {acta.get('n_acta_pk')}, \nc_estado_digitalizacion = {acta.get('c_estado_digitalizacion')}, \nn_digitalizacion_escrutinio = {acta.get('n_digitalizacion_escrutinio')}, \nn_digitalizacion_instalacion_sufragio = {acta.get('n_digitalizacion_instalacion_sufragio')}, \nc_observacion_digitalizacion_escrutinio = {acta.get('c_observacion_digitalizacion_escrutinio')}, \nc_observacion_digitalizacion_instalacion_sufragio = {acta.get('c_observacion_digitalizacion_instalacion_sufragio')}, \nd_aud_fecha_modificacion = {acta.get('d_aud_fecha_modificacion')} \n", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    logger.info(f"Estado de digitalizacion = {acta.get('c_estado_digitalizacion')}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    if acta.get('c_estado_digitalizacion') == 'D':
        logger.info("Ejecutando insercion de accion y transmision...", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        create_det_acta_accion(acta_id, usuario, codigo_cc, update_doc['d_aud_fecha_modificacion'])
        if get_control_automatico(log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE):
            mensaje_ok_combinado = _determinar_acta_aprobada_combinada(acta)
            logger.info(f"mensaje_ok (acta_type actual={acta_type}): {mensaje_ok} | mensaje_ok_combinado (ambos tipos): {mensaje_ok_combinado}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
            procesar_control_automatico(
                acta_id, usuario, codigo_cc, abrev_proceso, mensaje_ok_combinado,
                acta['n_archivo_escrutinio'], acta['n_archivo_instalacion_sufragio']
            )

    logger.info(f"Proceso de validacion Finalizado: acta_id: {acta_id}, file_id: {file_id}, acta_type: {acta_type}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

def validate_acta_celeste(acta_id: int, file_id: int, acta_type: int, usuario: str, codigo_cc: str, abrev_proceso: str):
    logger.info(f"validate_mesa, acta_id: {acta_id}, file_id: {file_id}, acta_type: {acta_type}, usuario: {usuario}, codigo_cc: {codigo_cc}, abrev_proceso: {abrev_proceso}", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    acta = get_data_cab_acta_celeste(acta_id, log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    if acta is None:
        return f"acta celeste no encontrado en la base de datos {acta_id} file_id: {file_id}"

    if acta_type == 2:
        if acta.get("n_archivo_escrutinio") != file_id:
            return "[CELESTE] El archivo no corresponde al acta de escrutinio"
        campo = "n_digitalizacion_escrutinio"
    elif acta_type == 3:
        if acta.get("n_archivo_instalacion_sufragio") != file_id:
            return "[CELESTE] El archivo no corresponde al acta de instalación/sufragio"
        campo = "n_digitalizacion_instalacion_sufragio"
    else:
        return f"[CELESTE] Tipo de acta inválido: {acta_type}"

    file_path = download_file_from_dir(get_guid_by_archivo_pk(file_id, constantes.QUEUE_LOGGER_VALUE_VALIDATE), log_queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    is_valid = validate_imagen_acta_celeste(file_path)

    if os.path.exists(file_path):
        os.remove(file_path)

    if not is_valid:
        return "[CELESTE] El archivo recibido no es válido (dañado o ilegible)"

    logger.info(f"[CELESTE] Archivo validado correctamente para tipo {acta_type}", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    handle_validation_success_celeste(acta_id, acta, campo, usuario)

    return "[CELESTE] Validación completada correctamente"

def validar_documento(acta_id, acta, file_path, codigo_eleccion, acta_type, cantidad_candidatos, acta_tipo_disenio, campo_digital, campo_obs, update_doc, is_convencional, tab_mesa):
    logger.info(f"validate_acta {file_path}", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    try:
        mensaje = validate_acta(file_path, codigo_eleccion, acta_type, acta['n_acta_pk'], cantidad_candidatos, acta_tipo_disenio, is_convencional, tab_mesa)
        mensaje_ok = mensaje == 'ok'
        if mensaje_ok:
            handle_validation_success(acta_id, acta, campo_digital, campo_obs, update_doc)
        else:
            handle_validation_error(acta_id, acta, campo_digital, campo_obs, mensaje, update_doc, acta_type)
        acta_aprobada = True
        if mensaje in [
            constantes.MENSAJE_VALIDACION_TABLA_VOTOS, 
            constantes.MENSAJE_VALIDACION_TABLA_PREFERENCIAL, 
            constantes.MENSAJE_VALIDACION_TABLAS_AMBOS,
            constantes.MENSAJE_VALIDACION_MARCADORES_NEGROS,
            
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_FOUND,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_DECODED,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_MATCH,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_ERROR
            ] and acta_type == 2:
            acta_aprobada = False
        if mensaje in [
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_FOUND,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_DECODED,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_MATCH,
            constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_ERROR
        ] or not mensaje_ok and acta_type == 3:
            acta_aprobada = False
        return acta_aprobada
    except Exception as e:
        logger.info(f"Error {e}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        handle_validation_error(acta_id, acta, campo_digital, campo_obs, 'Error de validación', update_doc, acta_type)
        e.with_traceback()
        return False

def validate_imagen_acta_celeste(file_path: str) -> bool:
    try:
        logger.info(f"[CELESTE] Validando imagen light: {file_path}")

        image = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)  # Carga en escala de grises
        if image is None:
            logger.warning(f"[CELESTE] No se pudo cargar la imagen (es None): {file_path}")
            return False

        std_dev = np.std(image)
        logger.info(f"[CELESTE] Desviación estándar de imagen: {std_dev}")

        if std_dev < 5:
            logger.warning(f"[CELESTE] Imagen con muy poca variación (posiblemente vacía o muy oscura): std={std_dev}")
            return False

        return True

    except Exception as e:
        logger.exception(f"[CELESTE] Error al validar imagen : {e}")
        return False

def handle_validation_success_celeste(acta_id: int, acta: dict, campo: str, usuario: str):
    update_doc = {
        campo: 1,
        'c_aud_usuario_modificacion': usuario,
        'd_aud_fecha_modificacion': datetime.now()
    }

    if _ambos_campos_validados(acta, campo):
        update_doc['c_estado_digitalizacion'] = 'D'
        logger.info("[CELESTE] Ambos lados digitalizados. Actualizando estado a 'D'.", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    update_cab_acta_celeste(acta_id, update_doc, constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    logger.info(f"[CELESTE] Se actualizó campo '{campo}' con éxito para acta_id={acta_id}", queue = constantes.QUEUE_LOGGER_VALUE_VALIDATE)

def handle_validation_success(acta_id, acta, campo, campo_obs, update_doc):
    update_doc[campo] = 1
    if _ambos_campos_validados(acta, campo):
        update_doc['c_estado_digitalizacion'] = 'D'
    update_doc[campo_obs] = "Sin Observaciones"
    update_doc['d_aud_fecha_modificacion'] = datetime.now()
    logger.info(f"Validacion de parametros antes de llegar a update en validation_success = {update_doc}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    update_cab_acta(acta_id, update_doc, constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    logger.info(f"\nConsulta a row del acta justo despues de ejecutar update (success) = \nid_acta = {acta.get('n_acta_pk')}, \nc_estado_digitalizacion = {acta.get('c_estado_digitalizacion')}, \nn_digitalizacion_escrutinio = {acta.get('n_digitalizacion_escrutinio')}, \nn_digitalizacion_instalacion_sufragio = {acta.get('n_digitalizacion_instalacion_sufragio')}, \nc_observacion_digitalizacion_escrutinio = {acta.get('c_observacion_digitalizacion_escrutinio')}, \nc_observacion_digitalizacion_instalacion_sufragio = {acta.get('c_observacion_digitalizacion_instalacion_sufragio')}, \nd_aud_fecha_modificacion = {acta.get('d_aud_fecha_modificacion')} \n", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)


def handle_validation_error(acta_id, acta, campo, campo_obs, mensaje, update_doc, acta_type):
    if mensaje in [constantes.MENSAJE_VALIDACION_TABLA_VOTOS, constantes.MENSAJE_VALIDACION_TABLA_PREFERENCIAL, constantes.MENSAJE_VALIDACION_TABLAS_AMBOS, constantes.MENSAJE_VALIDACION_MARCADORES_NEGROS]:
       update_doc[campo] = 3
    elif mensaje in [constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_FOUND,
                     constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_DECODED,
                     constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_MATCH,
                     constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_ERROR]:
       update_doc[campo] = 4
    elif mensaje == constantes.MENSAJE_VALIDACION_MARCADORES_NEGROS_INTEGRIDAD_OK:
        update_doc[campo] = 2
    else:
        update_doc[campo] = 3 if acta_type == 2 else 2
        logger.warning(f"handle_validation_error: mensaje no reconocido '{mensaje}' para acta_id={acta_id} acta_type={acta_type}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    if _ambos_campos_validados(acta, campo):
        update_doc['c_estado_digitalizacion'] = 'D'
    update_doc[campo_obs] = mensaje
    update_doc['d_aud_fecha_modificacion'] = datetime.now()
    logger.info(f"Validacion de parametros antes de llegar a update en validation_error = {update_doc}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    update_cab_acta(acta_id, update_doc, constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    logger.info(f"\nConsulta al acta justo despues de ejecutar update (error) = \nid_acta = {acta.get('n_acta_pk')}, \nc_estado_digitalizacion = {acta.get('c_estado_digitalizacion')}, \nn_digitalizacion_escrutinio = {acta.get('n_digitalizacion_escrutinio')}, \nn_digitalizacion_instalacion_sufragio = {acta.get('n_digitalizacion_instalacion_sufragio')}, \nc_observacion_digitalizacion_escrutinio = {acta.get('c_observacion_digitalizacion_escrutinio')}, \nc_observacion_digitalizacion_instalacion_sufragio = {acta.get('c_observacion_digitalizacion_instalacion_sufragio')}, \nd_aud_fecha_modificacion = {acta.get('d_aud_fecha_modificacion')} \n", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

def _ambos_campos_validados(acta, campo_actual):
    if campo_actual == 'n_digitalizacion_escrutinio':
        return acta.get('n_digitalizacion_instalacion_sufragio', 0) >= 1
    if campo_actual == 'n_digitalizacion_instalacion_sufragio':
        return acta.get('n_digitalizacion_escrutinio', 0) >= 1
    return False


def procesar_control_automatico(acta_id: int, usuario: str, codigo_cc: str, abrev_proceso: str, mensaje_ok: bool, file_escrutinio: int, file_instalacion_sufragio: int):
    logger.info("Se inicia el proceso de control automatico", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    try:
        if mensaje_ok:
            url = f"{config.API_URL_CENTRO_COMPUTO}/digitization/approveMesaModelo"
            logger.info(f"Url configurada: {url}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

            payload = {
                "actaId": acta_id,
                "estado": "A",
                "fileId1": file_escrutinio,
                "fileId2": file_instalacion_sufragio,
                "usuario": usuario,
                "codigoCc": codigo_cc,
                "abrevProceso": abrev_proceso,
            }

            headers = {
                'Content-Type': 'application/json',
                'accept': '*/*'
            }

            logger.info(f"Payload: {payload}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
            response = requests.post(url, headers=headers, json=payload, timeout=constantes.DEFAULT_TIMEOUT)
            response.raise_for_status()

            logger.info(f"Respuesta del servicio: {response.text}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        else:
            logger.info("Detectada una observacion en el acta por el modelo.", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)

    except requests.Timeout:
        logger.error(f"No se pudo aprobar automáticamente el acta {acta_id}: timeout al llamar al servicio {url}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    except requests.RequestException as e:
        logger.error(f"No se pudo aprobar automáticamente el acta {acta_id}: error en la petición HTTP: {str(e)}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
    except Exception as e:
        logger.exception(f"Error inesperado en procesar_control_automatico: {str(e)}", queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE)
        raise


def _determinar_acta_aprobada_combinada(acta):
    """
    Determina si ambas pasadas (escrutinio y AIS) fueron aprobadas consultando
    los campos de observacion ya persistidos en el acta re-fetcheado.
    """
    obs_escrutinio = acta.get('c_observacion_digitalizacion_escrutinio') or ''
    obs_ais = acta.get('c_observacion_digitalizacion_instalacion_sufragio') or ''

    mensajes_error_tabla = [
        constantes.MENSAJE_VALIDACION_TABLA_VOTOS,
        constantes.MENSAJE_VALIDACION_TABLA_PREFERENCIAL,
        constantes.MENSAJE_VALIDACION_TABLAS_AMBOS,
        constantes.MENSAJE_VALIDACION_MARCADORES_NEGROS,

        constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_FOUND,
        constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_DECODED,
        constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_NOT_MATCH,
        constantes.MENSAJE_VALIDACION_CODIGO_BARRAS_ERROR
    ]
    escrutinio_aprobado = obs_escrutinio not in mensajes_error_tabla
    ais_aprobado = obs_ais == 'Sin Observaciones'

    logger.info(
        f"_determinar_acta_aprobada_combinada: obs_escrutinio='{obs_escrutinio}' escrutinio_aprobado={escrutinio_aprobado} | "
        f"obs_ais='{obs_ais}' ais_aprobado={ais_aprobado}",
        queue=constantes.QUEUE_LOGGER_VALUE_VALIDATE
    )
    return escrutinio_aprobado and ais_aprobado