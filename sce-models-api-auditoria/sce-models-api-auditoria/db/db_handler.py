from psycopg_pool import ConnectionPool, TooManyRequests, PoolTimeout
from contextlib import contextmanager
import psycopg
import config
from logger_config import logger
import time
import random
import os
import threading

_pool_failure_count = 0
_pool_failure_lock  = threading.Lock()
_POOL_RESET_THRESHOLD = 5


def _increment_failure() -> int:
    global _pool_failure_count
    with _pool_failure_lock:
        _pool_failure_count += 1
        return _pool_failure_count


def _reset_failure_count():
    global _pool_failure_count
    with _pool_failure_lock:
        _pool_failure_count = 0


def _reset_pool():
    global _db_pool
    logger.warning("[DB-POOL] Reiniciando pool por fallos consecutivos...", queue="default")
    with _db_pool_lock:
        old = _db_pool
        _db_pool = None
    if old:
        try:
            old.close()
        except Exception:
            pass
    return _get_pool()

DB_CONNECT_TIMEOUT_SEC   = 30
DB_STATEMENT_TIMEOUT_MS  = 100000
DB_POOL_WAIT_TIMEOUT_SEC = 30

def _get_connection_string():
    return (
        f"host={config.POSTGRE_HOST} "
        f"port={config.POSTGRE_PORT} "
        f"dbname={config.POSTGRE_DATABASE} "
        f"user={config.POSTGRE_USER} "
        f"password={config.POSTGRE_PASSWORD} "
        f"connect_timeout={DB_CONNECT_TIMEOUT_SEC} "
        f"options='-c statement_timeout={DB_STATEMENT_TIMEOUT_MS}' "
        f"keepalives=1 "
        f"keepalives_idle=10 "
        f"keepalives_interval=5 "
        f"keepalives_count=3 "
    )

def _configure_connection(conn):
    conn.execute(f"SET search_path TO {config.POSTGRE_DEFAULT_SCHEMA}")
    conn.commit()

_db_pool: ConnectionPool | None = None
_db_pool_lock = threading.Lock()

def _get_pool() -> ConnectionPool:
    global _db_pool
    if _db_pool is None:
        with _db_pool_lock:
            if _db_pool is None:
                logger.info(f"[DB-POOL] Creando pool pid={os.getpid()} | min=1 max=1", queue="default")
                _db_pool = ConnectionPool(
                    conninfo=_get_connection_string(),
                    min_size=1,
                    max_size=1,
                    max_waiting=4,
                    max_lifetime=3600,
                    max_idle=600,
                    reconnect_timeout=DB_CONNECT_TIMEOUT_SEC,
                    configure=_configure_connection,
                    check=ConnectionPool.check_connection,
                    open=True,
                )
    return _db_pool


@contextmanager
def get_cursor(log_queue="default", with_conn=False):
    # Si ya hay un contexto activo, reutiliza su conexión
    # Importación local para evitar circular
    from db.execution_context import get_context
    ctx = get_context()
    if ctx is not None and ctx.conn is not None and not ctx.conn.closed:
        cur = ctx.conn.cursor()
        try:
            if with_conn:
                yield cur, ctx.conn
            else:
                yield cur
        finally:
            if not cur.closed:
                cur.close()
        return  # ← sale sin tocar el pool

    # Sin contexto activo: pide conexión al pool normalmente
    max_retries = 1
    for attempt in range(1, max_retries + 1):
        try:
            with _get_pool().connection(timeout=DB_POOL_WAIT_TIMEOUT_SEC) as conn:
                cur = conn.cursor()
                try:
                    if with_conn:
                        yield cur, conn
                    else:
                        yield cur
                finally:
                    if not cur.closed:
                        cur.close()
            _reset_failure_count()
            return

        except TooManyRequests as e:
            count = _increment_failure()
            if count >= _POOL_RESET_THRESHOLD:
                logger.error(
                    f"[DB-POOL] {count} fallos consecutivos — forzando reset del pool",
                    queue="default"
                )
                _reset_pool()
                _reset_failure_count()
            if attempt == max_retries:
                logger.exception(f"DB pool saturado tras {max_retries} intentos: {e}", queue="default")
                raise
            wait = 0.5 * attempt + random.uniform(0, 0.3)
            logger.warning(f"DB pool lleno, reintentando en {wait:.1f}s (intento {attempt})", queue="default")
            time.sleep(wait)

        except PoolTimeout as e:
            logger.warning("[DB-POOL] PoolTimeout — reseteando pool inmediatamente", queue="default")
            _reset_pool()
            _reset_failure_count()
            if attempt == max_retries:
                logger.exception(f"DB pool timeout tras {max_retries} intentos: {e}", queue="default")
                raise
            wait = 2 * attempt + random.uniform(0, 0.5)
            logger.warning(f"DB pool reseteado, reintentando en {wait:.1f}s (intento {attempt})", queue="default")
            time.sleep(wait)

        except psycopg.OperationalError as e:
            if attempt == max_retries:
                logger.exception(f"DB conexión perdida tras {max_retries} intentos: {e}", queue="default")
                raise
            wait = 2 * attempt + random.uniform(0, 0.5)
            logger.warning(f"DB conexión perdida, reintentando en {wait:.1f}s (intento {attempt})", queue="default")
            time.sleep(wait)
        except psycopg.errors.QueryCanceled as e:
            logger.exception(f"DB query cancelada por timeout: {e}", queue="default")
            raise
        except Exception as e:
            logger.exception(f"DB Error: {e}", queue="default")
            raise