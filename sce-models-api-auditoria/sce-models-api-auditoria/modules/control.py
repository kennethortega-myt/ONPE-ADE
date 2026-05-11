import os
from datetime import datetime
import json
import cv2
from concurrent.futures import ThreadPoolExecutor
import db.model_integrity_state as integrity_state
from logger_config import logger, get_threads_budget
import time
import config
import multiprocessing as mp
from models import actasmodel
from models.digitsdetector import DigitsDetector
from models.imageutils import ImageFromDisk, ImageFromMemory
from models.signvalidator import SignValidator
from models.votesprefprocesor import VotesPrefImageProcessor
from models.votesprocessor import VotesImageProcessor
from secciones_rectangulos import main_rectangulos, crear_pagina_fallback
from secciones_miembros_mesa import get_secciones
from models.detectormodel.new_evaluate_image import evaluate_image_np_tiled
from db.progress import get_cantidad_agrupaciones_politicas, get_cantidad_columnas_preferenciales, get_secciones_by_abreviaturas
from db.progress import get_cantidad_paginas, submit_json, update_table_column
from db.progress import get_data_cab_acta, update_cab_acta, insert_det_acta_accion,select_det_acta_accion,get_guid_by_archivo_pk, download_file_from_dir_and_align, upload_file_to_dir_process_acta, upload_files_batch_to_dir_process_acta
from db.progress import get_data_tab_mesa, get_data_det_ubigeo_eleccion, get_data_mae_eleccion
from db.progress import get_coordenadas_por_eleccion_documento_electoral, insert_det_acta_rectangulo, get_seccion_abreviatura, procesar_marcadores, get_valor_copia_a_color, get_digitalizacion_flags, get_digitalizacion_procesamiento_manual_flag
from db.progress import delete_data_from_table, get_c_ubigeo_by_mesa_id, download_file_from_dir, procesar_marcadores_stae_vd, update_det_parametro_by_nombre, get_nombre_by_archivo_pk, get_codigo_eleccion_principal, get_n_tipo_transmision_by_mesa
from models.detectormodel.boxes_util import assign_bboxes_to_cells, evaluate_merge_bboxes
from models.detectormodel.contour_detector import evaluate_image_find_contour
from util.modules_utils import determinar_tipo_acta, obtener_cantidad_candidatos, obtener_contexto, ajustar_acta_por_convencional, determinar_acta_instalacion_sufragio
from util import constantes
from models.tipo_acta import TipoActa
from models.votesprocessorbase import VotesProcessorBase
from models.imageutils import ImagesLoader
from db.model_integrity import new_verify_model_weights, reset_weights_verification
from db.model_integrity_state import reset_integrity_failure
from models.binarymodel.valid_trazo_classification import MOBILENETV3_PATH, SPINALVGG_PATH
from models.mnistmodel.model import MODEL_PATH
from models.detectormodel.new_evaluate_image import RFDET_PATH
import numpy as np
from pdf2image import convert_from_path
from pathlib import Path
from dataclases.data_models import SectionParams, VotosParams, ProcessContext, VotosSectionParams, SectionStaeVdParams, TotalVotesContext, PrefRowProcessingContext, PrefVotesContext
from db.progress import detectar_marcadores_acta
from typing import Any
import traceback
import copy
from util.imagen_util import encode_to_jpeg
from util.exceptions import LEFallbackError, MMFallbackError, ProcessActaFallbackError

from db.execution_context import use_execution_context, get_context, resolve_workspace_path, _thread_local

def make_votos_params_preferenciales(
    base_params: SectionParams,
    rec_loader: Any,
    columns: int
) -> VotosParams:
    """Construye los parámetros para votos preferenciales"""
    return VotosParams(
        **base_params.__dict__,
        rec_loader=rec_loader,
        columns=columns
    )

def make_votos_params_normales(
    base_params: SectionParams,
    rec_loader: Any
) -> VotosParams:
    """Construye los parámetros para votos normales"""
    return VotosParams(
        **base_params.__dict__,
        rec_loader=rec_loader
    )

def _wait_for_file_ready(path: str, timeout: float = 3.0, interval: float = 0.05, log_queue: str = "default") -> bool:
    """Espera hasta que el archivo exista, tenga tamaño > 0 y se pueda abrir."""
    deadline = time.monotonic() + timeout
    warned   = False
    while time.monotonic() < deadline:
        if os.path.exists(path) and os.path.getsize(path) > 0:
            try:
                with open(path, 'rb'):
                    return True
            except OSError:
                pass
        if not warned:
            logger.warning(f"[FILE-WAIT] Archivo no listo aún, reintentando... | path={path}",queue=log_queue)
            warned = True
        time.sleep(interval)

    logger.error(f"[FILE-WAIT] Archivo no disponible tras {timeout}s | path={path}",queue=log_queue)
    return False

def extract_rectangles(source_file, rectangles, acta_observada, is_convencional, square_coords, acta_type, codigo_eleccion, obs_corregida, process_type = True, log_queue = constantes.QUEUE_LOGGER_VALUE_PROCESS):
  if process_type ==True:
    rectangles = [(
      x['section']['id'],
      (x['topLeft']['x'], x['topLeft']['y']),
      (x['bottomRight']['x'], x['bottomRight']['y'])
    ) for x in rectangles]

  logger.info(f"source_file extract_rectangles: {source_file}", queue = log_queue)
  if source_file and not _wait_for_file_ready(source_file, timeout=3.0, interval=0.05, log_queue=log_queue):
        raise ValueError(f"extract_rectangles: archivo no disponible tras espera | fuente={source_file}")
  image_loader = ImageFromDisk(source_file, log_queue)
  return actasmodel.extract_rectangles(image_loader, rectangles, acta_observada, is_convencional, square_coords, acta_type, codigo_eleccion, obs_corregida, log_queue=log_queue)


