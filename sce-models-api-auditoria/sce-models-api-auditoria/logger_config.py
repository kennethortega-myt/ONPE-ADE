import config
import logging
from logging.handlers import RotatingFileHandler
import os
import sys
import io
from datetime import datetime
import threading

if os.name == 'nt':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

_log_context = threading.local()

def set_worker_context(worker_id: int, threads_budget: int = 1):
    _log_context.worker_id     = worker_id
    _log_context.threads_budget = threads_budget

def get_worker_context():
    return getattr(_log_context, "worker_id", None)

def get_threads_budget() -> int:
    """Cualquier función en cualquier nivel puede llamar esto."""
    return getattr(_log_context, "threads_budget", 1)

QUEUE_VALUES = [
    "VALIDATE",
    "PROCESS",
    "PROCESS_PREDICTION",
    "MIEMBROS_MESA",
    "LISTA_ELECTORES",
    "STAE",
    "default"
]

BASE_LOG_DIR = os.environ.get('LOG_DIR')

if not (BASE_LOG_DIR and os.path.exists(BASE_LOG_DIR)):
    BASE_LOG_DIR = "logs"


MAX_WORKERS = config.PROCESS_WORKERS_NUM
MAX_LE_WORKERS = config.LE_PROCESS_WORKERS_NUM
class DailyDirectoryHandler(RotatingFileHandler):
    """RotatingFileHandler que cambia de carpeta/archivo cuando cambia el día."""

    def __init__(self, base_log_dir, filename_template, **kwargs):
        self._base_log_dir = base_log_dir
        self._filename_template = filename_template  # e.g. "VALIDATE-{date}.log"
        self._current_date = datetime.now().strftime("%Y-%m-%d")
        file_path = self._make_file_path(self._current_date)
        super().__init__(file_path, **kwargs)

    def _make_file_path(self, date_str):
        daily_dir = os.path.join(self._base_log_dir, f"sce-models-api-{date_str}")
        os.makedirs(daily_dir, exist_ok=True)
        return os.path.join(daily_dir, self._filename_template.format(date=date_str))

    def emit(self, record):
        current_date = datetime.now().strftime("%Y-%m-%d")
        if current_date != self._current_date:
            self._current_date = current_date
            self.close()
            self.baseFilename = os.path.abspath(self._make_file_path(current_date))
            self.stream = self._open()
        super().emit(record)

class QueueLogFilter(logging.Filter):
    def __init__(self, queue_name):
        super().__init__()
        self.queue_name = queue_name

    def filter(self, record):
        return getattr(record, "queue", "default") == self.queue_name

class WorkerQueueFilter(logging.Filter):
    def __init__(self, queue_name, worker_id):
        super().__init__()
        self.queue_name = queue_name
        self.worker_id = worker_id

    def filter(self, record):
        record_queue = getattr(record, "queue", "default")
        record_worker = getattr(record, "worker_id", None)

        if self.queue_name in ("PROCESS", "PROCESS_PREDICTION", "LISTA_ELECTORES"):
            return (
                record_queue == self.queue_name and
                record_worker == self.worker_id
            )

        return record_queue == self.queue_name

class CustomLogger(logging.Logger):
    def _inject_extra(self, prod, queue, kwargs):
        extra = kwargs.get('extra', {})
        extra['prod'] = prod
        extra['queue'] = queue or "default"
        worker_id = get_worker_context()
        if queue not in ("PROCESS", "PROCESS_PREDICTION", "LISTA_ELECTORES"):
            worker_id = "main"
        extra['worker_id'] = worker_id if worker_id is not None else "main"
        kwargs['extra'] = extra
        return kwargs

    def debug(self, msg, *args, **kwargs):
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().debug(msg, *args, **kwargs)

    def info(self, msg, *args, **kwargs):
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().info(msg, *args, **kwargs)

    def warning(self, msg, *args, **kwargs):
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().warning(msg, *args, **kwargs)

    def error(self, msg, *args, **kwargs):
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().error(msg, *args, **kwargs)

    def critical(self, msg, *args, **kwargs):
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().critical(msg, *args, **kwargs)

    def exception(self, msg, *args, **kwargs):
        kwargs['exc_info'] = True
        kwargs = self._inject_extra(kwargs.pop('prod', False),
                                    kwargs.pop('queue', None),
                                    kwargs)
        super().error(msg, *args, **kwargs)

logging.setLoggerClass(CustomLogger)

logger = logging.getLogger("mi_logger")
logger.setLevel(logging.DEBUG)

formatter = logging.Formatter(
    "%(asctime)s - %(levelname)s - [%(queue)s] | [W%(worker_id)s] - %(message)s"
)

for queue_name in QUEUE_VALUES:
    if queue_name == "PROCESS":
        worker_range = range(MAX_WORKERS)
    elif queue_name == "LISTA_ELECTORES":
        worker_range = range(MAX_LE_WORKERS)
    else:
        worker_range = ["main"]

    for worker_id in worker_range:
        if worker_id == "main":
            filename_template = f"{queue_name}-{{date}}.log"
        else:
            filename_template = f"{queue_name}-worker-{worker_id}-{{date}}.log"

        handler = DailyDirectoryHandler(
            BASE_LOG_DIR,
            filename_template,
            maxBytes=500 * 1024 * 1024,
            backupCount=1,
            encoding="utf-8"
        )

        handler.setFormatter(formatter)
        handler.setLevel(logging.DEBUG)
        handler.addFilter(WorkerQueueFilter(queue_name, worker_id))
        logger.addHandler(handler)

console_handler = logging.StreamHandler(sys.stdout)
console_handler.setFormatter(formatter)
console_handler.setLevel(logging.DEBUG)
logger.addHandler(console_handler)