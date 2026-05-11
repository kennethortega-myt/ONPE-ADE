import cv2
import os
from db.progress import submit_json, get_data_tab_documento_electoral, select_data, upload_file_to_dir, update_table_column, get_seccion_abreviatura, download_file_from_dir_and_align
from models.imageutils import ImageFromMemory
from models.votesprefprocesor import VotesPrefImageProcessor
from models.votesprocessor import VotesImageProcessor
from models.imageutils import ImageFromDisk
from models import actasmodel
from util import constantes
from logger_config import logger

def get_page_ids(id, abreviatura_id):

  page_entries = []
  mesa_pk = select_data('tab_mesa', {'n_mesa_pk': id}, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)[0]['n_mesa_pk']
  documento_pk = select_data('tab_documento_electoral', {'n_documento_electoral_pk': abreviatura_id}, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)[0]['n_documento_electoral_pk']

  conditions = {
    "n_mesa": mesa_pk , 
    'n_documento_electoral' : documento_pk
  }
  results = select_data('det_mesa_documento_electoral_archivo', conditions, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  for doc in results:
    archive_type= doc['c_tipo_archivo']

    if archive_type == 'image/tiff':
      id_archivo = select_data('tab_archivo', {'n_archivo_pk': doc['n_archivo']}, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)[0]['c_guid']
      pagina = doc['n_pagina']
      page_entries.append([pagina, id_archivo])

  page_entries_sorted = sorted(page_entries, key=lambda x: x[0])
  archivo_guids = [item[1] for item in page_entries_sorted]
  return archivo_guids

def get_id_elecc_doc(id):

  documento_pk = select_data('tab_documento_electoral', {'n_documento_electoral_pk': id}, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)[0]['n_documento_electoral_pk']
  results = select_data('det_tipo_eleccion_documento_electoral', {'n_documento_electoral' : documento_pk}, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  for doc in results:
    id_elecc_doc = doc['n_det_tipo_eleccion_documento_electoral_pk']
    return id_elecc_doc
  
def get_cortes_mm(cortes_id):
  cortes = []

  documento_pk = cortes_id


  conditions = {
  "n_det_tipo_eleccion_documento_electoral": documento_pk , 
  'n_seccion' : get_seccion_abreviatura(constantes.LIST_MM)
  }
  results = select_data('det_configuracion_documento_electoral', conditions, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  logger.info(results, queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
  for doc in results:
    x1 = doc['c_coordenada_relativa_superior_x']
    y1 = doc['c_coordenada_relativa_superior_y']
    x2 = doc['c_coordenada_relativa_inferior_x']
    y2 = doc['c_coordenada_relativa_inferior_y']
    cortes.append([x1,y1,x2,y2])
  
  logger.info(f"cortes = {cortes}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  conditions = {
  "n_det_tipo_eleccion_documento_electoral": documento_pk , 
  'n_seccion' : get_seccion_abreviatura(constantes.OBS_MM)
  }
  results = select_data('det_configuracion_documento_electoral', conditions, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  for doc in results:
    x1 = doc['c_coordenada_relativa_superior_x']
    y1 = doc['c_coordenada_relativa_superior_y']
    x2 = doc['c_coordenada_relativa_inferior_x']
    y2 = doc['c_coordenada_relativa_inferior_y']
    cortes.append([x1,y1,x2,y2])

  logger.info(f"cortes = {cortes}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  return cortes

def get_cortes_mmc(cortes_id):
  cortes = []

  documento_pk = cortes_id

  conditions = {
  "n_det_tipo_eleccion_documento_electoral": documento_pk , 
  'n_seccion' : get_seccion_abreviatura(constantes.LIST_MMC)
  }
  results = select_data('det_configuracion_documento_electoral', conditions, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

  for doc in results:
    x1 = doc['c_coordenada_relativa_superior_x']
    y1 = doc['c_coordenada_relativa_superior_y']
    x2 = doc['c_coordenada_relativa_inferior_x']
    y2 = doc['c_coordenada_relativa_inferior_y']
    cortes.append([x1,y1,x2,y2])

  conditions = {
  "n_det_tipo_eleccion_documento_electoral": documento_pk , 
  'n_seccion' : get_seccion_abreviatura(constantes.OBS_MMC)
  }
  results = select_data('det_configuracion_documento_electoral', conditions, constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA) 

  for doc in results:
    x1 = doc['c_coordenada_relativa_superior_x']
    y1 = doc['c_coordenada_relativa_superior_y']
    x2 = doc['c_coordenada_relativa_inferior_x']
    y2 = doc['c_coordenada_relativa_inferior_y']
    cortes.append([x1,y1,x2,y2])

  return cortes

def aplicar_corte(image, fix, alto, largo):
    fix = [float(x) for x in fix]

    y1, y2 = int(fix[1] * alto), int(fix[3] * alto)
    x1, x2 = int(fix[0] * largo), int(fix[2] * largo)

    if y2 < y1:
        logger.error(f" ERROR: Rango inválido para el corte ({y1}, {y2}, {x1}, {x2})", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        y1, y2 = sorted([int(fix[1] * alto), int(fix[3] * alto)])
        x1, x2 = sorted([int(fix[0] * largo), int(fix[2] * largo)])
        y1, y2 = max(0, y1), min(alto, y2)
        x1, x2 = max(0, x1), min(largo, x2)

    return image[y1:y2, x1:x2, :]

def generate_cortes_1(img_name, cortes, is_convencional, usuario, cod_centro_computo):
    file_name_temp = os.path.abspath(img_name)
    file_name = os.path.basename(file_name_temp)
    file_path, _, _, _ = download_file_from_dir_and_align(file_name, False, is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

    try:
        rectangles_named = [
            ("OBS_MM", (float(cortes[1][0]), float(cortes[1][1])), (float(cortes[1][2]), float(cortes[1][3]))),
            ("LISTA_MM", (float(cortes[0][0]), float(cortes[0][1])), (float(cortes[0][2]), float(cortes[0][3])))
        ]

        image_loader = ImageFromDisk(file_path, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        rectangles, _ = actasmodel.extract_rectangles(image_loader, rectangles_named, acta_observada = False, is_convencional=is_convencional, square_coords_alineados = None, acta_type = None, codigo_eleccion = None,log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        rect_dict = dict(rectangles)

        obs1 = rect_dict["OBS_MM"]
        image = rect_dict["LISTA_MM"]

        if obs1.size == 0 or image.size == 0:
            raise ValueError(f"Corte resultó vacío (coordenadas invertidas): OBS_MM={obs1.shape}, LISTA_MM={image.shape}")


    except Exception as e:
        logger.warning(f"[Fallback] extract_rectangles Fallo: {e}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        image = cv2.imread(file_path, cv2.IMREAD_UNCHANGED)
        alto, largo = image.shape[:2]

        fix = cortes[1]
        obs1 = aplicar_corte(image, fix, alto, largo)

        fix = cortes[0]
        image = aplicar_corte(image, fix, alto, largo)

    try:
        # Guardar OBS y subir
        obs_filename = 'OBSERVACIONES_MM_TEMP.png'
        cv2.imwrite(obs_filename, obs1)
        try:
            obs1 = upload_file_to_dir(file_path=obs_filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        finally:
            if os.path.exists(obs_filename):
                os.remove(obs_filename)

        # Guardar LISTA y subir
        lista_filename = 'LISTA_MM_TEMP.png'
        cv2.imwrite(lista_filename, image)
        try:
            tabla_id = upload_file_to_dir(file_path=lista_filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        finally:
            if os.path.exists(lista_filename):
                os.remove(lista_filename)

        # Dividir en secciones
        secciones_id = []
        secciones_list = []

        rec_loader = ImageFromMemory(image, rotate=False)
        info = VotesPrefImageProcessor(rec_loader).get_data_secciones_mm_1()

        for i, part in enumerate(info):
            filename = f"seccion_mesa_{i+1}.png"
            cv2.imwrite(filename, part)
            try:
                seccion_mesa_id = upload_file_to_dir(file_path=filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
            finally:
                if os.path.exists(filename):
                    os.remove(filename)
            secciones_id.append(seccion_mesa_id)
            secciones_list.append({
                "archivo_seccion": seccion_mesa_id,
                "cargo": i + 1,
                "firma": False
            })

        data = {
            "pagina": 1,
            "archivo_pagina": tabla_id,
            "archivo_observacion": obs1,
            "secciones": secciones_list
        }

        return data
    finally:
        if os.path.exists(file_path):
            os.remove(file_path)

def generate_cortes_2(img_name, cortes, is_convencional, usuario, cod_centro_computo):
    file_name_temp = os.path.abspath(img_name)
    file_name = os.path.basename(file_name_temp)
    file_path, _, _, _ = download_file_from_dir_and_align(file_name, False, is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

    try:
        rectangles_named = [
            ("OBS_MMC", (float(cortes[1][0]), float(cortes[1][1])), (float(cortes[1][2]), float(cortes[1][3]))),
            ("LISTA_MMC", (float(cortes[0][0]), float(cortes[0][1])), (float(cortes[0][2]), float(cortes[0][3])))
        ]

        image_loader = ImageFromDisk(file_path, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        rectangles, _ = actasmodel.extract_rectangles(image_loader, rectangles_named, acta_observada = False, square_coords_alineados = None, is_convencional=is_convencional, acta_type = None, codigo_eleccion = None, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        rect_dict = dict(rectangles)

        obs2 = rect_dict["OBS_MMC"]
        image = rect_dict["LISTA_MMC"]

        if obs2.size == 0 or image.size == 0:
            raise ValueError(f"Corte resultó vacío (coordenadas invertidas): OBS_MMC={obs2.shape}, LISTA_MMC={image.shape}")


    except Exception as e:
        logger.warning(f"[Fallback] extract_rectangles Fallo: {e}", queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        image = cv2.imread(file_path, cv2.IMREAD_UNCHANGED)
        alto, largo = image.shape[:2]

        fix = cortes[1]
        obs2 = aplicar_corte(image, fix, alto, largo)

        fix = cortes[0]
        image = aplicar_corte(image, fix, alto, largo)

    try:
        obs_filename = 'OBSERVACIONES_MMC_TEMP.png'
        cv2.imwrite(obs_filename, obs2)
        try:
            obs2 = upload_file_to_dir(file_path=obs_filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        finally:
            if os.path.exists(obs_filename):
                os.remove(obs_filename)

        lista_filename = 'LISTA_MMC_TEMP.png'
        cv2.imwrite(lista_filename, image)
        try:
            tabla_id = upload_file_to_dir(file_path=lista_filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        finally:
            if os.path.exists(lista_filename):
                os.remove(lista_filename)

        secciones_id = []
        secciones_list = []

        rec_loader = ImageFromMemory(image, rotate=False)
        info = VotesImageProcessor(rec_loader).get_data_secciones_mm_2()

        for i, part in enumerate(info):
            filename = f"seccion_mesa_{i+1}.png"
            cv2.imwrite(filename, part)
            try:
                seccion_mesa_id = upload_file_to_dir(file_path=filename, cod_usuario=usuario, centro_computo=cod_centro_computo, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
            finally:
                if os.path.exists(filename):
                    os.remove(filename)
            secciones_id.append(seccion_mesa_id)
            secciones_list.append({
                "archivo_seccion": seccion_mesa_id,
                "cargo": i + 1,
                "firma": False
            })

        data = {
            "pagina": 2,
            "archivo_pagina": tabla_id,
            "archivo_observacion": obs2,
            "secciones": secciones_list
        }

        return data
    finally:
        if os.path.exists(file_path):
            os.remove(file_path)


def get_secciones(mesa_id, abreviatura, contexto_mm, usuario, cod_centro_computo):

    abrev_mm = contexto_mm['abrev_mm']
    abrev_mmc = contexto_mm['abrev_mmc']
    is_convencional = contexto_mm['is_convencional']
    
    id_mm = get_data_tab_documento_electoral(abrev_mm, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)['n_documento_electoral_pk']
    id_mmc = get_data_tab_documento_electoral(abrev_mmc, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)['n_documento_electoral_pk']
    id_data = get_data_tab_documento_electoral(abreviatura, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)['n_documento_electoral_pk']


    img1, img2 = get_page_ids(mesa_id, id_data)

    id_elecc_doc_mm = get_id_elecc_doc(id_mm)

    id_elecc_doc_mmc = get_id_elecc_doc(id_mmc)

    cortes_mm = get_cortes_mm(id_elecc_doc_mm)
    cortes_mmc = get_cortes_mmc(id_elecc_doc_mmc)


    try:
        secciones_id_1 = generate_cortes_1(img1, cortes_mm, is_convencional, usuario, cod_centro_computo)
    except Exception as e:
        logger.error(f"[get_secciones] Error en generate_cortes_1: {e}", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        raise

    try:
        secciones_id_2 = generate_cortes_2(img2, cortes_mmc, is_convencional, usuario, cod_centro_computo)
    except Exception as e:
        logger.error(f"[get_secciones] Error en generate_cortes_2: {e}", queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)
        raise

    data = {
       "n_mesa" : mesa_id,
       "tipo" : "MM",
       "paginas": [secciones_id_1, secciones_id_2]
    }

    submit_json(data['paginas'], 'det_mm_rectangulo', 'c_paginas', mesa_id, log_queue=constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA)

    update_table_column(
    table_name="det_mm_rectangulo",
    where_column="n_mesa",
    where_value=mesa_id,
    update_column="c_tipo",
    update_value=abreviatura,
    log_queue = constantes.QUEUE_LOGGER_VALUE_MIEMBROS_MESA
    )
