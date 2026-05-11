### === UTIL PROCESAR MARCADORES === ###
import os
from util import constantes
import cv2
from models.tipo_acta import CONFIGURACION_ELECCION, CONFIGURACION_ELECCION_STAE_VD
from logger_config import logger
from db.execution_context import get_context, resolve_workspace_path

def obtener_configuraciones_stae_vd(codigo_eleccion):
    conf_raw = CONFIGURACION_ELECCION_STAE_VD.get(codigo_eleccion)

    if conf_raw is None:
        raise ValueError(f"Sin configuración STAE_VD para {codigo_eleccion}")

    configuraciones = []

    for key, conf in conf_raw.items():
        variante = 1 if key.endswith("_1") else 2

        configuraciones.append({
            "key": key,
            "conf": conf,
            "ajuste": "original",
            "variante": variante
        })

        if key.startswith("totales"):
            conf_shift = conf.copy()
            conf_shift["cols"] = [c - 1 for c in conf["cols"]]

            configuraciones.append({
                "key": key,
                "conf": conf_shift,
                "ajuste": "shift_left",
                "variante": variante
            })

    return configuraciones

def cargar_y_preparar_imagen(
    acta_type,
    file_path,
    hoja_idx: int | None = None,
    total_hojas: int = 1
):
    

    img = cv2.imread(file_path)
    if img is None:
        logger.info(
            f"Error: No se pudo cargar la imagen {file_path}",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
        return None

    if total_hojas > 1 and hoja_idx is not None:
        BASE_DIR = os.path.dirname(os.path.abspath(__file__))
        MODULES_DIR = os.path.join(BASE_DIR, "..", "modules")
        # rotación SOLO para escrutinio horizontal
        if acta_type in [constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL]:
            img = cv2.rotate(img, cv2.ROTATE_90_CLOCKWISE)
        output_path = os.path.join(MODULES_DIR,f"imagen_limpia_resultado_marcador_{hoja_idx + 1}.png")
        cv2.imwrite(output_path, img)
        return output_path

    else:
        filename = "imagen_limpia_resultado_marcador_2.png"
        output_path = resolve_workspace_path(filename)

    cv2.imwrite(output_path, img)
    ctx = get_context()
    if ctx:
        ctx.add_temp_file(output_path)
    return output_path

def obtener_configuracion(codigo, is_preferencial, ajustar_corte_totales=False):
    raw_conf = CONFIGURACION_ELECCION.get(codigo)

    if raw_conf is None:
        raise ValueError(f" Código de elección desconocido o sin configuración: {codigo}")

    # Elegir entre totales / preferenciales si aplica
    if isinstance(raw_conf, dict) and "totales" in raw_conf:
        conf = raw_conf["preferenciales"] if is_preferencial else raw_conf["totales"]
    else:
        conf = raw_conf

    # Ajustar corte si es totales
    if ajustar_corte_totales and not is_preferencial:
        conf = conf.copy()
        conf["cols"] = [c - 1 for c in conf["cols"]]

    return conf


def generar_guide_lines(matriz, conf):
    if conf["guia_filas"] == []:
        return []
    guide_lines = []
    total_filas = len(matriz)
    start_idx = conf["guia_filas"][0]
    end_idx = total_filas + conf["guia_filas"][1]
    for idx in range(start_idx, end_idx):
        fila = matriz[idx]
        puntos = [p for p in fila if p is not None]
        if len(puntos) >= 2:
            guide_lines.append((puntos[0][0], puntos[0][1], puntos[-1][0], puntos[-1][1]))
    return guide_lines


def generar_lineas_resultados(matriz, conf):
    total_filas = len(matriz)
    total_columnas = len(matriz[0])
    tmp_fil = matriz[total_filas - 1][0]
    tmp_col = matriz[0][total_columnas - 1]

    fila_1_ini = matriz[conf["rows"][0]][0]
    fila_1_fin = (tmp_col[0], fila_1_ini[1])
    fila_2_ini = matriz[conf["rows"][1]][0]
    fila_2_fin = (tmp_col[0], fila_2_ini[1])

    col_1_ini = matriz[0][conf["cols"][0]]
    col_1_fin = (col_1_ini[0], tmp_fil[1])
    col_2_ini = matriz[0][conf["cols"][1]]
    col_2_fin = (col_2_ini[0], tmp_fil[1])

    return [
        (fila_1_ini, fila_1_fin),
        (fila_2_ini, fila_2_fin),
        (col_1_ini, col_1_fin),
        (col_2_ini, col_2_fin),
    ]

def insert_extra_black_marker(matriz, is_convencional, shift_y=47):
    try:
        # Ultimo par de coordenadas en la fila inferior
        bottom_left = matriz[-1][0]
        bottom_right = matriz[-1][-1]

        if not bottom_left or not bottom_right:
            logger.info("No se pueden insertar marcadores fantasma por datos incompletos", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
            return

        x1, y1 = bottom_left
        x2, y2 = bottom_right
        if is_convencional == constantes.FLUJO_CONVENCIONAL:
            real_shift_y = shift_y
        else:
            real_shift_y = shift_y - 7

        new_left = (x1, y1 - real_shift_y)
        new_right = (x2, y2 - real_shift_y)

        # Armamos la nueva fila con marcadores fantasmas a los extremos
        phantom_row = [None for _ in matriz[0]]
        phantom_row[0] = new_left
        phantom_row[-1] = new_right

        # Insertamos antes de la ultima fila
        matriz.insert(len(matriz) - 1, phantom_row)

    except Exception as e:
        logger.info(f"Error insertando marcador fantasma: {e}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
### === UTIL PROCESAR MARCADORES === ###
