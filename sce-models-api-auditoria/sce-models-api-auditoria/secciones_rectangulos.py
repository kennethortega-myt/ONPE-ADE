import numpy as np
import os
import tempfile
import cv2
import psycopg
from concurrent.futures import ThreadPoolExecutor, as_completed
from db.progress import submit_json, select_data, upload_file_to_dir, update_table_column
from models.imageutils import ImageFromMemory
from models.votesprefprocesor import VotesProcessorBase
from db.progress import get_data_tab_documento_electoral, get_data_det_mesa_documento_electoral_archivo, download_file_from_dir_and_align, get_cantidad_paginas
from logger_config import logger, get_worker_context, get_threads_budget, set_worker_context
from util import constantes
import config

def get_page_ids(id, abreviatura_id):

  page_names = []
  n_paginas = []
  
  results = get_data_det_mesa_documento_electoral_archivo(id, abreviatura_id)

  for doc in results:

    doc_type= doc['c_tipo_archivo']
    

    if doc_type == 'image/tiff':
      id_archivo = select_data('tab_archivo', {'n_archivo_pk': doc['n_archivo']}, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)[0]['c_guid']
      page_names.append(id_archivo)
      n_pagina = doc['n_pagina']
      n_paginas.append(n_pagina)
  return page_names , n_paginas


def apply_kernel(img):
   
  # Crear un kernel de 21x21 con todos los valores en 1
  kernel = np.ones((21, 21), np.float32)

  # Establecer el valor central del kernel en 0
  kernel[10, 10] = 0

  # Aplicar la convolución con el kernel
  filtered_image = cv2.filter2D(img, -1, kernel)

  return filtered_image


def join_images_dynamically(info):
    # Obtenemos el número de filas y columnas de la lista de listas
    num_rows = len(info)
    num_cols = len(info[0])

    # Unir imágenes verticalmente para cada columna
    column_images = [cv2.vconcat([info[row][col] for row in range(num_rows)]) for col in range(num_cols)]

    # Unir todas las columnas horizontalmente
    combined_image = cv2.hconcat(column_images)

    return combined_image

from util.coordenadas_util import (
    detectar_marcadores_xc_yc, 
    construir_matriz,
)
def process_image(img_name, n_pagina, cod_usuario="", cod_centro_computo=""):
    template = _load_template()
    if template is None:
        return None

    file_path1, aligned_image = _load_aligned_image(img_name)
    if aligned_image is None:
        if file_path1 and os.path.exists(file_path1):
            os.remove(file_path1)
        return None

    try:
        porcentaje_x = _detectar_marcadores(aligned_image, template)
        table_img, _, table_location, obs_location, obs_valido = _recortar_y_guardar_imagenes(
            aligned_image, porcentaje_x, cod_usuario, cod_centro_computo)

        sub_images, recortes_location = _generar_subimagenes(
            table_img, cod_usuario, cod_centro_computo)

        secciones = _analizar_secciones(
            sub_images, recortes_location, n_pagina)

        return {
            'pagina': n_pagina,
            'archivo_pagina': table_location,
            'archivo_observacion': obs_location,
            'existe_observacion': obs_valido,
            'secciones': secciones
        }
    finally:
        if file_path1 and os.path.exists(file_path1):
            os.remove(file_path1)

