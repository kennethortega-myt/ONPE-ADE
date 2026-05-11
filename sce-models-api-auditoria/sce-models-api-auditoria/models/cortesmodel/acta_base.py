import cv2
import numpy as np
from util.coordenadas_util import (filtrar_col_fila_falsa_en_grilla, grilla_autocompletar_siguiendo_lineas)
from util import constantes
from models.imageutils import ImageFromMemory
from models.votesprocessorbase import VotesProcessorBase

class ActaBase:

    def __init__(self, imagen_votos):
        self._imagen_color = None
        self._imagen_gris = None
        self._acta_corregida = None
        self._imagen_votos = imagen_votos

    def obtener_grilla_votos(self, copia_a_color, is_convencional = constantes.FLUJO_CONVENCIONAL, usar_extremos = False, flag_total_votos = False):
        if copia_a_color:
            image_no_numbers = self._grilla_1_limpiar_azul()
            binary = self._grilla_2_binarizacion(image_no_numbers, flag_total_votos)
            morfologia = self._grilla_3_morfologia(binary)
        else:
            seccion = ImageFromMemory(self._imagen_votos)
            morfologia = VotesProcessorBase(seccion).get_remove_small_dots(modo="cuadricula", is_convencional=is_convencional, log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        
        horizontal_lines, vertical_lines = self._grilla_4_lineas_hori_verti(morfologia)
        coordenadas_detectadas = self._grilla_5_intersecciones(horizontal_lines, vertical_lines)
        coordenadas_detectadas = filtrar_col_fila_falsa_en_grilla(coordenadas_detectadas, 10, 10)
        grilla_completa, _ = grilla_autocompletar_siguiendo_lineas(
            coordenadas_detectadas,
            tol_y=10,
            tol_x=10,
            min_sep_x=20
        )

        if usar_extremos:
            grilla_completa = self.obtener_extremos_por_fila(grilla_completa)

        return grilla_completa


    def obtener_extremos_por_fila(self, puntos, tolerancia_y=5):
        if not puntos or len(puntos) < 4:
            raise ValueError(f"[EXTREMOS] Puntos insuficientes o vacíos: {puntos}")

        puntos_validos = [
            (x, y)
            for p in puntos
            if isinstance(p, (tuple, list))
            and len(p) == 2
            and isinstance((x := p[0]), int)
            and isinstance((y := p[1]), int)
            and x >= 0
            and y >= 0
        ]

        if len(puntos_validos) < 4:
            raise ValueError("[EXTREMOS] Coordenadas inválidas o ruidosas detectadas.")

        puntos_ordenados = sorted(puntos_validos, key=lambda p: (p[1], p[0]))

        filas = []
        fila_actual = [puntos_ordenados[0]]

        for punto in puntos_ordenados[1:]:
            if abs(punto[1] - fila_actual[-1][1]) <= tolerancia_y:
                fila_actual.append(punto)
            else:
                filas.append(fila_actual)
                fila_actual = [punto]

        filas.append(fila_actual)

        filas_ordenadas = [sorted(fila, key=lambda p: p[0]) for fila in filas]

        primera_fila = filas_ordenadas[0]
        ultima_fila = filas_ordenadas[-1]

        return [
            primera_fila[0],
            primera_fila[-1],
            ultima_fila[0],
            ultima_fila[-1],
        ]

    def _grilla_1_limpiar_azul(self):
        # Convertir la imagen a espacio de color HSV para detectar y eliminar los números azules
        hsv = cv2.cvtColor(self._imagen_votos, cv2.COLOR_BGR2HSV)
        # Eliminar números azules (copias a color)
        lower_blue = np.array([100, 30, 50])
        upper_blue = np.array([165, 255, 255])
        mask = cv2.inRange(hsv, lower_blue, upper_blue)
        return cv2.inpaint(self._imagen_votos, mask, inpaintRadius=3, flags=cv2.INPAINT_TELEA)

    def _grilla_2_binarizacion(self, image_no_numbers, flag_total_votos):
        gray_no_numbers = cv2.cvtColor(image_no_numbers, cv2.COLOR_BGR2GRAY)
        blur = cv2.GaussianBlur(gray_no_numbers, (5,5), 0)
        _, binary = cv2.threshold(blur, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
        if flag_total_votos:
            h, w = binary.shape[:2]
            PAD = 8
            w_interno = w - 2 * PAD

            x_positions = [
                int(PAD + (w_interno * (1/3))),
                int(PAD + (w_interno * (2/3)))
            ]
            line_thickness = 12

            for x in x_positions:
                cv2.line(binary, (x, 0), (x, h), 0, line_thickness)
        return binary
    
    def _grilla_3_morfologia(self, binary):
        # Aplicar morfología para mejorar la detección de la grilla
        # no cambiar a (2,2) números par genera desplazamiento hacia abajo.
        kernel = np.ones((3, 3), np.uint8)
        return cv2.morphologyEx(binary, cv2.MORPH_CLOSE, kernel, iterations=2)
        
    def _grilla_4_lineas_hori_verti(self, morfologia):
        # Detectar líneas horizontales y verticales para resaltar la grilla
        horizontal_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (40, 1))
        vertical_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, 40))
        horizontal_lines = cv2.morphologyEx(morfologia, cv2.MORPH_OPEN, horizontal_kernel)
        vertical_lines = cv2.morphologyEx(morfologia, cv2.MORPH_OPEN, vertical_kernel)
        return horizontal_lines, vertical_lines
    
    def _grilla_5_intersecciones(self, horizontal_lines, vertical_lines):
        intersections = cv2.bitwise_and(horizontal_lines, vertical_lines)

        # Encontrar regiones conectadas en las intersecciones
        num_labels, _, _, centroids = cv2.connectedComponentsWithStats(intersections)
        centroides_coordenadas_grilla = []
        for i in range(1, num_labels):  # Ignorar el fondo (etiqueta 0)
            cx, cy = centroids[i]
            cx, cy = int(cx), int(cy)
            centroides_coordenadas_grilla.append((cx, cy))
        return centroides_coordenadas_grilla
    