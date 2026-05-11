import cv2
import numpy as np
from sklearn.cluster import DBSCAN
from typing import List, Tuple, Optional
from itertools import combinations

def agrupar_coordenadas_por_proximidad(puntos, distancia):
    """
    Agrupa puntos cercanos usando DBSCAN y devuelve el centro de cada grupo.
    :param puntos: lista de listas [[x1, y1], [x2, y2], ...]
    :param distancia: distancia máxima para agrupar puntos (en píxeles)
    :return: lista de centros [(x, y), ...]
    """
    if not puntos:
        return []

    puntos_np = np.array(puntos)
    clustering = DBSCAN(eps=distancia, min_samples=1).fit(puntos_np)
    etiquetas = clustering.labels_
    centros = []
    for etiqueta in set(etiquetas):
        grupo = puntos_np[etiquetas == etiqueta]
        centro = np.mean(grupo, axis=0)
        centros.append(tuple(int(c) for c in np.round(centro)))
    return centros

def obtener_coordenadas_extremos_tl_tr_br_bl(coordenadas):
    coordenadas = np.array(coordenadas)
    suma = coordenadas.sum(axis=1)
    resta = np.diff(coordenadas, axis=1).flatten()
    top_left     = coordenadas[np.argmin(suma)]
    bottom_right = coordenadas[np.argmax(suma)]
    top_right    = coordenadas[np.argmin(resta)]
    bottom_left  = coordenadas[np.argmax(resta)]
    return np.array([top_left, top_right, bottom_right, bottom_left])

# Para eliminar coordenadas falsas por eje X
def filtrar_vecinos_en_x(puntos, margen=21):
    filtrados_x = []
    for i, (xi, yi) in enumerate(puntos):
        tiene_vecino_x = any(
            abs(xi - xj) <= margen and i != j
            for j, (xj, _) in enumerate(puntos)
        )
        if tiene_vecino_x:
            filtrados_x.append((xi, yi))
    return filtrados_x

# Para eliminar coordenadas falsas por eje Y
def filtrar_vecinos_en_y(puntos, margen=21):
    filtrados_y = []
    for i, (xi, yi) in enumerate(puntos):
        tiene_vecino_y = any(
            abs(yi - yj) <= margen and i != j
            for j, (_, yj) in enumerate(puntos)
        )
        if tiene_vecino_y:
            filtrados_y.append((xi, yi))
    return filtrados_y

# requiere margen X e Y
# el margen en x sería la altura de líneas.
# el margen en y sería la anchura de columnas
def elminar_coordenadas_falsas(puntos, margen_x=10, margen_y=10):
    puntos_con_vecino_x = filtrar_vecinos_en_x(puntos, margen_x)
    puntos_finales = filtrar_vecinos_en_y(puntos_con_vecino_x, margen_y)
    return puntos_finales

def construir_rectangulos_yx(puntos, margen_y=10):
    rectangulos_yx = []
    filas = []
    fila_actual = []

    for pt in puntos:
        if not fila_actual:
            fila_actual.append(pt)
        elif abs(pt[1] - fila_actual[-1][1]) < margen_y:
            fila_actual.append(pt)
        else:
            filas.append(fila_actual)
            fila_actual = [pt]
    if fila_actual:
        filas.append(fila_actual)

    # --- Paso 3: Ordenar puntos dentro de cada fila por X
    for fila in filas:
        fila.sort(key=lambda p: p[0])

    
    for i in range(len(filas) - 1):
        fila_superior = filas[i]
        fila_inferior = filas[i + 1]
        min_len = min(len(fila_superior), len(fila_inferior))  # asegurar pares válidos

        for j in range(min_len - 1):
            x1, y1 = fila_superior[j]
            x2 = fila_superior[j + 1][0]
            y2 = fila_inferior[j][1]
            rectangulos_yx.append(((x1, y1), (x2, y2)))

    return rectangulos_yx


def agrupar_puntos_por_proximidad(puntos, distancia=10):
    """
    Agrupa puntos cercanos usando DBSCAN y devuelve el centro de cada grupo.
    
    :param puntos: lista de tuplas [(x1, y1), (x2, y2), ...]
    :param distancia: distancia máxima para agrupar puntos (en píxeles)
    :return: lista de centros [(x, y), ...]
    """
    if not puntos:
        return []

    puntos_np = np.array(puntos)
    clustering = DBSCAN(eps=distancia, min_samples=1).fit(puntos_np)
    etiquetas = clustering.labels_

    centros = []
    for etiqueta in set(etiquetas):
        grupo = puntos_np[etiquetas == etiqueta]
        centro = np.mean(grupo, axis=0)
        centros.append(tuple(int(c) for c in np.round(centro)))

    return centros

