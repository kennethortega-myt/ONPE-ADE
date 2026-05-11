import numpy as np
from models.imageutils import ImagesLoader
from models.votesprocessorbase import VotesProcessorBase
import cv2
import os

from db.progress import get_cantidad_agrupaciones_politicas
from db.progress import get_cantidad_agrupaciones_politicas_revocatoria, upload_file_to_dir_process_acta
from util import constantes
from datetime import datetime
from logger_config import logger
from models.cortesmodel.acta_base import ActaBase
from util.coordenadas_util import construir_matriz
from db.execution_context import  get_context, resolve_workspace_path

class VotesImageProcessor(VotesProcessorBase):
  def __init__(self, image_loader: ImagesLoader):
    super().__init__(image_loader)
    self.image_loader = image_loader
    self.ratio = 1
    self.new_width = 2500
    self.new_height = 1e10

  def get_image_rect(self, p0, p1, p2, p3):
    """Extract from self.img a rectangle defined by the four points p0, p1, p2, p3"""
    width = max(np.linalg.norm(p1 - p0), np.linalg.norm(p2 - p3))
    height = max(np.linalg.norm(p3 - p0), np.linalg.norm(p2 - p1))
    width, height = int(width), int(height)
    src = np.array([p0, p1, p2, p3], dtype='float32')
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')
    mp = cv2.getPerspectiveTransform(src, dst)
    img_transformed = cv2.warpPerspective(self.image_loader.get_image(), mp, (width, height))
    return img_transformed

  def get_data_cortes_revocatoria_alterno(self, acta_pk, img_limpia_path, point0, point3):
    """Divide la imagen en `rows` filas y 8 columnas usando los extremos de la imagen completa."""

    rows = get_cantidad_agrupaciones_politicas_revocatoria(acta_pk)
    columns = 5

    logger.info(f"Filas detectadas en revocatoria: {rows}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    point0_fix = (point0[0] + 8, point0[1] + 8)
    point3_fix = (point3[0] - 8, point3[1] - 8)

    img_limpia = cv2.imread(img_limpia_path)

    img_table = img_limpia[point0_fix[1]:point3_fix[1], point0_fix[0]:point3_fix[0]]

    height, width = img_table.shape[:2]

    part_height = height // rows
    part_width = width // columns

    ans = []

    for row_idx in range(rows):
        row = []
        for col_idx in range(columns):
            start_x = col_idx * part_width
            end_x = min(start_x + part_width, width)

            start_y = row_idx * part_height
            end_y = min(start_y + part_height, height)

            cell_img = img_table[start_y:end_y, start_x:end_x]

            if cell_img.size == 0:
                logger.info(f"Imagen vacía en fila {row_idx+1}, columna {col_idx+1}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
                continue

            row.append(cell_img)
        ans.append(row)
    return ans

  def _procesar_total_votos_principal(self, img_crop, copia_a_color, num_rows, is_convencional, margin):
    grilla = ActaBase(img_crop).obtener_grilla_votos(copia_a_color, is_convencional=is_convencional, flag_total_votos=True)
    matriz_detec = construir_matriz(grilla, 10, 10)

    if len(matriz_detec) != num_rows or len(matriz_detec[0]) != 2:
        raise ValueError(
            f"Matriz total votos inválida: esperadas filas={num_rows}x2, "
            f"detectado={len(matriz_detec)}x{len(matriz_detec[0])}"
        )
    
    self._validate_table_structure(
        matriz_detec,
        num_rows,
        1,
        tolerance_x=8,
        enforce_spacing=False
    )

    self.validar_dimensiones_celdas(matriz_detec, tolerancia=0.2, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    img_crop_smart, offset_x, offset_y = self._recrop_from_points_smart(
        img_crop,
        matriz_detec,
        margin=8+margin
    )

    new_matriz = [
        [(x - offset_x, y - offset_y) for (x, y) in row]
        for row in matriz_detec
    ]

    filtered_img, new_img_preprocess = self.get_binary_table(img_crop_smart, copia_a_color, new_matriz, name="total_votos")
    ans, ans_preprocess, matriz_info = self._process_grilla_preferencial(img_crop_smart, new_img_preprocess, new_matriz)
    return ans, ans_preprocess, matriz_info, filtered_img

  def _procesar_total_votos_alternativo(self, img_crop, copia_a_color, num_rows, is_convencional, margin):
      self.image_loader.set_image(img_crop)
      table_points = self.get_table_points(modo="cuadricula_votos", is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

      if len(table_points) != num_rows:
          logger.info(f"Filas detectadas = {len(table_points)}, Filas Esperadas = {num_rows}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
          logger.warning("Advertencia: Puntos de corte insuficientes.",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
          raise ValueError("table_points incompletos")
  
      if (len(table_points[0]) - 1) != 1:
          logger.info(f"Columnas detectadas = {len(table_points[0])}, Columnas Esperadas = 2", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
          logger.warning("ERROR: No hay suficientes columnas detectadas.",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
          raise ValueError("tabla no es de una columna")
  
      self._validate_table_structure(
        table_points,
        num_rows,
        1,
        tolerance_x=8,
        enforce_spacing=False
    )
      
      self.validar_dimensiones_celdas(table_points, tolerancia=0.2, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            
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
  
      ans = [[] for _ in range(new_rows)]
      ans_preprocess = [[] for _ in range(new_rows)]
      info = [[] for _ in range(new_rows)]

      thick_line = 6
      if margin != 0:
          thick_line = 7
  
      filtered_img, new_img_preprocess = self.get_binary_table(new_img, copia_a_color, new_table_points,name="total_votos", thick_line=thick_line)
      mx, my = 5, 5
  
      for r in range(new_rows):
          (x1, y1) = new_table_points[r][0]
          (x2, y2) = new_table_points[r + 1][0]
  
          bx1 = x1
          bx2 = new_img.shape[1] - 1
          by1 = y1
          by2 = y2
  
          x1m = max(0, bx1 - mx)
          y1m = max(0, by1 - my)
          x2m = min(new_img.shape[1], bx2 + mx + 1)
          y2m = min(new_img.shape[0], by2 + my + 1)
  
          cell_img = new_img[y1m:y2m, x1m:x2m]
          cell_img_2 = new_img_preprocess[y1m:y2m, x1m:x2m]
  
          if cell_img.size == 0:
              cell_img = new_img[by1:by2, bx1:bx2]
              cell_img_2 = new_img_preprocess[by1:by2, bx1:bx2]
  
          ans[r].append(cell_img)
          ans_preprocess[r].append(cell_img_2)
          info[r].append((x1m, y1m, x2m, y2m))
  
      return ans, ans_preprocess, info, filtered_img
  
  def get_data_cortes_coordenadas(self,acta_pk,img_limpia_path,point0,point3,guide_lines,copia_a_color,is_convencional):
    num_rows = get_cantidad_agrupaciones_politicas(acta_pk,constantes.QUEUE_LOGGER_VALUE_PROCESS)
    for margin in (0, 2, 4):
        logger.info(f"Intentando procesamiento tabla TOTAL VOTOS con margin={8+margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        img_crop = self._build_table_image(
            img_limpia_path,
            point0,
            point3,
            margin=margin
        )

        try:
            ans, ans_preprocess, matriz_info, filtered_img = self._procesar_total_votos_principal(img_crop, copia_a_color, num_rows, is_convencional, margin)
            logger.info(f"Metodo principal exitoso con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            return ans, ans_preprocess, matriz_info, filtered_img, True, True

        except Exception as e1:
            logger.info(f"Metodo principal falló con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info(f"Error: {e1}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

            try:
                ans, ans_preprocess, info, filtered_img = self._procesar_total_votos_alternativo(img_crop,copia_a_color, num_rows,is_convencional, margin)
                logger.info(f"Metodo alternativo exitoso con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                return ans, ans_preprocess, info, filtered_img, True, True

            except Exception as e2:

                logger.info(f"Metodo alternativo falló con margin={8 + margin}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                logger.info(f"Error: {e2}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                continue

    logger.info("Contramedida Final Activada (cortes dinámicos)",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    ans, matriz_info, filtered_img = self.get_data_cortes_dinamico(acta_pk,img_limpia_path,point0,point3,guide_lines,copia_a_color)
    return ans, None, None, filtered_img, False, False

  def get_data_cortes_dinamico(self, acta_pk, img_limpia_path, point0, point3, guide_lines, copia_a_color, ajuste_filas =-1):
      point0, point3 = VotesProcessorBase(None)._fix_points(point0, point3)
      num_rows = get_cantidad_agrupaciones_politicas(acta_pk, log_queue= constantes.QUEUE_LOGGER_VALUE_PROCESS) + ajuste_filas
      img_full = cv2.imread(img_limpia_path)
      img_table = img_full[point0[1]:point3[1], point0[0]:point3[0]]
      filtered_img,_ = self.get_binary_table(img_table, copia_a_color, matriz_detec=None, name="total_votos_dinamico")
      img_height, img_width = img_table.shape[:2]
      matriz_info = []  
      if not guide_lines:
          segment = np.linspace(0, img_height, num_rows + 1, dtype=int)
          ans = []
          for y1, y2 in zip(segment[:-1], segment[1:]):
              cell = img_table[y1:y2, 0:img_width]
              if cell.size == 0:
                  continue
              ans.append([cell])
              matriz_info.append([(0, y1, img_width, y2)])
          return ans, matriz_info, filtered_img
      
      # con lineas guia
      ans, matriz_info = self.cortes_por_zonas(img_table, guide_lines, point0,img_height, img_width, num_rows)
      return ans, matriz_info, filtered_img

  def cortes_por_zonas(self, img_table, guide_lines, y_offset, img_height, img_width, num_rows):
    zonas, filas_por_zona = self.dividir_en_zonas_y_filas(img_height, guide_lines, y_offset, num_rows)
    ans = []
    matriz_info = []
    for (y_ini, y_fin), filas_zona in zip(zonas, filas_por_zona):
        if filas_zona <= 0:
            continue
        segment = np.linspace(y_ini, y_fin, filas_zona + 1, dtype=int)
        for y1, y2 in zip(segment[:-1], segment[1:]):
            cell_img = img_table[y1:y2, 0:img_width]
            if cell_img.size == 0:
                continue
            ans.append([cell_img])
            matriz_info.append([(0, y1, img_width, y2)])
    return ans, matriz_info

  def dividir_en_zonas_y_filas(self, img_height, guide_lines, y_offset, num_rows):
    """
    Calcula las zonas verticales de corte y la cantidad de filas por cada zona
    basándose en las líneas guía detectadas y la altura de la imagen.
    """
    guide_y = sorted([y1 - y_offset[1] for (_, y1, _, _) in guide_lines if y1 > 0])
    filas_sector = constantes.NUMERO_FILAS_ANTES_DE_LINEAS_GUIAS
    sectores = num_rows // filas_sector
    sobrantes = num_rows % filas_sector

    if len(guide_y) >= sectores:
        cortes_y = [0] + guide_y
        while len(cortes_y) < sectores:
            cortes_y.append(int(len(cortes_y) * img_height / (sectores + 1)))
        cortes_y = sorted(cortes_y + [img_height])
        zonas = [(cortes_y[i], cortes_y[i + 1]) for i in range(sectores)]
        filas_por_zona = [filas_sector] * sectores
        if sobrantes > 0:
            zonas.append((cortes_y[-2], cortes_y[-1]))
            filas_por_zona.append(sobrantes)
    elif guide_y:
        y0 = guide_y[0]
        zonas = [(0, y0), (y0, img_height)]
        filas_por_zona = [filas_sector, num_rows - filas_sector]
    else:
        zonas = [(0, img_height)]
        filas_por_zona = [num_rows]

    return zonas, filas_por_zona

  def get_data_secciones_mm_2(self):
    """Encuentra la región de la tabla y la divide en 3 partes iguales."""

    img_full = self.image_loader.get_image().copy()
    height, width = img_full.shape[:2]

    point0 = (0, 0)
    point1 = (width - 1, 0)
    point2 = (width - 1, height - 1)
    point3 = (0, height - 1)

    img_table = self.get_image_rect_mm_2(point0, point1, point2, point3)

    height, width = img_table.shape[:2]
    part_height = height // 3
    sections = []

    for i in range(3):  # 3 filas
        start_y = i * part_height
        end_y = start_y + part_height if i < 2 else height
        part = img_table[start_y:end_y, :]
        sections.append(part)

    return sections

  def get_image_rect_mm_2(self, p0, p1, p2, p3):
    """Extrae un rectángulo de la imagen basado en los puntos dados."""

    p0, p1, p2, p3 = map(np.array, [p0, p1, p2, p3])
    width = int(np.linalg.norm(p1 - p0))
    height = int(np.linalg.norm(p3 - p0))

    src = np.array([p0, p1, p2, p3], dtype='float32')
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')

    mp = cv2.getPerspectiveTransform(src, dst)
    return cv2.warpPerspective(self.image_loader.get_image(), mp, (width, height))

  def _lineas_a_eliminar(self, codigo_eleccion):
    if codigo_eleccion in [
        constantes.COD_ELEC_DIPUTADO,
        constantes.COD_ELEC_PARLAMENTO,
        constantes.COD_ELEC_SENADO_MULTIPLE,
        constantes.COD_ELEC_SENADO_UNICO
    ]:
        return 2
    return 1

  def _recortar_filas_observada(self, table_points, acta_type, codigo_eleccion, lineas_eliminar):
      if (
          acta_type in [
              constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,
              constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
              constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO
          ]
          and codigo_eleccion in [
              constantes.COD_ELEC_DIPUTADO,
              constantes.COD_ELEC_SENADO_UNICO
          ]
      ):
          return table_points[lineas_eliminar:]
      return table_points[lineas_eliminar:-1]
  
  
  def _ajustar_puntos_con_margen(self, p0, p1, p2, p3, mx, my):
      h, w = self.image_loader.get_image().shape[:2]
  
      p0[0] = max(0, p0[0] - mx)
      p3[0] = max(0, p3[0] - mx)
      p1[0] = min(w, p1[0] + mx)
      p2[0] = min(w, p2[0] + mx)
  
      p0[1] = max(0, p0[1] - my)
      p1[1] = max(0, p1[1] - my)
      p2[1] = min(h, p2[1] + my)
      p3[1] = min(h, p3[1] + my)
  
      return p0, p1, p2, p3
  
  def _obtener_new_table_points(self, table_points,col_start, col_end, offset_x, offset_y, x_start, log_queue):
        logger.info("Obteniendo coordenadas a traves del table_points original...",queue=log_queue)
        ntp = []
        for row in table_points:
            ntp.append([row[col_start], row[col_end]])
        for r in range(len(ntp)):
            for c in range(2):
                px, py = ntp[r][c]
                ntp[r][c] = (px - offset_x - x_start, py - offset_y)
        return ntp
  
  def _construir_celdas(self, new_img, new_img_preprocess, new_table_points):
      rows = len(new_table_points) - 1
      ans = [[] for _ in range(rows)]
      ans_preprocess = [[] for _ in range(rows)]
      info = [[] for _ in range(rows)]
  
      mx, my = 5, 5
  
      for r in range(rows):
          (x1, y1), (x2, y2) = new_table_points[r]
          (x3, y3), (x4, y4) = new_table_points[r + 1]
  
          bx1, bx2 = min(x1, x3), max(x2, x4)
          by1, by2 = min(y1, y2), max(y3, y4)
  
          x1m = max(0, bx1 - mx)
          y1m = max(0, by1 - my)
          x2m = min(new_img.shape[1], bx2 + mx + 1)
          y2m = min(new_img.shape[0], by2 + my + 1)
  
          cell = new_img[y1m:y2m, x1m:x2m]
          cell_p = new_img_preprocess[y1m:y2m, x1m:x2m]
  
          if cell.size == 0:
              cell = new_img[by1:by2, bx1:bx2]
              cell_p = new_img_preprocess[by1:by2, bx1:bx2]
  
          ans[r].append(cell)
          ans_preprocess[r].append(cell_p)
          info[r].append((x1m, y1m, x2m, y2m))
  
      return ans, ans_preprocess, info
  
  def get_data_cortes_observada(self, acta_pk, section_name, codigo_eleccion, acta_type,cod_usuario, centro_computo, copia_a_color, is_convencional, digitization_mode=False, log_queue="default"):
    nuevo_modo = self._resolver_modo_horizontal(
        acta_type, is_convencional,
        prefijo_convencional="total_votos_acta_horizontal_convencional",
        prefijo_extranjero="total_votos_acta_horizontal_extranjero",
        modo_default="total_votos",
    )
    rows = get_cantidad_agrupaciones_politicas(acta_pk, log_queue) - 1
    table_points = self.get_table_points(modo=nuevo_modo, is_convencional=is_convencional, log_queue=log_queue)

    if not self._validar_table_points_basico(table_points, log_queue):
        return [], [], True, None, None, None

    if (len(table_points[0]) - 1) != 3:
        logger.info(f"Columnas detectadas = {len(table_points[0]) - 1}, Columnas Esperadas = 3", queue = log_queue)
        logger.warning("ERROR: No hay suficientes columnas detectadas.", queue=log_queue)
        return [], [], True, None, None, None
    
    lineas_eliminar = self._lineas_a_eliminar(codigo_eleccion)
    table_points = table_points[lineas_eliminar:-1]
    
    if len(table_points) != rows + 1:
        logger.info(f"Filas detectadas = {len(table_points)}, Filas Esperadas = {rows + 1}", queue = log_queue)
        logger.warning("Advertencia: Puntos de corte insuficientes.", queue=log_queue)
        return [], [], True, None, None, None
    
    if not self._validar_proporciones_table_points(table_points, log_queue=log_queue):
        return [], [], True, None, None, None

    if digitization_mode:
        return [], [], False, None, None, None
    
    try:
        p0, p1, p2, p3 = (
            table_points[0][0].copy(),
            table_points[0][-1].copy(),
            table_points[-1][-1].copy(),
            table_points[-1][0].copy()
        )

        archivos_columna = self._cortar_columnas_observada(table_points, acta_pk, section_name, cod_usuario, centro_computo)
        p0, p1, p2, p3 = self._ajustar_puntos_con_margen(p0, p1, p2, p3, 8, 8)
    except Exception:
        logger.warning("ERROR: No se pudieron definir correctamente los puntos de corte.", queue=log_queue)
        return [], [], True, None, None, None

    img_table = self.get_image_rect(p0, p1, p2, p3)
    new_img = img_table.copy()

    col_start, col_end = 2, 3
    offset_x = p0[0]
    offset_y = table_points[0][col_start][1] - 8

    x_start = max(0, table_points[0][col_start][0] - offset_x - 8)
    x_end = min(new_img.shape[1], table_points[0][col_end][0] - offset_x + 16)

    new_img = new_img[:, x_start:x_end]

    new_table_points = self._obtener_new_table_points(table_points,col_start, col_end, offset_x, offset_y, x_start, log_queue = log_queue)
    filtered_img, new_img_preprocess = self.get_binary_table(img_crop=new_img,copia_a_color=copia_a_color,matriz_detec=new_table_points,name="total_votos",thick_line=6)
    ans, ans_preprocess, info = self._construir_celdas(new_img, new_img_preprocess, new_table_points)

    return ans, ans_preprocess, False, archivos_columna, info, filtered_img

  def _cortar_columnas_observada(
    self, table_points, acta_pk, nombre_seccion,
    cod_usuario, centro_computo
    ):
    """
    Corta la tabla observada en 2 segmentos horizontales:
    - Entre las líneas verticales 0 y 2
    - Entre las líneas verticales 2 y 3
    
    Retorna una lista de IDs de archivos subidos (o vacía si no se suben).
    """
    archivos_columnas = []
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S%f')

    img_crop = self.image_loader.get_image().copy()

    try:
        h_img, w_img = img_crop.shape[:2]
        margin_adjust = 7

        # Corte 1: entre líneas 0 y 2
        x1 = table_points[0][0][0]
        x2 = table_points[0][2][0]
        y1 = table_points[0][0][1]
        y2 = table_points[-1][0][1]

        x1_adj = max(x1 - margin_adjust, 0)
        x2_adj = min(x2 + margin_adjust, w_img)
        y1_adj = max(y1 - margin_adjust, 0)
        y2_adj = min(y2 + margin_adjust, h_img)

        col_crop1 = img_crop[y1_adj:y2_adj, x1_adj:x2_adj]
        col_filename1 = f"{nombre_seccion}_columna_{acta_pk}_{timestamp}.png"
        col_path1 = resolve_workspace_path(col_filename1)
        cv2.imwrite(col_path1, col_crop1)
        ctx = get_context()
        if ctx:
            ctx.add_temp_file(col_path1)
        
        archivo_id = upload_file_to_dir_process_acta(col_path1, cod_usuario, centro_computo)
        archivos_columnas.append(archivo_id)

        # Corte 2: entre líneas 2 y 3
        x1 = table_points[0][2][0]
        x2 = table_points[0][3][0]
        y1 = table_points[0][2][1]
        y2 = table_points[-1][2][1]

        x1_adj = max(x1 - margin_adjust, 0)
        x2_adj = min(x2 + margin_adjust, w_img)
        y1_adj = max(y1 - margin_adjust, 0)
        y2_adj = min(y2 + margin_adjust, h_img)

        col_crop2 = img_crop[y1_adj:y2_adj, x1_adj:x2_adj]
        col_filename2 = f"{nombre_seccion}_tabla_{acta_pk}_{timestamp}.png"
        col_path2 = resolve_workspace_path(col_filename2)
        cv2.imwrite(col_path2, col_crop2)
        ctx = get_context()
        if ctx:
            ctx.add_temp_file(col_path2)
        
        archivo_id = upload_file_to_dir_process_acta(col_path2, cod_usuario, centro_computo)
        archivos_columnas.append(archivo_id)

    except Exception as e:
        logger.exception(f"Error en corte por columnas observada: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    return archivos_columnas
