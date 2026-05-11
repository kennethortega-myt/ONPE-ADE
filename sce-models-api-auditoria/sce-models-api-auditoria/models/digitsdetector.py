import db.model_integrity_state as integrity_state
from config import ENABLE_DETECTOR, ENABLE_DUAL_PREDICTION
import numpy as np
import cv2
from models.binarymodel.valid_trazo_classification import load_multiclass_model
from models.imageutils import ImagesCache, ImagesLoader, cache_result, ImageFromMemory
from models.mnistmodel.model import load_model
from util import constantes
from logger_config import logger
from sklearn.cluster import DBSCAN
import re
from models.votesprocessorbase import VotesProcessorBase
from util.imagen_util import mascara_dinamica

from models.cortesmodel.acta_base import ActaBase
from util.coordenadas_util import construir_matriz
from models.detectormodel.new_evaluate_image import evaluate_image_np_tiled
from models.detectormodel.boxes_util import evaluate_image_find_contour, _try_model_direct, validate_against_logic, process_seccion_total


class DigitsDetector(ImagesCache):
  def __init__(self, image_loader: ImagesLoader):
    super().__init__()
    self.image_loader = image_loader

    self.param_blur_kernel_size = (5, 5)
    self.lower_blue = np.array([80, 40, 50])
    self.upper_blue = np.array([180, 255, 255])

  def merge_overlapping_contours(self, contours, overlap_thresh=0.15):
    rects = [cv2.boundingRect(c) for c in contours]
    n = len(rects)
    groups = []

    def overlaps(r1, r2):
        x1, y1, w1, h1 = r1
        x2, y2, w2, h2 = r2
        dx = min(x1 + w1, x2 + w2) - max(x1, x2)
        dy = min(y1 + h1, y2 + h2) - max(y1, y2)
        if dx > 0 and dy > 0:
            overlap_area = dx * dy
            area1 = w1 * h1
            area2 = w2 * h2
            overlap_ratio = overlap_area / float(min(area1, area2))
            return overlap_ratio > overlap_thresh
        return False

    visited = set()

    def dfs(i, group):
        for j in range(n):
            if j in visited:
                continue
            if overlaps(rects[i], rects[j]):
                visited.add(j)
                group.append(rects[j])
                dfs(j, group)

    for i in range(n):
        if i in visited:
            continue
        group = [rects[i]]
        visited.add(i)
        dfs(i, group)
        groups.append(group)

    merged_boxes = []
    for group in groups:
        x_vals = [r[0] for r in group]
        y_vals = [r[1] for r in group]
        x2_vals = [r[0] + r[2] for r in group]
        y2_vals = [r[1] + r[3] for r in group]
        merged_box = (
            min(x_vals),
            min(y_vals),
            max(x2_vals) - min(x_vals),
            max(y2_vals) - min(y_vals)
        )
        merged_boxes.append(merged_box)

    return merged_boxes

  def validar_trazos_usando_clustering(self, contours):
    centers = self._calcular_centros(contours)
    if not centers:
        return []

    clusters = self._obtener_clusters(centers)
    cluster_contours = self._agrupar_por_cluster(contours, clusters)
    largest_cluster = self._obtener_cluster_mayor_area(cluster_contours)
    return self._obtener_rectangulo_cluster(cluster_contours, largest_cluster)

  def _calcular_centros(self, contours):
    return [[x + w // 2, y + h // 2] for x, y, w, h in contours]

  def _obtener_clusters(self, centers):
      centers = np.array(centers)
      dbscan = DBSCAN(eps=int(constantes.THRESHOLD_CLUSTERING_DBSCAN), min_samples=1, metric='euclidean')
      dbscan.fit(centers)
      return dbscan.labels_

  def _agrupar_por_cluster(self, contours, clusters):
      agrupados = {}
      for i, contour in enumerate(contours):
          cluster_idx = clusters[i]
          agrupados.setdefault(cluster_idx, []).append(contour)
      return agrupados

  def _obtener_cluster_mayor_area(self, cluster_contours):
      max_area = 0
      largest_cluster = None
      for cluster_idx, contornos in cluster_contours.items():
          area = sum(w * h for _, _, w, h in contornos)
          if area > max_area:
              max_area = area
              largest_cluster = cluster_idx
      return largest_cluster

  def _obtener_rectangulo_fusionado(self, contornos):
      x_vals = [x for x, _, _, _ in contornos]
      y_vals = [y for _, y, _, _ in contornos]
      x2_vals = [x + w for x, _, w, _ in contornos]
      y2_vals = [y + h for _, y, _, h in contornos]
      x = min(x_vals)
      y = min(y_vals)
      w = max(x2_vals) - x
      h = max(y2_vals) - y
      return (x, y, w, h)

  def _obtener_rectangulo_cluster(self, cluster_contours, target_idx):
      contornos = cluster_contours.get(target_idx, [])
      if not contornos:
          return None
      return self._obtener_rectangulo_fusionado(contornos)

  def _get_config_votos(self, codigo):
    return {
        constantes.COD_ELEC_SENADO_MULTIPLE: {"total": (120, 220), "votoPreferencial": (80, 180)},
        constantes.COD_ELEC_DIPUTADO: {"total": (130, 220), "votoPreferencial": (100, 180)},
        constantes.COD_ELEC_PRESIDENTE: {"total": (120, 220), "votoPreferencial": (100, 180)},
        constantes.COD_ELEC_PARLAMENTO: {"total": (140, 220), "votoPreferencial": (100, 180)},
        constantes.COD_ELEC_REVOCATORIA: {"total": (160, 180), "votoPreferencial": (100, 180)},
        constantes.COD_ELEC_SENADO_UNICO: {"total": (120, 210), "votoPreferencial": (80, 180)},
    }.get(codigo, {"total": (150, 230), "votoPreferencial": (100, 200)})

  def _get_white_pixel_threshold(self, config, is_total_votos, rows, is_convencional):
      key = "total" if is_total_votos else "votoPreferencial"
      base, max_val = config[key]
      threshold = max(base, round(max_val - (rows * 2)))
      if is_convencional == constantes.FLUJO_EXTRANJERO:
          REDUCTION_FACTOR = 0.85 
          threshold = round(threshold * REDUCTION_FACTOR)
      return threshold

  def draw_bboxes(self, image, bboxes, color=(0,255,0), thickness=2):
    """Dibuja bounding boxes en una imagen (acepta GRAY o BGR/RGB)."""
    if len(image.shape) == 2:  
        img = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
    else:
        img = image.copy()

    for (x1, y1, x2, y2) in bboxes:
        cv2.rectangle(img, (x1, y1), (x2, y2), color, thickness)

    return img
  
  def to_rgb(self, img):
    """Convierte a RGB desde GRAY o BGR sin causar errores."""
    if len(img.shape) == 2:
        return cv2.cvtColor(img, cv2.COLOR_GRAY2RGB)
    if img.shape[2] == 3:
        return cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    return img
  
  def _recortar_seccion_total_no_modelo(self, img, copia_a_color, is_convencional):
    try:
        seccion = ActaBase(img)
        grilla = seccion.obtener_grilla_votos(
            copia_a_color, is_convencional, usar_extremos=True
        )
        matriz = construir_matriz(grilla, 10, 10)

        if len(matriz) == 2 and len(matriz[0]) == 2:
            tl, tr = matriz[0]
            bl, br = matriz[1]

            x1 = min(tl[0], bl[0])
            x2 = max(tr[0], br[0])
            y1 = min(tl[1], tr[1])
            y2 = max(bl[1], br[1])

            if x2 > x1 and y2 > y1:
                crop = img[y1:y2, x1:x2]
                return crop, True, None, None

    except Exception as e:
        logger.info(f"[SIN MODELO] fallo grilla: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    # fallback CVAS
    try:
        rec_loader = ImageFromMemory(img, rotate=False)
        points = VotesProcessorBase(rec_loader).get_data_tabla(
            modo="cvas",
            is_convencional=is_convencional,
            log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )

        x1, y1 = points[0]
        x2, y2 = points[1]

        if x2 > x1 and y2 > y1:
            crop = img[y1:y2, x1:x2]
            return crop, True, None, None

    except Exception as e:
        logger.info(f"[SIN MODELO] fallo CVAS: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    return img, False, None, None

  def _recortar_seccion_total_bb(self, img, copia_a_color, is_convencional):
    """
    Si detecta la grilla: recorta la sección de TOTAL VOTOS,
    corre YOLO sobre el crop y devuelve bounding boxes globales.
    
    Si NO detecta la grilla: corre YOLO sobre toda la imagen original
    y devuelve bounding boxes globales tal cual.
    """
    external_bboxes = []
    try:
        seccion = ActaBase(img)
        processor = VotesProcessorBase(ImagesLoader())
        grilla = seccion.obtener_grilla_votos(copia_a_color, is_convencional, usar_extremos=True)
        matriz = construir_matriz(grilla, 10, 10)

        if len(matriz) == 2 and len(matriz[0]) == 2:
            tl, tr = matriz[0]
            bl, br = matriz[1]
            x1 = min(tl[0], bl[0])
            x2 = max(tr[0], br[0])
            y1 = min(tl[1], tr[1])
            y2 = max(bl[1], br[1])
            if x2 > x1 and y2 > y1:
                crop = img[y1:y2, x1:x2]
                binary_crop, _ = processor.get_binary_table(crop, copia_a_color, matriz_detec=None ,name="seccion_total")
                crop_rgb = self.to_rgb(binary_crop)
                crop_bboxes, bbox_valid = self._evaluate_bboxes_vs_contour(crop_rgb)
                for (bx1, by1, bx2, by2) in crop_bboxes:
                    external_bboxes.append(
                        (bx1 + x1, by1 + y1, bx2 + x1, by2 + y1)
                    )
                logger.info(f"[RD-DETR] {len(external_bboxes)} trazos detectados en sección TOTAL_VOTOS (crop).", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

                return crop, True, external_bboxes, crop_bboxes, bbox_valid
    except Exception as e:
        logger.info(f"Error en detección sección total: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

    try:
        ANCHO_MIN = 190
        ANCHO_MAX = 350
        ALTO_MIN = 80
        ALTO_MAX = 150
        logger.info("Intentando contramedida: detección cuadrado", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

        rec_loader = ImageFromMemory(img, rotate=False)
        points = VotesProcessorBase(rec_loader).get_data_tabla(modo="cvas", is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

        x1, y1 = points[0]
        x2, y2 = points[1]

        ancho = x2 - x1
        alto = y2 - y1

        logger.info(f"Recuadro CVAS detectado ancho={ancho}, alto={alto}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

        if not (x2 > x1 and y2 > y1):
            raise ValueError("Coordenadas de firma inválidas")

        if not (ANCHO_MIN <= ancho <= ANCHO_MAX and ALTO_MIN <= alto <= ALTO_MAX):
            raise ValueError(f"Coordenadas fuera de rango esperado (ancho={ancho}, alto={alto})")

        crop = img[y1:y2, x1:x2]
        binary_crop, _ = processor.get_binary_table(crop, copia_a_color, matriz_detec= None,name="seccion_total")
        crop_rgb = self.to_rgb(binary_crop)

        crop_bboxes, bbox_valid = self._evaluate_bboxes_vs_contour(crop_rgb)
        external_bboxes = [
            (bx1 + x1, by1 + y1, bx2 + x1, by2 + y1)
            for (bx1, by1, bx2, by2) in crop_bboxes
        ]

        logger.info(f"[RFDETR] {len(external_bboxes)} trazos detectados usando metodo get_table_points.", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        return crop, True, external_bboxes, crop_bboxes, bbox_valid

    except Exception as e:
        logger.info(f"Fallo en deteccion de recuadro CVAS: {e}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    logger.info("No se detectó grilla. Ejecutando RF-DETR sobre imagen completa.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    binary_img, _ = processor.get_binary_table(img, copia_a_color)
    img_rgb = self.to_rgb(binary_img)

    global_bboxes = evaluate_image_np_tiled(img_rgb, seccion_total=True)
    global_bboxes = process_seccion_total(global_bboxes, binary_img)
    external_bboxes = [(x1, y1, x2, y2) for x1, y1, x2, y2 in global_bboxes]

    return img, False, external_bboxes, global_bboxes, True

  def _evaluate_bboxes_vs_contour(
      self, crop_rgb, coverage_thresh=0.8, fallback_threshold=0.35
  ):
      """
      Valida bboxes del modelo contra lógica de contornos.
      Retorna (bboxes, is_valid).
      """
      try:
          bboxes_logic = evaluate_image_find_contour(crop_rgb)
          bboxes_model = evaluate_image_np_tiled(crop_rgb, seccion_total=True)

          if _try_model_direct(bboxes_logic, bboxes_model, coverage_thresh):
              logger.info("Coincidencia entre las capas logica y modelo validada con exito en CVAS.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
              return bboxes_model, True

          fallback_boxes = evaluate_image_np_tiled(
              crop_rgb,
              seccion_total=True,
              threshold=fallback_threshold,
          )

          fallback_ok = (
              fallback_boxes
              and len(fallback_boxes) >= len(bboxes_logic)
              and validate_against_logic(bboxes_logic, fallback_boxes, coverage_thresh)
          )

          if fallback_ok:
              logger.info("Coincidencia entre las capas logica y modelo validada con exito (2do intento) en CVAS.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
              return fallback_boxes, True

          logger.info("No se pudo encontrar una coincidencia entre las capas logica y modelo en CVAS.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
          return [], False

      except Exception as e:
          logger.exception(f"[evaluate_bboxes_vs_contour] Error: {e}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
          return [], False
  
  def _get_binary_image(self, img, copia_a_color):
    low, high = mascara_dinamica(img, copia_a_color)
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    mask = cv2.inRange(hsv, low, high)
    return np.where(mask > 0, 255, 0).astype(np.uint8)

  def _get_borders(self, shape, codigo, is_total, is_section, is_coord, detected, copia_a_color):
    h, w = shape[:2]
    if is_coord:
        return self._borders_coord(codigo, copia_a_color)

    if is_section:
        return self._borders_section(h, w, detected)

    if is_total:
        return self._borders_total(h, w, codigo)

    return self._borders_default(h, w)

  def _borders_coord(self, codigo, copia_a_color):
      if codigo == constantes.COD_ELEC_DISTRITAL:
          return (9, 8, 9, 9)
      return (7, 6, 7, 7) if copia_a_color else (12, 9, 12, 10)

  def _borders_section(self, h, w, detected):
      if not detected:
          logger.info("No se pudo detectar celda única, usando imagen completa", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
          return tuple(int(dim * 0.10) for dim in (h, h, w, w))
      return tuple(int(dim * 0.05) if i < 2 else int(dim * 0.02) for i, dim in enumerate((h, h, w, w)))

  def _borders_total(self, h, w, codigo):
      if codigo == constantes.COD_ELEC_REVOCATORIA:
          return tuple(int(dim * 0.025) for dim in (h, h, w, w))
      return tuple(int(dim * 0.095 if i < 2 else 0.03 * w) for i, dim in enumerate((h, h, w, w)))

  def _borders_default(self, h, w):
      return (
        max(1, int(h * 0.085)), 
        max(1, int(h * 0.015)), 
        max(1, int(w * 0.025)), 
        max(1, int(w * 0.065))
    )

  def _pintar_bordes(self, img, borders):
    top, bot, left, right = borders
    img[0:top, :] = 0
    img[-bot:, :] = 0
    img[:, 0:left] = 0
    img[:, -right:] = 0
    return img

  def _preprocesar_contornos(self, img, copia_a_color):
    _, binary = cv2.threshold(img.copy(), 128, 255, cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(binary, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    mask = np.zeros_like(binary)
    input_contorno_minimo = 3
    if copia_a_color:
       input_contorno_minimo = 5
    for c in contours:
        if cv2.contourArea(c) >= input_contorno_minimo:
            cv2.drawContours(mask, [c], -1, 255, thickness=cv2.FILLED)
    filtered = cv2.bitwise_and(binary, mask)
    kernel = np.ones((2, 2), np.uint8)
    return cv2.morphologyEx(cv2.dilate(filtered, kernel, 1), cv2.MORPH_CLOSE, kernel)

  def _filtrar_contornos(self, img, threshold, codigo, is_total_votos, copia_a_color):
    contours, _ = cv2.findContours(img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    valid = []
    h, w = img.shape
    if (
        codigo in [
            constantes.COD_ELEC_PARLAMENTO,
            constantes.COD_ELEC_DIPUTADO,
            constantes.COD_ELEC_SENADO_UNICO
        ]
        and not is_total_votos
    ):
        if copia_a_color:
            perc_h = 0.70
            perc_w = 0.85
        else:
            perc_h = 0.60
            perc_w = 0.75

    elif codigo == constantes.COD_ELEC_REVOCATORIA:
        perc_h = 0.80
        perc_w = 0.85

    else:
        perc_h = 0.90
        perc_w = 0.85
    crop_h, crop_w = int(h * perc_h), int(w * perc_w)
    crop = img[(h - crop_h) // 2:(h + crop_h) // 2, (w - crop_w) // 2:(w + crop_w) // 2]

    if cv2.countNonZero(crop) <= threshold:
        return [], False

    for c in contours:
        _, _, w, h = cv2.boundingRect(c)
        if w * h > constantes.INPUT_AREA and h > constantes.INPUT_TRAZOS_ALTURA:
            valid.append(c)
    return valid, True

  def _pad_and_resize(self, img, size):
      h, w = img.shape
      max_dim = max(h, w)
      pad_y = (max_dim - h) // 2
      pad_x = (max_dim - w) // 2
      padded = cv2.copyMakeBorder(img, pad_y, pad_y, pad_x, pad_x, cv2.BORDER_CONSTANT, value=0)
      interp = cv2.INTER_AREA if max_dim > size else cv2.INTER_LINEAR
      return cv2.resize(padded, (size, size), interpolation=interp)

  def _clasificar_numeros(self, img, boxes):
      number_img = []
      exists = False
      is_cero = True

      for x, y, w, h in boxes:
          if w * h < constantes.INPUT_AREA or h < constantes.INPUT_ALTURA:
              continue
          exists = True

          x = max(0, x)
          y = max(0, y)
          w = max(0, w)
          h = max(0, h)
          if w == 0 or h == 0:
              continue
          
          x2 = min(img.shape[1], x + w)
          y2 = min(img.shape[0], y + h)
          w = x2 - x
          h = y2 - y
          if w <= 0 or h <= 0:
              continue
          
          sub = img[y:y + h, x:x + w]
          square = self.prepare_for_mnist_input(sub)
          square_multi = self._pad_and_resize(sub, 32)
          pred, is_cero = self._predecir_digito(square, square_multi)
          x_center = x + w // 2
          number_img.append([x_center, pred])
      return number_img, exists, is_cero

  def prepare_for_mnist_input(self, img):
    img = (img > 0).astype(np.uint8) * 255
    ys, xs = np.nonzero(img > 0)
    if len(xs) == 0:
        return np.zeros((28, 28), dtype=np.uint8)
    img = img[min(ys):max(ys)+1, min(xs):max(xs)+1]
    h, w = img.shape
    scale = 20 / max(h, w)
    new_w = max(1, int(round(w * scale)))
    new_h = max(1, int(round(h * scale)))
    img = cv2.resize(img, (new_w, new_h), interpolation=cv2.INTER_AREA)
    canvas = np.zeros((28, 28), dtype=np.uint8)
    ys, xs = np.nonzero(img > 0)
    cy, cx = np.mean(ys), np.mean(xs)

    y0 = int(round(14 - cy))
    x0 = int(round(14 - cx))
    y1 = max(0, y0)
    x1 = max(0, x0)

    y2 = min(28, y0 + img.shape[0])
    x2 = min(28, x0 + img.shape[1])

    img_y1 = max(0, -y0)
    img_x1 = max(0, -x0)
    img_y2 = img_y1 + (y2 - y1)
    img_x2 = img_x1 + (x2 - x1)

    canvas[y1:y2, x1:x2] = img[img_y1:img_y2, img_x1:img_x2]

    return canvas


  def _predict_digit_core(self, img):
    dataset = np.array([img]) / 256
    dataset = dataset[..., None].astype("float32")

    model = load_model()
    raw_pred = model.predict(dataset)

    if isinstance(raw_pred, list):
        raw_pred = raw_pred[0]

    pred = np.asarray(raw_pred).reshape(-1)

    num = int(np.argmax(pred))
    prob_num = float(pred[num])

    is_cero = prob_num >= 0.93 if num == 0 else True

    return str(num), is_cero

  def _predecir_digito(self, img, square_multi):

    if ENABLE_DUAL_PREDICTION == 2:
        return self._predict_digit_core(img)

    valid_model = load_multiclass_model()
    prediction = valid_model.predict_single_image(square_multi)

    pred_spinal = prediction['predicted_class_spinal']
    pred_mobile = prediction['predicted_class_mobilenet']

    # Caso 1: parece dígito usar modelo principal
    if '0' in (pred_spinal, pred_mobile):
        return self._predict_digit_core(img)

    # Caso 2: ruido / inválido
    if '2' in (pred_spinal, pred_mobile):
        return "#", True

    # Caso 3: vacío
    return "v", True

  def _fusionar_contornos_verticales(self, merged_boxes, filtered_image):
    _, w_img = filtered_image.shape[:2]
    region_width = w_img // 3
    forced_merged_boxes = []

    for i in range(3):
        start_x = i * region_width
        end_x = start_x + region_width

        boxes_in_section = []
        for (x, y, w, h) in merged_boxes:
            center_x = x + w // 2
            if start_x <= center_x < end_x and (h > constantes.INPUT_TRAZOS_ALTURA and w * h > constantes.INPUT_AREA):
                boxes_in_section.append((x, y, w, h))

        if len(boxes_in_section) <= 1:
            forced_merged_boxes.extend(boxes_in_section)
        elif len(boxes_in_section) == 2:
            merged_box = self._obtener_rectangulo_fusionado(boxes_in_section)
            forced_merged_boxes.append(merged_box)
        else:
            merged_box = self.validar_trazos_usando_clustering(boxes_in_section)
            forced_merged_boxes.append(merged_box)

    # Solo usar cajas forzadas si hubo al menos una fusión real
    if any(len(b) > 0 for b in forced_merged_boxes):
        return forced_merged_boxes

    return merged_boxes

  def _unir_resultado(self, number_img, is_cero, pipe_implemented):
    sorted_data = sorted(number_img, key=lambda x: x[0])
    final_result = ''.join(str(char) for _, char in sorted_data)

    if integrity_state.HAY_FALLO_INTEGRIDAD_MODELOS or not pipe_implemented or ENABLE_DETECTOR == 2:
        if 'v' in final_result:
            return "#"
        return final_result

    if is_cero and re.fullmatch(r'[0vV]+', final_result):
        final_result = ""
    else:
      if 'v' in final_result:
          final_result = "#"
      else:
          final_result = final_result.lstrip('0')
          if not final_result:
              final_result = ""

    return final_result

  def _prepare_image(self, codigo_eleccion, rows, is_total_votos, is_section_total_votos,
                   is_coordenadas, copia_a_color, is_convencional):
    config = self._get_config_votos(codigo_eleccion)
    white_thr = self._get_white_pixel_threshold(config, is_total_votos, rows, is_convencional)

    img = self.image_loader.get_image()
    if img is None:
        logger.error("Error: No se pudo cargar la imagen.")
        return None, None, None, None, None, True

    apply_detected = False
    external_global = external_local = None
    bbox_detector_valid = True

    if is_section_total_votos:
        if ENABLE_DETECTOR == 1:
            img, apply_detected, external_global, external_local, bbox_detector_valid = self._recortar_seccion_total_bb(img, copia_a_color, is_convencional)
        else:
            img, apply_detected, external_global, external_local = self._recortar_seccion_total_no_modelo(img, copia_a_color, is_convencional)

    binary = self._get_binary_image(img, copia_a_color)
    borders = self._get_borders(
        binary.shape, codigo_eleccion, is_total_votos,
        is_section_total_votos, is_coordenadas,
        apply_detected, copia_a_color
    )
    binary = self._pintar_bordes(binary, borders)
    filtered = self._preprocesar_contornos(binary, copia_a_color)

    return filtered, white_thr, apply_detected, external_global, external_local, bbox_detector_valid

  def _clean_external_bboxes(self, external_bboxes, filtered_image):
      cleaned = []
      for box in external_bboxes:
          if isinstance(box, dict):
              if box.get("type") == "FRAGMENT":
                  x1, y1, x2, y2 = box["local"]
                  filtered_image[y1:y2, x1:x2] = 0
              elif box.get("type") == "LOCAL":
                  cleaned.append(box["local"])
          else:
              cleaned.append(box)
      return cleaned

  def _filter_valid_boxes(self, filtered_image, boxes):
      valid = []
      h_img, w_img = filtered_image.shape[:2]
      MIN_WHITE = 100
      for x1, y1, x2, y2 in boxes:
          x1e = max(0, x1)
          y1e = max(0, y1)
          x2e = min(w_img, x2)
          y2e = min(h_img, y2)
          if x2e <= x1e or y2e <= y1e:
              continue
          crop = filtered_image[y1e:y2e, x1e:x2e]
          if crop.size == 0:
              continue
          if np.sum(crop == 255) >= MIN_WHITE:
              valid.append((x1, y1, x2e - x1e, y2e - y1e))
      return valid

  def _center_fallback_check(self, filtered_image):
      h, w = filtered_image.shape[:2]
      ch, cw = int(h * 0.7), int(w * 0.75)
      y1 = (h - ch) // 2
      y2 = (h + ch) // 2
      x1 = (w - cw) // 2
      x2 = (w + cw) // 2
      crop = filtered_image[y1:y2, x1:x2]
      if crop.size == 0:
          return False
      return (np.count_nonzero(crop) / crop.size) >= 0.02

  def _legacy_pipeline_boxes(self, filtered_image, white_thr, codigo_eleccion, is_total_votos, copia_a_color):
      if is_total_votos and not copia_a_color:
          h, w = filtered_image.shape[:2]
          PAD = 8
          xs = [
              int(PAD + (w - 2 * PAD) / 3),
              int(PAD + 2 * (w - 2 * PAD) / 3)
          ]
          for x in xs:
              cv2.line(filtered_image, (x, 0), (x, h), 0, 8)

      contours, has_content = self._filtrar_contornos(
          filtered_image, white_thr, codigo_eleccion, is_total_votos, copia_a_color
      )

      if is_total_votos:
          boxes = self.merge_overlapping_contours(contours)
          return self._fusionar_contornos_verticales(boxes, filtered_image), has_content

      boxes = [cv2.boundingRect(c) for c in contours]
      return boxes, has_content

  def _run_pipe_pipeline(self,filtered_image,external_bboxes,is_section_total_votos,apply_detected,ext_global,ext_local):
    if external_bboxes is not None:
        external_bboxes = self._clean_external_bboxes(
            external_bboxes, filtered_image
        )

    if is_section_total_votos:
        external_bboxes = ext_local if apply_detected else ext_global

    return self._filter_valid_boxes(filtered_image, external_bboxes or [])

  def alternate_prepare_image_no_prediction(self, codigo_eleccion, rows, is_total_votos, is_section_total_votos,
                   is_coordenadas, copia_a_color, is_convencional):
        config = self._get_config_votos(codigo_eleccion)
        white_thr = self._get_white_pixel_threshold(config, is_total_votos, rows, is_convencional)

        img = self.image_loader.get_image()
        if img is None:
            logger.error("Error: No se pudo cargar la imagen.")
            return None, None, None, None, None

        apply_detected = False
        external_global = external_local = None

        if is_section_total_votos:
            if ENABLE_DETECTOR == 2:
                img, apply_detected, external_global, external_local = self._recortar_seccion_total_no_modelo(img, copia_a_color, is_convencional)
            else:
                img, apply_detected, external_global, external_local = self._recortar_seccion_total_bb(img, copia_a_color, is_convencional)

        binary = self._get_binary_image(img, copia_a_color)
        borders = self._get_borders(
            binary.shape, codigo_eleccion, is_total_votos,
            is_section_total_votos, is_coordenadas,
            apply_detected, copia_a_color
        )
        binary = self._pintar_bordes(binary, borders)
        filtered = self._preprocesar_contornos(binary, copia_a_color)

        return filtered, white_thr, apply_detected, external_global, external_local
  
  def get_number_no_prediction(self, rows, codigo_eleccion, is_total_votos,is_section_total_votos, 
                 is_coordenadas, copia_a_color, pipe_implemented, 
                 external_bboxes=None, is_convencional = constantes.FLUJO_CONVENCIONAL,
                 cell_pipe_implemented = True):
      
      filtered_image, white_thr, apply_detected, ext_global, ext_local = self.alternate_prepare_image_no_prediction(codigo_eleccion, rows,is_total_votos, is_section_total_votos, 
                                                                                             is_coordenadas, copia_a_color, is_convencional)
      merged_boxes = []
      use_pipe = (
          pipe_implemented 
          and ENABLE_DETECTOR == 1
      )

      if use_pipe:
          merged_boxes = self._run_pipe_pipeline(filtered_image,external_bboxes,is_section_total_votos,apply_detected,ext_global,ext_local)
          if not merged_boxes and self._center_fallback_check(filtered_image):
              return "#"
      else:
          merged_boxes, _ = self._legacy_pipeline_boxes(filtered_image, white_thr,codigo_eleccion, is_total_votos, copia_a_color)

      if merged_boxes:
          return "#"
      return ""

  def get_number(self, rows, codigo_eleccion, is_total_votos,is_section_total_votos, 
                 is_coordenadas, copia_a_color, pipe_implemented, 
                 external_bboxes=None, is_convencional = constantes.FLUJO_CONVENCIONAL,
                 cell_pipe_implemented = True):
      
      try:
          filtered_image, white_thr, apply_detected, ext_global, ext_local, bbox_detector_valid = self._prepare_image(codigo_eleccion, rows,is_total_votos, is_section_total_votos,
                                                                                               is_coordenadas, copia_a_color, is_convencional)
      except Exception:
          return "#"
      if filtered_image is None:
          return "#"
      if is_section_total_votos and not bbox_detector_valid:
          cell_pipe_implemented = False
      merged_boxes = []
      use_pipe = (
          cell_pipe_implemented 
          and pipe_implemented 
          and not integrity_state.HAY_FALLO_INTEGRIDAD_MODELOS
      )

      if use_pipe:
          merged_boxes = self._run_pipe_pipeline(filtered_image,external_bboxes,is_section_total_votos,apply_detected,ext_global,ext_local)
          has_content = self._center_fallback_check(filtered_image)
          if not merged_boxes and has_content:
              return "#"
      else:
          merged_boxes, has_content = self._legacy_pipeline_boxes(filtered_image, white_thr,codigo_eleccion, is_total_votos, copia_a_color)
      number_img, exists, is_cero = self._clasificar_numeros(filtered_image, merged_boxes)
      result = self._unir_resultado(number_img, is_cero, pipe_implemented)
      if is_section_total_votos and not exists:
          return "N"
      if has_content and not exists:
          return "#"
      if "#" in result:
          return "#"
      return result
