import numpy as np
from models.imageutils import ImagesCache, ImagesLoader, ImageFromMemory
import cv2
from logger_config import logger
import os
from util.coordenadas_util import obtener_coordenadas_extremos_tl_tr_br_bl
from util import constantes
from models.tipo_acta import TipoActa
from util.imagen_util import extraer_esquinas_tl_tr_br_bl_marcadores, get_marcadores, corregir_imagen, extraer_franjas_t_r_l_marcadores, _preprocesar_imagen_marcadores, detect_flujo_by_dimensions
from util.coordenadas_util import construir_matriz, validar_matriz_marcadores

EPS = 0.01
VECTORS = np.array([[-1, -1], [1, -1], [1, 1], [-1, 1]])
GLOBAL_PAD_MARCADOR = 35

class ActaReferencePoints(ImagesCache):
  def __init__(self, image_loader: ImagesLoader, tipo_acta : TipoActa = None, is_convencional: str = constantes.FLUJO_CONVENCIONAL):
    super().__init__()
    self.image_loader = image_loader
    self.is_convencional = is_convencional
    self._valida_imagen()
    self._template_marcador = None
    self._modo_horizontal = False
    self._tipo_acta = tipo_acta
    self._verifica_si_horiztonal()
    self._imagen_corregida = None
    self._marcadores_esquinas = None
    self._marcadores_all = None
    self._last_centers = None
    self.h, self.w = self.image_loader.get_image().shape[:2]
    self.kernel_size = (5, 5)
    if self.is_convencional == constantes.FLUJO_STAE:
        self.cs_sz = self.w // 10
    else:
        self.cs_sz = self.w // 15
    if self.is_convencional == constantes.FLUJO_EXTRANJERO:
       self.adjust_flag_method_1 = True
    else:
       self.adjust_flag_method_1 = False
    self.black_lower = np.array([0, 0, 0])
    self.black_upper = np.array([180, 255, 135])

  def _valida_imagen(self):
    if self.image_loader is None:
      raise ValueError("No se ha proporcionado un cargador de imágenes válido.")

    image = self.image_loader.get_image()
    if image is None:
        source = getattr(self.image_loader, 'source', None) or getattr(self.image_loader, 'path', None)
        detail = f" | fuente={source}" if source else ""
        raise ValueError(f"ActaReferencePoints: cv2.imread devolvió None |{detail}| — archivo inexistente, corrupto o bloqueado.")

  def _verifica_si_horiztonal(self):
     h, w = self.image_loader.get_image().shape[:2]
     if self._tipo_acta is not None and self._tipo_acta.get_es_horizontal() and h > w and self.is_convencional != constantes.FLUJO_STAE:
        self._modo_horizontal = True
        self.image_loader = ImageFromMemory(cv2.rotate(self.image_loader.get_image(), cv2.ROTATE_90_CLOCKWISE))

  def _get_template_marcador(self):
    if self._template_marcador is None:
      BASE_DIR = os.path.dirname(os.path.abspath(__file__))
      UTILS_DIR = os.path.join(os.path.dirname(BASE_DIR), "util")
      template_path = os.path.join(UTILS_DIR, "template_blanco_36x36.png")
      self._template_marcador = cv2.imread(template_path, 0)
    return self._template_marcador

  def _get_template_marcador_corregido(self, inverted = False):
    """
    Obtiene el template de los marcadores corregido en base a la altura de la acta.
    """
    if inverted:
      template = self._get_template_marcador_negro()
    else:
      template = self._get_template_marcador()
    template_resized_flag = False
    # Si la altura de la acta es diferente a la altura por defecto, se debe corregir el template en base a la proporcion.
    if abs(self.h - constantes.ACTA_HEIGHT_DEFAULT) > 100:
        proporcion = self.h / constantes.ACTA_HEIGHT_DEFAULT
        new_template_w = int(template.shape[0] * proporcion)
        new_template_h = int(template.shape[1] * proporcion)
        new_template = cv2.resize(template, (new_template_w, new_template_h))
        template = new_template
        template_resized_flag = True
    return template, template_resized_flag

  def get_squares_normal(self, is_convencional, log_queue = "default"):
    """
      Flujo estándar (actas NO observadas).
    """
    logger.info("Ejecutando get_squares...", prod=True, queue = log_queue)
    if self._marcadores_esquinas is None:
        corners = extraer_esquinas_tl_tr_br_bl_marcadores(
            self.image_loader.get_image(), self.cs_sz, self.kernel_size,
            self.black_lower, self.black_upper
        )
        template = self._get_template_marcador()
        template_resized_flag = False
        if is_convencional == constantes.FLUJO_EXTRANJERO or is_convencional == constantes.FLUJO_STAE:
          template, template_resized_flag = self._get_template_marcador_corregido()
          if template_resized_flag:
              logger.warning("Template corregido en base a la altura de la acta.", queue = log_queue)
        centers = []

        for indice, corner in enumerate(corners):
            offset_x = 0 if indice in {0, 3} else self.w - self.cs_sz
            offset_y = 0 if indice in {0, 1} else self.h - self.cs_sz
            corner_square = get_marcadores(corner, template, EPS, offset_x, offset_y, template_resized_flag=template_resized_flag, adjust_flag_method_1 = self.adjust_flag_method_1)
            self._verificar_marcador(corner_square, indice)
            centers.extend(corner_square)
        self._marcadores_esquinas = obtener_coordenadas_extremos_tl_tr_br_bl(centers)
    return self._marcadores_esquinas

  def _verificar_marcador(self, corner_square, indice):
    if not corner_square:
      posiciones = {
          0: "SUPERIOR IZQUIERDA",
          1: "SUPERIOR DERECHA",
          2: "INFERIOR DERECHA",
          3: "INFERIOR IZQUIERDA"
      }
      raise ValueError(f"Error: no se encontró marcador en la parte {posiciones.get(indice, 'DESCONOCIDA')}")

  def get_imagen_corregida(self, is_convencional, log_queue="default", image_loader_override=None):
    logger.info("Ejecutando get_imagen_corregida...", queue=log_queue)

    if image_loader_override is not None:
        original_loader = self.image_loader
        self.image_loader = image_loader_override
        self._marcadores_esquinas = None
        self._imagen_corregida = None
        self.h, self.w = self.image_loader.get_image().shape[:2]
    try:
        if self._imagen_corregida is None:
            square_coords = self.get_squares_normal(is_convencional, log_queue)

            expand_pixels = GLOBAL_PAD_MARCADOR
            expanded_coords = [
                [square_coords[0][0] - expand_pixels, square_coords[0][1] - expand_pixels],
                [square_coords[1][0] + expand_pixels, square_coords[1][1] - expand_pixels],
                [square_coords[2][0] + expand_pixels, square_coords[2][1] + expand_pixels],
                [square_coords[3][0] - expand_pixels, square_coords[3][1] + expand_pixels]
            ]

            tl, tr, br, bl = expanded_coords
            self._imagen_corregida = corregir_imagen(
                self.image_loader.get_image(), None, None, tl, tr, br, bl
            )
        return self._imagen_corregida, square_coords

    finally:
        if image_loader_override is not None:
            self.image_loader = original_loader

  def get_squares_all(self, is_convencional, already_align = False, log_queue="default"):
    logger.info("Ejecutando get_squares_all...", prod=True, queue=log_queue)

    if self._marcadores_all is not None:
        return self._marcadores_all

    if not already_align:
        if is_convencional == constantes.FLUJO_STAE:
            self._imagen_corregida = self.image_loader.get_image()
        else:
            if self._imagen_corregida is None:
                self.get_imagen_corregida(is_convencional, log_queue)

    corners = extraer_franjas_t_r_l_marcadores(
        self._imagen_corregida,
        GLOBAL_PAD_MARCADOR * 2,
        self.kernel_size,
        self.black_lower,
        self.black_upper
    )

    template, template_resized_flag = self._resolve_template(is_convencional, log_queue)
    centers = self._detect_markers(corners, template, template_resized_flag, is_convencional)

    if not centers:
        raise ValueError("Error: No se encontraron marcadores después de la corrección.")

    h_tpl, w_tpl = template.shape[:2]
    self._marcadores_all = construir_matriz(centers, w_tpl, h_tpl)
    return self._marcadores_all
  
  def _resolve_template(self, is_convencional, log_queue):
    template = self._get_template_marcador()
    template_resized_flag = False

    logger.info(f"self._modo_horizontal={self._modo_horizontal}", queue=log_queue)

    if (
        is_convencional in {constantes.FLUJO_EXTRANJERO, constantes.FLUJO_STAE}
        and not self._modo_horizontal
    ):
        template, template_resized_flag = self._get_template_marcador_corregido()

    return template, template_resized_flag
  
  def _detect_markers(self, corners, template, template_resized_flag, is_convencional):
    _, w_img = self._imagen_corregida.shape[:2]
    centers = []

    for indice, corner in enumerate(corners):
        offset_x = 0 if indice in {0, 2} else w_img - GLOBAL_PAD_MARCADOR * 2

        square_all_flag = (
            self._modo_horizontal
            and is_convencional == constantes.FLUJO_EXTRANJERO
        )

        centers.extend(
            get_marcadores(corner,template,EPS,offset_x,0,True,True,
                           template_resized_flag=template_resized_flag,square_all_flag=square_all_flag,indice=indice,
                           adjust_flag_method_1 = self.adjust_flag_method_1
                           )
                      )
    return centers
  
  def _validar_marcadores_vs_proporcion(self, coords_detectados, w, h, log_queue = "default"):
    logger.info("Ejecutando validar_marcadores_vs_proporcion...", queue = log_queue)
    """
    Validación robusta:
    - Geometría mínima (ancho/alto)
    """
    coords_detectados = coords_detectados.astype(np.float32)
    x_coords = coords_detectados[:, 0]
    y_coords = coords_detectados[:, 1]

    ancho = np.max(x_coords) - np.min(x_coords)
    alto  = np.max(y_coords) - np.min(y_coords)

    ancho_rel = ancho / w
    alto_rel  = alto / h

    logger.info(f"ancho_rel={ancho_rel:.4f}", queue=log_queue)
    logger.info(f"alto_rel={alto_rel:.4f}", queue=log_queue)

    if ancho_rel < 0.5:  #colapsado horizontalmente
        return False

    if alto_rel < 0.5:   #colapsado verticalmente
        return False

    # Verificar que ningún lado del cuadrilatero este colapsado
    top_left, top_right, bottom_right, bottom_left = coords_detectados

    top_width_rel    = (top_right[0]    - top_left[0])    / w
    bottom_width_rel = (bottom_right[0] - bottom_left[0]) / w
    left_height_rel  = (bottom_left[1]  - top_left[1])    / h
    right_height_rel = (bottom_right[1] - top_right[1])   / h

    logger.info(f"top_width_rel={top_width_rel:.4f} bottom_width_rel={bottom_width_rel:.4f}", queue=log_queue)
    logger.info(f"left_height_rel={left_height_rel:.4f} right_height_rel={right_height_rel:.4f}", queue=log_queue)

    if top_width_rel < 0.5:
        logger.warning("Esquinas superiores colapsadas (top_width insuficiente)", queue=log_queue)
        return False
    if bottom_width_rel < 0.5:
        logger.warning("Esquinas inferiores colapsadas (bottom_width insuficiente)", queue=log_queue)
        return False
    if left_height_rel < 0.5:
        logger.warning("Lado izquierdo colapsado (left_height insuficiente)", queue=log_queue)
        return False
    if right_height_rel < 0.5:
        logger.warning("Lado derecho colapsado (right_height insuficiente)", queue=log_queue)
        return False

    # Verificar que cada punto esté dentro de la region de su esquina,
    pad = self.cs_sz
    corner_checks = [
        (top_left[0] < pad, top_left[1] < pad, "top_left"),
        (top_right[0] > w - pad, top_right[1] < pad, "top_right"),
        (bottom_right[0] > w - pad, bottom_right[1] > h - pad, "bottom_right"),
        (bottom_left[0] < pad, bottom_left[1] > h - pad, "bottom_left"),
    ]
    for in_x, in_y, name in corner_checks:
        if not in_x or not in_y:
            logger.warning(f"Marcador '{name}' fuera de su region de esquina (cs_sz={pad})", queue=log_queue)
            return False

    logger.info("Validacion de integridad de marcadores exitosa...", queue = log_queue)
    return True

  def validar_marcadores_all(self, is_convencional, log_queue = "default"):
    logger.info("Ejecutando validar_marcadores_all...", prod=True, queue = log_queue)
    if self._tipo_acta is None:
      raise ValueError("Este método require TipoActa en el constructor.")
    matriz_marcadores = self.get_squares_all(is_convencional, log_queue = log_queue)
    try:
      validar_matriz_marcadores(self._tipo_acta.get_template_marcadores(), matriz_marcadores)
    except Exception:
      validar_matriz_marcadores(self._tipo_acta.get_template_marcadores(True), matriz_marcadores)

  def obtener_coordenadas_extremas(self, marcadores_in_regions, w, h, log_queue, margen_inferior=120, marge_derecha=150):
    """
    Obtiene las 4 coordenadas extremas de los marcadores en la imagen.
    """
    marcadores = [marcador for marcadores in marcadores_in_regions for marcador in marcadores]

    if not marcadores:
        logger.warning("No se detectaron marcadores en ninguna región. Usando proporciones globales.", queue = log_queue)
        proporciones = [
            (0.0230722525804493, 0.01840250587314017),   # top_left
            (0.9817850637522769, 0.014291307752545028),  # top_right
            (0.9872495446265938, 0.9825763508222396),    # bottom_right
            (0.02853673345476624, 0.9845340642129993),   # bottom_left
        ]
        return np.array([(int(w * px), int(h * py)) for px, py in proporciones], dtype=np.int32)
    
    if not marcadores_in_regions[0]:  
        logger.warning("No se detectaron marcadores en la parte superior. Usando proporciones relativas para top.", queue = log_queue)
        proporciones_top = [
            (0.0230722525804493, 0.01840250587314017),   # top_left
            (0.9817850637522769, 0.014291307752545028),  # top_right
        ]
        top_left  = (int(w * proporciones_top[0][0]), int(h * proporciones_top[0][1]))
        top_right = (int(w * proporciones_top[1][0]), int(h * proporciones_top[1][1]))

        xs = [x for x,y in marcadores]
        ys = [y for x,y in marcadores]
        x_min, x_max = min(xs), max(xs)
        y_max = max(ys)

        bottom_right = [x_max, y_max]
        bottom_left  = [x_min, y_max]

        return np.array([top_left, top_right, bottom_right, bottom_left], dtype=np.int32)

    x_min = min([x for x,y in marcadores])
    x_max = max([x for x,y in marcadores])
    x_max = x_max if x_max > w - marge_derecha else w - marge_derecha//3 # Si no encuentra en la parte derecha
    y_min = min([y for x,y in marcadores])
    y_max = max([y for x,y in marcadores]) 
    y_max = y_max if y_max > h - margen_inferior else h - margen_inferior//2 # Si no encuentra en la parte inferior
    top_left     = [x_min, y_min]
    top_right    = [x_max, y_min]
    bottom_right = [x_max, y_max]
    bottom_left  = [x_min, y_max]
    return np.array([top_left, top_right, bottom_right, bottom_left], dtype=np.int32)

  def _get_template_marcador_negro(self):
    """
    Obtiene el template de los marcadores negros.
    """
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    UTILS_DIR = os.path.join(os.path.dirname(BASE_DIR), "util")
    template_path = os.path.join(UTILS_DIR, "marcador_36x36.png")
    template_marcador = cv2.imread(template_path, 0)
    return template_marcador

  def get_all_squares_por_regiones_de_busqueda(self, img, regiones_de_busqueda, is_convencional):
    """
      Flujo estándar (actas NO observadas).
    """
    template = self._get_template_marcador()
    template_resized_flag = False
    if is_convencional == constantes.FLUJO_EXTRANJERO or is_convencional == constantes.FLUJO_STAE:
      template, template_resized_flag = self._get_template_marcador_corregido(inverted=False)
    centers = []
    for region in regiones_de_busqueda:

        (x1, y1), (x2, y2) = region

        region_img = img[y1:y2, x1:x2]

        processed_regions = _preprocesar_imagen_marcadores(
            [region_img],
            self.kernel_size,
            self.black_lower,
            self.black_upper
        )

        region_proc = processed_regions[0]

        offset_x = 0
        offset_y = 0
        corner_square = get_marcadores(
            region_proc,
            template,
            EPS,
            offset_x,
            offset_y,
            metodo_2=False,
            template_resized_flag=template_resized_flag,
            square_all_flag=True,
            adjust_flag_method_1 = self.adjust_flag_method_1
        )

        centers.append(corner_square)
    return centers

  def aplicar_offsets_laterales(self, marcadores, regiones):
    """
    Aplica offsets según el origen (x0, y0) de cada región.
    Convierte coordenadas locales → coordenadas globales.
    """
    marcadores = [list(region) for region in marcadores]

    for i in range(len(marcadores)):
        if not marcadores[i]:
            continue

        x0, y0 = regiones[i][0]

        # Solo aplicar si hay desplazamiento real
        if x0 != 0 or y0 != 0:
            marcadores[i] = [[x + x0, y + y0] for x, y in marcadores[i]]

    return marcadores

  def calcular_angulo_vertical(self, markers):
    if len(markers) < 2:
        return None

    markers = sorted(markers, key=lambda p: p[1])
    (x1, y1), (x2, y2) = markers[0], markers[-1]

    if abs(y2 - y1) < 10:
        return None

    slope_angle = np.arctan2(y2 - y1, x2 - x1)
    return slope_angle - (np.pi / 2)

  def es_columna_estable(self, markers, max_std_x=20):
    if len(markers) < 2:
        return False
    xs = [pt[0] for pt in markers]
    return np.std(xs) < max_std_x

  def es_fila_estable(self, markers, max_std_y=25):
    if len(markers) < 2:
        return False
    ys = [pt[1] for pt in markers]
    return np.std(ys) < max_std_y

  def align_image_with_squares_observada(self, image_loader_2, is_convencional, proc_imagen_corregida = True, log_queue="default"):
    logger.info("Ejecutando align_image_with_squares_observada...", queue=log_queue)

    img = image_loader_2.get_image()
    h, w = img.shape[:2]
    obs_corregido = False

    if proc_imagen_corregida:
      try:
          logger.info("Intentando contramedida con get_imagen_corregida...", queue=log_queue)
          aligned_image, square_coords = self.get_imagen_corregida(
              is_convencional,
              log_queue,
              image_loader_override=image_loader_2
          )
          obs_corregido = True
          return aligned_image, square_coords, obs_corregido
      except Exception as e:
          logger.error(
              f"Fallback activado: get_imagen_corregida falló => {str(e)}",
              queue=log_queue
          )

    x1, y1, x2, y2, marcadores_top = self._obtener_puntos_top(img, w, is_convencional, log_queue)
    horizontal_angle = self._calcular_angulo_horizontal(marcadores_top, x1, y1, x2, y2, log_queue)
    rotated = self._aplicar_rotacion_si_valida(img, horizontal_angle)

    rotated = self._corregir_vertical(rotated, w, h, is_convencional, log_queue)
    marcadores_esquinas = self._obtener_esquinas_finales(rotated, w, h, is_convencional, log_queue)
    return rotated, marcadores_esquinas, obs_corregido
  
  def _obtener_puntos_top(self, img, w, is_convencional, log_queue):
    margen_top = 200
    regiones = [[(0, 0), (w, margen_top)]]
    marcadores = self.get_all_squares_por_regiones_de_busqueda(img, regiones, is_convencional)[0]

    if not marcadores:
        logger.warning("No se detectaron marcadores superiores. Usando fallback.",queue=log_queue)
        return 0, margen_top // 2, w, margen_top // 2, []

    id_min = np.argmin([x for x, _ in marcadores])
    id_max = np.argmax([x for x, _ in marcadores])

    x1, y1 = marcadores[id_min]
    x2, y2 = marcadores[id_max]

    return x1, y1, x2, y2, marcadores
  
  def _calcular_angulo_horizontal(self, marcadores, x1, y1, x2, y2, log_queue):
    if not marcadores or not self.es_fila_estable(marcadores):
        logger.warning("TOP descartado por inestabilidad en Y", queue=log_queue)
        return None

    angle = np.arctan2(y2 - y1, x2 - x1)
    deg = np.degrees(angle)

    logger.info(f"Ángulo horizontal detectado: {deg:.3f}°", queue=log_queue)

    if abs(deg) > 2.0:
        logger.warning(f"Ángulo horizontal descartado ({deg:.2f}°)",queue=log_queue,)
        return None
    return angle
  
  def _corregir_vertical(self, rotated, w, h, is_convencional, log_queue):
    regiones = [
        [(0, 0), (w, 200)],
        [(0, 0), (200, h)],
        [(w - 200, 0), (w, h)],
        [(0, h - 150), (w, h)],
    ]
    markers = self.get_all_squares_por_regiones_de_busqueda(
        rotated, regiones, is_convencional
    )
    markers = self.aplicar_offsets_laterales(markers, regiones)
    left = markers[1]
    right = markers[2]

    angle = self._calcular_angulo_vertical_promedio(left, right, log_queue)
    return self._aplicar_rotacion_si_valida(rotated, angle, max_deg=1.0)
  
  def _calcular_angulo_vertical_promedio(self, markers_left, markers_right, log_queue):
    angles = []
    if (
        markers_left
        and self.es_columna_estable(markers_left)
        and (a := self.calcular_angulo_vertical(markers_left)) is not None
    ):
        angles.append(a)
    else:
        logger.warning("LEFT descartado", queue=log_queue)

    if (markers_right and self.es_columna_estable(markers_right)
        and (a := self.calcular_angulo_vertical(markers_right)) is not None):
        angles.append(a)
    else:
        logger.warning("RIGHT descartado", queue=log_queue)

    if not angles:
        logger.warning("NO se pudo calcular ángulo vertical.", queue=log_queue)
        return None

    return float(np.mean(angles))
  
  def _aplicar_rotacion_si_valida(self, image, angle, max_deg=2.0):
    if angle is None:
        return image

    deg = np.degrees(angle)

    if abs(deg) > max_deg or abs(angle) < 0.0005:
        return image

    return rotate_image(image, angle)
  
  def _obtener_esquinas_finales(self, rotated, w, h, is_convencional, log_queue):
    regiones = [
        [(0, 0), (w, 200)],            # top
        [(0, 0), (200, h)],            # left
        [(w - 200, 0), (w, h)],        # right
        [(0, h - 150), (w, h)],        # bottom
    ]

    marcadores = self.get_all_squares_por_regiones_de_busqueda(rotated, regiones, is_convencional)
    marcadores = self.aplicar_offsets_laterales(marcadores, regiones)
    marcadores_esquinas = self.obtener_coordenadas_extremas(marcadores, w, h, log_queue)

    es_valido = self._validar_marcadores_vs_proporcion(marcadores_esquinas, w, h, log_queue)

    if not es_valido:
        logger.warning("Marcadores inválidos == usando proporciones globales", queue=log_queue)

        proporciones = [
            (0.0230722525804493, 0.01840250587314017),
            (0.9817850637522769, 0.014291307752545028),
            (0.9872495446265938, 0.9825763508222396),
            (0.02853673345476624, 0.9845340642129993),
        ]

        marcadores_esquinas = np.array([
            (int(w * px), int(h * py)) for px, py in proporciones
        ], dtype=np.int32)
        
    return marcadores_esquinas

  def align_image_with_squares(self, image_loader, acta_observada, is_convencional, log_queue = "default"):
    """
    Alinea la imagen utilizando los 4 cuadrados de referencia detectados en get_squares().
    """
    obs_corregida = False
    if acta_observada:
      aligned_image, square_coords, obs_corregida = self.align_image_with_squares_observada(image_loader_2=image_loader, is_convencional=is_convencional, log_queue=log_queue)
      return aligned_image, square_coords, obs_corregida
    
    try:
        aligned_image, square_coords = self.get_imagen_corregida(is_convencional,log_queue)
        logger.info(f"Valor de square_coords = {square_coords}", queue = log_queue)
    except (AssertionError, ValueError):
        logger.info("No se encontraron los 4 puntos de referencia.", queue = log_queue)
        return image_loader.get_original_image(), None, None

    if len(square_coords) != 4:
        logger.info("No se detectaron exactamente 4 puntos de referencia.", queue = log_queue)
        return image_loader.get_original_image(), None, None
    return aligned_image, square_coords, obs_corregida
  
  def usar_proporciones(self):
        logger.warning("Usando marcadores artificiales por proporciones", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        h, w = self.h, self.w
        proporciones = [
            (0.0230722525804493, 0.01840250587314017),   # top_left
            (0.9817850637522769, 0.014291307752545028),   # top_right
            (0.9872495446265938, 0.9825763508222396),   # bottom_right
            (0.02853673345476624, 0.9845340642129993),   # bottom_left
        ]
        self._marcadores_esquinas = np.array([
            (int(w * px), int(h * py)) for px, py in proporciones
        ])

def rotate_image(image, angle):
  # create rotation matrix
  h, w = image.shape[:2]
  center = (w // 2, h // 2)

  # negative angle for counter-clockwise rotation
  rotation_matrix = cv2.getRotationMatrix2D(center, np.degrees(angle), 1)

  # perform rotation
  corrected_image = cv2.warpAffine(image, rotation_matrix, (w, h))
  return corrected_image

def compute_relative_coordinates(image_loader, rectangles, is_convencional):
  logger.info("Ejecutando compute_relative_coordinates...")
  model = ActaReferencePoints(image_loader, is_convencional=is_convencional)

  top_left, top_right, _, bottom_left = model.get_squares_normal(is_convencional)


  vx = top_right - top_left
  vy = bottom_left - top_left

  ans = []
  for name, rec_top_left, rec_bottom_right in rectangles:
    p1 = rec_top_left - top_left
    p2 = rec_bottom_right - top_left

    # compute projection of p1 over vx and vy, use long precision to avoid overflow
    x1 = np.clip(np.dot(p1, vx) / np.dot(vx, vx), 0, 1)
    y1 = np.clip(np.dot(p1, vy) / np.dot(vy, vy), 0, 1)

    x2 = np.clip(np.dot(p2, vx) / np.dot(vx, vx), 0, 1)
    y2 = np.clip(np.dot(p2, vy) / np.dot(vy, vy), 0, 1)


    relative_rec_top_left = (x1, y1)
    relative_rec_bottom_right = (x2, y2)

    ans.append((name, relative_rec_top_left, relative_rec_bottom_right))

  return ans

def line_intersection(point1, direction_vector1, point2, direction_vector2):
  # Formulate the system of equations
  a = np.array([direction_vector1, -direction_vector2]).T
  b = np.array(point2 - point1)

  # Use np.linalg.solve to solve for the parameters
  try:
    t = np.linalg.solve(a, b)
  except np.linalg.LinAlgError:
    # If the lines are parallel or identical
    return None

  # If solutions exist, return the intersection point
  intersection_point = point1 + t[0] * direction_vector1
  return intersection_point

def acta_horizontal_girada(acta_type, codigo_eleccion, is_convencional, acta_observada):
   if codigo_eleccion is not None: codigo_eleccion = str(codigo_eleccion)
   return (codigo_eleccion and acta_type) and (acta_type in [constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL,constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO]
      and codigo_eleccion in [constantes.COD_ELEC_DIPUTADO,constantes.COD_ELEC_SENADO_UNICO]
      and is_convencional != constantes.FLUJO_STAE
      and not acta_observada
      )

def extract_rectangles(image_loader, rectangles, acta_observada, is_convencional, 
                       square_coords_alineados, acta_type, codigo_eleccion, obs_corregida = False, 
                       log_queue = "default"):
  logger.info("Empieza el metodo extract_rectangles...", queue = log_queue)
  image = image_loader.get_image()
  if image is None:
        source = getattr(image_loader, 'source', None) or getattr(image_loader, 'path', None)
        raise ValueError(
            f"extract_rectangles: imagen no cargada"
            f"{f' | fuente={source}' if source else ''} "
            f"— verifique que el archivo existe y no está bloqueado."
        )
  if acta_horizontal_girada(acta_type, codigo_eleccion, is_convencional, acta_observada):
        image = cv2.rotate(image, cv2.ROTATE_90_COUNTERCLOCKWISE)
        image_loader.set_image(image)

  if is_convencional == constantes.FLUJO_CONVENCIONAL:
    is_convencional = detect_flujo_by_dimensions(image_loader.get_image())
  model = ActaReferencePoints(image_loader, is_convencional=is_convencional)
  if not acta_observada:
      try:
          coordenadas_marcadores = model.get_squares_normal(is_convencional,log_queue)
          top_left, top_right, _, bottom_left = coordenadas_marcadores
      except Exception as e:
          logger.warning(f"get_squares_normal falló, intentando align_image_with_squares_observada: {e}",queue=log_queue)
          _, square_coords, _ = model.align_image_with_squares_observada(image_loader,is_convencional, False, log_queue)
          top_left, top_right, _, bottom_left = square_coords
  elif obs_corregida:
       try:
          coordenadas_marcadores = model.get_squares_normal(is_convencional,log_queue)
          top_left, top_right, _, bottom_left = coordenadas_marcadores
       except Exception as e:
           logger.warning(f"get_squares_normal falló en un acta observada, intentando align_image_with_squares_observada: {e}",queue=log_queue)
           _, square_coords, _ = model.align_image_with_squares_observada(image_loader,is_convencional, False, log_queue)
           top_left, top_right, _, bottom_left = square_coords
  else:
      top_left, top_right, _, bottom_left = square_coords_alineados

  vx = top_right - top_left
  vy = bottom_left - top_left

  vx_ort = np.array([-vx[1], vx[0]])
  vy_ort = np.array([vy[1], -vy[0]])

  ans = []
  for name, relative_rec_top_left, relative_rec_bottom_right in rectangles:
    p1x = top_left + relative_rec_top_left[0] * vx
    p1y = top_left + relative_rec_top_left[1] * vy

    p2x = top_left + relative_rec_bottom_right[0] * vx
    p2y = top_left + relative_rec_bottom_right[1] * vy

    rec_top_left = line_intersection(p1x, vx_ort, p1y, vy_ort).astype(int)
    rec_bottom_right = line_intersection(p2x, vx_ort, p2y, vy_ort).astype(int)

    rectangle = image[rec_top_left[1]:rec_bottom_right[1], rec_top_left[0]:rec_bottom_right[0]]

    logger.info(f"{rec_top_left[1]},{rec_bottom_right[1]}, {rec_top_left[0]},{rec_bottom_right[0]}", queue = log_queue)

    ans.append((name, rectangle))
  return ans, is_convencional