def _load_template():
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    template_path = os.path.join(BASE_DIR, "util", "marcador_36x36.png")
    template = cv2.imread(template_path, 0)
    if template is None:
        logger.error(f" Error: No se pudo cargar la plantilla {template_path}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    return template

def _load_aligned_image(img_name):
    file_path1, _, _, _ = download_file_from_dir_and_align(img_name, False, is_convencional=constantes.FLUJO_CONVENCIONAL, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    aligned_image = cv2.imread(file_path1)
    if aligned_image is None:
        logger.error(f" Error: No se pudo cargar la imagen {file_path1}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    return file_path1, aligned_image


def _detectar_marcadores(aligned_image, template):
    try:
        img_gray = cv2.cvtColor(aligned_image, cv2.COLOR_BGR2GRAY)
        h_img, w_img = img_gray.shape[:2]
        margen_x = int(0.025 * w_img)
        margen_y = int(0.025 * h_img)

        puntos = detectar_marcadores_xc_yc(img_gray, template, margen_x, margen_y)
        if not puntos:
            raise ValueError("No se encontraron marcadores después de la corrección.")

        w, h = template.shape[::-1]
        matriz = construir_matriz(puntos, w, h)

        if (
            not isinstance(matriz, list) or len(matriz) != 2 or
            any(not isinstance(fila, list) or len(fila) != 3 for fila in matriz) or
            matriz[0][1] is None
        ):
            raise ValueError("La matriz de marcadores no tiene la estructura esperada.")

        ref_x, _ = matriz[0][1]
        porcentaje_x = ref_x / w_img
        logger.info(f" Porcentaje de marcador detectado: {porcentaje_x:.3f}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        return porcentaje_x

    except Exception as e:
        logger.warning(f"Error al detectar marcadores: {e}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        return None

def _detectar_tabla_principal(img, porcentaje_x):
    h_img, _ = img.shape[:2]
    top, bottom = int(0.12 * h_img), int(0.8915 * h_img)
    table_img = img[top:bottom, :, :]
    h_table, w_table = table_img.shape[:2]

    rec_loader = ImageFromMemory(table_img, rotate=False)
    try:
        points = VotesProcessorBase(rec_loader).get_data_tabla(log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        x1, y1 = points[0]
        x2, y2 = points[1]
        crudo_y1, crudo_y2 = int(0.025 * h_table), int(0.995 * h_table)
        crudo_x1, crudo_x2 = _coord_crudas_x(porcentaje_x, w_table)

        if not _validar_dimensiones((x1, y1), (x2, y2), crudo_x1, crudo_x2, crudo_y1, crudo_y2):
            raise ValueError("Coordenadas con dimensiones fuera de tolerancia")

        return table_img, (x1, y1, x2, y2), 10
    except Exception as e:
        logger.warning(f"Falló detección automática de tabla: {e}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        y1, y2 = int(0.020 * h_table), int(0.985 * h_table)
        x1, x2 = _coord_crudas_x(porcentaje_x, w_table)
        return table_img, (x1, y1, x2, y2), 30


def _analizar_observacion(obs_file, obs_img, h_img, x1, x2, margen_obs, margen_obs_eleccion_eg):
    obs_top = int(0.883 * h_img)
    obs_bottom = int(0.944 * h_img)
    obs_left, obs_right = x1 - margen_obs, x2 + margen_obs - margen_obs_eleccion_eg
    obs_img = obs_img[obs_top:obs_bottom, obs_left:obs_right]
    cv2.imwrite(obs_file, obs_img)

    ANCHO_BASE, ALTO_BASE = 1900, 200
    TOL = 0.4
    ANCHO_MIN, ANCHO_MAX = ANCHO_BASE * (1 - TOL), ANCHO_BASE * (1 + TOL)
    ALTO_MIN, ALTO_MAX = ALTO_BASE * (1 - TOL), ALTO_BASE * (1 + TOL)
    recorte_obs = True

    try:
        logger.info("Inicia el analisis para la imagen de observacion...", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        rec_loader = ImageFromMemory(obs_img, rotate=False)
        points = VotesProcessorBase(rec_loader).get_data_tabla(modo="obs_lista_electores", log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        x1, y1 = points[0]
        x2, y2 = points[1]
        ancho, alto = x2 - x1, y2 - y1

        if x2 > x1 and y2 > y1 and (ANCHO_MIN <= ancho <= ANCHO_MAX) and (ALTO_MIN <= alto <= ALTO_MAX):
            obs_img = obs_img[y1:y2, x1:x2]
            borde = 7
            h, w = obs_img.shape[:2]
            if h > 2 * borde and w > 2 * borde:
                obs_img = obs_img[borde:h-borde, borde:w-borde]
        else:
            raise ValueError("Dimensiones o coordenadas inválidas")
    except Exception as e:
        logger.warning(f"Rectangulo no detectado: {e}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        recorte_obs = False

    return obs_img, recorte_obs


def _binarizar_observacion(obs_img, recorte_obs = True):
    hsv_obs = cv2.cvtColor(obs_img, cv2.COLOR_BGR2HSV)
    obs_mask = _recorte_binario(hsv_obs, 0.0, 0.0, 1.0, 1.0, [0, 0, 0], [179, 255, 216])
    binary_img = np.where(obs_mask > 0, 255, 0).astype(np.uint8)
    contours, _ = cv2.findContours(binary_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    clean_mask = np.zeros(binary_img.shape, dtype=np.uint8)
    if recorte_obs:
        for contour in contours:
            if cv2.contourArea(contour) >= 5:
                cv2.drawContours(clean_mask, [contour], -1, 255, thickness=cv2.FILLED)

    h, w = clean_mask.shape[:2]
    x1, y1 = 0, 0
    x2, y2 = int(0.15 * w), int(0.26 * h)
    cv2.rectangle(clean_mask, (x1, y1), (x2, y2), 0, thickness=-1)

    fallback_lines_y = [0.262, 0.565, 0.848]

    try:
        logger.info("Intentando deteccion automatica de lineas en imagen de observacion...", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        rec_loader = ImageFromMemory(obs_img, rotate=False)
        l_hor, _ = VotesProcessorBase(rec_loader).get_lines(modo="obs_lista_electores",log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)

        if not l_hor:
            raise ValueError("No horizontal lines detected")

        ys = set()
        for pt0, pt1 in l_hor:
            y = int((pt0[1] + pt1[1]) / 2)
            ys.add(y)

        for y in ys:
            cv2.line(clean_mask, (0, y), (w, y), 0, thickness=8)

    except Exception as e:
        logger.warning(f"Error detectado en obtener las lineas de la observacion e = {e}", queue = constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
        for y_rel in fallback_lines_y:
            y = int(y_rel * h)
            cv2.line(clean_mask, (0, y), (w, y), 0, thickness=8)

    ratio = np.count_nonzero(clean_mask) / clean_mask.size
    return ratio > 0.001


def _recortar_y_guardar_imagenes(img, porcentaje_x, cod_usuario, cod_centro_computo):
    """Recorta tabla y zona de observación, guarda imágenes y evalúa validez."""
    h_img, _ = img.shape[:2]

    table_img, (x1, y1, x2, y2), margen_obs = _detectar_tabla_principal(img, porcentaje_x)
    table_width = x2 - x1
    margen_obs_eleccion_eg = int(table_width * 0.12)

    table_img = table_img[y1:y2, x1:x2]
    with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tf:
        table_tmp = tf.name
    try:
        cv2.imwrite(table_tmp, table_img)
        table_location = upload_file_to_dir(table_tmp, cod_usuario, cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    finally:
        os.remove(table_tmp)

    with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tf:
        obs_tmp = tf.name
    try:
        obs_img, recorte_obs = _analizar_observacion(obs_tmp, img, h_img, x1, x2, margen_obs, margen_obs_eleccion_eg)
        obs_location = upload_file_to_dir(obs_tmp, cod_usuario, cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    finally:
        os.remove(obs_tmp)

    obs_valido = _binarizar_observacion(obs_img, recorte_obs)

    return table_img, obs_img, table_location, obs_location, obs_valido

def _coord_crudas_x(porcentaje_x, w_table):
    if porcentaje_x is not None and porcentaje_x > 0.85:
        return int(0.032 * w_table), int(0.940 * w_table)
    return int(0.060 * w_table), int(0.970 * w_table)

def _validar_dimensiones(p1, p2, crudo_x1, crudo_x2, crudo_y1, crudo_y2):
    ancho, alto = p2[0] - p1[0], p2[1] - p1[1]
    crudo_ancho = crudo_x2 - crudo_x1
    crudo_alto = crudo_y2 - crudo_y1
    if ancho <= 0 or alto <= 0:
        return False
    return abs(ancho - crudo_ancho) <= 0.2 * crudo_ancho and abs(alto - crudo_alto) <= 0.2 * crudo_alto

def _generar_subimagenes(table_img, cod_usuario, cod_centro_computo):
    h, w = table_img.shape[:2]
    rows, cols = 5, 2
    cell_h, cell_w = h // rows, w // cols
    sub_images, recortes_location = [], []

    for col in range(cols):
        for row in range(rows):
            x1, y1 = col * cell_w, row * cell_h
            x2, y2 = (col + 1) * cell_w, (row + 1) * cell_h
            sub_image = table_img[y1:y2, x1:x2]
            sub_images.append(sub_image)
            with tempfile.NamedTemporaryFile(suffix='.png', delete=False) as tf:
                sub_tmp = tf.name
            try:
                cv2.imwrite(sub_tmp, sub_image)
                file = upload_file_to_dir(sub_tmp, cod_usuario, cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            finally:
                os.remove(sub_tmp)
            recortes_location.append(file)

    return sub_images, recortes_location

def _analizar_secciones(sub_images, recortes_location, n_pagina):
    secciones = []
    for idx, sub_image in enumerate(sub_images):
        h, w = sub_image.shape[:2]
        hsv = cv2.cvtColor(sub_image, cv2.COLOR_BGR2HSV)

        huella_region = _recorte_binario(hsv, 0.797, 0.135, 0.971, 0.504, [0, 0, 0], [180, 70, 230])
        firma_region = _recorte_binario(hsv, 0.362, 0.675, 0.952, 0.895, [0, 0, 0], [180, 255, 230]) # Azul [100, 20, 50], [150, 255, 255]
        _ = sub_image[int(0.331*h):int(0.969*h), int(0.037*w):int(0.227*w)]
        no_voto_region = _recorte_binario(hsv, 0.235, 0.755, 0.326, 0.950, [100, 20, 50], [150, 255, 255])

        data = {
            "archivo_seccion": recortes_location[idx],
            "orden": 10*(n_pagina-1) + idx + 1,
            "huella": np.count_nonzero(huella_region) / huella_region.size > 0.005,
            "firma": np.count_nonzero(firma_region) / firma_region.size > 0.015,
            "no_voto": np.count_nonzero(no_voto_region) / no_voto_region.size > 0.05
        }
        secciones.append(data)

    return secciones

def _recorte_binario(hsv_img, x1f, y1f, x2f, y2f, lower, upper):
    h, w = hsv_img.shape[:2]
    x1, y1 = int(x1f * w), int(y1f * h)
    x2, y2 = int(x2f * w), int(y2f * h)
    mask = cv2.inRange(hsv_img, np.array(lower), np.array(upper)) # Mascara azul
    return mask[y1:y2, x1:x2]

def crear_pagina_fallback(n_pagina):
    """
        Construye una pagina de contramedida con 10 secciones y archivo_seccion en -1   
    """
    secciones = []
    for i in range(1, 11):
        secciones.append({
            "firma": False,
            "orden": 10*(n_pagina-1) + i,
            "huella": False,
            "no_voto": True,
            "archivo_seccion": -1
        })

    return {
        "pagina": n_pagina,
        "secciones": secciones,
        "archivo_pagina": -1,
        "existe_observacion": False,
        "archivo_observacion": -1
    }

def _crear_pagina_vacia(n_pagina):
    """
        Construye una pagina vacia con 10 secciones.
    """
    secciones = []
    for i in range(1, 11):
        secciones.append({
            "firma": False,
            "orden": 10*(n_pagina-1) + i,
            "huella": False,
            "no_voto": False,
            "archivo_seccion": None
        })

    return {
        "pagina": n_pagina,
        "secciones": secciones,
        "archivo_pagina": None,
        "existe_observacion": False,
        "archivo_observacion": None
    }

def main_rectangulos(mesa_id, abreviatura, usuario, cod_centro_computo):
    tab_documento_electoral = get_data_tab_documento_electoral(abreviatura, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    abreviatura_id = tab_documento_electoral['n_documento_electoral_pk']
    page_names, n_paginas = get_page_ids(mesa_id, abreviatura_id)
    cantidad_paginas = get_cantidad_paginas(mesa_id)

    paginas_dict = dict(zip(n_paginas, page_names))
    _worker_id = get_worker_context()
    _threads_budget = get_threads_budget()

    def _procesar_pagina(pagina):
        set_worker_context(_worker_id, _threads_budget)
        if pagina not in paginas_dict:
            logger.info(f"Pagina {pagina} no encontrada en n_paginas, creando entrada vacía...", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            return pagina, _crear_pagina_vacia(pagina)
        img_name = paginas_dict[pagina]
        try:
            logger.info(f"Procesando pagina {pagina} de {cantidad_paginas} en mesa {mesa_id}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            return pagina, process_image(img_name, pagina, usuario, cod_centro_computo)
        except psycopg.Error as e:
            logger.error(f" Error de base de datos al procesar pagina {pagina} de la mesa {mesa_id}: {e}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            raise
        except Exception as e:
            logger.error(f" Error al procesar pagina {pagina} de la mesa {mesa_id}: {e}", queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
            raise

    resultados = {}
    workers_page = config.WORKERS_PAGINA_LE
    with ThreadPoolExecutor(max_workers=min(cantidad_paginas, workers_page)) as executor:
        futures = {executor.submit(_procesar_pagina, p): p for p in range(1, cantidad_paginas + 1)}
        for future in as_completed(futures):
            pagina_num, seccion = future.result()
            resultados[pagina_num] = seccion

    secciones = [resultados[p] for p in range(1, cantidad_paginas + 1)]

    json_data = {
        "n_mesa": mesa_id,
        "tipo": abreviatura,
        "paginas":secciones
    }

    submit_json(json_data['paginas'], 'det_le_rectangulo', 'c_paginas', mesa_id, log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES)
    update_table_column(
    table_name="det_le_rectangulo",
    where_column="n_mesa",
    where_value=mesa_id,
    update_column="c_tipo",
    update_value="LE",
    log_queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
    )
    return json_data
