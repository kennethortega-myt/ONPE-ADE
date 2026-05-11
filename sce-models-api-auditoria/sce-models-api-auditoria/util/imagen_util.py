import cv2
import numpy as np

from util.coordenadas_util import agrupar_coordenadas_por_proximidad
from util import constantes

def extraer_esquinas_tl_tr_br_bl_marcadores(imagen, pad, kernel_size, black_lower, black_upper):
    corners = [
        imagen[:pad, :pad],
        imagen[:pad, -pad:],
        imagen[-pad:, -pad:],
        imagen[-pad:, :pad]
    ]
    return _preprocesar_imagen_marcadores(corners, kernel_size, black_lower, black_upper)  

def extraer_franjas_t_r_l_marcadores(imagen, pad, kernel_size, black_lower, black_upper):
    h_img, w_img = imagen.shape[:2]
    zonas = [
        imagen[0:pad, 0:w_img],
        imagen[0:h_img, w_img - pad:w_img],
        imagen[0:h_img, 0:pad],
    ]
    return _preprocesar_imagen_marcadores(zonas, kernel_size, black_lower, black_upper)

def _preprocesar_imagen_marcadores(corners, kernel_size, black_lower, black_upper):
    processed_corners = []
    for corner in corners:
        corner_proc = corner.copy()
        corner_proc[(corner[:, :, 0] > 180) | (corner[:, :, 1] > 180) | (corner[:, :, 2] > 180)] = [255, 255, 255]
        corner_proc = _filtro_para_marcadores(corner_proc, kernel_size, black_lower, black_upper)
        processed_corners.append(corner_proc)
    return processed_corners

