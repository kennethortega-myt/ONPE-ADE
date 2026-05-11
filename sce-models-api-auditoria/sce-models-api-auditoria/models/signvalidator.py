import numpy as np
import cv2
from models.imageutils import ImagesCache, ImagesLoader, cache_result, ImageFromMemory
from models.votesprefprocesor import VotesProcessorBase#, VotesPrefImageProcessor 
from util import constantes
from logger_config import logger
from util.imagen_util import mascara_dinamica

import os
class SignValidator(ImagesCache):
  def __init__(self, image_loader: ImagesLoader):
    super().__init__()
    self.image_loader = image_loader
    self.__result = None
    self.average_threshold = 180
    self.std_dev_threshold = 22

  @cache_result
  def get_average(self):
    return np.average(self.image_loader.get_image(), axis=-1)

  @cache_result
  def get_std_dev(self):
    return np.std(self.image_loader.get_image(), axis=-1)

  @cache_result
  def get_form(self):
    mask = np.logical_or(self.get_average() >= self.average_threshold,
                         self.get_std_dev() >= self.std_dev_threshold)
    filtered = self.image_loader.get_original_image().copy()
    filtered[mask] = [255, 255, 255]
    return filtered

  @cache_result
  def get_form_binary(self):
    gray_image = cv2.cvtColor(self.get_form(), cv2.COLOR_BGR2GRAY)
    _, binary_image = cv2.threshold(gray_image, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)

    height, _ = binary_image.shape[:2]
    top_mask_height = int(height * 0.25)
    binary_image[:top_mask_height, :] = 0

    return binary_image

  @cache_result
  def get_form_lines(self):
    ans = np.zeros_like(self.get_form_binary())
    lines = cv2.HoughLinesP(self.get_form_binary(), 1, np.pi / 180, 100, minLineLength=50, maxLineGap=20)

    for line in lines:
      x1, y1, x2, y2 = line[0]
      cv2.line(ans, (x1, y1), (x2, y2), (255, 255, 255), 1, cv2.LINE_AA)

    return ans

  @cache_result
  def get_content(self):
    mask = np.logical_and(self.get_average() < self.average_threshold,
                          self.get_std_dev() < self.std_dev_threshold)
    filtered = self.image_loader.get_original_image().copy()
    filtered[mask] = [255, 255, 255]
    return filtered

  @cache_result
  def get_content_binary(self):
    gray_image = cv2.cvtColor(self.get_content(), cv2.COLOR_BGR2GRAY)
    _, binary_image = cv2.threshold(gray_image, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return binary_image

  def get_average_from(self, img):
    return np.average(img, axis=-1)

  def get_std_dev_from(self, img):
      return np.std(img, axis=-1)

  def extract_data_rectangles(self, is_emc: bool, copia_a_color: bool, is_convencional: bool, is_instalacion_sufragio: bool):
    image = self.image_loader.get_image()
    height, width = image.shape[:2]

    rectangles = (
        self._extract_rectangles_emc() if is_emc 
        else self._extract_rectangles_firma(image, is_convencional, is_instalacion_sufragio)
    )

    # Fallback si no hay 4 rectángulos
    if len(rectangles) != 4:
        rectangles = self._fallback_rectangles(height, width)

    # Obtener imagen binaria filtrada
    content_binary = self._get_filtered_binary(image, copia_a_color)

    # Cortar regiones
    return self._crop_regions(rectangles, content_binary, is_convencional, is_instalacion_sufragio)

  def _extract_rectangles_emc(self):
      rectangles = []
      lines = self.get_lines_general(is_emc=True)
      if len(lines) == 4:
          x0 = lines[0][0][0]
          x1 = max(line[1][0] for line in lines)
          y0 = 0
          for line in lines:
              top_left = (x0, y0)
              bottom_right = (x1, line[0][1])
              rectangles.append((top_left, bottom_right))
              y0 = line[0][1]
      else:
          logger.info("No se detectaron suficientes lineas, usando fallback.", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
      return rectangles

  def _get_dimensiones_base(self, is_convencional, is_instalacion_sufragio):
      if is_convencional == constantes.FLUJO_CONVENCIONAL:
          return 636, 194
      if not is_instalacion_sufragio:
          return 575, 165  #Segunda vuelta
      return 435, 145

  def _extract_rectangles_firma(self, image, is_convencional, is_instalacion_sufragio):
        rectangles = []
        x1 = y1 = x2 = y2 = 0

        ANCHO_BASE, ALTO_BASE = self._get_dimensiones_base(is_convencional, is_instalacion_sufragio)
        TOL = 0.3
        ANCHO_MIN, ANCHO_MAX = ANCHO_BASE * (1 - TOL), ANCHO_BASE * (1 + TOL)
        ALTO_MIN, ALTO_MAX = ALTO_BASE * (1 - TOL), ALTO_BASE * (1 + TOL)
  
        # Cuadrado de firma
        try:
            rec_loader = ImageFromMemory(image, rotate=False)
            points = VotesProcessorBase(rec_loader).get_data_tabla(modo="firma", is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            x1, y1 = points[0]
            x2, y2 = points[1]
            ancho = x2 - x1
            alto = y2 - y1
            logger.info(f"Firma detectada ancho={ancho}, alto={alto}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info(f"Alto minimo={ALTO_MIN}, Alto maximo={ALTO_MAX}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            logger.info(f"Ancho minimo={ANCHO_MIN}, Ancho maximo={ANCHO_MAX}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            if x2 > x1 and y2 > y1:
                if (ANCHO_MIN <= ancho <= ANCHO_MAX) and (ALTO_MIN <= alto <= ALTO_MAX):
                    rectangles.append(((x1, y1), (x2, y2)))
                else:
                    raise ValueError(f"Coordenadas fuera de rango esperado (ancho={ancho}, alto={alto})")
            else:
                raise ValueError("Coordenadas invalidas")
        except Exception as e:
           logger.info(f"Fallo cuadrado firma: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
           return []

        correccion_alturas = 0
        MARGEN_INICIAL = -20
        if is_convencional == constantes.FLUJO_EXTRANJERO or is_convencional == constantes.FLUJO_STAE:
           correccion_alturas = 15
           MARGEN_INICIAL = -15

        # Rectangulos fijos
        try:
            ALTURAS = [72 - correccion_alturas, 63 - correccion_alturas, 63 - correccion_alturas]
            image_height = image.shape[0]
            espacio_necesario = y2 + MARGEN_INICIAL + sum(ALTURAS)
            if espacio_necesario > image_height:
                raise ValueError(f"Sin espacio para rectangulos fijos (y2={y2}, necesario={espacio_necesario}, alto_imagen={image_height})")
            
            current_y = y2 + MARGEN_INICIAL
            for altura in ALTURAS:
                top_left = (x1, current_y)
                bottom_right = (x2, current_y + altura)
                rectangles.append((top_left, bottom_right))
                current_y += altura
        except Exception as e:
            logger.info("Error al generar rectangulos por offset fijo: %s", e, queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)

        return rectangles

  def _fallback_rectangles(self, height, width):
      logger.info("Aplicando fallback con proporciones", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
      proportions = [0.47, 0.20, 0.15, 0.15]
      y_cursor = 0
      rectangles = []
      for p in proportions:
          y1 = y_cursor
          y2 = min(y_cursor + int(p * height), height)
          rectangles.append(((0, y1), (width, y2)))
          y_cursor = y2
      return rectangles

  def _get_filtered_binary(self, image, copia_a_color):
      hsv_img = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
      low, high = mascara_dinamica(image, copia_a_color)
      mask = cv2.inRange(hsv_img, low, high)
      binary_img = np.where(mask > 0, 255, 0).astype(np.uint8)

      contours, _ = cv2.findContours(binary_img, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
      clean_mask = np.zeros(binary_img.shape, dtype=np.uint8)
      for contour in contours:
          if cv2.contourArea(contour) >= 5:
              cv2.drawContours(clean_mask, [contour], -1, 255, thickness=cv2.FILLED)

      filtered_image = cv2.bitwise_and(binary_img, clean_mask)
      kernel = np.ones((3, 3), np.uint8)
      morphed = cv2.dilate(filtered_image, kernel, iterations=1)
      filtered_and_morphed = cv2.cvtColor(morphed, cv2.COLOR_GRAY2RGB)

      return filtered_and_morphed

  def _crop_regions(self, rectangles, content_binary, is_convencional, is_instalacion_sufragio):
      if is_convencional == constantes.FLUJO_EXTRANJERO or is_convencional == constantes.FLUJO_STAE:
         b_border = 0
         t_border = 0
      else:
         b_border = 6
         t_border = 3
      x_mult_1 = 0.21 if is_instalacion_sufragio else 0.18
      x_mult_2 = 0.22 if is_instalacion_sufragio else 0.19
      ans = []
      for i, (top_left, bottom_right) in enumerate(rectangles):
          x_start, y_start = int(top_left[0]), int(top_left[1])
          x_end, y_end = int(bottom_right[0]), int(bottom_right[1])

          height, width = y_end - y_start, x_end - x_start

          if i == 1:
              y_start += int(height * 0.35)
              x_start += int(width * x_mult_1)
          elif i == 2:
              y_start += int(height * 0.05)
              x_start += int(width * x_mult_2)
          elif i == 3:
              y_start += int(height * 0.05)
              x_start += int(width * 0.10)

          roi = content_binary[y_start:y_end, x_start:x_end]
          if i == 0:
            BORDER = 8
            BOTTOM_BORDER = 40
            h, w = roi.shape[:2]
            
            roi[:BORDER, :, :] = 0          # top
            roi[h-BOTTOM_BORDER:, :, :] = 0 # bottom
            roi[:, :BORDER, :] = 0          # left
            roi[:, w-BORDER:, :] = 0        # right
          elif i in (1, 2, 3):
            # SOLO BORDE INFERIOR
            BOTTOM_BORDER = b_border  # puedes ajustar si quieres más/menos grosor
            UP_BORDER = t_border
            h = roi.shape[0]
            roi[:UP_BORDER, :, :] = 0
            roi[h-BOTTOM_BORDER:, :, :] = 0  # bottom only
          ans.append(roi)
      return ans

  def get_lines_general(self, img=None, is_emc=False):
    is_custom = img is not None
    img = img if is_custom else self.image_loader.get_image()

    # --- Filtrado (como get_form) ---
    avg = self.get_average_from(img) if is_custom else self.get_average()
    std = self.get_std_dev_from(img) if is_custom else self.get_std_dev()
    mask = np.logical_or(avg >= self.average_threshold, std >= self.std_dev_threshold)
    filtered = img.copy()
    filtered[mask] = [255, 255, 255]

    # --- Binarizacion ---
    gray_image = cv2.cvtColor(filtered, cv2.COLOR_BGR2GRAY)
    _, binary_image = cv2.threshold(gray_image, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)

    if is_emc:
        height, _ = binary_image.shape
        binary_image[:int(height * 0.25), :] = 0

    # --- Linea base ---
    base_line_img = np.zeros_like(binary_image)
    lines = cv2.HoughLinesP(binary_image, 1, np.pi / 180, 100, minLineLength=50, maxLineGap=20)
    if lines is not None:
        for line in lines:
            x1, y1, x2, y2 = line[0]
            cv2.line(base_line_img, (x1, y1), (x2, y2), 255, 1, cv2.LINE_AA)

    # --- Reprocesar para detectar lineas ---
    min_width = img.shape[1] * 0.6
    detected_lines = cv2.HoughLinesP(base_line_img, 1, np.pi / 180, 100,
                                     minLineLength=int(min_width), maxLineGap=1)

    horizontal_lines = self._filtrar_lineas_horizontales_unified(detected_lines)
    horizontal_lines.sort(key=lambda x: x[0][1])
    return self._filtrar_lineas_unicas_unified(horizontal_lines)

  def _filtrar_lineas_horizontales_unified(self, detected_lines, angle_threshold=5):
    if detected_lines is None:
        return []

    horizontales = []

    for line in detected_lines:
        x1, y1, x2, y2 = line[0]
        dx = abs(x2 - x1)
        dy = abs(y2 - y1)

        if dx == 0:
            continue

        angle = np.degrees(np.arctan2(dy, dx))
        if angle > angle_threshold:
            continue

        p1, p2 = (x1, y1), (x2, y2)
        if x1 > x2:
            p1, p2 = p2, p1
        horizontales.append((p1, p2))
    return horizontales

  def _filtrar_lineas_unicas_unified(self, horizontal_lines, delta_y=10):
    if not horizontal_lines:
        return []

    result = [horizontal_lines[0]]

    for i in range(1, len(horizontal_lines)):
        y_actual = horizontal_lines[i][0][1]
        y_ultima = result[-1][0][1]

        if abs(y_actual - y_ultima) > delta_y:
            result.append(horizontal_lines[i])
        else:
            actual_len = horizontal_lines[i][1][0] - horizontal_lines[i][0][0]
            ultima_len = result[-1][1][0] - result[-1][0][0]
            if actual_len > ultima_len:
                result[-1] = horizontal_lines[i]
    return result

  def validate(self, is_emc, copia_a_color, is_convencional, is_instalacion_sufragio):
    if self.__result is not None:
        return self.__result

    content_rectangles = self.extract_data_rectangles(is_emc, copia_a_color, is_convencional, is_instalacion_sufragio)

    # Thresholds personalizados por campo
    threshold_sign = 0.008
    threshold_name = 0.015
    threshold_last_name = 0.01
    threshold_dni = 0.02

    thresholds = [threshold_sign, threshold_name, threshold_last_name, threshold_dni]

    for i, rect in enumerate(content_rectangles):
        ratio = np.count_nonzero(rect) / rect.size

        if ratio < thresholds[i]:
            self.__result = False
            return self.__result

    self.__result = True
    return self.__result
