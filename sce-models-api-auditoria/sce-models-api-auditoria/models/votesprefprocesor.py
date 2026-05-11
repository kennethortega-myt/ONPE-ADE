import numpy as np
from models.imageutils import ImagesLoader, cache_result
from models.votesprocessorbase import VotesProcessorBase
import cv2
from db.progress import get_cantidad_agrupaciones_politicas,get_cantidad_columnas_preferenciales, upload_file_to_dir_process_acta
import os
from datetime import datetime

from util import constantes
from logger_config import logger
from models.cortesmodel.acta_base import ActaBase
from util.coordenadas_util import construir_matriz
from db.execution_context import  get_context, resolve_workspace_path

class VotesPrefImageProcessor(VotesProcessorBase):
  def __init__(self, image_loader: ImagesLoader):
    super().__init__(image_loader)
    self.image_loader = image_loader

  @cache_result
  def get_dilated(self):
    kernel = np.ones((3, 3), np.uint8)
    return cv2.dilate(super().get_remove_small_dots(), kernel, iterations=2)

  def get_image_rect(self, p0, p1, p2, p3):
    """Extract from self.img a rectangle defined by the four points p0, p1, p2, p3"""
    width = int(np.linalg.norm(p0 - p1))
    height = int(np.linalg.norm(p3 - p0))
    src = np.array([p0, p1, p2, p3], dtype='float32')
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')
    mp = cv2.getPerspectiveTransform(src, dst)
    return cv2.warpPerspective(self.image_loader.get_image(), mp, (width, height))

  def _cortar_columnas_preferencial(
    self, img_crop, matriz_detec, num_columns,
    acta_pk, nombre_seccion, cod_usuario=None, centro_computo=None):
    """
    Corta la imagen de preferenciales por columnas, usando la grilla detectada
    o en su defecto corte por igual ancho (contingencia).
    Retorna una lista de IDs de archivos subidos (o vacía si no se suben).
    """
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S%f')

    try:
        if len(matriz_detec[0]) - 1 > num_columns:
            raise ValueError("Contingencia activada")

        return self._corte_por_grilla(
            img_crop, matriz_detec, acta_pk, nombre_seccion,
            timestamp, cod_usuario, centro_computo
        )

    except Exception as e:
        logger.warning(
            f"Error en corte por grilla preferencial, usando contingencia: {e}",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
        return self._corte_por_igual_ancho(
            img_crop, num_columns, acta_pk, nombre_seccion,
            timestamp, cod_usuario, centro_computo
        )

  def _guardar_y_subir_columna(self, col_crop, filename, cod_usuario, centro_computo):
    col_path = resolve_workspace_path(filename)
    cv2.imwrite(col_path, col_crop)
    ctx = get_context()
    if ctx:
        ctx.add_temp_file(col_path)
    if cod_usuario and centro_computo:
        return upload_file_to_dir_process_acta(col_path, cod_usuario, centro_computo)
    return None

  def _corte_por_grilla(self, img_crop, matriz_detec, acta_pk, nombre_seccion,
                    timestamp, cod_usuario, centro_computo):
    archivos = []
    num_cols = len(matriz_detec[0]) - 1
    for col in range(num_cols):
        x1, y1 = matriz_detec[0][col]
        x2, y2 = matriz_detec[0][col + 1][0], matriz_detec[-1][col + 1][1]
        col_crop = img_crop[
            max(y1 - 5, 0):min(y2 + 6, img_crop.shape[0]),
            max(x1 - 5, 0):min(x2 + 6, img_crop.shape[1])
        ]
        col_filename = (f"{nombre_seccion}_columna_grilla_{col+1}_{acta_pk}_{timestamp}.png")
        archivo_id = self._guardar_y_subir_columna(col_crop, col_filename, cod_usuario, centro_computo)
        if archivo_id:
            archivos.append(archivo_id)
    return archivos

  def _corte_por_igual_ancho(self, img_crop, num_columns, acta_pk, nombre_seccion,
                        timestamp, cod_usuario, centro_computo):
    archivos = []
    _, width, _ = img_crop.shape
    col_width = width // num_columns
    for i in range(num_columns):
        x_start, x_end = i * col_width, (i + 1) * col_width
        col_crop = img_crop[:, x_start:x_end]
        col_filename = f"{nombre_seccion}_columna_{i+1}_{acta_pk}_{timestamp}.png"
        archivo_id = self._guardar_y_subir_columna(col_crop, col_filename, cod_usuario, centro_computo)
        if archivo_id:
            archivos.append(archivo_id)
    return archivos

  def _procesar_grilla_preferencial_principal(self, img_crop, copia_a_color, num_rows, num_columns,acta_pk, nombre_seccion, is_convencional, margin, cod_usuario, centro_computo):
    grilla = ActaBase(img_crop).obtener_grilla_votos(copia_a_color,is_convencional=is_convencional)
    matriz_detec = construir_matriz(grilla, 10, 10)

    if len(matriz_detec) != num_rows or (len(matriz_detec[0]) - 1) != num_columns:
        raise ValueError(
            f"Matriz preferencial inválida: esperadas filas={num_rows}, columnas={num_columns}, "
            f"detectado={len(matriz_detec)}x{len(matriz_detec[0]) - 1}"
        )
    
    self._validate_table_structure(matriz_detec,num_rows,num_columns,tolerance_x=7,enforce_spacing=True)
    self.validar_dimensiones_celdas(matriz_detec, tolerancia=0.2, log_queue= constantes.QUEUE_LOGGER_VALUE_PROCESS)
    img_crop_smart, offset_x, offset_y = self._recrop_from_points_smart(img_crop,matriz_detec,margin=8+margin)

    new_matriz = [
        [(x - offset_x, y - offset_y) for (x, y) in row]
        for row in matriz_detec
    ]

    filtered_img, new_img_preprocess = self.get_binary_table(img_crop_smart, copia_a_color, new_matriz)
    archivos_columnas = self._cortar_columnas_preferencial(img_crop_smart, new_matriz, num_columns,acta_pk, nombre_seccion, cod_usuario, centro_computo)
    ans, ans_preprocess, matriz_info = self._process_grilla_preferencial(img_crop_smart, new_img_preprocess, new_matriz)

    return ans, ans_preprocess, matriz_info, filtered_img, archivos_columnas

  def _procesar_grilla_preferencial_alternativa(self, img_crop, copia_a_color, num_rows, num_columns,acta_pk, is_convencional, margin, cod_usuario, centro_computo):
      self.image_loader.set_image(img_crop)
      table_points = self.get_table_points(modo="preferenciales", is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
  
      if len(table_points) != num_rows:
          logger.info(f"Filas detectadas = {len(table_points)}, Filas Esperadas = {num_rows}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
          raise ValueError("table_points incompletos en filas")
  
      if (len(table_points[0]) - 1) != num_columns:
          logger.info(f"Columnas detectadas = {len(table_points[0])-1}, Columnas Esperadas = {num_columns}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
          raise ValueError("Numero de columnas incorrecto en table_points")

      self._validate_table_structure(
            table_points,
            num_rows,
            num_columns,
            tolerance_x=7,
            enforce_spacing=True
        )
      self.validar_dimensiones_celdas(table_points, tolerancia=0.2, log_queue= constantes.QUEUE_LOGGER_VALUE_PROCESS)
  
      new_img = self.image_loader.get_image().copy()
      new_img, offset_x, offset_y = self._recrop_from_points_smart(
            new_img,
            table_points,
            margin=8+margin
        )
  
      new_table_points = [
          [(px - offset_x, py - offset_y) for px, py in row]
          for row in table_points
      ]
  
      new_rows = len(new_table_points) - 1
      new_cols = len(new_table_points[0]) - 1
  
      ans = [[] for _ in range(new_rows)]
      ans_preprocess = [[] for _ in range(new_rows)]
      info = [[] for _ in range(new_rows)]

      thick_line = 6
      if margin != 0:
          thick_line = 7
  
      filtered_img, new_img_preprocess = self.get_binary_table(new_img, copia_a_color, new_table_points,name="preferenciales", thick_line=thick_line)
  
      px, py = 5, 5
  
      for r in range(new_rows):
          for c in range(new_cols):
              tl = new_table_points[r][c]
              tr = new_table_points[r][c + 1]
              bl = new_table_points[r + 1][c]
              br = new_table_points[r + 1][c + 1]
  
              x1 = min(tl[0], bl[0])
              x2 = max(tr[0], br[0])
              y1 = min(tl[1], tr[1])
              y2 = max(bl[1], br[1])
  
              x1m = max(0, x1 - px)
              y1m = max(0, y1 - py)
              x2m = min(new_img.shape[1], x2 + px + 1)
              y2m = min(new_img.shape[0], y2 + py + 1)
  
              cell_img = new_img[y1m:y2m, x1m:x2m]
              cell_img_2 = new_img_preprocess[y1m:y2m, x1m:x2m]
  
              if cell_img.size == 0:
                  cell_img = new_img[y1:y2, x1:x2]
                  cell_img_2 = new_img_preprocess[y1:y2, x1:x2]
  
              ans[r].append(cell_img)
              ans_preprocess[r].append(cell_img_2)
              info[r].append((x1m, y1m, x2m, y2m))
  
      archivos_columnas = self._cortar_columnas_preferencial_observada(new_table_points, acta_pk, "voto_preferencial",num_columns, cod_usuario, centro_computo)
  
      return ans, ans_preprocess, info, filtered_img, archivos_columnas
  
  def get_data_cortes_preferenciales_coordenadas(self,acta_pk,img_limpia_path,point0,point3,
                                                 guide_lines,copia_a_color,nombre_seccion,is_convencional,cod_usuario,centro_computo):

    num_rows = get_cantidad_agrupaciones_politicas(acta_pk, constantes.QUEUE_LOGGER_VALUE_PROCESS) - 3
    num_columns = get_cantidad_columnas_preferenciales(acta_pk, constantes.QUEUE_LOGGER_VALUE_PROCESS)
    for margin in (0, 2, 4):
        logger.info(f"Intentando procesamiento tabla PREFERENCIAL con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        img_crop = self._build_table_image(img_limpia_path,point0,point3,margin=margin)
        try:
            ans, ans_preprocess, matriz_info, filtered_img, archivos_columnas = self._procesar_grilla_preferencial_principal(img_crop, copia_a_color, num_rows, num_columns, acta_pk, nombre_seccion, is_convencional, margin, cod_usuario, centro_computo)
            logger.info(f"Metodo principal preferencial exitoso con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            return ans, ans_preprocess, matriz_info, filtered_img, archivos_columnas, True, True
        except Exception as e1:
            logger.info(f"Metodo principal preferencial falló con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info(f"Error: {e1}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            try:
                ans, ans_preprocess, info, filtered_img, archivos_columnas = self._procesar_grilla_preferencial_alternativa(img_crop, copia_a_color, num_rows, num_columns, acta_pk, is_convencional, margin, cod_usuario, centro_computo)
                logger.info(f"Metodo alternativo preferencial exitoso con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                return ans, ans_preprocess, info, filtered_img, archivos_columnas, True, True
            except Exception as e2:
                logger.info(f"Metodo alternativo preferencial falló con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                logger.info(f"Error: {e2}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                continue

    logger.info("Metodo dinámico final para preferenciales activado...",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    ans, matriz_info, filtered_img = self.get_data_cortes_preferencial_dinamico(acta_pk,img_limpia_path,point0,point3,guide_lines,copia_a_color)
    img_crop_final = self._build_table_image(img_limpia_path,point0,point3,margin=0)
    archivos_columnas = self._cortar_columnas_preferencial(img_crop_final,[],num_columns,acta_pk,nombre_seccion,cod_usuario,centro_computo)

    return ans, None, matriz_info, filtered_img, archivos_columnas, False, False

  def get_data_cortes_preferencial_dinamico(self, acta_pk, img_limpia_path, point0, point3, guide_lines, copia_a_color):
    point0_fix = (point0[0] + 8, point0[1] + 8)
    point3_fix = (point3[0] - 8, point3[1] - 8)

    num_rows = get_cantidad_agrupaciones_politicas(acta_pk, constantes.QUEUE_LOGGER_VALUE_PROCESS) - 4
    num_columns = get_cantidad_columnas_preferenciales(acta_pk, constantes.QUEUE_LOGGER_VALUE_PROCESS)

    img_limpia = cv2.imread(img_limpia_path)
    img_table = img_limpia[point0_fix[1]:point3_fix[1], point0_fix[0]:point3_fix[0]]
    filtered_img, _ = self.get_binary_table(img_table, copia_a_color, matriz_detec=None, name="otro")
    img_height, img_width = img_table.shape[:2]

    col_x_values = np.linspace(0, img_width, num_columns + 1, dtype=int)

    zonas, filas_por_zona = self.calcular_zonas_y_filas(guide_lines, point0_fix[1], img_height, num_rows)

    ans, matriz_info = self.cortar_celdas_preferencial_con_info(
        img_table, zonas, filas_por_zona, col_x_values
    )

    return ans, matriz_info, filtered_img

  def calcular_zonas_y_filas(self, guide_lines, y_offset, img_height, num_rows):
    if not guide_lines:
        return [(0, img_height)], [num_rows]

    guide_y_positions = sorted([y1 - y_offset for (_, y1, _, _) in guide_lines if y1 > 0])
    filas_sector = constantes.NUMERO_FILAS_ANTES_DE_LINEAS_GUIAS
    sectores = num_rows // filas_sector
    sobrantes = num_rows % filas_sector

    if len(guide_y_positions) >= sectores:
        cortes_y = [0] + guide_y_positions
        while len(cortes_y) < sectores:
            cortes_y.append(int(len(cortes_y) * img_height / (sectores + 1)))
        cortes_y = sorted(cortes_y + [img_height])
        zonas = [(cortes_y[i], cortes_y[i + 1]) for i in range(sectores)]
        if sobrantes > 0:
            zonas.append((cortes_y[-2], cortes_y[-1]))
        filas_por_zona = [filas_sector] * sectores
        if sobrantes > 0:
            filas_por_zona.append(sobrantes)
    elif guide_y_positions:
        y0 = guide_y_positions[0]
        zonas = [(0, y0), (y0, img_height)]
        filas_por_zona = [filas_sector, num_rows - filas_sector]
    else:
        zonas = [(0, img_height)]
        filas_por_zona = [num_rows]

    return zonas, filas_por_zona

  def cortar_celdas_preferencial_con_info(self, img_table, zonas, filas_por_zona, col_x_values):

    total_columnas = len(col_x_values) - 1
    ans = []
    matriz_info = []

    for (y_ini, y_fin), filas_zona in zip(zonas, filas_por_zona):
        if filas_zona <= 0:
            continue

        y_cortes = np.linspace(y_ini, y_fin, filas_zona + 1, dtype=int)

        for idx in range(len(y_cortes) - 1):
            y1, y2 = y_cortes[idx], y_cortes[idx + 1]
            fila_actual = []
            fila_info = []

            for col_idx in range(total_columnas):
                x1, x2 = col_x_values[col_idx], col_x_values[col_idx + 1]
                cell_img = img_table[y1:y2, x1:x2]
                if cell_img.size > 0:
                    fila_actual.append(cell_img)
                    fila_info.append((x1, y1, x2, y2))
            ans.append(fila_actual)
            matriz_info.append(fila_info)
    return ans, matriz_info

  def cortar_celdas_por_zona(self, img_table, zonas, filas_por_zona, col_x_values):
    total_columnas = len(col_x_values) - 1
    filas = []
    for (y_ini, y_fin), filas_zona in zip(zonas, filas_por_zona):
        if filas_zona <= 0:
            continue
        y_cortes = np.linspace(y_ini, y_fin, filas_zona + 1, dtype=int)
        for row_idx in range(len(y_cortes) - 1):
            y1, y2 = y_cortes[row_idx], y_cortes[row_idx + 1]
            fila_actual = []
            for col_idx in range(total_columnas):
                x1, x2 = col_x_values[col_idx], col_x_values[col_idx + 1]
                cell_img = img_table[y1:y2, x1:x2]
                if cell_img.size > 0:
                    fila_actual.append(cell_img)
            filas.append(fila_actual)
    return filas


  def get_data_secciones_mm_1(self):
    """Encuentra la region de la tabla y la divide en 6 partes iguales."""

    img_full = self.image_loader.get_image().copy()
    height, width = img_full.shape[:2]

    point0 = (0, 0)
    point1 = (width - 1, 0)
    point2 = (width - 1, height - 1)
    point3 = (0, height - 1)

    img_table = self.get_image_rect_mm_1(point0, point1, point2, point3)
    
    num_miembros_mesa = constantes.NRO_MIEMBROS_MESA

    height, width = img_table.shape[:2]
    part_height = height // num_miembros_mesa
    sections = []

    for i in range(num_miembros_mesa):
        start_y = i * part_height
        end_y = min(start_y + part_height, height)
        part = img_table[start_y:end_y, :]
        sections.append(part)

    return sections

  def get_image_rect_mm_1(self, p0, p1, p2, p3):
    """Extrae un rectángulo de la imagen basado en los puntos dados."""
    p0, p1, p2, p3 = map(np.array, [p0, p1, p2, p3])
    width = int(np.linalg.norm(p1 - p0))
    height = int(np.linalg.norm(p3 - p0))

    src = np.array([p0, p1, p2, p3], dtype='float32')
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')

    mp = cv2.getPerspectiveTransform(src, dst)
    return cv2.warpPerspective(self.image_loader.get_image(), mp, (width, height))
  
  def _corregir_fila_espuria_final(self, table_points, rows, acta_type, log_queue):
    if len(table_points) == rows + 1:
        return table_points, True
    actas_horizontales = [
        constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
        constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
        constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO,
    ]
    if len(table_points) == rows + 2 and acta_type in actas_horizontales:
        img_height = self.image_loader.get_image().shape[0]
        last_y = int(table_points[-1][0][1])
        last_gap = last_y - int(table_points[-2][0][1])
        internal_gaps = [
            int(table_points[i + 1][0][1]) - int(table_points[i][0][1])
            for i in range(len(table_points) - 2)
        ]
        median_gap = sorted(internal_gaps)[len(internal_gaps) // 2]
        if last_y >= img_height * 0.985 and last_gap < median_gap * 0.90:
            logger.info(
                f"Contramedida: fila espuria al final eliminada "
                f"(last_y={last_y}, img_height={img_height}, last_gap={last_gap}, median_gap={median_gap})",
                queue=log_queue
            )
            return table_points[:-1], True
    logger.info(f"Filas detectadas = {len(table_points)}, Filas Esperadas = {rows + 1}", queue=log_queue)
    logger.warning("Advertencia: Puntos de corte insuficientes para las filas.", queue=log_queue)
    return table_points, False
  
  def get_data_cortes_preferencial_observada(self, acta_pk, section_name, cod_usuario, centro_computo, copia_a_color, is_convencional, acta_type, digitization_mode = False, log_queue = "default"):
    rows = get_cantidad_agrupaciones_politicas(acta_pk, log_queue=log_queue)-4
    num_columns = get_cantidad_columnas_preferenciales(acta_pk, log_queue=log_queue)
    nuevo_modo = self._resolver_modo_horizontal(
        acta_type, is_convencional,
        prefijo_convencional="preferencial_acta_horizontal_convencional",
        prefijo_extranjero="preferencial_acta_horizontal_extranjero",
        modo_default="preferenciales",
    )

    table_points = self.get_table_points(modo=nuevo_modo, is_convencional=is_convencional, log_queue=log_queue)

    if not self._validar_table_points_basico(table_points, log_queue):
        return [], [], True, None, None, None

    if len(table_points) < 3:
        logger.warning(f"ERROR: table_points insuficiente para slicing. len={len(table_points)}", queue=log_queue)
        return [], [], True, None, None, None

    table_points = table_points[2:]

    table_points, fila_ok = self._corregir_fila_espuria_final(table_points, rows, acta_type, log_queue)
    if not fila_ok:
        return [], [], True, None, None, None
    if len(table_points[0]) != num_columns + 1:
        logger.info(f"Columnas detectadas = {len(table_points[0])}, Columnas Esperadas = {num_columns + 1}", queue=log_queue)
        logger.warning("Advertencia: Puntos de corte insuficientes para las columnas.", queue=log_queue)
        return [], [], True, None, None, None

    try:
        self.validar_dimensiones_celdas(table_points, tolerancia=0.2, log_queue=log_queue)
    except ValueError as e:
        logger.warning(f"El metodo de validacion de dimensiones de celdas falló: {e}", queue=log_queue)
        return [], [], True, None, None, None
    
    if digitization_mode:
        return [], [], False, None, None, None

    try:
        extra_margin_y = 8
        extra_margin_x = 8

        point0 = table_points[0][0].copy()
        point1 = table_points[0][-1].copy()
        point2 = table_points[-1][-1].copy()
        point3 = table_points[-1][0].copy()

        archivos_columna = self._cortar_columnas_preferencial_observada(table_points, acta_pk ,section_name, num_columns, cod_usuario, centro_computo)

        # Ajustar valores de Y
        point0[1] = max(0, point0[1] - extra_margin_y)
        point1[1] = max(0, point1[1] - extra_margin_y)
        point2[1] = min(self.image_loader.get_image().shape[0], point2[1] + extra_margin_y)
        point3[1] = min(self.image_loader.get_image().shape[0], point3[1] + extra_margin_y)

        point0[0] = max(0, point0[0] - extra_margin_x)
        point3[0] = max(0, point3[0] - extra_margin_x)
        point1[0] = min(self.image_loader.get_image().shape[1], point1[0] + extra_margin_x)
        point2[0] = min(self.image_loader.get_image().shape[1], point2[0] + extra_margin_x)
    except IndexError:
        logger.exception("ERROR: No se pudieron definir correctamente los puntos de corte.", queue = log_queue)
        return [], [], True, None, None, None

    img_table = self.get_image_rect(point0, point1, point2, point3)
    h_img, w_img = img_table.shape[:2]

    offset_x, offset_y = point0

    rows = len(table_points) - 1
    cols = len(table_points[0]) - 1

    ans  = [[] for _ in range(rows)]
    ans_preprocess = [[] for _ in range(rows)]
    info = [[] for _ in range(rows)]

    margin_x = 5
    margin_y = 5

    rel_points = [
        [
            (px - offset_x, py - offset_y)
            for (px, py) in row
        ]
        for row in table_points
    ]
    
    filtered_img, new_image_preprocess = self.get_binary_table(img_crop=img_table, copia_a_color=copia_a_color, matriz_detec=rel_points, name="otros", thick_line = 6)

    for r in range(rows):
        for c in range(cols):

            (x1, y1) = rel_points[r][c]
            (x2, y2) = rel_points[r][c+1]
            (x3, y3) = rel_points[r+1][c]
            (x4, y4) = rel_points[r+1][c+1]

            bx1 = min(x1, x3)
            bx2 = max(x2, x4)
            by1 = min(y1, y2)
            by2 = max(y3, y4)

            # agregar margen
            x1m = max(0, bx1 - margin_x)
            y1m = max(0, by1 - margin_y)
            x2m = min(w_img, bx2 + margin_x + 1)
            y2m = min(h_img, by2 + margin_y + 1)

            # recorte final
            cell_img = img_table[y1m:y2m, x1m:x2m]
            cell_img_2 = new_image_preprocess[y1m:y2m, x1m:x2m]

            if cell_img.size == 0:
                cell_img = img_table[by1:by2, bx1:bx2]
                cell_img_2 = new_image_preprocess[by1:by2, bx1:bx2]

            ans[r].append(cell_img)
            ans_preprocess[r].append(cell_img_2)
            info[r].append((x1m, y1m, x2m, y2m))
    
    return ans, ans_preprocess, False, archivos_columna, info, filtered_img
  
  def _cortar_columnas_preferencial_observada(
    self, table_points, acta_pk, nombre_seccion,
    num_columns, cod_usuario, centro_computo):
    """
    Corta la tabla preferencial observada en tantas columnas como se detecten (num_columns).
    Guarda cada columna como archivo y retorna una lista de IDs subidos.
    """
    archivos_columnas = []
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S%f')

    img_crop = self.image_loader.get_image().copy()

    try:
        h_img, w_img = img_crop.shape[:2]
        margin_left = 5
        margin_right = 6
        margin_top = 5
        margin_bottom = 6
            
        for col in range(num_columns):
            # Coordenadas de la columna en table_points
            x1 = table_points[0][col][0]
            x2 = table_points[0][col + 1][0]
            y1 = table_points[0][col][1]
            y2 = table_points[-1][col][1]
            y3 = table_points[-1][col + 1][1]
            x1_adj = max(x1 - margin_left, 0)
            x2_adj = min(x2 + margin_right, w_img)
            y1_adj = max(min(y1, y2) - margin_top, 0)
            y2_adj = min(max(y2, y3) + margin_bottom, h_img)
            # Recorte
            col_crop = img_crop[y1_adj:y2_adj, x1_adj:x2_adj]
            # Guardado temporal
            col_filename = f"{nombre_seccion}_columna_grilla_{col+1}_{acta_pk}_{timestamp}.png"
            col_path = resolve_workspace_path(col_filename)
            cv2.imwrite(col_path, col_crop)
            ctx = get_context()
            if ctx:
                ctx.add_temp_file(col_path)
            # Subida (si corresponde)
            if cod_usuario and centro_computo:
                archivo_id = upload_file_to_dir_process_acta(col_path, cod_usuario, centro_computo)
                archivos_columnas.append(archivo_id)

    except Exception as e:
        logger.exception(f" Error en corte por columnas preferencial observada: {e}")

    return archivos_columnas
  
  def _procesar_columnas_preferenciales_stae_vd(self, acta_pk, num_columns, cod_usuario, centro_computo, copia_a_color):
        img_crop = self.image_loader.get_image()
        grilla = ActaBase(img_crop).obtener_grilla_votos(copia_a_color)
        matriz_detec = construir_matriz(grilla, 10, 10)

        if (
            not matriz_detec
            or not matriz_detec[0]
            or len(matriz_detec[0]) - 1 > num_columns
        ):
            logger.warning(
                f"[Preferencial STAE/VD] Matriz inválida "
                f"(detectado={len(matriz_detec)}x{len(matriz_detec[0]) if matriz_detec else 0}), "
                f"esperado columnas={num_columns}. Fallback activado."
            )
            matriz_detec = None
  
        nombre_seccion = "Voto_Preferencial"
        archivos_columnas = self._cortar_columnas_preferencial(img_crop, matriz_detec, num_columns,acta_pk, nombre_seccion, cod_usuario, centro_computo)

        return archivos_columnas
