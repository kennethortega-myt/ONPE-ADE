import threading
import os
from pathlib import Path
from contextlib import contextmanager
import config
from db.db_handler import _get_pool

_thread_local = threading.local()

class ExecutionContext:
    def __init__(self, workspace: str | None = None):
        self.conn = None
        self.cur = None
        self.temp_files = set()
        self.permanent_files = set()
        self.failed = False
        self.workspace = workspace

    def has_workspace(self) -> bool:
        """Indica si hay un workspace activo."""
        return bool(self.workspace)

    def add_temp_file(self, path: str):
        if path:
            self.temp_files.add(path)

    def add_permanent_file(self, path: str):
        if path:
            self.permanent_files.add(path)

    def cleanup(self):
        """Elimina archivos temporales. Si fallo, tambien elimina permanentes."""
        all_files = self.temp_files.copy()
        if self.failed:
            all_files |= self.permanent_files

        for path in all_files:
            if os.path.exists(path):
                try:
                    os.remove(path)
                except Exception as e:
                    print(f"[WARN] No se pudo eliminar {path}: {e}")

        self.temp_files.clear()
        if self.failed:
            self.permanent_files.clear()

def resolve_workspace_path(filename: str) -> str:
    ctx = get_context()
    if ctx and ctx.workspace:
        base = Path(ctx.workspace)
        base.mkdir(parents=True, exist_ok=True)
        return str(base / filename)
    return filename

@contextmanager
def use_execution_context(workspace: str | None = None):
    ctx = ExecutionContext(workspace=workspace)
    _thread_local.ctx = ctx
    try:
        # psycopg3: el pool entrega la conexión via context manager
        # commit/rollback automático al salir del with
        with _get_pool().connection() as conn:
            ctx.conn = conn
            ctx.cur  = conn.cursor()
            yield ctx
            # si no hubo excepción → commit automático
    except Exception:
        ctx.failed = True
        raise   # rollback automático
    finally:
        if hasattr(ctx, "cur") and ctx.cur and not ctx.cur.closed:
            ctx.cur.close()
        ctx.cleanup()
        _thread_local.ctx = None

def get_context() -> ExecutionContext:
    """Obtiene el contexto activo del hilo."""
    return getattr(_thread_local, "ctx", None)
