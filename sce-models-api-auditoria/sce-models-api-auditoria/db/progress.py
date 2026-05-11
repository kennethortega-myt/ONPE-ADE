import config
import os
import cv2
import shutil
import traceback
import hashlib
from datetime import datetime
import json
from util import constantes
from PIL import Image
from logger_config import logger
import time
from models.actasmodel import ActaReferencePoints
from models.imageutils import ImageFromDisk
from util.imagen_util import detect_flujo_by_dimensions
from db.db_handler import get_cursor
from util.hash_util import sha256_file
from typing import Optional
import io
from pathlib import Path
import uuid
import psycopg

from db.execution_context import get_context, resolve_workspace_path

def get_progress():
    try:
        with get_cursor() as cur:
            cur.execute("SELECT version();")
            db_version = cur.fetchone()
            logger.info(f"Connected to - {db_version}")
            return db_version
    except Exception as e:
        logger.exception(f"Error in get_progress(): {e}")
        return None

def get_digitos_chequeo_cab_acta(n_acta_pk, log_queue="default"):
    logger.info("Ejecutando get_digitos_chequeo_cab_acta...", queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT c_numero_copia, c_digito_chequeo_escrutinio,
                c_digito_chequeo_instalacion
                FROM cab_acta WHERE cab_acta.n_acta_pk = %s;
            """
            logger.info("Query: %s | params: %s", query.strip(), (n_acta_pk,), queue=log_queue)
            cur.execute(query, (n_acta_pk,))
            result = cur.fetchone()
            if result:
                column_names = [desc[0] for desc in cur.description]
                return dict(zip(column_names, result))
            return None
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_digitos_chequeo_cab_acta(): {e}", queue=log_queue)
        raise
    except Exception as e:
        logger.exception(f"Error in get_digitos_chequeo_cab_acta(): {e}", queue=log_queue)
        return None

def get_data_cab_acta(acta_id, log_queue = "default"):
    logger.info("Ejecutando get_data_cab_acta...", queue=log_queue)

    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT * FROM cab_acta 
                WHERE n_acta_pk = %s;
            """
            logger.info("Query: %s | params: %s", query.strip(), (acta_id,), queue=log_queue)
            cur.execute(query, (acta_id,))
            result = cur.fetchone()

            if result:
                column_names = [desc[0] for desc in cur.description]
                return dict(zip(column_names, result))
            return None
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_cab_acta(): {e}", queue=log_queue)
        raise
    except Exception as e:
        logger.exception(f"Error in get_data_cab_acta(): {e}", queue=log_queue)
        return None


def update_det_parametro_by_nombre(cod_usuario, c_nombre,nuevo_valor,log_queue="default"):
    try:
        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            update_query = """
                UPDATE det_parametro
                SET 
                    c_aud_usuario_modificacion = %s,
                    c_valor = %s,
                    d_aud_fecha_modificacion = NOW()
                WHERE c_nombre = %s
            """
            params = [cod_usuario, nuevo_valor, c_nombre]

            logger.info(
                "update_det_parametro_by_nombre: %s | params=%s",
                update_query.strip(), params,
                queue=log_queue
            )

            cur.execute(update_query, params)
            rows_affected = cur.rowcount
            conn.commit()
            logger.info(f"Parámetro '{c_nombre}' actualizado a '{nuevo_valor}', filas afectadas: {rows_affected}",queue=log_queue)
            return rows_affected > 0

    except Exception as e:
        logger.exception(f"Error en update_det_parametro_by_nombre({c_nombre}): {e}",queue=log_queue)
        return False
    

def get_det_parametro_valor(c_nombre, log_queue="default"):
    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT c_valor
                FROM det_parametro
                WHERE c_nombre = %s
                LIMIT 1
            """
            cur.execute(query, (c_nombre,))
            row = cur.fetchone()
            return row[0] if row else None

    except Exception as e:
        logger.exception(f"Error consultando det_parametro ({c_nombre}): {e}",queue=log_queue)
        return None


def get_all_active_model_hashes(log_queue="default"):
    """
    Obtiene todos los hashes activos desde BD.
    """
    logger.info("Ejecutando get_all_active_model_hashes...",queue=log_queue)

    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT c_cadena
                FROM tab_version_modelo
                WHERE n_activo = 1;
            """
            logger.info("Consulta: %s", query.strip(), queue=log_queue)
            cur.execute(query)
            rows = cur.fetchall()
            if rows:
                hashes = {row[0] for row in rows}
                logger.info("Hashes activos obtenidos desde BD: %s",len(hashes),queue=log_queue)
                return hashes

            logger.warning("No se encontraron hashes activos en BD",queue=log_queue)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_all_active_model_hashes(): {e}", queue=log_queue)
        raise
    except Exception as e:
        logger.warning(f"Error consultando hashes en BD: {e}", queue=log_queue)
        return None


def get_data_cab_acta_celeste(acta_id: int, log_queue="default"):
    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT * FROM cab_acta_celeste 
                WHERE n_acta_celeste_pk = %s;
            """
            logger.info("Query: %s | params: %s", query.strip(), (acta_id,), queue=log_queue)
            cur.execute(query, (acta_id,))
            result = cur.fetchone()
            if not result:
                logger.warning(f"[CELESTE] No se encontró cab_acta_celeste con id={acta_id}", queue=log_queue)
                return None
            column_names = [desc[0] for desc in cur.description]
            return dict(zip(column_names, result))
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_cab_acta_celeste(): {e}", queue=log_queue)
        raise
    except Exception as error:
        logger.exception(f"Error in get_data_cab_acta_celeste(): {error}", queue=log_queue)
        return None

def update_cab_acta(acta_id, update_values, log_queue = "default"):
    try:
        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            update_query = "UPDATE cab_acta SET "
            update_query += ", ".join([f"{key} = %s" for key in update_values.keys()])
            update_query += " WHERE n_acta_pk = %s"

            params = list(update_values.values()) + [acta_id]
            logger.info("update_query: %s, %s", update_query, params, queue=log_queue)

            cur.execute(update_query, params)
            rows_affected = cur.rowcount
            conn.commit()

            logger.info(f"UPDATE ejecutado, filas afectadas: {rows_affected}", queue = log_queue)

            if rows_affected > 0:
                cols = ", ".join(update_values.keys())
                select_query = f"SELECT {cols} FROM cab_acta WHERE n_acta_pk = %s"
                cur.execute(select_query, (acta_id,))
                verificacion = cur.fetchone()
                logger.info(f"Post-update verificado para acta_id={acta_id}, columnas={list(update_values.keys())}, valores={verificacion}", queue=log_queue)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in update_cab_acta(): {e}", queue=log_queue)
        raise
    except Exception as e:
        logger.exception(f"Error in update_cab_acta(): {e}", queue = log_queue)

def update_cab_acta_celeste(acta_id: int, update_values: dict, log_queue="default"):
    try:
        with get_cursor(log_queue) as cur:
            update_query  = "UPDATE cab_acta_celeste SET "
            update_query += ", ".join([f"{key} = %s" for key in update_values.keys()])
            update_query += " WHERE n_acta_celeste_pk = %s"
            params = list(update_values.values()) + [acta_id]
            logger.info("update_query: %s | params: %s", update_query.strip(), params, queue=log_queue)
            cur.execute(update_query, params)
            logger.info(f"[CELESTE] Updated acta celeste {acta_id} with values: {update_values}", queue=log_queue)
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in update_cab_acta_celeste(): {e}", queue=log_queue)
        raise
    except Exception as error:
        logger.exception(f"Error in update_cab_acta_celeste(): {error}", queue=log_queue)

def insert_det_acta_accion(data, log_queue = "default"):
    try:
        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            insert_query = """
            INSERT INTO det_acta_accion (
                n_acta, c_accion, c_tiempo, n_iteracion, n_orden,
                c_usuario_accion, d_fecha_accion, c_codigo_centro_computo ,n_activo,
                c_aud_usuario_creacion, d_aud_fecha_creacion
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """

            values = (
                data['id_cab_acta'],
                data['c_accion'],
                data['c_tiempo'],
                data['n_iteracion'],
                data['n_orden'],
                data['c_usuario_accion'],
                data['d_fecha_accion'],
                data['c_codigo_centro_computo'],
                data['n_activo'],
                data['c_aud_usuario_creacion'],
                data['d_aud_fecha_creacion']
            )

            cur.execute(insert_query, values)
            logger.info("Nuevo registro insertado correctamente.", queue = log_queue)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in insert_det_acta_accion(): {e}", queue=log_queue)
        raise
    except Exception as error:
        logger.exception(f"Error al insertar el registro: {error}", queue = log_queue)

def select_det_acta_accion(conditions=None, log_queue="default"):
    try:
        with get_cursor(log_queue) as cur:
            base_query = """
            SELECT n_det_acta_accion_pk, n_acta, c_accion, c_tiempo,
                   n_iteracion, n_orden, c_usuario_accion, d_fecha_accion, n_activo
            FROM det_acta_accion
            """
            values = []
            if conditions:
                where_clauses = [f"{key} = %s" for key in conditions.keys()]
                values = list(conditions.values())
                base_query += " WHERE " + " AND ".join(where_clauses)

            logger.info("query data: %s, %s", base_query, values, queue=log_queue)
            cur.execute(base_query, values)

            rows = cur.fetchall()
            column_names = [desc[0] for desc in cur.description]
            return [dict(zip(column_names, row)) for row in rows]

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in select_det_acta_accion(): {e}", queue=log_queue)
        raise
    except Exception as error:
        logger.exception(f"Error al realizar la consulta: {error}", queue=log_queue)
        return []

def get_guid_by_archivo_pk(n_archivo_pk, log_queue="default", max_retries=3, retry_delay=1):
    logger.info("Ejecutando get_guid_by_archivo_pk para n_archivo_pk=%s", n_archivo_pk, queue=log_queue)

    for attempt in range(max_retries):
        try:
            with get_cursor(log_queue) as cur:
                query = """
                SELECT c_guid
                FROM tab_archivo
                WHERE n_archivo_pk = %s
                """
                logger.debug("Ejecutando query: %s con parámetro %s", query.strip(), n_archivo_pk, queue=log_queue)
                cur.execute(query, (n_archivo_pk,))
                row = cur.fetchone()

                if row and row[0]:
                    c_guid = row[0]
                    logger.info("Intento %d - c_guid encontrado: %s", attempt + 1, c_guid, queue=log_queue)
                    return c_guid
                else:
                    logger.warning("Intento %d - No se encontró c_guid para n_archivo_pk=%s", attempt + 1, n_archivo_pk, queue=log_queue)

        except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
            logger.exception(f"Error in get_guid_by_archivo_pk(): {e}", queue=log_queue)
            raise

        except Exception as error:
            logger.exception(f"Intento {attempt+1} - Error en la consulta: {error}", queue=log_queue)
            traceback.print_exc()

        if attempt < max_retries - 1:
            logger.debug("Reintentando en %s segundos...", retry_delay, queue=log_queue)
            time.sleep(retry_delay)

    logger.error("No se pudo obtener c_guid después de %d intentos para n_archivo_pk=%s", max_retries, n_archivo_pk, queue=log_queue)
    return None

def download_file_from_dir(filename, log_queue ="default"):
    logger.info("Ejecutando download_file_from_dir...", queue = log_queue)
    try:
       # Obtener rutas
        source_dir = config.IMAGES_DIR
        source_file = os.path.join(source_dir, filename)
        logger.info("source_file download_file_from_dir: %s", source_file, queue = log_queue)

        # Definir la ruta local donde se guardará el archivo
        local_dir = os.getcwd()  # Obtener el directorio actual de trabajo
        local_file = os.path.join(local_dir, filename)

        # Verificar si el archivo existe en el directorio fuente
        if os.path.exists(source_file):
           logger.info(f"Archivo encontrado: {source_file}", queue = log_queue)

           # Copiar el archivo a la ruta local
           shutil.copy2(source_file, local_file)

           # Imprimir la ruta donde se ha guardado el archivo
           logger.info(f"Archivo copiado como: {local_file}", queue = log_queue)
           logger.info(f"Ruta final donde se guardó el archivo: {os.path.abspath(local_file)}", queue = log_queue)
           return os.path.abspath(local_file)

        else:
            logger.error(f"El archivo {filename} no existe en el directorio fuente.", queue = log_queue)
            return None
    except Exception as e:
        logger.exception(f"Error al copiar el archivo: {e}", queue = log_queue)
        return None

def _resolve_output_paths(filename: str, ctx) -> tuple[str, str]:
    local_dir = os.getcwd()

    if ctx and ctx.has_workspace():
        final_name = filename
        final_file = resolve_workspace_path(final_name)
        return final_file, final_file
    else:
        final_file = os.path.join(local_dir, filename)
        return final_file, final_file

def _wait_for_file_ready(path: str, timeout: float = 3.0, interval: float = 0.05) -> bool:
    """Espera hasta que el archivo exista y tenga tamaño > 0."""
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        if os.path.exists(path) and os.path.getsize(path) > 0:
            try:
                with open(path, 'rb'):
                    return True
            except OSError:
                pass
        time.sleep(interval)
    return False

def download_file_from_dir_and_align(filename, acta_observada, is_convencional, codigo_eleccion=None, acta_type=None, use_working_dir: bool = False, log_queue="default"):
    try:
        ctx = get_context()
        source_dir = os.getcwd() if use_working_dir else config.IMAGES_DIR
        source_file = os.path.join(source_dir, filename)

        _, base_file = _resolve_output_paths(filename, ctx)
        output_file = str(Path(base_file).with_suffix(".tiff"))

        if os.path.exists(source_file):
            logger.info(f" Archivo encontrado: {source_file}", queue=log_queue)

            image_loader = ImageFromDisk(source_file, log_queue)
            if is_convencional == constantes.FLUJO_CONVENCIONAL:
                is_convencional = detect_flujo_by_dimensions(image_loader.get_image())
            if codigo_eleccion and acta_type:
                tipo_acta = TipoActa(tipo_eleccion=codigo_eleccion, tipo_acta=2, cantidad_candidatos=0, acta_tipo_disenio=acta_type)
                acta_reference_points = ActaReferencePoints(image_loader, tipo_acta, is_convencional=is_convencional)
            else:
                acta_reference_points = ActaReferencePoints(image_loader, is_convencional=is_convencional)

            aligned_image, square_coords, obs_corregida = acta_reference_points.align_image_with_squares(image_loader, acta_observada, is_convencional, log_queue)

            success = cv2.imwrite(output_file, aligned_image)
            if not success:
                logger.error("Error: No se pudo guardar la imagen alineada.", queue=log_queue)
                return None, None, None, None

            # esperar que el archivo esté listo antes de retornar
            if not _wait_for_file_ready(output_file):
                logger.error(f"Archivo no legible tras escritura: {output_file}", queue=log_queue)
                return None, None, None, None

            if ctx and ctx.has_workspace():
                ctx.add_permanent_file(output_file)

            logger.info(f" Imagen alineada y guardada en: {os.path.abspath(output_file)}", queue=log_queue)
            return os.path.abspath(output_file), square_coords, acta_reference_points, obs_corregida

        else:
            logger.error(f"El archivo {filename} no existe en el directorio fuente.", queue=log_queue)
            return None, None, None, None

    except Exception as e:
        logger.exception(f"Error al procesar el archivo: {e}", queue=log_queue)
        traceback.print_exc()
        return None, None, None, None

from dataclases.data_models import ProcessContext

def detectar_marcadores_acta(rtgctx: ProcessContext, acta_reference_points):
    matriz_marcadores = acta_reference_points.get_squares_all(
        rtgctx.is_convencional,
        already_align = True,
        log_queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
    )
    img = cv2.imread(rtgctx.file1_path)
    return matriz_marcadores, img

from models.tipo_acta import TipoActa

from util.coordenadas_util import (
    calcular_todas_intersecciones,
    obtener_puntos_extremos_tl_tr_bl_br,
)
from util.procesar_marcador_util import (
    cargar_y_preparar_imagen, 
    obtener_configuracion,
    generar_guide_lines,
    generar_lineas_resultados,
    insert_extra_black_marker,
    obtener_configuraciones_stae_vd
)

def procesar_marcadores(codigo_eleccion, acta_type, file_path, name, is_convencional, matriz_marcadores, ajustar_corte_totales=False):
    output_path = cargar_y_preparar_imagen(acta_type, file_path)

    conf = obtener_configuracion(codigo_eleccion, constantes.ABREV_TABLA_VOTO_PREFERENCIAL in name, ajustar_corte_totales=ajustar_corte_totales)
    # Insert extra marker if required
    if acta_type in [constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL, constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO] and codigo_eleccion in [constantes.COD_ELEC_DIPUTADO, constantes.COD_ELEC_SENADO_UNICO]:
        insert_extra_black_marker(matriz_marcadores, is_convencional)

    guide_lines = generar_guide_lines(matriz_marcadores, conf)

    lineas_resultados = generar_lineas_resultados(matriz_marcadores, conf)
    intersecciones_resultados = calcular_todas_intersecciones(lineas_resultados)

    tl, tr, bl, br = obtener_puntos_extremos_tl_tr_bl_br(intersecciones_resultados)
    coords_finales = ((tl[0]-8, tl[1]-8), (tr[0]+8, tr[1]-8), (bl[0]-8, bl[1]+8), (br[0]+8, br[1]+8))

    return output_path, coords_finales, guide_lines

def procesar_marcadores_stae_vd(
    codigo_eleccion,
    acta_type,
    lista_filepaths,
    num_candidatos,
    is_convencional
):

    assert isinstance(lista_filepaths, list)
    assert len(lista_filepaths) >= 1

    configuraciones = obtener_configuraciones_stae_vd(codigo_eleccion)

    output_paths = []
    cortes_globales = []

    total_hojas = len(lista_filepaths)

    for hoja_idx, file_path in enumerate(lista_filepaths):
        output_path = cargar_y_preparar_imagen(
            acta_type,
            file_path,
            hoja_idx=hoja_idx,
            total_hojas=total_hojas
        )
        output_paths.append(output_path)

        tipo_acta = TipoActa(codigo_eleccion,2,num_candidatos,acta_type)

        image_loader = ImageFromDisk(file_path, log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)
        acta_reference_points = ActaReferencePoints(image_loader,tipo_acta,is_convencional)
        matriz_marcadores = acta_reference_points.get_squares_all(is_convencional,log_queue=constantes.QUEUE_LOGGER_VALUE_STAE)
        es_ultima_hoja = (hoja_idx == total_hojas - 1)
        variante_usada = 2 if es_ultima_hoja else 1
        cortes_hoja = []

        for cfg in configuraciones:
            if cfg["variante"] != variante_usada:
                continue

            conf = cfg["conf"]

            lineas = generar_lineas_resultados(matriz_marcadores, conf)
            inters = calcular_todas_intersecciones(lineas)
            coords = calcular_coords_por_conf(matriz_marcadores, conf)

            cortes_hoja.append({
                "seccion": cfg["key"],
                "ajuste": cfg["ajuste"],
                "coords": coords,
                "lineas": lineas,
                "intersecciones": inters,
                "hoja_idx": hoja_idx,
                "variante": variante_usada
            })

        cortes_globales.extend(cortes_hoja)

    return output_paths, cortes_globales

def calcular_coords_por_conf(matriz_marcadores, conf):
    lineas = generar_lineas_resultados(matriz_marcadores, conf)
    inters = calcular_todas_intersecciones(lineas)

    tl, tr, bl, br = obtener_puntos_extremos_tl_tr_bl_br(inters)

    return (
        (tl[0] - 8, tl[1] - 8),
        (tr[0] + 8, tr[1] - 8),
        (bl[0] - 8, bl[1] + 8),
        (br[0] + 8, br[1] + 8),
    )

def get_data_tab_mesa(mesa_id, log_queue = "default"):
    logger.info("Ejecutando get_data_tab_mesa...", queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT * FROM tab_mesa 
                WHERE n_mesa_pk = %s;
            """, (mesa_id,))

            result = cur.fetchone()
            if not result:
                return None

            column_names = [desc[0] for desc in cur.description]
            return dict(zip(column_names, result))

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_tab_mesa(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_tab_mesa: {error}", queue=log_queue)
        return None

def get_data_det_ubigeo_eleccion(det_ubigeo_eleccion_pk, log_queue = "default"):
    logger.info("Ejecutando get_data_det_ubigeo_eleccion...", queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT * FROM det_ubigeo_eleccion 
                WHERE n_det_ubigeo_eleccion_pk = %s;
            """, (det_ubigeo_eleccion_pk,))

            result = cur.fetchone()
            if not result:
                return None

            column_names = [desc[0] for desc in cur.description]
            return dict(zip(column_names, result))

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_det_ubigeo_eleccion(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_det_ubigeo_eleccion: {error}", queue=log_queue)
        return None

def get_data_mae_eleccion(n_eleccion, log_queue = "default"):
    logger.info("Ejecutando get_data_mae_eleccion...", queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT * FROM mae_eleccion 
                WHERE n_eleccion_pk = %s;
            """, (n_eleccion,))

            results = cur.fetchall()
            if not results:
                return None

            column_names = [desc[0] for desc in cur.description]
            # Si esperas solo uno, tomamos el primero
            return dict(zip(column_names, results[0]))

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_mae_eleccion(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_mae_eleccion: {error}", queue=log_queue)
        return None

def get_data_mae_ubigeo(n_ubigeo_pk, log_queue="default"):
    logger.info("Ejecutando get_data_mae_ubigeo...", queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT * FROM mae_ubigeo 
                WHERE n_ubigeo_pk = %s;
            """, (n_ubigeo_pk,))

            result = cur.fetchone()
            if not result:
                return None

            column_names = [desc[0] for desc in cur.description]
            return dict(zip(column_names, result))

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_mae_ubigeo(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_mae_ubigeo: {error}", queue=log_queue)
        return None

def get_data_det_distrito_electoral_eleccion(n_distrito_electoral_pk, n_eleccion_pk, log_queue="default"):
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT n_cantidad_candidatos
                FROM det_distrito_electoral_eleccion
                WHERE n_distrito_electoral = %s AND n_eleccion = %s
                LIMIT 1;
            """, (n_distrito_electoral_pk, n_eleccion_pk))

            result = cur.fetchone()
            if result is None:
                logger.warning("No se encontró información para los parámetros dados.", queue=log_queue)
                return None
            return result[0]

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_det_distrito_electoral_eleccion(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_det_distrito_electoral_eleccion: {error}", queue=log_queue)
        return None

def get_coordenadas_por_eleccion_documento_electoral(eleccion_id:int, abreviatura:str, log_queue = "default"):
    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT
                    ts.n_seccion_pk,
                    ts.c_abreviatura,
                    dc.c_coordenada_relativa_superior_x,
                    dc.c_coordenada_relativa_inferior_x,
                    dc.c_coordenada_relativa_superior_y,
                    dc.c_coordenada_relativa_inferior_y
                FROM det_configuracion_documento_electoral dc
                INNER JOIN tab_seccion ts ON dc.n_seccion = ts.n_seccion_pk
                INNER JOIN det_tipo_eleccion_documento_electoral dte ON dc.n_det_tipo_eleccion_documento_electoral = dte.n_det_tipo_eleccion_documento_electoral_pk
                INNER JOIN tab_documento_electoral tde ON tde.n_documento_electoral_pk = dte.n_documento_electoral
                INNER JOIN mae_proceso_electoral mpe ON mpe.n_proceso_electoral_pk = dte.n_proceso_electoral
                INNER JOIN mae_eleccion me ON me.n_eleccion_pk = dte.n_eleccion
                WHERE tde.c_abreviatura = %s 
                  AND me.n_eleccion_pk = %s 
                  AND mpe.n_activo = 1 
                  AND tde.n_activo = 1 
                  AND dte.n_activo = 1 
                  AND dc.n_activo = 1
                ORDER BY ts.n_seccion_pk
            """
            cur.execute(query, (abreviatura, eleccion_id))
            results = cur.fetchall()

            if not results:
                return {}

            result_dict = {row[0]: row for row in results}

            logger.info("Resultados de coordenadas: %s", result_dict, queue=log_queue)
            return result_dict

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_coordenadas_por_eleccion_documento_electoral(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_coordenadas_por_eleccion_documento_electoral: {error}", queue=log_queue)
        return {}


def update_tab_mesa(mesa_id, update_values, log_queue = "default"):
    try:
        with get_cursor(log_queue) as cur:
            update_query = "UPDATE tab_mesa SET "
            update_query += ", ".join([f"{key} = %s" for key in update_values.keys()])
            update_query += " WHERE n_mesa_pk = %s"

            cur.execute(update_query, list(update_values.values()) + [mesa_id])
            logger.info(f"Updated mesa {mesa_id} with values: {update_values}", queue=log_queue)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in update_tab_mesa(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en update_tab_mesa: {error}", queue=log_queue)

def insert_det_acta_rectangulo(acta_id:int, eleccion_id:str, acta_type:str, archive_doc_id: Optional[int], data:dict, log_queue="default"):
    try:
        insert_data = {
            "n_acta": acta_id,
            "n_eleccion": eleccion_id,
            "c_tipo": acta_type,
            "n_archivo": archive_doc_id
        }
        insert_data.update(data)

        columns = ", ".join(insert_data.keys())
        placeholders = ", ".join(["%s"] * len(insert_data))

        insert_query = """
            INSERT INTO det_acta_rectangulo ({cols})
            VALUES ({vals})
            RETURNING n_det_acta_rectangulo_pk;
        """.format(cols=columns, vals=placeholders)

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            logger.info(
                "Query a ejecutar: %s | params: %s",
                insert_query.strip(), list(insert_data.values()),
                queue=log_queue
            )
            cur.execute(insert_query, list(insert_data.values()))
            inserted_id = cur.fetchone()[0]
            return inserted_id

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in insert_det_acta_rectangulo(): {e}", queue=log_queue)
        raise

    except Exception as e:
        logger.exception("Error en insert_det_acta_rectangulo: %s", e, queue=log_queue)
        return None

def get_data_det_mesa_documento_electoral_archivo(mesa_id, documento_electoral_id, log_queue = "default"):
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT dmda.* 
                FROM det_mesa_documento_electoral_archivo dmda
                INNER JOIN tab_mesa tm ON dmda.n_mesa = tm.n_mesa_pk
                INNER JOIN tab_documento_electoral tde ON dmda.n_documento_electoral = tde.n_documento_electoral_pk
                WHERE dmda.n_mesa = %s 
                AND dmda.n_documento_electoral = %s;
            """, (mesa_id, documento_electoral_id))

            results = cur.fetchall()
            column_names = [desc[0] for desc in cur.description]
            return [dict(zip(column_names, row)) for row in results]

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_data_det_mesa_documento_electoral_archivo(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_det_mesa_documento_electoral_archivo: {error}", queue=log_queue)
        return []

def submit_json(json_data, table_name, column_name, mesa_id, log_queue="default"):
    try:
        json_string = json.dumps(json_data)

        insert_query = """
            INSERT INTO {table} ({col}, n_mesa)
            VALUES (%s, %s)
            RETURNING n_{table}_pk;
        """.format(table=table_name, col=column_name)

        with get_cursor(log_queue) as cur:
            cur.execute(insert_query, (json_string, mesa_id))
            inserted_id = cur.fetchone()[0]

        logger.info(
            "JSON insertado correctamente en %s.%s con ID: %s",
            table_name, column_name, inserted_id,
            queue=log_queue
        )
        return inserted_id

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in submit_json(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception("Error al insertar JSON: %s", error, queue=log_queue)
        raise

def get_data_tab_documento_electoral(c_abreviatura, log_queue = "default"):
    logger.info("get_data_tab_documento_electoral: %s", c_abreviatura, queue=log_queue)
    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT * FROM tab_documento_electoral 
                WHERE c_abreviatura = %s;
            """, (c_abreviatura,))
            
            result = cur.fetchone()
            if not result:
                return None

            column_names = [desc[0] for desc in cur.description]
            return dict(zip(column_names, result))

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_data_tab_documento_electoral(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_data_tab_documento_electoral: {error}", queue=log_queue)
        return None

def select_data(table_name, conditions=None, log_queue="default"):
    logger.info("Ejecutando select_data...", queue=log_queue)
    try:
        base_query = f"SELECT * FROM {table_name}"
        values = []

        if conditions:
            where_clauses = [f"{key} = %s" for key in conditions.keys()]
            values = list(conditions.values())
            base_query += " WHERE " + " AND ".join(where_clauses)

        with get_cursor(log_queue) as cur:
            logger.info(f"Ejecutando consulta: {base_query} con valores: {values}", queue=log_queue)
            cur.execute(base_query, values)

            rows = cur.fetchall()
            column_names = [desc[0] for desc in cur.description]
            return [dict(zip(column_names, row)) for row in rows]

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in select_data(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en select_data: {error}", exc_info=True, queue=log_queue)
        return []

def upload_file_to_dir(file_path: str, cod_usuario="", centro_computo="", log_queue="default"):
    try:
        name = os.path.basename(file_path)
        unique_id  = uuid.uuid4().hex
        sha256hash = centro_computo + "_" + sha256_file(file_path) + "_" + unique_id
        dest_file = os.path.join(config.IMAGES_DIR, sha256hash)
        with Image.open(file_path) as img:
            rgb_img = img.convert("RGB")
            rgb_img.save(dest_file, "JPEG", quality=55, optimize=True)
        size = os.path.getsize(dest_file)
        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            cur.execute(f"SET search_path TO {config.POSTGRE_DEFAULT_SCHEMA}")
            insert_query = """
            INSERT INTO tab_archivo 
            (c_formato, c_guid, c_nombre, c_peso, d_aud_fecha_creacion, n_activo, c_aud_usuario_creacion)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            RETURNING n_archivo_pk;
            """
            cur.execute(insert_query, (
                'image/jpeg',
                sha256hash,
                name,
                size,
                datetime.now(),
                1,
                cod_usuario
            ))
            result = cur.fetchone()
            if not result:
                raise RuntimeError("INSERT no devolvió ningún ID de archivo")
            archivo_id = result[0]
            conn.commit()
            return archivo_id
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in upload_file_to_dir(): {e}", queue=log_queue)
        raise

    except Exception as e:
        logger.exception(f"Error en upload_file_to_dir: {e}", exc_info=True, queue=log_queue)
        raise

def upload_file_to_dir_process_acta(file_path: str,cod_usuario: str = "",centro_computo: str = "",file_bytes: Optional[bytes] = None):
    ctx = get_context()
    if ctx is None:
        raise RuntimeError("upload_file_to_dir llamado fuera de un ExecutionContext")
    try:
        name = os.path.basename(file_path)
        if file_bytes is not None:
            sha = sha256_file(file_bytes, is_bytes=True)
        else:
            sha = sha256_file(file_path)

        unique_id  = uuid.uuid4().hex
        sha256hash = centro_computo + "_" + sha + "_" + unique_id
        dest_file = os.path.join(config.IMAGES_DIR, sha256hash)

        if file_bytes is not None:
            img = Image.open(io.BytesIO(file_bytes))
        else:
            img = Image.open(file_path)

        rgb_img = img.convert("RGB")
        rgb_img.save(dest_file, "JPEG", quality=65, optimize=True)

        ctx.add_permanent_file(dest_file)

        size = os.path.getsize(dest_file)
        with ctx.conn.cursor() as cur:
            cur.execute(
                """
                INSERT INTO tab_archivo 
                (c_formato, c_guid, c_nombre, c_peso, d_aud_fecha_creacion, n_activo, c_aud_usuario_creacion)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                RETURNING n_archivo_pk;
                """,
                ('image/jpeg', sha256hash, name, size, datetime.now(), 1, cod_usuario)
            )
            result = cur.fetchone()
            if not result:
                raise RuntimeError("INSERT no devolvió ningún ID de archivo")
            archivo_id = result[0]

        return archivo_id

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in upload_file_to_dir_process_acta(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as e:
        logger.exception(f"Error en upload_file_to_dir: {e}", exc_info=True)
        raise

def upload_files_batch_to_dir_process_acta(
    items: list[tuple[bytes, str]],   # [(jpeg_bytes, name), ...]
    cod_usuario: str = "",
    centro_computo: str = "",
) -> list[int]:
    """
    Escribe N archivos a NFS y hace UN SOLO INSERT batch → N IDs.
    Retorna los IDs en el mismo orden que items.
    """
    ctx = get_context()
    if ctx is None:
        raise RuntimeError("upload_files_batch llamado fuera de un ExecutionContext")
    if not items:
        return []

    rows = []
    for file_bytes, name in items:
        sha        = hashlib.sha256(file_bytes).hexdigest()
        unique_id  = uuid.uuid4().hex
        sha256hash = f"{centro_computo}_{sha}_{unique_id}"
        dest_file  = os.path.join(config.IMAGES_DIR, sha256hash)

        # Escribir a NFS
        with open(dest_file, "wb") as f:
            f.write(file_bytes)
        ctx.add_permanent_file(dest_file)

        rows.append((sha256hash, name, len(file_bytes), dest_file))

    now = datetime.now()
    values_template = ",".join(
        ["(%s, %s, %s, %s, %s, %s, %s)"] * len(rows)
    )
    params = []
    for sha256hash, name, size, _ in rows:
        params.extend([
            "image/jpeg", sha256hash, name, size, now, 1, cod_usuario
        ])

    #with _db_lock:
    with ctx.conn.cursor() as cur:
        cur.execute(
            f"""
            INSERT INTO tab_archivo
                (c_formato, c_guid, c_nombre, c_peso,
                 d_aud_fecha_creacion, n_activo, c_aud_usuario_creacion)
            VALUES {values_template}
            RETURNING n_archivo_pk;
            """,
            params
        )
        results = cur.fetchall()

    if len(results) != len(rows):
        raise RuntimeError(f"INSERT batch devolvió {len(results)} IDs para {len(rows)} archivos")

    return [r[0] for r in results]

def update_table_column(table_name, where_column, where_value, update_column, update_value, log_queue="default"):
    logger.info("Ejecutando update_table_column en %s...", table_name, queue=log_queue)
    try:
        update_query = """
            UPDATE {table}
            SET {col_update} = %s
            WHERE {col_where} = %s
        """.format(
            table=table_name,
            col_update=update_column,
            col_where=where_column
        )

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            logger.info(
                "Ejecutando consulta: %s con valores: %s, %s",
                update_query, update_value, where_value,
                queue=log_queue
            )
            cur.execute(update_query, [update_value, where_value])
            conn.commit()

        logger.info(
            "Updated %s where %s = %s with %s = %s",
            table_name, where_column, where_value, update_column, update_value,
            queue=log_queue
        )

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in update_table_column(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception("Error en update_table_column: %s", error, exc_info=True, queue=log_queue)
        raise

def insertar_trama_transmision(acta, usuario, log_queue="default"):
    logger.info("Ejecutando insertar_trama_transmision...", queue=log_queue)
    try:
        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            fecha_actual = datetime.now()
            acta_id      = acta['n_acta_pk']

            insert_query = """
                INSERT INTO tab_acta_transmision (
                    n_acta, n_estado_transmitido_nacion, n_transmite,
                    d_fecha_transmision, c_accion, c_usuario_transmision,
                    c_peticion_acta_transmision, c_tipo_transmision,
                    n_intento, n_activo, c_aud_usuario_creacion, d_aud_fecha_creacion
                ) VALUES (
                    %s, 0, 0, %s, 'ACTA', %s, NULL,
                    'DIGTAL_RECIBIDA_TRANSMISION', 1, 1, %s, %s
                ) RETURNING n_transmision_pk
            """
            cur.execute(insert_query, (acta_id, fecha_actual, usuario, usuario, fecha_actual))
            transmision_id = cur.fetchone()[0]
            conn.commit()
            logger.info(f"Registro de transmisión insertado para acta {acta_id} con ID {transmision_id}", queue=log_queue)

            json_path = os.path.join('db', 'transmision.json')
            with open(json_path, 'r') as file:
                transmision_data = json.load(file)

            transmision_data['idTransmision'] = transmision_id
            transmision_data['usuarioTransmision'] = usuario
            transmision_data['actaTransmitida']['cvas']  = acta['n_cvas']
            transmision_data['actaTransmitida']['activo'] = acta['n_activo']
            transmision_data['actaTransmitida']['idActa'] = acta_id
            transmision_data['actaTransmitida']['idMesa'] = acta['n_mesa']
            transmision_data['actaTransmitida']['estadoCc'] = acta['c_estado_computo']
            transmision_data['actaTransmitida']['estadoActa'] = acta['c_estado_acta']
            transmision_data['actaTransmitida']['numeroCopia'] = acta['c_numero_copia']
            transmision_data['actaTransmitida']['audFechaCreacion'] = acta['d_aud_fecha_creacion'].strftime("%d-%m-%Y %H:%M:%S")
            transmision_data['actaTransmitida']['electoresHabiles'] = acta['n_electores_habiles']
            transmision_data['actaTransmitida']['audUsuarioCreacion'] = acta['c_aud_usuario_creacion']
            transmision_data['actaTransmitida']['estadoActaOriginal'] = acta['c_estado_acta']
            transmision_data['actaTransmitida']['idArchivoEscrutinio'] = acta['n_archivo_escrutinio']
            transmision_data['actaTransmitida']['idDetUbigeoEleccion'] = acta['n_det_ubigeo_eleccion']
            transmision_data['actaTransmitida']['observDigEscrutinio'] = acta['c_observacion_digitalizacion_escrutinio']
            transmision_data['actaTransmitida']['audFechaModificacion'] = acta['d_aud_fecha_modificacion'].strftime("%d-%m-%Y %H:%M:%S")
            transmision_data['actaTransmitida']['estadoDigitalizacion'] = acta['c_estado_digitalizacion']
            transmision_data['actaTransmitida']['audUsuarioModificacion'] = acta['c_aud_usuario_modificacion']
            transmision_data['actaTransmitida']['digitoChequeoEscrutinio'] = acta['c_digito_chequeo_escrutinio']
            transmision_data['actaTransmitida']['digitalizacionEscrutinio'] = acta['n_digitalizacion_escrutinio']
            transmision_data['actaTransmitida']['idArchivoInstalacionSufragio'] = acta['n_archivo_instalacion_sufragio']
            transmision_data['actaTransmitida']['observDigInstalacionSufragio'] = acta['c_observacion_digitalizacion_instalacion_sufragio']
            transmision_data['actaTransmitida']['digitoChequeoInstalacionSufragio'] = acta['c_digito_chequeo_instalacion']
            transmision_data['actaTransmitida']['digitalizacionInstalacionSufragio'] = acta['n_digitalizacion_instalacion_sufragio']

            update_query = """
                UPDATE tab_acta_transmision
                SET c_peticion_acta_transmision = %s
                WHERE n_transmision_pk = %s
            """
            cur.execute(update_query, (json.dumps(transmision_data), transmision_id))
            conn.commit()
            logger.info(f"JSON de transmisión actualizado para transmisión ID {transmision_id}", queue=log_queue)

            return transmision_id

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in insertar_trama_transmision(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en insertar_trama_transmision: {error}", queue=log_queue)
        raise

def get_cantidad_agrupaciones_politicas(acta_pk, log_queue="default"):
    logger.info("Ejecutando get_cantidad_agrupaciones_politicas...", queue=log_queue)
    try:
        query = """
            SELECT COUNT(*) + 1 AS cantidad_filas 
            FROM det_ubigeo_eleccion_agrupacion_politica
            WHERE n_det_ubigeo_eleccion = (
                SELECT n_det_ubigeo_eleccion 
                FROM cab_acta 
                WHERE n_acta_pk = %s
            )
        """

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            cur.execute(query, (acta_pk,))
            result = cur.fetchone()
            return result[0] if result else 0

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_cantidad_agrupaciones_politicas(): {e}", queue=log_queue)
        raise
    except Exception as error:
        logger.exception("Error en get_cantidad_agrupaciones_politicas: %s", error, queue=log_queue)
        return 0

def get_cantidad_agrupaciones_politicas_revocatoria(acta_pk, log_queue="default"):
    logger.info("Ejecutando get_cantidad_agrupaciones_politicas_revocatoria...", queue=log_queue)
    try:
        query = """
            SELECT COUNT(*) AS cantidad_filas 
            FROM det_ubigeo_eleccion_agrupacion_politica
            WHERE n_det_ubigeo_eleccion = (
                SELECT n_det_ubigeo_eleccion 
                FROM cab_acta 
                WHERE n_acta_pk = %s
            )
        """

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            cur.execute(query, (acta_pk,))
            result = cur.fetchone()
            return result[0] if result else 0

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_cantidad_agrupaciones_politicas_revocatoria(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception("Error en get_cantidad_agrupaciones_politicas_revocatoria: %s", error, queue=log_queue)
        return 0

def get_cantidad_columnas_preferenciales(acta_pk, log_queue = "default"):
    logger.info("Ejecutando get_cantidad_columnas_preferenciales...", queue=log_queue)
    try:
        query = """
            SELECT {schema}.fn_obtener_cantidad_columnas_acta(
                %s, %s, %s, %s, %s, %s, %s, %s
            );
        """.format(schema=config.POSTGRE_DEFAULT_SCHEMA)

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            cur.execute(query, (
                config.POSTGRE_DEFAULT_SCHEMA,
                acta_pk,
                constantes.COD_ELEC_PRESIDENTE,
                constantes.COD_ELEC_CONGRESAL,
                constantes.COD_ELEC_PARLAMENTO,
                constantes.COD_ELEC_DIPUTADO,
                constantes.COD_ELEC_SENADO_MULTIPLE,
                constantes.COD_ELEC_SENADO_UNICO
            ))
            result = cur.fetchone()
            return result[0] if result else 0

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_cantidad_columnas_preferenciales(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception("Error en get_cantidad_columnas_preferenciales: %s", error, queue=log_queue)
        return 0

def get_control_automatico(log_queue = "default"):
    logger.info("Ejecutando get_control_automatico...", queue=log_queue)
    try:
        query = """
            SELECT c_valor 
            FROM det_parametro 
            WHERE c_nombre = 'p_control_automatico'
        """

        with get_cursor(log_queue, with_conn=True) as (cur, conn):
            cur.execute(query)
            result = cur.fetchone()
            return result[0].lower() == "true" if result else False

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_control_automatico(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception("Error en get_control_automatico: %s", error, queue=log_queue)
        return False

def get_seccion_abreviatura(c_abreviatura, log_queue="default"):
    try:
        query = """
            SELECT n_seccion_pk 
            FROM tab_seccion 
            WHERE c_abreviatura = %s
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, (c_abreviatura,))
            result = cur.fetchone()
            return result[0] if result else None

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_seccion_abreviatura(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception(f"Error en get_seccion_abreviatura: {error}", queue=log_queue)
        return None

def get_secciones_by_abreviaturas(abreviaturas: list[str], log_queue="default") -> dict:
    try:
        placeholders = ','.join(['%s'] * len(abreviaturas))
        query = f"""
            SELECT 
                n_seccion_pk,
                c_nombre,
                c_abreviatura
            FROM tab_seccion
            WHERE c_abreviatura IN ({placeholders})
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, tuple(abreviaturas))
            rows = cur.fetchall()

        result_dict = {row[2]: {"n_seccion_pk": row[0], "c_nombre": row[1], "c_abreviatura": row[2]} for row in rows}

        # Marcar si falta alguna abreviatura
        for abbr in abreviaturas:
            if abbr not in result_dict:
                result_dict[abbr] = None

        return result_dict

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_secciones_by_abreviaturas(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise
    
    except Exception as e:
        logger.exception(f"Error al obtener secciones por abreviaturas: {e}", queue=log_queue)
        return {}

def get_valor_copia_a_color(log_queue="default"):
    try:
        query = """
            SELECT c_valor
            FROM det_parametro
            WHERE c_nombre = %s
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, ("p_copia_a_color",))
            result = cur.fetchone()
            valor = result[0] if result else None
            logger.info(f"Valor obtenido para p_copia_a_color: {valor}",queue=log_queue)
            return valor if valor is not None else "true"

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error in get_valor_copia_a_color(): {e}", queue=log_queue)
        raise

    except Exception as error:
        logger.exception(f"Error en get_valor_copia_a_color: {error}", queue=log_queue)
        return "true"

def get_digitalizacion_flags(acta_id, log_queue="default"):
    try:
        query = """
            SELECT n_digitalizacion_escrutinio, n_digitalizacion_instalacion_sufragio
            FROM cab_acta
            WHERE n_acta_pk = %s;
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, (acta_id,))
            result = cur.fetchone()

        n_digitalizacion_escrutinio, n_digitalizacion_instalacion_sufragio = result

        return (n_digitalizacion_escrutinio == 2,
                n_digitalizacion_instalacion_sufragio == 2)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_digitalizacion_flags(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.info(
            f"Error en get_digitalizacion_flags(): {error}",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
        return False, False

def get_digitalizacion_procesamiento_manual_flag(acta_id, log_queue="default"):
    try:
        query = """
            SELECT n_digitalizacion_escrutinio
            FROM cab_acta
            WHERE n_acta_pk = %s;
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, (acta_id,))
            result = cur.fetchone()

        n_digitalizacion_escrutinio = result[0]

        return (n_digitalizacion_escrutinio == 3)

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_digitalizacion_procesamiento_manual_flag(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.info(
            f"Error en get_digitalizacion_flags(): {error}",
            queue=constantes.QUEUE_LOGGER_VALUE_PROCESS
        )
        return False

def get_cantidad_paginas(mesa_id, log_queue = "default"):
    try:
        query = """
            SELECT n_cantidad_electores_habiles
            FROM tab_mesa
            WHERE n_mesa_pk = %s
        """

        with get_cursor(log_queue) as cur:
            cur.execute(query, (mesa_id,))
            result = cur.fetchone()

        if not result:
            logger.info(
                f"No se encontraron registros en la mesa con id: {mesa_id}",
                queue=constantes.QUEUE_LOGGER_VALUE_LISTA_ELECTORES
            )
            return 0

        cantidad_electores = result[0]
        cantidad_paginas = (cantidad_electores // 10) + (1 if cantidad_electores % 10 > 0 else 0)

        return cantidad_paginas

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_cantidad_paginas(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.info(f"Error en get_cantidad_paginas(): {error}", queue=log_queue)
        return 0
    
def delete_data_from_table(table_name, condition_column, condition_value, log_queue = "default"):
    logger.info("Ejecutando delete_data en tabla %s...", table_name, queue = log_queue)
    try:
        delete_query = """
            DELETE FROM {table}
            WHERE {col_where} = %s;
        """.format(
            table=table_name,
            col_where=condition_column
        )

        with get_cursor(log_queue, with_conn = True) as (cur, conn):
            logger.info(
                "Ejecutando consulta: %s | params: %s",
                delete_query.strip(), (condition_value,),
                queue=log_queue
            )

            cur.execute(delete_query, (condition_value,))
            conn.commit()

            rows_deleted = cur.rowcount
            logger.info("Filas eliminadas: %s", rows_deleted, queue = log_queue)
            return rows_deleted

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in delete_data_from_table(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception(
            "Error en delete_data_from_table: %s\n",error, queue = log_queue)
        raise

def get_c_ubigeo_by_mesa_id(mesa_id, log_queue="default"):
    logger.info("Ejecutando get_c_ubigeo_by_mesa_id...", queue=log_queue)

    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT u.c_ubigeo
                FROM tab_mesa m
                JOIN mae_local_votacion lv 
                    ON lv.n_local_votacion_pk = m.n_local_votacion
                JOIN mae_ubigeo u 
                    ON u.n_ubigeo_pk = lv.n_ubigeo
                WHERE m.n_mesa_pk = %s;
            """

            logger.info("Query a Ejecutar: %s | params: %s", query.strip(), (mesa_id,), queue=log_queue)

            cur.execute(query, (mesa_id,))
            result = cur.fetchone()

            if result:
                return result[0]

            return None

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_c_ubigeo_by_mesa_id(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as e:
        logger.exception(f"Error in get_c_ubigeo_by_mesa_id(): {e}", queue=log_queue)
        return None

def get_nombre_by_archivo_pk(n_archivo_pk, log_queue="default", max_retries=3, retry_delay=1):
    logger.info("Ejecutando get_guid_by_archivo_pk para n_archivo_pk=%s", n_archivo_pk, queue=log_queue)

    for attempt in range(max_retries):
        try:
            with get_cursor(log_queue) as cur:
                query = """
                SELECT c_nombre
                FROM tab_archivo
                WHERE n_archivo_pk = %s
                """
                logger.debug("Ejecutando query: %s con parámetro %s", query.strip(), n_archivo_pk, queue=log_queue)
                cur.execute(query, (n_archivo_pk,))
                row = cur.fetchone()

                if row and row[0]:
                    c_nombre = row[0]
                    logger.info("Intento %d - c_nombre encontrado: %s", attempt + 1, c_nombre, queue=log_queue)
                    return c_nombre
                else:
                    logger.warning("Intento %d - No se encontró c_nombre para n_archivo_pk=%s", attempt + 1, n_archivo_pk, queue=log_queue)

        except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
            logger.exception(f"Error grave de conexion in get_nombre_by_archivo_pk(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
            raise
        
        except Exception as error:
            logger.exception(f"Intento {attempt+1} - Error en la consulta: {error}", queue=log_queue)
            traceback.print_exc()

        if attempt < max_retries - 1:
            logger.debug("Reintentando en %s segundos...", retry_delay, queue=log_queue)
            time.sleep(retry_delay)

    logger.error("No se pudo obtener c_nombre después de %d intentos para n_archivo_pk=%s", max_retries, n_archivo_pk, queue=log_queue)
    return None


def get_codigo_eleccion_principal(log_queue="default"):
    logger.info("Ejecutando get_codigo_eleccion_principal...",queue=log_queue)

    try:
        with get_cursor(log_queue) as cur:
            cur.execute("""
                SELECT c_codigo
                FROM mae_eleccion
                WHERE n_principal = 1;
            """)

            result = cur.fetchone()
            if not result:
                return None

            return result[0]

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_codigo_eleccion_principal(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception(f"Error en get_codigo_eleccion_principal: {error}",queue=log_queue)
        return None


def get_n_tipo_transmision_by_mesa(n_mesa, log_queue="default"):
    logger.info("Ejecutando get_n_tipo_transmision_by_mesa...", queue=log_queue)

    try:
        with get_cursor(log_queue) as cur:
            query = """
                SELECT ca.n_tipo_transmision
                FROM cab_acta ca
                INNER JOIN det_ubigeo_eleccion due
                    ON ca.n_det_ubigeo_eleccion = due.n_det_ubigeo_eleccion_pk
                WHERE ca.n_mesa = %s
                  AND due.n_eleccion = 10;
            """

            logger.info("Query a Ejecutar: %s | params: %s", query.strip(), (n_mesa,), queue=log_queue)

            cur.execute(query, (n_mesa,))
            result = cur.fetchone()

            if result:
                return result[0]

            return None

    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_n_tipo_transmision_by_mesa(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as e:
        logger.exception(
            f"Error in get_n_tipo_transmision_by_mesa(): {e}",
            queue=log_queue
        )
        return None
    

def get_archivos_by_ids(archivo_ids: list[int], log_queue="default") -> dict[int, dict]:
    """
    Devuelve un dict  { archivoId: row_dict }  con todos los archivos
    pedidos en una sola consulta con IN.
    """
    if not archivo_ids:
        return {}
 
    try:
        with get_cursor(log_queue) as cur:
 
            placeholders = ", ".join(["%s"] * len(archivo_ids))
 
            cur.execute(f"""
                SELECT *
                FROM tab_archivo
                WHERE n_archivo_pk IN ({placeholders});
            """, tuple(archivo_ids))
 
            rows = cur.fetchall()
 
            if not rows:
                logger.warning(
                    f"No se encontraron archivos para los IDs: {archivo_ids}",
                    queue=log_queue
                )
                return {}
 
            # Mapeamos por n_archivo_pk para acceso O(1) después
            col_names = [desc[0] for desc in cur.description]
            archivos = {
                row[col_names.index("n_archivo_pk")]: dict(zip(col_names, row))
                for row in rows
            }
 
            logger.info(
                f"Archivos cargados: {len(archivos)}/{len(archivo_ids)}",
                queue=log_queue
            )
            return archivos
 
    except (psycopg.OperationalError, psycopg.errors.AdminShutdown) as e:
        logger.exception(f"Error grave de conexion in get_archivos_by_ids(): {e}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
        raise

    except Exception as error:
        logger.exception(
            f"Error en get_archivos_by_ids: {error}",
            queue=log_queue
        )
        return {}