def obtener_puntos_extremos_tl_tr_bl_br(puntos):
    puntos = np.array(puntos)
    suma = puntos.sum(axis=1)
    resta = np.diff(puntos, axis=1).flatten()

    top_left     = puntos[np.argmin(suma)]
    bottom_right = puntos[np.argmax(suma)]
    top_right    = puntos[np.argmin(resta)]
    bottom_left  = puntos[np.argmax(resta)]

    return top_left, top_right, bottom_left, bottom_right

def detectar_marcadores_x0_y0(img_gray, template, margen_x, margen_y):
    puntos_qr = detectar_qr(img_gray)
    w, _ = template.shape[::-1]  # Ancho y alto del template

    res = cv2.matchTemplate(img_gray, template, cv2.TM_CCOEFF_NORMED)

    # Definir un umbral para considerar coincidencias válidas
    threshold = 0.8
    loc = np.nonzero(res >= threshold)

    # invertir puntos
    puntos_detectados = list(zip(*loc[::-1]))

    h_img, w_img = img_gray.shape[:2]  # Dimensiones de la imagen

    puntos_filtrados = [
        (x, y) for x, y in puntos_detectados
        if (x < margen_x or x > w_img - margen_x) or (y < margen_y or y > h_img - margen_y)
    ]
    
    # Agrupar puntos por cercanía
    # los puntos agrupados se ubican en la parte superior izquierda
    #puntos_agrupados = agrupar_puntos_por_proximidad(puntos_detectados, w)
    puntos_agrupados = agrupar_puntos_por_proximidad(puntos_filtrados, w)
    puntos_detectados = filtrar_puntos_fuera_qr(puntos_detectados, puntos_qr)
    return puntos_agrupados