def _filtro_para_marcadores(imagen, kernel_size, black_lower, black_upper):
    # Morfología para limpiar ruido
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, kernel_size)
    clean = cv2.morphologyEx(imagen, cv2.MORPH_OPEN, kernel)
    # Detección de negro en HSV
    hsv = cv2.cvtColor(clean, cv2.COLOR_BGR2HSV)
    black_mask = cv2.inRange(hsv, black_lower, black_upper)
    filtered = clean.copy()
    filtered[black_mask == 0] = (255, 255, 255)
    # binarización
    gray_image = cv2.cvtColor(filtered, cv2.COLOR_BGR2GRAY)
    _, bin_image = cv2.threshold(gray_image, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return bin_image

def get_marcadores(imagen, template, eps2, offset_x, offset_y, metodo_1 : bool = True, metodo_2 : bool = True, template_resized_flag: bool = False, square_all_flag: bool = False, indice: int = None, adjust_flag_method_1: bool = False):
    h, _ = template.shape
    corner_square = []
    if metodo_1:
        lista = _get_marcadores_opcion_1(imagen, eps2, adjust_flag_method_1)
        corner_square.extend(lista)
    if metodo_2:
        corner_square.extend(_get_marcadores_opcion_2(imagen, template, threshold=0.75, template_resized_flag=template_resized_flag, square_all_flag=square_all_flag, indice=indice))
    corner_square = agrupar_coordenadas_por_proximidad(corner_square, h)
    return [[x + offset_x, y + offset_y] for x, y in corner_square]

def _is_valid_square_marker(contour, imagen, eps2, min_dim, max_dim, max_aspect_ratio):
    epsilon = 2 * eps2 * cv2.arcLength(contour, True)
    approx = cv2.approxPolyDP(contour, epsilon, True)

    if len(approx) != 4:
        return None

    area_tmp = cv2.contourArea(contour)

    if area_tmp < min_dim**2 or area_tmp > max_dim**2:
        return None

    x, y, w, h = cv2.boundingRect(approx)

    if w < min_dim or w > max_dim or h < min_dim or h > max_dim:
        return None

    aspect_ratio = float(w) / h
    if aspect_ratio < 0.80 or aspect_ratio > max_aspect_ratio:
        return None

    roi = imagen[y:y + h, x:x + w]

    black_pixels = np.sum(roi == 0)
    total_pixels = roi.size
    black_ratio = black_pixels / total_pixels if total_pixels > 0 else 0

    if black_ratio > 0.20:
        return None

    return approx

def _get_marcadores_opcion_1(imagen, eps2, adjust_flag_method_1=False):
    contours, _ = cv2.findContours(imagen, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    min_dim, max_dim = 28, 44
    max_aspect_ratio = 1.15
    if adjust_flag_method_1:
        min_dim, max_dim = 15, 30
        max_aspect_ratio = 1.30
    corner_squares = []
    for contour in contours:
        approx = _is_valid_square_marker(contour, imagen, eps2, min_dim, max_dim, max_aspect_ratio)
        if approx is None:
            continue

        center = _get_center_marcador(approx)

        if center is not None:
            corner_squares.append(center)

    return corner_squares


def _get_marcadores_opcion_2(imagen, template, threshold: float = 0.75, template_resized_flag: bool = False, square_all_flag: bool = False, indice=None):
    res = cv2.matchTemplate(imagen, template, cv2.TM_CCOEFF_NORMED)
    threshold = 0.61 if template_resized_flag else threshold

    if square_all_flag:
        threshold = 0.41 # fixing deteccion de todos los cuadrados en actas extranjeras y tamanho 3.2K x 2.2K
        if indice == 0: # top region of the acta
            return _get_marcadores_opcion_1(imagen, 0.01, True)

    loc = np.nonzero(res >= threshold)
    h, w = template.shape
    centers = []
    for pt_y, pt_x in zip(*loc):
        center_x = pt_x + w // 2
        center_y = pt_y + h // 2
        centers.append([center_x, center_y])

    if centers:
        return agrupar_coordenadas_por_proximidad(centers, h)

    return centers


def _get_center_marcador(contour):
    moments = cv2.moments(contour)
    if moments["m00"] == 0:
        return

    c_x = int(round(moments["m10"] / moments["m00"], 0))
    c_y = int(round(moments["m01"] / moments["m00"], 0))
    return np.array([c_x, c_y])

def corregir_imagen(image, width, height, tl, tr, br, bl):
    if width is None:
        width = int(np.linalg.norm(np.array(tl) - np.array(tr)))
    if height is None:
        height = int(np.linalg.norm(np.array(bl) - np.array(tl)))
    src = np.array([tl, tr, br, bl], dtype='float32')
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')
    mp = cv2.getPerspectiveTransform(src, dst)
    return cv2.warpPerspective(image, mp, (width, height), cv2.INTER_LINEAR, borderValue=(255, 255, 255))

def compute_vmax(brillo_norm):
    brillo_norm = np.clip(brillo_norm, 0.75, 0.90)
    v_max = 125 + (brillo_norm - 0.75) * (54 / 0.15)

    return int(v_max)

def mascara_dinamica(img, resaltar_azules=True):
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    _, _, V = cv2.split(hsv)

    brillo_prom = np.mean(V)

    brillo_norm = brillo_prom / 255.0

    if resaltar_azules:
        low = np.array([100, 30, 50])
        high = np.array([165, 255, 255])
    else:
        v_max = compute_vmax(brillo_norm)

        low = np.array([0, 0, 0])
        high = np.array([179, 255, v_max])
    return low, high

def detect_flujo_by_dimensions(image):
    h, w = image.shape[:2]
    tol = constantes.A4_TOLERANCE
    w_match = abs(w - constantes.A4_WIDTH_PX) <= constantes.A4_WIDTH_PX * tol
    h_match = abs(h - constantes.A4_HEIGHT_PX) <= constantes.A4_HEIGHT_PX * tol
    if w_match and h_match:
        return constantes.FLUJO_EXTRANJERO
    return constantes.FLUJO_CONVENCIONAL

def encode_to_jpeg(img_array: np.ndarray, quality: int = 85) -> bytes:
    """Codifica numpy array a JPEG en RAM, sin pasar por PIL ni disco."""
    success, buf = cv2.imencode(".jpg", img_array, [cv2.IMWRITE_JPEG_QUALITY, quality])
    if not success:
        raise RuntimeError("cv2.imencode falló al convertir imagen a JPEG")
    return buf.tobytes()