def prepare_votes_data_revocatoria(table_data, centro_computo,cod_usuario, rows, codigo_eleccion, copia_a_color):
  is_coordenadas = False

  votes_column = [0, 1, 2, 3, 4]  # Columnas de los votos
  headers = ["ORGANIZACIONES POLITICAS", ""]  # Encabezados de la tabla
  body = []
  new_verify_model_weights(
                    models=[
                        {"model_path": MODEL_PATH},
                        {"model_path": MOBILENETV3_PATH},
                        {"model_path": SPINALVGG_PATH},
                        {"model_path": RFDET_PATH},
                    ],
                    usuario=cod_usuario,
                    raise_on_error=True
                )
  for i, row_data in enumerate(table_data):
      row = {
          "nro": i + 1,
          "oopp": "",  # Ahora se deja vacío por defecto
      }
      for j, vote_col in enumerate(votes_column):
          hora_ms = datetime.now().strftime("%H_%M_%S_%f")
          fecha_hora = datetime.now().strftime("%Y_%m_%d")
          file_name = f"vote_{i+1}_{j}_{fecha_hora}_{hora_ms}.png"
          file_path = resolve_workspace_path(file_name)
          cv2.imwrite(file_path, row_data[vote_col])
          ctx = get_context()
          if ctx:
            ctx.add_temp_file(file_path)
          archivo_id = upload_file_to_dir_process_acta(file_path, cod_usuario=cod_usuario, centro_computo=centro_computo)
          predicted_number = DigitsDetector(ImageFromMemory(row_data[vote_col])).get_number(rows, codigo_eleccion, True, False, is_coordenadas, copia_a_color, pipe_implemented=False)
          row[f"total_votos_{j}"] = {
              "predicted": predicted_number,
              "file": archivo_id
          }
      body.append(row)
  logger.info(body, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
  return headers, body

def prepare_votes_data_paralelo(table_data, ans_preprocess, centro_computo, cod_usuario, rows, codigo_eleccion, 
                                is_coordenadas, copia_a_color, matriz_info, filtered_img, is_convencional,
                                pipe_implemented = True, error_corte_tabla = False):
    """
    Versión optimizada con procesamiento en paralelo usando ThreadPoolExecutor.
    """
    logger.info(f"Comienza el procesamiento paralelo de la tabla de votos totales, tabla localizada = {not error_corte_tabla}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    row_count = len(table_data)
    headers = ["ORGANIZACIONES POLITICAS", ""]
    if error_corte_tabla:
        return headers, [], []
    
    cell_bboxes_map = None
    pipe_status_map = None
    detector_enabled = config.ENABLE_DETECTOR == 1
    if not detector_enabled:
        pipeline_type = "SIN DETECTOR"
    elif pipe_implemented:
        pipeline_type = "NUEVO (DETECTOR)"
    else:
        pipeline_type = "LEGACY (DETECTOR DESACTIVADO POR INTEGRIDAD DE LA TABLA)"

    logger.info(
    f"[TOTAL VOTOS - PIPELINE] Detector={'ACTIVADO' if detector_enabled else 'DESACTIVADO'} "
    f"(ENABLE_DETECTOR={config.ENABLE_DETECTOR}) | "
    f"Pipe={'ON' if pipe_implemented else 'OFF'} | "
    f"Modo={pipeline_type}",
    queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
    )
    if pipe_implemented and config.ENABLE_DETECTOR == 1:
        # Convertir binaria → RGB si es necesario
        if len(filtered_img.shape) == 2:
            filtered_rgb = cv2.cvtColor(filtered_img, cv2.COLOR_GRAY2RGB)
        else:
            filtered_rgb = filtered_img
        bboxes_logic = evaluate_image_find_contour(filtered_rgb)
        bboxes_model = evaluate_image_np_tiled(filtered_rgb)
        bboxes_final, pipe_status_map = evaluate_merge_bboxes(
            bboxes_logic,
            bboxes_model,
            matriz_info,
            filtered_rgb
        )
        cell_bboxes_map = assign_bboxes_to_cells(bboxes_final, matriz_info, filtered_img)
        logger.info(f"{len(bboxes_final)} bounding boxes cargados y asignados a celdas.", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    vote_columns = [0]
    logger.info(f"Procesamiento con {row_count} filas", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    tvctx = TotalVotesContext(
        centro_computo=centro_computo,
        cod_usuario=cod_usuario,
        rows=rows,
        codigo_eleccion=codigo_eleccion,
        vote_columns=vote_columns,
        total_rows=row_count,
        is_coordenadas=is_coordenadas,
        copia_a_color=copia_a_color,
        pipe_implemented=pipe_implemented,
        is_convencional=is_convencional
    )
    main_ctx = get_context()
    new_verify_model_weights(
                    models=[
                        {"model_path": MODEL_PATH},
                        {"model_path": MOBILENETV3_PATH},
                        {"model_path": SPINALVGG_PATH},
                        {"model_path": RFDET_PATH},
                    ],
                    usuario=cod_usuario,
                    raise_on_error=True
                )

    def thread_safe_process_vote(i):
        _thread_local.ctx = main_ctx
        try:
            return process_total_votes(
                i,
                table_data[i],
                ans_preprocess[i] if ans_preprocess is not None else None,
                cell_bboxes_map,
                pipe_status_map,
                tvctx
            )
        finally:
            _thread_local.ctx = None

    max_inner_threads = min(row_count, get_threads_budget())
    logger.info(f"Hilos Asignados a este worker Total Votos = {max_inner_threads}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    with ThreadPoolExecutor(max_workers=max_inner_threads) as executor:
        processed_rows = list(executor.map(thread_safe_process_vote, range(row_count)))

    footer_count = min(3, len(processed_rows))
    body = [row for row in processed_rows[:-footer_count] if "nro" in row]
    footer = processed_rows[-footer_count:]

    return [headers, body, footer]

def process_total_votes(i, row_data, row_preproc, cell_bboxes_map, pipe_status_map, tvctx: TotalVotesContext):
    if not isinstance(row_data, list) or len(row_data) == 0:
        return {"nro": i + 1}

    is_footer = (i >= tvctx.total_rows - 3)
    row       = {"item": ""} if is_footer else {"nro": i + 1}

    detector   = DigitsDetector(None)
    fecha_hora = datetime.now().strftime("%Y_%m_%d")

    # 1: filtrar columnas válidas
    cols_validas = [
        (j, vote_col)
        for j, vote_col in enumerate(tvctx.vote_columns)
        if vote_col < len(row_data)
    ]
    if not cols_validas:
        return row

    # 2: encode todas las celdas en RAM
    items_upload = []
    for j, vote_col in cols_validas:
        hora_ms    = datetime.now().strftime("%H_%M_%S_%f")
        file_name  = f"vote_{i+1}_{j}_{fecha_hora}_{hora_ms}.png"
        jpeg_bytes = encode_to_jpeg(row_data[vote_col])
        items_upload.append((jpeg_bytes, file_name))

    # 3: un solo INSERT batch para toda la fila
    archivo_ids = upload_files_batch_to_dir_process_acta(
        items_upload,
        cod_usuario=tvctx.cod_usuario,
        centro_computo=tvctx.centro_computo,
    )

    for idx, (j, vote_col) in enumerate(cols_validas):
        vote_img = row_preproc[vote_col] if row_preproc else row_data[vote_col]

        cell_bboxes           = None
        cell_pipe_implemented = False
        if tvctx.pipe_implemented and config.ENABLE_DETECTOR == 1:
            cell_bboxes           = cell_bboxes_map.get((i, j), [])
            cell_pipe_implemented = pipe_status_map.get((i, j), False)

        detector.image_loader = ImageFromMemory(vote_img)
        if config.CUT_AND_PREDICT != 2:
            predicted_number = detector.get_number(
                tvctx.rows, tvctx.codigo_eleccion, True, False,
                tvctx.is_coordenadas, tvctx.copia_a_color, tvctx.pipe_implemented, 
                cell_bboxes, tvctx.is_convencional,
                cell_pipe_implemented=cell_pipe_implemented
            )
        else:
            predicted_number = detector.get_number_no_prediction(tvctx.rows, tvctx.codigo_eleccion, True, False,
                tvctx.is_coordenadas, tvctx.copia_a_color, tvctx.pipe_implemented, 
                cell_bboxes, tvctx.is_convencional,
                cell_pipe_implemented=cell_pipe_implemented)

        row[f"total_votos_{j}"] = {
            "predicted": predicted_number,
            "file": archivo_ids[idx],
        }

    return row

def process_pref_row(i, row_data, row_preproc, row_ctx: PrefRowProcessingContext):
    detector = DigitsDetector(None)
    pfctx    = row_ctx.pfctx
    row      = {"nro": i + 1}
    now = datetime.now()
    fecha_hora = now.strftime("%Y_%m_%d")

    # 1: encode todas las celdas en RAM
    items_upload = []   # [(jpeg_bytes, name), ...]
    for j in range(row_ctx.columns_count):
        hora_ms   = datetime.now().strftime("%H_%M_%S_%f")
        file_name = f"vote_{i+1}_{j}_{fecha_hora}_{hora_ms}.png"
        jpeg_bytes = encode_to_jpeg(row_data[j])
        items_upload.append((jpeg_bytes, file_name))

    # 2: un solo INSERT batch para toda la fila
    archivo_ids = upload_files_batch_to_dir_process_acta(
        items_upload,
        cod_usuario=pfctx.cod_usuario,
        centro_computo=pfctx.centro_computo,
    )

    # 3: predicción por celda
    for j in range(row_ctx.columns_count):
        vote_img_pref = row_preproc[j] if row_preproc else row_data[j]

        cell_bboxes          = None
        cell_pipe_implemented = False
        if pfctx.pipe_implemented and config.ENABLE_DETECTOR == 1:
            cell_bboxes           = row_ctx.cell_bboxes_map.get((i, j), [])
            cell_pipe_implemented = row_ctx.pipe_status_map.get((i, j), False)

        detector.image_loader = ImageFromMemory(vote_img_pref)
        if config.CUT_AND_PREDICT != 2:
            predicted_number = detector.get_number(
                pfctx.rows, pfctx.codigo_eleccion, False, False,
                pfctx.is_coordenadas, pfctx.copia_a_color, pfctx.pipe_implemented,
                cell_bboxes, pfctx.is_convencional,
                cell_pipe_implemented=cell_pipe_implemented
            )
        else:
            predicted_number = detector.get_number_no_prediction(
                pfctx.rows, pfctx.codigo_eleccion, False, False,
                pfctx.is_coordenadas, pfctx.copia_a_color, pfctx.pipe_implemented,
                cell_bboxes, pfctx.is_convencional,
                cell_pipe_implemented=cell_pipe_implemented
            )

        row[f"votos_{j + 1}"] = {
            "predicted": predicted_number,
            "file": archivo_ids[j],
        }
    return row

def _log_pipeline_mode(detector_enabled, pfctx):
    if not detector_enabled:
        pipeline_type = "SIN DETECTOR"
    elif pfctx.pipe_implemented:
        pipeline_type = "NUEVO (DETECTOR)"
    else:
        pipeline_type = "LEGACY (DETECTOR DESACTIVADO POR INTEGRIDAD DE LA TABLA)"
    logger.info(
        f"[PREFERENCIAL - PIPELINE] Detector={'ACTIVADO' if detector_enabled else 'DESACTIVADO'} "
        f"(ENABLE_DETECTOR={config.ENABLE_DETECTOR}) | "
        f"Pipe={'ON' if pfctx.pipe_implemented else 'OFF'} | "
        f"Modo={pipeline_type}",
        queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
    )

def _build_cell_bboxes_map(matriz_info, filtered_img):
    filtered_rgb = cv2.cvtColor(filtered_img, cv2.COLOR_GRAY2RGB) if len(filtered_img.shape) == 2 else filtered_img
    bboxes_logic = evaluate_image_find_contour(filtered_rgb)
    bboxes_model = evaluate_image_np_tiled(filtered_rgb)
    bboxes_final, pipe_status_map = evaluate_merge_bboxes(bboxes_logic, bboxes_model, matriz_info, filtered_rgb)
    cell_bboxes_map = assign_bboxes_to_cells(bboxes_final, matriz_info, filtered_img)
    logger.info(f"{len(bboxes_final)} bounding boxes cargados y asignados a celdas.", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    return cell_bboxes_map, pipe_status_map

def prepare_votes_pref_data(table_data, ans_preprocess, pfctx:PrefVotesContext, matriz_info, filtered_img, error_corte_tabla=False):

    logger.info(f"Comienza el procesamiento paralelo de la tabla de votos preferenciales, tabla localizada = {not error_corte_tabla}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    headers = ['' for _ in range(pfctx.columns)]
    if error_corte_tabla:
        return headers, []

    cell_bboxes_map = None
    pipe_status_map = None
    detector_enabled = config.ENABLE_DETECTOR == 1
    _log_pipeline_mode(detector_enabled, pfctx)
    if pfctx.pipe_implemented and detector_enabled:
        cell_bboxes_map, pipe_status_map = _build_cell_bboxes_map(matriz_info, filtered_img)

    columns_count = len(table_data[0])

    logger.info(f"Procesamiento con {len(table_data)} filas y {columns_count} columnas", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    row_ctx = PrefRowProcessingContext(
        pfctx=pfctx,
        columns_count=columns_count,
        cell_bboxes_map=cell_bboxes_map,
        pipe_status_map=pipe_status_map
    )

    main_ctx = get_context()
    def thread_safe_process_row(i, row_data):
        """Copia el contexto principal al hilo actual antes de procesar"""
        _thread_local.ctx = main_ctx
        try:
            return process_pref_row(
                i,
                row_data,
                ans_preprocess[i] if ans_preprocess is not None else None,
                row_ctx
            )
        finally:
            _thread_local.ctx = None

    new_verify_model_weights(
                    models=[
                        {"model_path": MODEL_PATH},
                        {"model_path": MOBILENETV3_PATH},
                        {"model_path": SPINALVGG_PATH},
                        {"model_path": RFDET_PATH},
                    ],
                    usuario=pfctx.cod_usuario,
                    raise_on_error=True
                )
    def process_with_threads():
        max_inner_threads = min(len(table_data), get_threads_budget())
        logger.info(f"Hilos Asignados a este worker Preferencial = {max_inner_threads}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        with ThreadPoolExecutor(max_workers=max_inner_threads) as executor:
            futures = [
                executor.submit(thread_safe_process_row, i, row_data)
                for i, row_data in enumerate(table_data)
            ]
            return [future.result() for future in futures]

    def process_sequential():
        body = []
        for i, row_data in enumerate(table_data):
            body.append(process_pref_row(
                i,
                row_data,
                ans_preprocess[i] if ans_preprocess is not None else None,
                row_ctx
            ))
        return body

    try:
        body = process_with_threads()
        logger.info(f"Procesamiento paralelo completado con {len(body)} filas", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    except Exception as e:
        logger.warning(f"Error en procesamiento paralelo, usando secuencial: {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        body = process_sequential()

    return headers, body

def _process_rectangles_calidad_solo(rectangle, rec_id, rtgctx: ProcessContext):

    section_name = rtgctx.config_map[rec_id].lower().replace(" ", "_")
    final_name, temp_name = generate_file_names(section_name)
    final_path = save_and_prepare_image(rectangle, final_name, temp_name)

    name = final_path
    archive_doc_id = _save_rotated_image(rtgctx.acta_type, rtgctx.codigo_eleccion, final_name, section_name, rtgctx.centro_computo, rtgctx.cod_usuario)
    
    params = SectionStaeVdParams(
        rectangle=rectangle,
        name=name,
        rec_id=rec_id,
        codigo_eleccion=rtgctx.codigo_eleccion,
        need_rotate=rtgctx.need_rotate,
        acta_type=rtgctx.acta_type,
        acta_id=rtgctx.acta_id,
        eleccion_id=rtgctx.eleccion_id,
        centro_computo=rtgctx.centro_computo,
        cod_usuario=rtgctx.cod_usuario
    )

    data = process_section_stae_vd(params)

    insert_det_acta_rectangulo(rtgctx.acta_id, rtgctx.eleccion_id, rtgctx.acta_type, archive_doc_id, data, constantes.QUEUE_LOGGER_VALUE_PROCESS)

def new_flujo_cortes_calidad_only(acta_id, file_id, is_convencional, eleccion_id, acta_type, copia_a_color, is_instalacion_sufragio, centro_computo, cod_usuario, codigo_eleccion):
    acta_observada = True
    file1_path, square_coords, _, obs_corregida = _download_and_log(file_id, acta_observada, is_convencional, str(eleccion_id), acta_type)
    config_map, rectangles, _ = _prepare_rectangles(file1_path, acta_observada, is_convencional, eleccion_id, acta_type, square_coords, is_instalacion_sufragio = is_instalacion_sufragio, obs_corregida = obs_corregida)

    need_rotate = acta_type in [
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO,
            constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
            constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
            constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO,
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO,
        ]

    for rec_id, rectangle in rectangles:
            rtgctx = ProcessContext(
            acta_id=acta_id,
            eleccion_id=eleccion_id,
            acta_type=acta_type,
            codigo_eleccion=str(eleccion_id),
            file1_path=file1_path,
            config_map=config_map,
            need_rotate=need_rotate,
            copia_a_color=copia_a_color,
            acta_observada=acta_observada,
            is_convencional=is_convencional,
            centro_computo=centro_computo,
            cod_usuario=cod_usuario
            )
            _process_rectangles_calidad_solo(rectangle, rec_id, rtgctx)
    
    if not is_instalacion_sufragio:
        _insert_and_cleanup_json(
            None, "votos_por_agrupacion_politica",
            acta_id, eleccion_id, acta_type,
            constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS
        )
        if codigo_eleccion in [constantes.COD_ELEC_PARLAMENTO, constantes.COD_ELEC_DIPUTADO,
                                constantes.COD_ELEC_SENADO_UNICO, constantes.COD_ELEC_SENADO_MULTIPLE]:
            _insert_and_cleanup_json(
                None, "votos_preferenciales",
                acta_id, eleccion_id, acta_type,
                constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS_PREFERENCIALES
            )

    if file1_path and os.path.exists(file1_path):
        try:
            os.remove(file1_path)
            logger.info(f"Archivo de imagen temporarl eliminado correctamente: {file1_path}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        except Exception as remove_error:
            logger.warning(f"No se pudo eliminar el archivo de imagen temporal {file1_path}: {remove_error}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)


def process_file(acta_id, eleccion_id, codigo_eleccion, acta_type, file_id,
                 copia_a_color, acta_observada, is_convencional, 
                 flag_procesamiento_manual, centro_computo="", cod_usuario=""):

    logger.info(f"Procesing acta_id:{acta_id}, file: {file_id}, elección: {eleccion_id}, tipo acta: {acta_type}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    file1_path = None
    is_instalacion_sufragio = acta_type in [
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO,
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO
        ]
    if flag_procesamiento_manual:
        logger.warning(f"Ejecutando cortes MANUALES de respaldo para acta {acta_id}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        new_flujo_cortes_calidad_only(
            acta_id, file_id, is_convencional, eleccion_id,
            acta_type, copia_a_color, is_instalacion_sufragio,
            centro_computo, cod_usuario, codigo_eleccion
        )
        return
    try:
        file1_path, square_coords, acta_reference_points, obs_corregida = _download_and_log(file_id, acta_observada, is_convencional, codigo_eleccion, acta_type)
        is_instalacion_sufragio = acta_type in [
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO,
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO
        ]
        config_map, rectangles, is_convencional = _prepare_rectangles(file1_path, acta_observada, is_convencional, eleccion_id, acta_type, square_coords, is_instalacion_sufragio, obs_corregida)

        need_rotate = acta_type in [
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO,
            constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
            constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
            constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO,
            constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO,
        ]
        rows = get_cantidad_agrupaciones_politicas(int(acta_id), constantes.QUEUE_LOGGER_VALUE_PROCESS)

        rtgctx = ProcessContext(
            acta_id=acta_id,
            eleccion_id=eleccion_id,
            acta_type=acta_type,
            codigo_eleccion=codigo_eleccion,
            file1_path=file1_path,
            config_map=config_map,
            need_rotate=need_rotate,
            copia_a_color=copia_a_color,
            acta_observada=acta_observada,
            is_convencional=is_convencional,
            centro_computo=centro_computo,
            cod_usuario=cod_usuario
            )
        for rec_id, rectangle in rectangles:
            _process_rectangle(rectangle, rec_id, acta_reference_points, is_instalacion_sufragio, rows, rtgctx)
    except Exception as e:
        logger.exception(f"Error en process_file (acta_id={acta_id}, file_id={file_id}): {e}\n{traceback.format_exc()}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    finally:
        if file1_path and os.path.exists(file1_path):
            try:
                os.remove(file1_path)
                logger.info(f"Archivo de imagen temporarl eliminado correctamente: {file1_path}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            except Exception as remove_error:
                logger.warning(f"No se pudo eliminar el archivo de imagen temporal {file1_path}: {remove_error}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)


def _download_and_log(file_id, acta_observada, is_convencional, codigo_eleccion, acta_type):
    ftp_uuid = get_guid_by_archivo_pk(file_id, constantes.QUEUE_LOGGER_VALUE_PROCESS)
    file1_path, square_coords, acta_reference_points, obs_corregida = download_file_from_dir_and_align(ftp_uuid, acta_observada, is_convencional, codigo_eleccion, acta_type, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    logger.info(f"Procesando archivo con ftp_uuid:{ftp_uuid} y file1_path:{file1_path}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    return file1_path, square_coords, acta_reference_points, obs_corregida


def _prepare_rectangles(file1_path, acta_observada, is_convencional, eleccion_id, acta_type, square_coords, is_instalacion_sufragio, obs_corregida):
    
    if is_instalacion_sufragio:
        eleccion_codigo = get_codigo_eleccion_principal(log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

        if eleccion_codigo is None:
            raise ValueError("No se encontró elección principal (n_principal = 1)")
    else:
        eleccion_codigo = eleccion_id
    datos = get_coordenadas_por_eleccion_documento_electoral(eleccion_codigo, acta_type, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    config_map = {datos[key][0]: datos[key][1] for key in datos}
    rectangles = [
        (datos[key][0],
         (float(datos[key][2]), float(datos[key][4])),
         (float(datos[key][3]), float(datos[key][5])))
        for key in datos
    ]
    rectangles, is_convencional = extract_rectangles(file1_path, rectangles, acta_observada, is_convencional, square_coords, acta_type, eleccion_codigo, obs_corregida, process_type=False)
    return config_map, rectangles, is_convencional

def _process_rectangle(rectangle, rec_id, acta_reference_points, is_instalacion_sufragio, rows, rtgctx: ProcessContext):

    section_name = rtgctx.config_map[rec_id].lower().replace(" ", "_")
    final_name, temp_name = generate_file_names(section_name)
    final_path = save_and_prepare_image(rectangle, final_name, temp_name)

    name = final_path
    img_limpia_path, point0, point1, point2, point3, guide_lines = None, None, None, None, None, None

    if not rtgctx.acta_observada and not is_instalacion_sufragio:
        if rtgctx.marcadores_cache is None:
            matriz_marcadores, img = detectar_marcadores_acta(
                rtgctx,
                acta_reference_points
            )
            rtgctx.marcadores_cache = {
                "matriz": matriz_marcadores,
                "img": img
            }
        img_limpia_path, point0, point1, point2, point3, guide_lines = _maybe_procesar_marcadores(
            rtgctx.acta_type, rtgctx.codigo_eleccion, rtgctx.file1_path, name, rtgctx.acta_id,
            section_name, rtgctx.is_convencional, rtgctx.cod_usuario, rtgctx.centro_computo,
            rtgctx
        )

    archive_doc_id = _save_rotated_image(rtgctx.acta_type, rtgctx.codigo_eleccion, final_name, section_name, rtgctx.centro_computo, rtgctx.cod_usuario)
    
    params = SectionParams(
        rectangle=rectangle,
        name=name,
        rec_id=rec_id,
        codigo_eleccion=rtgctx.codigo_eleccion,
        copia_a_color=rtgctx.copia_a_color,
        need_rotate=rtgctx.need_rotate,
        rows=rows,
        acta_type=rtgctx.acta_type,
        acta_id=rtgctx.acta_id,
        eleccion_id=rtgctx.eleccion_id,
        img_limpia_path=img_limpia_path,
        point0=point0,
        point1=point1,
        point2=point2,
        point3=point3,
        guide_lines=guide_lines,
        acta_observada=rtgctx.acta_observada,
        centro_computo=rtgctx.centro_computo,
        cod_usuario=rtgctx.cod_usuario
    )

    data = _process_section(params, rtgctx.is_convencional, is_instalacion_sufragio)

    insert_det_acta_rectangulo(rtgctx.acta_id, rtgctx.eleccion_id, rtgctx.acta_type, archive_doc_id, data, constantes.QUEUE_LOGGER_VALUE_PROCESS)

def _maybe_procesar_marcadores(acta_type, codigo_eleccion, file1_path, name, acta_id,
                               section_name, is_convencional, cod_usuario, centro_computo, rtgctx: ProcessContext):

    is_preferencial = constantes.ABREV_TABLA_VOTO_PREFERENCIAL in name
    es_voto = constantes.ABREV_TABLA_VOTO_TOTAL in name and not is_preferencial

    if acta_type == constantes.ABREV_ACTA_INSTALACION_SUFRAGIO or acta_type == constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO or not (es_voto or is_preferencial):
        return None, None, None, None, None, None

    ajustes = [True, False] if es_voto else [False]

    img_limpia_path, point0, point1, point2, point3, guide_lines = None, None, None, None, None, None

    for ajustar in ajustes:
        matriz_marcadores = copy.deepcopy(rtgctx.marcadores_cache["matriz"])
        result = procesar_marcadores(
            codigo_eleccion, acta_type, file1_path, name, is_convencional, 
            matriz_marcadores, ajustar_corte_totales=ajustar
        )
        if not result:
            continue

        img_limpia_path, intersecciones, guide_lines = result
        point0, point1, point2, point3 = intersecciones

        if es_voto:
            processor = VotesProcessorBase(ImagesLoader())
            tipo = "columna" if ajustar else "tabla"
            archivos = processor.cortar_imagenes_total_votos(
                img_limpia_path, point0, point3, acta_id,
                section_name, tipo, cod_usuario, centro_computo
            )
            _insert_json_if_tabla(archivos, section_name, acta_id, codigo_eleccion, acta_type, tipo)

    return img_limpia_path, point0, point1, point2, point3, guide_lines


def _save_rotated_image(acta_type, codigo_eleccion, final_name, section_name, centro_computo, cod_usuario, is_convencional = constantes.FLUJO_CONVENCIONAL):
    now = datetime.now()
    fecha_hora = now.strftime("%Y_%m_%d")
    hora_ms = now.strftime("%H_%M_%S_%f")
    archive_doc_id = save_image_with_optional_rotation(acta_type, codigo_eleccion, final_name,section_name, 
                                                       fecha_hora, hora_ms, centro_computo, 
                                                       cod_usuario, is_convencional)
    return archive_doc_id


def _get_rotation_decision(
    acta_type,
    codigo_eleccion,
    final_name,
    is_convencional
):
    """
    Retorna:
    - must_rotate (bool)
    - rotate_left (bool)  → True = 90° CCW, False = 90° CW
    """
    rotate_flag = False
    tipo = TipoActa(codigo_eleccion)
    if constantes.ABREV_FIRMA_PERSONEROS in final_name:
        if is_convencional == constantes.FLUJO_CONVENCIONAL:
            rotate_flag = tipo.get_personero_rotation_flag()
        else:
            rotate_flag = tipo.get_personero_stae_rotation_flag()

    if is_convencional == constantes.FLUJO_CONVENCIONAL:
        must_rotate = (
            acta_type in {
                constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
                constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
                constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO,
            }
            or rotate_flag
        )
        return must_rotate, False

    if is_convencional == constantes.FLUJO_STAE:
        must_rotate = acta_type in {
            constantes.ABREV_ACTA_INSTALACION_STAE,
            constantes.ABREV_ACTA_SUFRAGIO_STAE,
        } or rotate_flag
        must_rotate_left = acta_type in {
            constantes.ABREV_ACTA_INSTALACION_STAE,
            constantes.ABREV_ACTA_SUFRAGIO_STAE,
        }
        return must_rotate, must_rotate_left

    return rotate_flag, False


def save_image_with_optional_rotation(
    acta_type,
    codigo_eleccion,
    final_name,
    section_name,
    fecha_hora,
    hora_ms,
    centro_computo,
    cod_usuario,
    is_convencional=constantes.FLUJO_CONVENCIONAL
):

    must_rotate, rotate_left = _get_rotation_decision(
        acta_type,
        codigo_eleccion,
        final_name,
        is_convencional
    )
    ctx = get_context()
    original_path = resolve_workspace_path(final_name)

    if must_rotate:
        img = cv2.imread(original_path)
        rotated_name = f"{section_name}_rotated_{fecha_hora}_{hora_ms}.png"
        rotated_path = resolve_workspace_path(rotated_name)


        rotation = (
            cv2.ROTATE_90_COUNTERCLOCKWISE
            if rotate_left
            else cv2.ROTATE_90_CLOCKWISE
        )

        img = cv2.rotate(img, rotation)
        cv2.imwrite(rotated_path, img)
        if ctx:
            ctx.add_temp_file(rotated_path)

        return upload_file_to_dir_process_acta(
            rotated_path,
            cod_usuario=cod_usuario,
            centro_computo=centro_computo
        )

    ctx = get_context()
    if ctx:
        ctx.add_temp_file(original_path)

    return upload_file_to_dir_process_acta(
        original_path,
        cod_usuario=cod_usuario,
        centro_computo=centro_computo
    )


def _process_section(params: SectionParams, is_convencional, is_instalacion_sufragio) -> dict:

    data = {"n_seccion": params.rec_id}

    if constantes.ABREV_FIRMA in params.name:
        data["b_valido"] = process_firma_section(params.rectangle, params.name, params.codigo_eleccion, 
                                                 params.copia_a_color, is_convencional, is_instalacion_sufragio)

    elif constantes.ABREV_VOTO in params.name:
        rec_loader = ImageFromMemory(params.rectangle, rotate=params.need_rotate)
        votos_params = VotosSectionParams(
            rec_loader=rec_loader,
            name=params.name,
            rows=params.rows,
            is_convencional=is_convencional,
            section=params
        )
        votos_data = process_votos_section(votos_params)
        data["c_votos"] = json.dumps(votos_data)

    elif constantes.ABREV_TOTAL_CIUDADANOS in params.name:
        detector = DigitsDetector(ImageFromMemory(params.rectangle, rotate=params.need_rotate))
        pipe_implemented = False if config.ENABLE_DETECTOR == 2 else True
        if config.CUT_AND_PREDICT != 2:
            new_verify_model_weights(
                    models=[
                        {"model_path": MODEL_PATH},
                        {"model_path": MOBILENETV3_PATH},
                        {"model_path": SPINALVGG_PATH},
                        {"model_path": RFDET_PATH},
                    ],
                    usuario=params.cod_usuario,
                    raise_on_error=True
                )
            data["c_valor_modelo"] = detector.get_number(params.rows, params.codigo_eleccion, True, True, is_coordenadas=False, copia_a_color=params.copia_a_color, 
                        pipe_implemented=pipe_implemented, is_convencional=is_convencional)
        else:
            data["c_valor_modelo"] = detector.get_number_no_prediction(params.rows, params.codigo_eleccion, True, True, is_coordenadas=False, copia_a_color=params.copia_a_color, 
                     pipe_implemented=pipe_implemented, is_convencional=is_convencional)
    return data

def _insert_json_if_tabla(archivos, section_name, acta_id, eleccion_id, acta_type, tipo):
    processor = VotesProcessorBase(ImagesLoader())
    temp_json_path = None
    try:
        json_result, temp_json_path = processor.generar_json_votos(archivos, section_name, acta_id, tipo)
        if tipo == "tabla":
            insert_json_votos_by_abreviatura(
                acta_id, eleccion_id, acta_type, "TOT_VOTOS", json_result
            )
    except Exception as e:
        import traceback
        logger.exception(f"Error generando el json del segundo control de calidad (TOTAL_VOTOS): {e}\n{traceback.format_exc()}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    finally:
        if tipo == "tabla" and temp_json_path and os.path.exists(temp_json_path):
            try:
                os.remove(temp_json_path)
                logger.info(f"Archivo temporal eliminado: {temp_json_path}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            except Exception as cleanup_err:
                logger.exception(f"No se pudo eliminar archivo temporal {temp_json_path}: {cleanup_err}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)


def insert_json_votos_by_abreviatura(acta_id: int, eleccion_id: str, acta_type: str, abreviatura: str, votos_json: dict, log_queue:str = constantes.QUEUE_LOGGER_VALUE_PROCESS):
    try:
        secciones = get_secciones_by_abreviaturas([abreviatura])
        seccion = secciones.get(abreviatura)

        if not seccion:
            raise ValueError(f" Abreviatura no encontrada: {abreviatura}")

        data = {
            "n_seccion": seccion["n_seccion_pk"],
            "c_votos": json.dumps(votos_json)
        }

        archive_doc_id = None
        inserted_id = insert_det_acta_rectangulo(
            acta_id=acta_id,
            eleccion_id=eleccion_id,
            acta_type=acta_type,
            archive_doc_id=archive_doc_id,
            data=data,
            log_queue=log_queue
        )

        logger.info(f"Sección {abreviatura} insertada con ID {inserted_id}", queue = log_queue)
        return inserted_id

    except Exception as e:
        logger.info(f"Error en insert_json_votos_by_abreviatura ({abreviatura}): {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        return None


def generate_file_names(section_name):
    now = datetime.now()
    hora_ms = now.strftime("%H_%M_%S_%f")
    fecha_hora = now.strftime("%Y_%m_%d")
    final_name = f"{section_name}_{fecha_hora}_{hora_ms}.png"
    temp_name = f"temp_{int(now.timestamp())}_{section_name}.png"
    return final_name, temp_name

def save_and_prepare_image(rectangle, final_name, temp_name):
    ctx = get_context()
    temp_path = resolve_workspace_path(temp_name)
    final_path = resolve_workspace_path(final_name)
    cv2.imwrite(temp_path, rectangle)
    if ctx:
        ctx.add_temp_file(temp_path)
    os.replace(temp_path, final_path)
    if ctx:
        ctx.temp_files.discard(temp_path)
        ctx.add_temp_file(final_path)
    return final_path

def process_firma_section(rectangle, name, codigo_eleccion, copia_a_color, is_convencional, is_instalacion_sufragio):
    is_emc = False
    if codigo_eleccion in [constantes.COD_ELEC_DISTRITAL]:
        is_emc = True
    h, w = rectangle.shape[:2]
    rec_loader = ImageFromMemory(rectangle, rotate=h > w)
    if constantes.ABREV_FIRMA_PERSONEROS not in name:
        try:
            return SignValidator(rec_loader).validate(is_emc, copia_a_color, is_convencional, is_instalacion_sufragio)
        except Exception:
            return False
    return None

def process_votos_section(params: VotosSectionParams) -> dict:
    sect = params.section

    columns = get_cantidad_columnas_preferenciales(
        int(sect.acta_id),
        log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
    )

    if constantes.ABREV_TABLA_VOTO_PREFERENCIAL in params.name:
        votos_params = make_votos_params_preferenciales(
            sect, params.rec_loader, columns
        )
        return _process_votos_preferenciales(votos_params, params.is_convencional)

    if sect.codigo_eleccion == constantes.COD_ELEC_REVOCATORIA:
        return _process_votos_revocatoria(
            params.rec_loader,
            sect.acta_id,
            sect.acta_type,
            sect.img_limpia_path,
            sect.point0, sect.point3,
            sect.centro_computo,
            sect.cod_usuario,
            params.rows,
            sect.codigo_eleccion,
            sect.copia_a_color
        )

    votos_params = make_votos_params_normales(sect, params.rec_loader)
    return _process_votos_normales(votos_params, params.is_convencional)

def _safe_remove(path: str, log_queue: str = constantes.QUEUE_LOGGER_VALUE_PROCESS):
    """Elimina un archivo ignorando errores de bloqueo en Windows."""
    try:
        os.remove(path)
    except PermissionError as e:
        logger.warning(
            f"[FILE-REMOVE] Archivo bloqueado, no se pudo eliminar | path={path} | error={e}",
            queue=log_queue
        )
    except FileNotFoundError:
        pass  # ya fue eliminado, no es un problema

def _process_votos_preferenciales(params: VotosParams, is_convencional) -> dict:

    section_name = "voto_preferencial"
    error_corte_tabla = False

    if not params.acta_observada:
        info, ans_preprocess, matriz_info, filtered_img, archivos_columnas, is_coordenadas, pipe_implemented = VotesPrefImageProcessor(params.rec_loader).get_data_cortes_preferenciales_coordenadas(
            params.acta_id, params.img_limpia_path, params.point0, params.point3,
            params.guide_lines, params.copia_a_color, section_name, is_convencional, params.cod_usuario, params.centro_computo
        )
        _safe_remove(params.img_limpia_path)

        _insert_and_cleanup_json(
            archivos_columnas,section_name, 
            params.acta_id, params.eleccion_id, params.acta_type, "VOTO_PRE"
        )

    else:
        is_coordenadas = True
        pipe_implemented = True
        matriz_info = None
        filtered_img = None
        info, ans_preprocess, error_corte_tabla, archivos_columnas, matriz_info, filtered_img = VotesPrefImageProcessor(params.rec_loader).get_data_cortes_preferencial_observada(
            params.acta_id, section_name, params.cod_usuario, params.centro_computo, params.copia_a_color, is_convencional, params.acta_type,
            log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
        
        _insert_and_cleanup_json(
            archivos_columnas, section_name,
            params.acta_id, params.eleccion_id, params.acta_type, "VOTO_PRE"
        )
    
    pfctx = PrefVotesContext(
        centro_computo=params.centro_computo,
        cod_usuario=params.cod_usuario,
        rows= params.rows,
        columns=params.columns,
        codigo_eleccion= params.codigo_eleccion,
        is_coordenadas = is_coordenadas,
        copia_a_color = params.copia_a_color,
        is_convencional = is_convencional,
        pipe_implemented=pipe_implemented
    )
    info = prepare_votes_pref_data(
        info, ans_preprocess, pfctx, matriz_info, 
        filtered_img, error_corte_tabla
    )

    return {"headers": info[0], "body": info[1]}

def _process_votos_revocatoria(rec_loader, acta_id, acta_type, img_limpia_path,
                               point0, point3,
                               centro_computo, cod_usuario, rows, codigo_eleccion, copia_a_color):

    if acta_type != constantes.ABREV_ACTA_INSTALACION_SUFRAGIO or constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO:
        info = VotesImageProcessor(rec_loader).get_data_cortes_revocatoria_alterno(
            acta_id, img_limpia_path, point0, point3
        )
        os.remove(img_limpia_path)

    info = prepare_votes_data_revocatoria(info, centro_computo, cod_usuario, rows, codigo_eleccion, copia_a_color)
    return {"headers": info[0], "body": info[1]}

def _process_votos_normales(params: VotosParams, is_convencional) -> dict:

    error_corte_tabla = False
    if params.acta_type != constantes.ABREV_ACTA_INSTALACION_SUFRAGIO or constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO:
        if not params.acta_observada:
            info, ans_preprocess, matriz_info, filtered_img, is_coordenadas, pipe_implemented = VotesImageProcessor(params.rec_loader).get_data_cortes_coordenadas(
                params.acta_id, params.img_limpia_path, params.point0, params.point3, params.guide_lines, params.copia_a_color,
                is_convencional
            )
            _safe_remove(params.img_limpia_path)
        else:
            is_coordenadas = True
            pipe_implemented = True
            matriz_info = None
            filtered_img = None
            section_name = "votos_por_agrupacion_politica"
            info, ans_preprocess, error_corte_tabla, archivos_columnas, matriz_info, filtered_img = VotesImageProcessor(params.rec_loader).get_data_cortes_observada(
                params.acta_id, section_name, params.codigo_eleccion, params.acta_type, params.cod_usuario, params.centro_computo, 
                params.copia_a_color, is_convencional,
                log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
            )

            _insert_and_cleanup_json(
                archivos_columnas, section_name,
                params.acta_id, params.eleccion_id, params.acta_type, "TOT_VOTOS"
            )
    info = prepare_votes_data_paralelo(
        info, ans_preprocess, params.centro_computo, params.cod_usuario, params.rows,
        params.codigo_eleccion, is_coordenadas, params.copia_a_color, 
        matriz_info, filtered_img, is_convencional, pipe_implemented, error_corte_tabla,
    )

    return {"headers": info[0], "body": info[1], "footer": info[2]}

def _insert_and_cleanup_json(archivos, section_name,
                             acta_id, eleccion_id, acta_type, tipo):
    processor = VotesProcessorBase(ImagesLoader())
    temp_json_path = None
    try:
        json_result, temp_json_path = processor.generar_json_votos(
            archivos, section_name, acta_id, tipo, 
        )
        insert_json_votos_by_abreviatura(
            acta_id, eleccion_id, acta_type, tipo, json_result
        )
    except Exception as e:
        import traceback
        logger.exception(f"Error generando el json del segundo control de calidad (TOTAL Y PREFERENCIAL): {e}\n{traceback.format_exc()}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    finally:
        if temp_json_path and os.path.exists(temp_json_path):
            try:
                os.remove(temp_json_path)
                logger.info(f"Archivo json temporal eliminado: {temp_json_path}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            except Exception as cleanup_err:
                logger.exception(f"No se pudo eliminar el archivo json temporal {temp_json_path}: {cleanup_err}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)


def process_files_lista_electores(mesa_id, abrev_documento, usuario, cod_centro_computo):
  logger.info(f"Generando Secciones: {mesa_id}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
  main_rectangulos(mesa_id, abrev_documento,usuario, cod_centro_computo)
  logger.info(f"Fin proceso generación de secciones LE: {mesa_id}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)

def process_files_miembros_mesa(mesa_id, abrev_documento, contexto_mm, usuario, cod_centro_computo):
  logger.info(f"Generando Secciones Mm : {mesa_id}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
  get_secciones(mesa_id, abrev_documento, contexto_mm, usuario, cod_centro_computo)
  logger.info(f"Fin proceso, generación de secciones MMm: {mesa_id}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

def _insertar_secciones_para_cara(acta_id: int, eleccion_id_fb, acta_type_fb):
    is_instalacion_or_sufragio = acta_type_fb in [
        constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO,
        constantes.ABREV_ACTA_INSTALACION_SUFRAGIO
    ]
    eleccion_codigo = get_codigo_eleccion_principal(log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS) if is_instalacion_or_sufragio else eleccion_id_fb
    secciones_fb = get_coordenadas_por_eleccion_documento_electoral(
        eleccion_codigo, acta_type_fb, constantes.QUEUE_LOGGER_VALUE_PROCESS
    )
    coords_all_none = secciones_fb and all(
        row[2] is None and row[3] is None and row[4] is None and row[5] is None
        for row in secciones_fb.values()
    )
    if coords_all_none:
        logger.info(f"Coordenadas todas en None para cara {acta_type_fb}, se omite inserción fallback.", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        return
    for sec_id in secciones_fb:
        insert_det_acta_rectangulo(
            acta_id=acta_id,
            eleccion_id=eleccion_id_fb,
            acta_type=acta_type_fb,
            archive_doc_id=-1,
            data={"n_seccion": sec_id},
            log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
    logger.info(f"Registros fallback insertados para cara {acta_type_fb}: {len(secciones_fb)}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)


def _insertar_fallback_rectangulos(acta_id: int, ctx):
    acta_fallback = get_data_cab_acta(acta_id, constantes.QUEUE_LOGGER_VALUE_PROCESS)
    if not acta_fallback:
        return
    eleccion_fb, ubigeo_fb, tab_mesa_fb, tipo_hoja_stae_fb = obtener_contexto(acta_fallback, constantes.QUEUE_LOGGER_VALUE_PROCESS)
    cantidad_candidatos_fb = obtener_cantidad_candidatos(eleccion_fb, ubigeo_fb)
    is_convencional_fb = constantes.FLUJO_EXTRANJERO if ubigeo_fb["c_ubigeo"][0] == "9" else constantes.FLUJO_CONVENCIONAL
    eleccion_id_fb = eleccion_fb['n_eleccion_pk']
    codigo_fb = eleccion_fb['c_codigo']
    is_stae_fb = tab_mesa_fb['n_solucion_tecnologica'] == constantes.SOLUCION_TECNOLOGICA_STAE
    tipo_acta_escrutinio_fb = determinar_tipo_acta(codigo_fb, is_stae_fb, tipo_hoja_stae_fb, cantidad_candidatos_fb)
    tipo_acta_escrutinio_fb = ajustar_acta_por_convencional(tipo_acta_escrutinio_fb, is_convencional_fb)
    tipo_acta_instalacion_fb = determinar_acta_instalacion_sufragio(is_convencional_fb)
    for acta_type_fb in [tipo_acta_escrutinio_fb, tipo_acta_instalacion_fb]:
        if not acta_type_fb:
            continue
        _insertar_secciones_para_cara(acta_id, eleccion_id_fb, acta_type_fb)
    if tipo_acta_escrutinio_fb:
        _insert_and_cleanup_json(
            None, "votos_por_agrupacion_politica",
            acta_id, eleccion_id_fb, tipo_acta_escrutinio_fb,
            constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS
        )
        if codigo_fb in [constantes.COD_ELEC_PARLAMENTO, constantes.COD_ELEC_DIPUTADO,
                         constantes.COD_ELEC_SENADO_UNICO, constantes.COD_ELEC_SENADO_MULTIPLE]:
            _insert_and_cleanup_json(
                None, "votos_preferenciales",
                acta_id, eleccion_id_fb, tipo_acta_escrutinio_fb,
                constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS_PREFERENCIALES
            )
    ctx.conn.commit()

def process_acta(acta_id: int, file_id1: int, file_id2: int, cod_usuario: str, centro_computo: str, workspace: str):
    reset_integrity_failure()
    reset_weights_verification()
    with use_execution_context(workspace=str(workspace)) as ctx:
        try:
            logger.info(f"Procesando acta: {acta_id}, file_id1: {file_id1}, file_id2: {file_id2}, cod_usuario: {cod_usuario}, centro_computo: {centro_computo}.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            acta = get_data_cab_acta(acta_id, constantes.QUEUE_LOGGER_VALUE_PROCESS)
            
            if not acta or acta['n_archivo_escrutinio'] != file_id1 or acta['n_archivo_instalacion_sufragio'] != file_id2:
                _log_acta_validation_errors(acta, file_id1, file_id2)
                return
            rows_deleted = delete_data_from_table(
                    "det_acta_rectangulo", "n_acta", acta_id,
                    log_queue = constantes.QUEUE_LOGGER_VALUE_PROCESS
                )
            logger.info(f"Registros de rectángulos eliminados por seguridad: {rows_deleted}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            
            observado_escrutinio, observado_instalacion_sufragio = get_digitalizacion_flags(acta_id, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            flag_procesamiento_manual = get_digitalizacion_procesamiento_manual_flag(acta_id, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info("Valor de Procesamiento Manual = %s",flag_procesamiento_manual, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info("Valor de Acta Observada Escrutinio = %s",observado_escrutinio, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info("Valor de Acta Observada Instalacion Sufragio = %s",observado_instalacion_sufragio, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            
            valor_str = get_valor_copia_a_color(log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            copia_a_color = valor_str.lower() == 'true'
            eleccion, ubigeo, tab_mesa, tipo_hoja_stae = obtener_contexto(acta, log_queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            cantidad_candidatos = obtener_cantidad_candidatos(eleccion, ubigeo)

            is_convencional = constantes.FLUJO_CONVENCIONAL
            if ubigeo["c_ubigeo"][0] == "9": is_convencional = constantes.FLUJO_EXTRANJERO
            
            _procesar_escrutinio(
                acta_id, eleccion, tab_mesa, tipo_hoja_stae,
                cantidad_candidatos, file_id1, cod_usuario, centro_computo, copia_a_color, 
                observado_escrutinio, is_convencional, flag_procesamiento_manual
            )
            
            tipo_acta_instalacion = determinar_acta_instalacion_sufragio(is_convencional)
            process_file(
                acta_id, eleccion['n_eleccion_pk'], eleccion['c_codigo'],
                tipo_acta_instalacion, file_id2, copia_a_color, observado_instalacion_sufragio, is_convencional,
                flag_procesamiento_manual, centro_computo=centro_computo, cod_usuario=cod_usuario
            )

            if integrity_state.HAY_FALLO_INTEGRIDAD_MODELOS:
                ok = update_det_parametro_by_nombre(cod_usuario, c_nombre="p_modelo_prediccion",nuevo_valor="false",log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                if ok:
                    logger.warning("Parámetro p_modelo_prediccion DESACTIVADO por fallo de integridad de modelos",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                else:
                    logger.exception("No se pudo desactivar p_modelo_prediccion tras fallo de integridad",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            
            _registrar_accion_final(acta_id, cod_usuario, centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info(f"Procesamiento Finalizado: acta_id: {acta_id}, file_id1: {file_id1}, file_id2: {file_id2},", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        except Exception as e:
            full_error_msg = traceback.format_exc()
            logger.exception(f"Error procesando el acta (acta_id={acta_id}): {e}\n{full_error_msg}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            MAX_LEN = 950
            if len(full_error_msg) <= MAX_LEN:
                error_msg = full_error_msg
            else:
                half = (MAX_LEN - len("\n...\n")) // 2
                error_msg = (
                    full_error_msg[:half]
                    + "\n...\n"
                    + full_error_msg[-half:]
                )

            ctx.failed = True
            try:
                rows_deleted = delete_data_from_table(
                    "det_acta_rectangulo", "n_acta", acta_id,
                    log_queue = constantes.QUEUE_LOGGER_VALUE_PROCESS
                )
                logger.info(f"Registros de rectángulos eliminados: {rows_deleted}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                
                update_cab_acta(
                    acta_id,
                    {
                        'n_digitalizacion_escrutinio': 3,
                        'c_observacion_digitalizacion_escrutinio': error_msg,
                        'c_aud_usuario_modificacion':cod_usuario,
                        'd_aud_fecha_modificacion': datetime.now()
                    },
                    constantes.QUEUE_LOGGER_VALUE_PROCESS
                )
                logger.info(f"Acta {acta_id} actualizada con estado de error del modelo.",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                try:
                    _insertar_fallback_rectangulos(acta_id, ctx)
                except Exception as fb_error:
                    logger.exception(f"Error al insertar registros fallback en det_acta_rectangulo: {fb_error}\n{traceback.format_exc()}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                _registrar_accion_final(acta_id, cod_usuario, centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                logger.info(f"Procesamiento Finalizado con error: acta_id: {acta_id}, file_id1: {file_id1}, file_id2: {file_id2},", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            except Exception as nested_error:
                logger.exception(f"Error al limpiar datos o actualizar acta tras fallo: {nested_error}\n{traceback.format_exc()}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                raise ProcessActaFallbackError(f"Fallback process_acta fallido para acta_id={acta_id}") from nested_error
            raise

def _log_acta_validation_errors(acta, file_id1, file_id2):
    if acta is None:
        logger.info("Acta no encontrada.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        return
    if acta['n_archivo_escrutinio'] != file_id1:
        logger.info(f"Error: n_archivo_escrutinio esperado: {acta['n_archivo_escrutinio']}, recibido: {file_id1}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    if acta['n_archivo_instalacion_sufragio'] != file_id2:
        logger.info(f"Error: n_archivo_instalacion_sufragio esperado: {acta['n_archivo_instalacion_sufragio']}, recibido: {file_id2}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)


def _procesar_escrutinio(acta_id, eleccion, tab_mesa, tipo_hoja_stae,
                          cantidad_candidatos, file_id1, cod_usuario, 
                          centro_computo, copia_a_color, observado_escrutinio, 
                          is_convencional, flag_procesamiento_manual):

    codigo = eleccion['c_codigo']
    pk = eleccion['n_eleccion_pk']

    def pf(tipo_acta):
        process_file(
            acta_id, pk, codigo, tipo_acta, file_id1, copia_a_color, observado_escrutinio, is_convencional,
            flag_procesamiento_manual, centro_computo=centro_computo, cod_usuario=cod_usuario
        )

    is_stae = tab_mesa['n_solucion_tecnologica'] == constantes.SOLUCION_TECNOLOGICA_STAE

    tipo_acta = determinar_tipo_acta(codigo, is_stae, tipo_hoja_stae, cantidad_candidatos)
    tipo_acta = ajustar_acta_por_convencional(tipo_acta, is_convencional)
    if tipo_acta:
        pf(tipo_acta)

def _registrar_accion_final(acta_id, cod_usuario, centro_computo, log_queue):
    nueva_iteracion = 1

    inicio_results = select_det_acta_accion({
        'c_accion': 'MODELO_PROCESAR',
        'n_acta': acta_id,
        'c_tiempo': 'INICIO'
    }, log_queue=log_queue)

    if inicio_results:
        iteraciones_inicio = [
            r['n_iteracion'] for r in inicio_results
            if isinstance(r.get('n_iteracion'), int)
        ]

        if iteraciones_inicio:
            max_inicio = max(iteraciones_inicio)

            if max_inicio > 1:
                nueva_iteracion = max_inicio

    insert_det_acta_accion({
        'id_cab_acta': acta_id,
        'c_accion': 'MODELO_PROCESAR',
        'c_tiempo': 'FIN',
        'n_orden': 6,
        'n_iteracion': nueva_iteracion,
        'c_usuario_accion': cod_usuario,
        'd_fecha_accion': datetime.now(),
        'n_activo': 1,
        'c_codigo_centro_computo':centro_computo ,
        'c_aud_usuario_creacion': cod_usuario,
        'd_aud_fecha_creacion': datetime.now()
    }, log_queue=log_queue)


def process_lista_electores(mesa_id: int, abrev_documento: str, cod_usuario:str, cod_centrocomputo:str):

  try:
    logger.info(f"Procesando lista de electores de la mesa: {mesa_id}, abrev_documento: {abrev_documento}, cod_usuario: {cod_usuario} , cod_centrocomputo: {cod_centrocomputo}.", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    rows_deleted = delete_data_from_table(
        "det_le_rectangulo", "n_mesa", mesa_id,
        log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
    )
    logger.info(f"Registros de rectángulos LE eliminados por seguridad: {rows_deleted}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    mesa = get_data_tab_mesa(mesa_id, constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)

    if mesa is None:
        logger.info(f"La mesa: {mesa_id}, no esta registrada en la BD.", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        return

    estado_digitalizacion_le = mesa['c_estado_digitalizacion_le']

    if estado_digitalizacion_le is None:
        logger.info(f"La columna c_estado_digitalizacion_le de la mesa : {mesa_id}, no esta registrada en la BD.", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        return

    if estado_digitalizacion_le != 'C':
        logger.info(f"La mesa : {mesa_id}, debe estar con estado_digitalizacion_le = 'C', de aprobada en LE.", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)

    process_files_lista_electores(mesa_id, abrev_documento, cod_usuario, cod_centrocomputo)
  except Exception as e:
        logger.exception(
            f"Error en process_lista_electores (mesa_id={mesa_id}): {e}\n{traceback.format_exc()}",
            queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
        )
        try:
            cantidad_paginas = get_cantidad_paginas(mesa_id)
            paginas_fallback = [crear_pagina_fallback(p) for p in range(1, cantidad_paginas + 1)]
            submit_json(paginas_fallback, 'det_le_rectangulo', 'c_paginas', mesa_id, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            update_table_column(
                table_name="det_le_rectangulo",
                where_column="n_mesa",
                where_value=mesa_id,
                update_column="c_tipo",
                update_value="LE",
                log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
            )
            logger.info(f"Registro de contramedida LE generado para mesa_id={mesa_id} con {cantidad_paginas} paginas en -1.", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        except Exception as e_fallback:
            logger.error(f"No se pudo generar registro de contramedida para mesa_id={mesa_id}: {e_fallback}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            raise LEFallbackError(f"Fallback LE fallido para mesa_id={mesa_id}") from e_fallback
        raise

def get_mm_contexto(solucion_tecnologica, extranjera, tipo_transmision):
    if (
        solucion_tecnologica == constantes.SOLUCION_TECNOLOGICA_STAE
        and tipo_transmision == constantes.TIPO_HOJA_CONTINGENCIA_A3
    ):
        return {
            "abrev_mm": constantes.ABREV_HOJA_ASISTENCIA_MM,
            "abrev_mmc": constantes.ABREV_RELACION_MM_NO_SORTEADOS,
            "is_convencional": constantes.FLUJO_CONVENCIONAL
        }

    if solucion_tecnologica == constantes.SOLUCION_TECNOLOGICA_STAE:
        return {
            "abrev_mm": constantes.ABREV_HOJA_ASISTENCIA_MM_STAE,
            "abrev_mmc": constantes.ABREV_RELACION_MM_NO_SORTEADOS_STAE,
            "is_convencional": constantes.FLUJO_STAE
        }

    if extranjera:
        return {
            "abrev_mm": constantes.ABREV_HOJA_ASISTENCIA_MM_EXTRANJERO,
            "abrev_mmc": constantes.ABREV_RELACION_MM_NO_SORTEADOS_EXTRANJERO,
            "is_convencional": constantes.FLUJO_EXTRANJERO
        }

    return {
        "abrev_mm": constantes.ABREV_HOJA_ASISTENCIA_MM,
        "abrev_mmc": constantes.ABREV_RELACION_MM_NO_SORTEADOS,
        "is_convencional": constantes.FLUJO_CONVENCIONAL
    }


def process_miembros_mesa(mesa_id: int, abrev_documento: str, cod_usuario:str, cod_centrocomputo:str):
  try:
    logger.info(f"Procesando miembros de mesa, de la mesa: {mesa_id}, abrev_documento: {abrev_documento}, cod_usuario: {cod_usuario}, cod_centrocomputo: {cod_centrocomputo}.", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    rows_deleted = delete_data_from_table(
        "det_mm_rectangulo", "n_mesa", mesa_id,
        log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA
    )
    logger.info(f"Registros de rectángulos MM eliminados por seguridad: {rows_deleted}", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    c_ubigeo = get_c_ubigeo_by_mesa_id(mesa_id=mesa_id, log_queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    mesa = get_data_tab_mesa(mesa_id, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    extranjera = c_ubigeo[0] == "9"
    solucion_tecnologica = mesa['n_solucion_tecnologica']
    tipo_transmision = get_n_tipo_transmision_by_mesa(mesa_id, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    contexto_mm = get_mm_contexto(solucion_tecnologica=solucion_tecnologica,extranjera=extranjera, tipo_transmision = tipo_transmision)

    # Validamos el config de MM y MMC
    logger.info(constantes.LIST_MM, queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    config_mm_list = get_seccion_abreviatura(constantes.LIST_MM )
    config_mmc_list = get_seccion_abreviatura(constantes.LIST_MMC)
    config_mm_obs = get_seccion_abreviatura(constantes.OBS_MM)
    config_mmc_obs = get_seccion_abreviatura(constantes.OBS_MMC)

    # Validar si alguna sección no está registrada
    if any(config is None for config, name in [
        (config_mm_list, constantes.LIST_MM),
        (config_mmc_list, constantes.LIST_MMC),
        (config_mm_obs, constantes.OBS_MM),
        (config_mmc_obs, constantes.OBS_MMC)
    ]):
        missing_sections = [
            name for config, name in [
                (config_mm_list, constantes.LIST_MM),
                (config_mmc_list, constantes.LIST_MMC),
                (config_mm_obs, constantes.OBS_MM),
                (config_mmc_obs, constantes.OBS_MMC)
            ] if config is None
        ]
        logger.info(f"SECCIONES CONSTANTES NO ESTÁN REGISTRADAS EN LA BD: {', '.join(missing_sections)}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
    if mesa is None:
        logger.info(f"La mesa: {mesa_id}, no esta registrada en la BD.", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        return

    estado_digitalizacion_mm = mesa['c_estado_digitalizacion_mm']

    if estado_digitalizacion_mm is None:
        logger.info(f"La columna c_estado_digitalizacion_mm de la mesa : {mesa_id}, no esta registrada en la BD.", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        return

    if estado_digitalizacion_mm != 'C':
        logger.info(f"La mesa : {mesa_id}, debe estar con estadoDigitalizacionMm = 'C', de aprobada en MM.", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

    process_files_miembros_mesa(mesa_id, abrev_documento, contexto_mm, cod_usuario, cod_centrocomputo)

  except Exception as e:
        logger.exception(
            f"Error en process_miembros_mesa (mesa_id={mesa_id}): {e}\n{traceback.format_exc()}",
            queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA
        )
        try:
            paginas_fallback = [
                {
                    "pagina": 1,
                    "archivo_pagina": -1,
                    "archivo_observacion": -1,
                    "secciones": [{"cargo": i, "firma": False, "archivo_seccion": -1} for i in range(1, 10)]
                },
                {
                    "pagina": 2,
                    "archivo_pagina": -1,
                    "archivo_observacion": -1,
                    "secciones": [{"cargo": i, "firma": False, "archivo_seccion": -1} for i in range(1, 4)]
                }
            ]
            submit_json(paginas_fallback, 'det_mm_rectangulo', 'c_paginas', mesa_id, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
            update_table_column(
                table_name="det_mm_rectangulo",
                where_column="n_mesa",
                where_value=mesa_id,
                update_column="c_tipo",
                update_value=abrev_documento,
                log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA
            )
            logger.info(f"Registro de contramedida MM generado para mesa_id={mesa_id}.", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        except Exception as e_fallback:
            logger.error(f"No se pudo generar registro de contramedida MM para mesa_id={mesa_id}: {e_fallback}", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
            raise MMFallbackError(f"Fallback MM fallido para mesa_id={mesa_id}") from e_fallback
        raise


CORTES_SEGUNDA_HOJA = {
    constantes.SECCION_OBSERVACION_ESCRUTINIO
    }

def _prepare_rectangles_stae_vd(lista_filepaths, codigo_eleccion, tipo_acta):
    """
    Regla:
    - Cortes normales → SIEMPRE en la primera hoja
    - Cortes en CORTES_SEGUNDA_HOJA → SOLO en la última hoja (si aplica)
    - Nunca repetir cortes
    - Ignorar hojas intermedias
    """

    n_seccion_obs = get_seccion_abreviatura(constantes.SECCION_OBSERVACION_ESCRUTINIO, log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)

    if tipo_acta == constantes.ABREV_ACTA_ESCRUTINIO_STAE:
        OBS_FALLBACK = (
        n_seccion_obs,
        constantes.SECCION_OBSERVACION_ESCRUTINIO,
        '0.047133153177722965',
        '0.8616168836685548',
        '0.7298303707507411',
        '0.8014234922924739'
    )
    else:
        OBS_FALLBACK = (
            n_seccion_obs,
            constantes.SECCION_OBSERVACION_ESCRUTINIO,
            '0.2973916057468248',
            '0.9124760663290508',
            '0.666764804543154',
            '0.7596545373330895'
        )

    is_instalacion_or_sufragio = tipo_acta in [
            constantes.ABREV_ACTA_INSTALACION_STAE,
            constantes.ABREV_ACTA_SUFRAGIO_STAE
        ]
    
    if is_instalacion_or_sufragio:
        eleccion_codigo = get_codigo_eleccion_principal(log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)

        if eleccion_codigo is None:
            raise ValueError("No se encontró elección principal (n_principal = 1)")
    else:
        eleccion_codigo = codigo_eleccion

    assert isinstance(lista_filepaths, list)
    assert len(lista_filepaths) >= 1

    resultado = {
        "principal": None,
        "segunda_hoja": None  # nombre legado
    }

    datos = get_coordenadas_por_eleccion_documento_electoral(eleccion_codigo, tipo_acta, log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)
    is_convencional = constantes.FLUJO_STAE
    usa_hoja_final = (
        len(lista_filepaths) >= 2 and
        codigo_eleccion in (
            constantes.COD_ELEC_PRESIDENTE,
            constantes.COD_ELEC_SENADO_MULTIPLE,
            constantes.COD_ELEC_SENADO_UNICO,
            constantes.COD_ELEC_DIPUTADO,
            constantes.COD_ELEC_PARLAMENTO,
        )
    )
    file_path_principal = lista_filepaths[0]

    datos_principal = {
        key: datos[key]
        for key in datos
        if (
            datos[key][1] not in CORTES_SEGUNDA_HOJA
            and datos[key][1] != constantes.SECCION_OBSERVACION_ESCRUTINIO
        )
    }

    config_map_principal = {
        datos_principal[key][0]: datos_principal[key][1]
        for key in datos_principal
    }

    rectangles_principal = [
        (
            datos_principal[key][0],
            (float(datos_principal[key][2]), float(datos_principal[key][4])),
            (float(datos_principal[key][3]), float(datos_principal[key][5]))
        )
        for key in datos_principal
    ]

    rectangles_principal, _ = extract_rectangles(
        file_path_principal,
        rectangles_principal,
        acta_observada=False,
        square_coords=None,
        is_convencional=is_convencional,
        acta_type=tipo_acta,
        codigo_eleccion=codigo_eleccion,
        obs_corregida=False,
        process_type=False,
        log_queue=constantes.QUEUE_LOGGER_VALUE_STAE
    )

    resultado["principal"] = {
        "file_path": file_path_principal,
        "config_map": config_map_principal,
        "rectangles": rectangles_principal
    }
    if usa_hoja_final:
        file_path_final = lista_filepaths[-1]

        datos_final = {
            OBS_FALLBACK[0]: OBS_FALLBACK
        }

        if datos_final:
            config_map_final = {
                datos_final[key][0]: datos_final[key][1]
                for key in datos_final
            }

            rectangles_final = [
                (
                    datos_final[key][0],
                    (float(datos_final[key][2]), float(datos_final[key][4])),
                    (float(datos_final[key][3]), float(datos_final[key][5]))
                )
                for key in datos_final
            ]

            rectangles_final, _ = extract_rectangles(
                file_path_final,
                rectangles_final,
                acta_observada=False,
                square_coords=None,
                is_convencional=is_convencional,
                acta_type=tipo_acta,
                codigo_eleccion=codigo_eleccion,
                obs_corregida=False,
                process_type=False,
                log_queue=constantes.QUEUE_LOGGER_VALUE_STAE
            )
            resultado["segunda_hoja"] = {
                "file_path": file_path_final,
                "config_map": config_map_final,
                "rectangles": rectangles_final
            }
    return resultado



def cortar_imagenes_control_calidad(resultados, acta_id, cod_usuario, centro_computo):
    """
    Guarda y sube imágenes ya recortadas STAE/VD.
    - total_votos: upload_file_to_dir_process_acta
    - preferenciales: _procesar_columnas_preferenciales_stae_vd
    """

    struct_archivos = {
        "total_votos": [],
        "preferenciales": []
    }

    ctx = get_context()
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S%f')
    for item in resultados:
        if item["tipo"] != "total_votos":
            continue

        imagen = item["imagen"]
        ajuste = item["ajuste"]

        nombre_seccion = "TOTAL_VOTOS"
        filename = f"{nombre_seccion}_{ajuste}_{acta_id}_{timestamp}.png"
        full_path = os.path.join(BASE_DIR, filename)

        cv2.imwrite(full_path, imagen)

        archivo_id = upload_file_to_dir_process_acta(
            full_path,
            cod_usuario,
            centro_computo
        )

        struct_archivos["total_votos"].append(archivo_id)

        if ctx:
            ctx.add_temp_file(full_path)
    for item in resultados:
        if item["tipo"] != "preferenciales":
            continue

        imagen = item["imagen"]

        rec_loader = ImageFromMemory(imagen, rotate=False)
        processor = VotesPrefImageProcessor(rec_loader)

        num_columns = get_cantidad_columnas_preferenciales(acta_id, constantes.QUEUE_LOGGER_VALUE_STAE)

        archivos_columnas = processor._procesar_columnas_preferenciales_stae_vd(
            acta_pk=acta_id,
            num_columns=num_columns,
            cod_usuario=cod_usuario,
            centro_computo=centro_computo,
            copia_a_color=False
        )

        struct_archivos["preferenciales"].extend(archivos_columnas)

    return struct_archivos

def orden_grupo(key):
    tipo, ajuste = key

    if tipo == "total_votos" and ajuste == "shift_left":
        return 0
    if tipo == "total_votos" and ajuste == "original":
        return 1
    if tipo == "preferenciales" and ajuste == "shift_left":
        return 2
    return 3


def crear_imagenes_recortes_stae_vd(img_limpia_paths, coords_finales):
    """
    Genera imágenes numpy de recortes STAE.
    Soporta N hojas.
    """
    _validar_paths(img_limpia_paths)
    imgs_base = _cargar_imagenes(img_limpia_paths)
    grupos = _agrupar_cortes(coords_finales)

    resultados = []
    for (tipo, ajuste) in _ordenar_grupos(grupos):
        imagen_final = _procesar_grupo(grupos[(tipo, ajuste)], imgs_base)
        if imagen_final is not None:
            resultados.append({
                "tipo": tipo,
                "ajuste": ajuste,
                "imagen": imagen_final
            })

    return resultados

def _validar_paths(paths):
    if not isinstance(paths, list):
        raise TypeError("img_limpia_paths debe ser una lista")
    if not paths:
        raise ValueError("img_limpia_paths no puede estar vacía")
    for path in paths:
        if not os.path.exists(path):
            raise FileNotFoundError(f"No existe la imagen: {path}")


def _cargar_imagenes(paths):
    imgs = {}
    for idx, path in enumerate(paths):
        img = cv2.imread(path)
        if img is None:
            raise RuntimeError(f"No se pudo leer la imagen: {path}")
        imgs[idx] = img
    return imgs


def _agrupar_cortes(coords_finales):
    grupos = {}
    for corte in coords_finales:
        tipo = "total_votos" if corte["seccion"].startswith("totales") else "preferenciales"
        key = (tipo, corte["ajuste"])
        grupos.setdefault(key, []).append(corte)
    return grupos

def _ordenar_grupos(grupos):
    return sorted(grupos.keys(), key=orden_grupo)

def _procesar_grupo(cortes, imgs_base):
    cortes = sorted(cortes, key=lambda c: c["seccion"])
    imagenes_por_hoja = {}

    for corte in cortes:
        recorte = _extraer_recorte(corte, imgs_base)
        if recorte is not None:
            imagenes_por_hoja.setdefault(corte["hoja_idx"], []).append(recorte)

    imagenes = _combinar_por_hoja(imagenes_por_hoja)
    return _apilar_imagenes(imagenes)

def _extraer_recorte(corte, imgs_base):
    img_base = imgs_base[corte["hoja_idx"]]
    (x1, y1), _, _, (x2, y2) = corte["coords"]
    recorte = img_base[y1:y2, x1:x2]
    return recorte if recorte.size else None

def _combinar_por_hoja(imagenes_por_hoja):
    imagenes = []
    hojas_ordenadas = sorted(imagenes_por_hoja.keys())
    total_hojas = len(hojas_ordenadas)
    for pos, hoja_idx in enumerate(hojas_ordenadas):
        recs = imagenes_por_hoja[hoja_idx]
        img = recs[0] if len(recs) == 1 else np.vstack(recs)
        img = ajustar_margenes_verticales(
            img,
            pos=pos,
            total=total_hojas,
            margen=8
        )
        imagenes.append(img)
    return imagenes


def _apilar_imagenes(imagenes):
    if not imagenes:
        return None

    max_width = max(img.shape[1] for img in imagenes)
    imagenes = [
        cv2.copyMakeBorder(
            img, 0, 0, 0, max_width - img.shape[1],
            cv2.BORDER_CONSTANT, value=(255, 255, 255)
        ) if img.shape[1] < max_width else img
        for img in imagenes
    ]
    return imagenes[0] if len(imagenes) == 1 else np.vstack(imagenes)

def ajustar_margenes_verticales(recorte, pos, total, margen=8):
    h = recorte.shape[0]
    top = 0
    bottom = h
    if total == 1:
        return recorte
    if pos == 0:
        bottom -= margen
    elif pos == total - 1:
        top += margen
    else:
        top += margen
        bottom -= margen
    if top >= bottom:
        return recorte
    return recorte[top:bottom, :]

def construir_json_votos_stae_vd(file_ids):
    """
    file_ids: List[int]
    """
    body_item = {"nro": 1}

    for idx, file_id in enumerate(file_ids, start=1):
        body_item[f"votos_{idx}"] = {
            "file": file_id
        }

    return {
        "body": [body_item],
        "headers": [""]
    }


def procesar_secciones_control_calidad(
    tipo_acta,
    codigo_eleccion,
    lista_filepaths,
    acta_id,
    cod_usuario,
    centro_computo
):
    num_candidatos = 0
    is_convencional = constantes.FLUJO_STAE

    if not isinstance(lista_filepaths, list):
        raise TypeError("lista_filepaths debe ser una lista")

    if len(lista_filepaths) == 0:
        raise ValueError("lista_filepaths no puede estar vacía")

    output_paths, cortes = procesar_marcadores_stae_vd(
        codigo_eleccion,
        tipo_acta,
        lista_filepaths,
        num_candidatos,
        is_convencional
    )

    resultados = crear_imagenes_recortes_stae_vd(
        output_paths,
        cortes
    )

    struct_archivos = cortar_imagenes_control_calidad(
        resultados,
        acta_id,
        cod_usuario,
        centro_computo
    )

    if struct_archivos.get("total_votos"):
        json_totales = construir_json_votos_stae_vd(
            struct_archivos["total_votos"]
        )
        insert_json_votos_by_abreviatura(
            acta_id,
            codigo_eleccion,
            tipo_acta,
            constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS,
            json_totales,
            log_queue=constantes.QUEUE_LOGGER_VALUE_STAE
        )
    if struct_archivos.get("preferenciales"):
        json_pref = construir_json_votos_stae_vd(
            struct_archivos["preferenciales"]
        )
        insert_json_votos_by_abreviatura(
            acta_id,
            codigo_eleccion,
            tipo_acta,
            constantes.ABREV_CONTROL_CALIDAD_TOTAL_VOTOS_PREFERENCIALES,
            json_pref,
            log_queue=constantes.QUEUE_LOGGER_VALUE_STAE
        )

    for path in output_paths:
        if os.path.exists(path):
            os.remove(path)



def _process_rectangles_stae_vd(rectangle, rec_id, acta_id, codigo_eleccion, tipo_acta, 
                                config_map, need_rotate, centro_computo, cod_usuario):
    section_name = config_map[rec_id].lower().replace(" ", "_")
    final_name, temp_name = generate_file_names(section_name)
    temp_path = os.path.join(os.path.dirname(final_name), temp_name)
    save_and_prepare_image(rectangle, final_name, temp_path)

    name = final_name

    archive_doc_id = _save_rotated_image(tipo_acta, codigo_eleccion, final_name, section_name, centro_computo, cod_usuario, is_convencional=constantes.FLUJO_STAE)

    params_stae_vd = SectionStaeVdParams(
        rectangle=rectangle,
        name=name,
        rec_id=rec_id,
        codigo_eleccion=codigo_eleccion,
        need_rotate=need_rotate,
        acta_type=tipo_acta,
        acta_id=acta_id,
        eleccion_id=codigo_eleccion,
        centro_computo=centro_computo,
        cod_usuario=cod_usuario
    )
    data = process_section_stae_vd(params_stae_vd)
    insert_det_acta_rectangulo(acta_id, codigo_eleccion, tipo_acta, archive_doc_id, data, constantes.QUEUE_LOGGER_VALUE_STAE)

    os.remove(final_name)

def process_section_stae_vd(params_stae_vd: SectionStaeVdParams) -> dict:
    data = {"n_seccion": params_stae_vd.rec_id}
    return data

def _procesar_escrutinio_stae_vd(acta_id, lista_filepaths, codigo_eleccion, tipo_acta, cod_usuario, centro_computo):
    
    resultados_rectangulos = _prepare_rectangles_stae_vd(lista_filepaths, codigo_eleccion, tipo_acta)
    need_rotate = False
    for bloque in ("principal", "segunda_hoja"):
        info = resultados_rectangulos.get(bloque)
        if not info:
            continue

        config_map = info["config_map"]
        rectangles = info["rectangles"]

        for rec_id, rectangle in rectangles:
            _process_rectangles_stae_vd(
                rectangle,
                rec_id,
                acta_id,
                codigo_eleccion,
                tipo_acta,
                config_map,
                need_rotate,
                centro_computo,
                cod_usuario
            )

    procesar_secciones_control_calidad(tipo_acta, codigo_eleccion, lista_filepaths, acta_id, cod_usuario, centro_computo)
    for file_path in lista_filepaths:
        if os.path.exists(file_path):
            os.remove(file_path)

def _procesar_instalacion_sufragio_stae_vd(
    acta_id,
    file_inst,
    file_sufr,
    codigo_eleccion,
    cod_usuario,
    centro_computo
):
    _procesar_archivo(
        file_inst,
        constantes.ABREV_ACTA_INSTALACION_STAE,
        acta_id,
        codigo_eleccion,
        cod_usuario,
        centro_computo
    )

    _procesar_archivo(
        file_sufr,
        constantes.ABREV_ACTA_SUFRAGIO_STAE,
        acta_id,
        codigo_eleccion,
        cod_usuario,
        centro_computo
    )


def _procesar_archivo(
    file_path,
    tipo_acta,
    acta_id,
    codigo_eleccion,
    cod_usuario,
    centro_computo
):
    if not file_path:
        return

    resultados = _prepare_rectangles_stae_vd(
        [file_path],
        codigo_eleccion,
        tipo_acta
    )

    for bloque in ("principal", "segunda_hoja"):
        info = resultados.get(bloque)
        if not info:
            continue

        for rec_id, rectangle in info["rectangles"]:
            _process_rectangles_stae_vd(
                rectangle,
                rec_id,
                acta_id,
                codigo_eleccion,
                tipo_acta,
                info["config_map"],
                False,
                centro_computo,
                cod_usuario
            )

    if os.path.exists(file_path):
        os.remove(file_path)


def pdf_to_aligned_images_stae_vd(
    ftp_uuid: str,
    *,
    dpi: int = 300,
    max_pages: int | None = None,
    is_convencional: str = constantes.FLUJO_STAE,
    log_queue=constantes.QUEUE_LOGGER_VALUE_STAE
):
    """
    Convierte un PDF a imágenes TIFF alineadas.
    
    Retorna:
    - lista de paths alineados (1 o más)
    """

    pdf_path = download_file_from_dir(
        ftp_uuid,
        log_queue=log_queue
    )

    images = convert_from_path(pdf_path=str(pdf_path),dpi=dpi,fmt="jpeg",thread_count=4)

    if max_pages:
        images = images[:max_pages]

    tiff_files = []

    for i, image in enumerate(images, start=1):
        tiff_name = f"{Path(ftp_uuid).stem}_page_{i}.tiff"

        image.save(
            tiff_name,
            format="TIFF",
            compression="tiff_lzw"
        )

        tiff_files.append(tiff_name)

    aligned_paths = []

    for tiff_path in tiff_files:
        aligned_path, _, _, _ = download_file_from_dir_and_align(
            tiff_path,
            acta_observada=False,
            is_convencional=is_convencional,
            codigo_eleccion = None, 
            acta_type = None,
            use_working_dir=True,
            log_queue=log_queue
        )

        aligned_paths.append(aligned_path)

    if os.path.exists(pdf_path):
        os.remove(pdf_path)
    return aligned_paths

def _determinar_tipo_acta_stae(codigo_eleccion, num_columns):

    if codigo_eleccion in [
        constantes.COD_ELEC_PRESIDENTE,
        constantes.COD_ELEC_SENADO_MULTIPLE
    ]:
        return constantes.ABREV_ACTA_ESCRUTINIO_STAE

    if codigo_eleccion == constantes.COD_ELEC_DIPUTADO and num_columns < 16:
        return constantes.ABREV_ACTA_ESCRUTINIO_STAE

    return constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL

def process_acta_stae_vd(acta_id: int,cod_usuario: str,centro_computo: str):
    logger.info(f"Procesando Acta STAE con acta_id {acta_id}", queue = constantes.QUEUE_LOGGER_VALUE_STAE)
    if acta_id is None:
        logger.info("Proceso STAE VD cancelado: acta_id es NULL",queue=constantes.QUEUE_LOGGER_VALUE_STAE)
        return
    rows_deleted = delete_data_from_table(
                    "det_acta_rectangulo", "n_acta", acta_id,
                    log_queue = constantes.QUEUE_LOGGER_VALUE_STAE
                )
    logger.info(f"Registros de rectángulos eliminados: {rows_deleted}",queue=constantes.QUEUE_LOGGER_VALUE_STAE)
    with use_execution_context():

        acta = get_data_cab_acta(acta_id, constantes.QUEUE_LOGGER_VALUE_STAE)

        if not acta:
            logger.info(f"Proceso STAE cancelado: no se encontro cabecera de acta para acta_id {acta_id}",queue=constantes.QUEUE_LOGGER_VALUE_STAE)
            return

        fileid_1 = acta['n_archivo_escrutinio_pdf_firmado']
        fileid_2 = acta['n_archivo_instalacion_pdf_firmado']
        fileid_3 = acta['n_archivo_sufragio_pdf_firmado']

        if not fileid_1 or not fileid_2 or not fileid_3:
            logger.info(f"Proceso STAE cancelado para acta_id {acta_id}: PDFs faltantes (escrutinio={fileid_1}, instalacion={fileid_2}, sufragio={fileid_3})",
                        queue=constantes.QUEUE_LOGGER_VALUE_STAE)
            return

        ftp_uuid_1 = get_nombre_by_archivo_pk(fileid_1, constantes.QUEUE_LOGGER_VALUE_STAE)
        ftp_uuid_2 = get_nombre_by_archivo_pk(fileid_2, constantes.QUEUE_LOGGER_VALUE_STAE)
        ftp_uuid_3 = get_nombre_by_archivo_pk(fileid_3, constantes.QUEUE_LOGGER_VALUE_STAE)

        ubigeo_eleccion = get_data_det_ubigeo_eleccion(acta['n_det_ubigeo_eleccion'], constantes.QUEUE_LOGGER_VALUE_STAE)
        eleccion = get_data_mae_eleccion(ubigeo_eleccion['n_eleccion'], constantes.QUEUE_LOGGER_VALUE_STAE)
        
        codigo_eleccion = eleccion['c_codigo']
        num_columns = get_cantidad_columnas_preferenciales(acta_id, constantes.QUEUE_LOGGER_VALUE_STAE)

        tipo_acta_escrutinio = _determinar_tipo_acta_stae(codigo_eleccion, num_columns)

        lista_imgs_escrutinio = pdf_to_aligned_images_stae_vd(ftp_uuid_1,log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)

        _procesar_escrutinio_stae_vd(acta_id,lista_imgs_escrutinio,codigo_eleccion,tipo_acta_escrutinio,cod_usuario,centro_computo)

        file_inst = pdf_to_aligned_images_stae_vd(ftp_uuid_2,max_pages=1,log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)[0]
        file_sufr = pdf_to_aligned_images_stae_vd(ftp_uuid_3,max_pages=1,log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)[0]

        _procesar_instalacion_sufragio_stae_vd(acta_id, file_inst, file_sufr, codigo_eleccion,cod_usuario,centro_computo)

        _registrar_accion_final(acta_id, cod_usuario, centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)
        logger.info(f"Finalizo Acta STAE con acta_id {acta_id}", queue = constantes.QUEUE_LOGGER_VALUE_STAE)