def detectar_marcadores_xc_yc(img_gray, template, margen_x, margen_y):
    w, h = template.shape[::-1]
    h_img, w_img = img_gray.shape[:2]
    puntos_agrupados = detectar_marcadores_x0_y0(img_gray, template, margen_x, margen_y)

    puntos_centrados_filtrados = [
    (x, y) for x, y in puntos_agrupados
    if (x < margen_x or x > w_img - margen_x) or (y < margen_y or y > h_img - margen_y)
    ]

    puntos_centrados = [(x + w // 2, y + h // 2) for (x, y) in puntos_centrados_filtrados]
    return puntos_centrados

def corregir_imagen(image, tl, tr, br, bl):
    
    margen_extra = 20
    tl = np.array(tl, dtype=np.float32)
    tr = np.array(tr, dtype=np.float32)
    br = np.array(br, dtype=np.float32)
    bl = np.array(bl, dtype=np.float32)

    tl -= [margen_extra, margen_extra]
    tr += [margen_extra, -margen_extra]
    br += [margen_extra, margen_extra]
    bl -= [margen_extra, -margen_extra]

    width = int(np.linalg.norm(tr - tl))
    height = int(np.linalg.norm(bl - tl))
    src = np.array([tl, tr, br, bl], dtype=np.float32)
    dst = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype=np.float32)
    mp = cv2.getPerspectiveTransform(src, dst)
    return cv2.warpPerspective(image, mp, (width, height))


Point = Tuple[int, int]
def agrupar_coordenadas(valores: List[int], tolerancia: int) -> List[int]:
    valores_ordenados = sorted(valores)
    grupos = []
    grupo_actual = [valores_ordenados[0]]
    
    for valor in valores_ordenados[1:]:
        if abs(valor - grupo_actual[-1]) <= tolerancia:
            grupo_actual.append(valor)
        else:
            grupos.append(grupo_actual)
            grupo_actual = [valor]
    grupos.append(grupo_actual)

    # Usamos el promedio de cada grupo como valor representativo
    return [int(sum(grupo) / len(grupo)) for grupo in grupos]

def construir_matriz(puntos: List[Point], tolerancia_x: int = 5, tolerancia_y: int = 5) -> List[List[Optional[Point]]]:
    coordenadas_x = [p[0] for p in puntos]
    coordenadas_y = [p[1] for p in puntos]
    
    agrupaciones_x = agrupar_coordenadas(coordenadas_x, tolerancia_x)
    agrupaciones_y = agrupar_coordenadas(coordenadas_y, tolerancia_y)
    
    agrupaciones_x.sort()
    agrupaciones_y.sort()
    
    # Crear una matriz vacía con filas = agrupaciones_y y columnas = agrupaciones_x
    matriz = [[None for _ in agrupaciones_x] for _ in agrupaciones_y]
    
    for punto in puntos:
        # Buscar la columna (X) y fila (Y) más cercana
        columna = min(range(len(agrupaciones_x)), key=lambda i, p=punto: abs(p[0] - agrupaciones_x[i]))
        fila = min(range(len(agrupaciones_y)), key=lambda i, p=punto: abs(p[1] - agrupaciones_y[i]))

        # Verificar si está dentro de la tolerancia permitida
        if abs(punto[0] - agrupaciones_x[columna]) <= tolerancia_x and abs(punto[1] - agrupaciones_y[fila]) <= tolerancia_y:
            matriz[fila][columna] = punto
    
    return matriz


# Devuelve el punto de intersección entre la línea (p1,p2) y (p3,p4) o None si son paralelas
def interseccion_lineas(p1, p2, p3, p4):
    
    A = np.array([[p2[0] - p1[0], -(p4[0] - p3[0])],
                  [p2[1] - p1[1], -(p4[1] - p3[1])]])
    B = np.array([p3[0] - p1[0], p3[1] - p1[1]])

    try:
        t = np.linalg.solve(A, B)
        x = p1[0] + t[0] * (p2[0] - p1[0])
        y = p1[1] + t[0] * (p2[1] - p1[1])
        return (int(round(x)), int(round(y)))
    except np.linalg.LinAlgError:
        return None  # Son paralelas o idénticas
    

def calcular_todas_intersecciones(lineas):
    puntos = []
    for (l1, l2) in combinations(lineas, 2):
        p = interseccion_lineas(*l1, *l2)
        if p:
            puntos.append(p)
    return puntos


def filtrar_puntos_fuera_qr(puntos, vertices_qr):
    if vertices_qr is None:
        return puntos
    contorno = np.asarray(vertices_qr, dtype=np.float32).reshape((-1, 1, 2))

    puntos_fuera = [
        tuple(pt) for pt in puntos
        if cv2.pointPolygonTest(contorno, (float(pt[0]), float(pt[1])), False) < 0
    ]
    return puntos_fuera

def detectar_qr(img_gray):
    qr_detector = cv2.QRCodeDetector()
    data, bbox, _ = qr_detector.detectAndDecode(img_gray)
    if data and bbox is not None:
        points = bbox[0]
        bbox_array = np.array(points, dtype=np.int32)
        return bbox_array
    else:
        return None
    
#--------------------------------   GRILLA ------------------------------------------

def filtrar_col_fila_falsa_en_grilla(coordenadas, tolerancia_x=10, tolerancia_y=10 ):
    matriz = construir_matriz(coordenadas, tolerancia_x, tolerancia_y)
    matriz_2 = filtrar_columnas_grilla(matriz, 0.4) # columna con 40% de coordenadas se eliminan
    return [coord for col in matriz_2 for coord in col if coord is not None]

def filtrar_columnas_grilla(matriz, umbral=0.4):
    if not matriz or not matriz[0]:
        return []

    # Transponer: convertir filas x columnas → columnas x filas
    columnas = list(zip(*matriz))

    filas = len(columnas[0])
    minimo_requerido = int(filas * umbral)
    columnas_validas = []

    for i, columna in enumerate(columnas):
        cantidad_validos = sum(1 for celda in columna if celda is not None)
        if cantidad_validos >= minimo_requerido:
            columnas_validas.append(i)

    # Reconstruir matriz filtrada en formato filas x columnas
    matriz_filtrada = [
        [fila[i] for i in columnas_validas]
        for fila in matriz
    ]

    return matriz_filtrada

def agrupar_puntos_por_fila(puntos, tol_y):
    filas = []
    for x, y in sorted(puntos, key=lambda p: p[1]):
        asignado = False
        for fila in filas:
            if abs(fila["y_ref"] - y) <= tol_y:
                fila["puntos"].append((x, y))
                asignado = True
                break
        if not asignado:
            filas.append({
                "y_ref": y,
                "puntos": [(x, y)]
            })
    return filas

def linea_real_de_fila(puntos_fila):
    """
    Devuelve (vx, vy, cx, cy) de la fila
    """
    pts = np.array(puntos_fila, dtype=np.float32)
    vx, vy, cx, cy = cv2.fitLine(
        pts, cv2.DIST_L2, 0, 0.01, 0.01
    )
    return float(vx), float(vy), float(cx), float(cy)

def obtener_columnas_reales(xs, tol_x):
    columnas = []
    for x in sorted(xs):
        if not any(abs(x - xc) <= tol_x for xc in columnas):
            columnas.append(x)
    return columnas

def grilla_autocompletar_siguiendo_lineas(
    puntos,
    tol_y=8,
    tol_x=8,
    min_sep_x=15
):
    """
    Autocompleta SIN enderezar.
    Sigue la inclinación real de cada fila.
    """
    filas = agrupar_puntos_por_fila(puntos, tol_y)
    xs_globales = [x for x, _ in puntos]
    columnas = obtener_columnas_reales(xs_globales, tol_x)

    puntos_set = set(puntos)
    grilla = list(puntos)
    faltantes = []

    for fila in filas:
        puntos_fila = fila["puntos"]

        # Ajustar línea real de la fila
        vx, vy, cx, cy = linea_real_de_fila(puntos_fila)

        xs_existentes = [x for x, _ in puntos_fila]

        for x in columnas:
            # Evitar duplicados / puntos muy cercanos
            if any(abs(x - xr) < min_sep_x for xr in xs_existentes):
                continue

            if abs(vx) < 1e-6:
                continue  # línea vertical degenerada

            y = cy + (x - cx) * (vy / vx)
            nuevo = (int(round(x)), int(round(y)))

            if nuevo not in puntos_set:
                grilla.append(nuevo)
                faltantes.append(nuevo)

    return grilla, faltantes


# Reconstrucción de grilla discreta basada en patrón y agrupamiento tolerante
def grilla_autocompletar(puntos, tolerancia_y=5, tolerancia_x=5, comparacion_tol_x=3, comparacion_tol_y=3):
    # Obtener todas las X y Y
    ys = [y for _, y in puntos]
    xs = [x for x, _ in puntos]

    # Agrupar filas (Y) y columnas (X)
    filas_y = grilla_agrupar_con_tolerancia(ys, tolerancia_y)
    columnas_x = grilla_agrupar_con_tolerancia(xs, tolerancia_x)

    # Generar la grilla ideal
    grilla_completa = [(x, y) for y in filas_y for x in columnas_x]

    # Detectar solo los puntos faltantes reales
    faltantes = [
        p for p in grilla_completa
        if not grilla_punto_ya_existe(p, puntos, tol_x=comparacion_tol_x, tol_y=comparacion_tol_y)
    ]

    return grilla_completa, faltantes

# Agrupación con tolerancia 
def grilla_agrupar_con_tolerancia(valores, tolerancia):
    grupos = []
    for v in sorted(valores):
        agregado = False
        for grupo in grupos:
            if abs(grupo[0] - v) <= tolerancia:
                grupo.append(v)
                agregado = True
                break
        if not agregado:
            grupos.append([v])
    # Promedio de cada grupo
    return [int(round(np.mean(g))) for g in grupos]

# Verificar si un punto ya existe con tolerancia
def grilla_punto_ya_existe(p, puntos_existentes, tol_x=3, tol_y=3):
    px, py = p
    for x, y in puntos_existentes:
        if abs(px - x) <= tol_x and abs(py - y) <= tol_y:
            return True
    return False

def dibujar_circulo_coordenadas(image, coordenadas, color, radio, thickness=2):
    for i in coordenadas:
        cv2.circle(image, i, radius=radio, color=color, thickness=thickness)

def dibujar_grilla(imagen, grilla, color, thickness=1):
    filas = len(grilla)
    columnas = len(grilla[0]) if filas > 0 else 0

    # Dibujar líneas horizontales
    for fila in grilla:
        for i in range(len(fila) - 1):
            cv2.line(imagen, fila[i], fila[i + 1], color, thickness)

    # Dibujar líneas verticales
    for j in range(columnas):
        for i in range(filas - 1):
            cv2.line(imagen, grilla[i][j], grilla[i + 1][j], color, thickness)


def validar_matriz_marcadores(template, matriz_detectado):
    if len(template) < len(matriz_detectado):
        raise ValueError("Se detectaron más Marcadores de lo esperado.")
    if len(template) > len(matriz_detectado):
        raise ValueError("Se detectaron menos Marcadores de lo esperado.")

    for i in range(len(template)):
        if len(template[i]) != len(matriz_detectado[i]):
            raise ValueError("Inconsistencia de cantidad de Marcadores detectados.")

        for j in range(len(template[i])):
            valor_modelo = template[i][j]
            valor_nuevo = matriz_detectado[i][j]
            if valor_modelo == 1 and not es_coordenada_valida(valor_nuevo):
                raise ValueError("No se detectó un marcador en una posición esperada.")
            elif valor_modelo == 0 and valor_nuevo is not None:
                raise ValueError("Se detectó un marcador en una posición NO esperada.")

def es_coordenada_valida(valor):
    return (
        isinstance(valor, list) and
        len(valor) == 2 and
        all(isinstance(coord, int) for coord in valor)
    )