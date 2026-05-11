import dotenv
import os

dotenv.load_dotenv()

API_URL_CENTRO_COMPUTO = os.environ.get('API_URL_ORC')
RABBITMQ_HOST = os.environ.get('RABBITMQ_HOST')
RABBITMQ_PORT = os.environ.get('RABBITMQ_PORT')
RABBITMQ_USER = os.environ.get('RABBITMQ_USER')
RABBITMQ_PASSWORD = os.environ.get('RABBITMQ_PASSWORD')

IMAGES_DIR = os.environ.get("IMAGES_DIR")
SUB_DIR = os.environ.get("SUBCARPETA")

if SUB_DIR:
    IMAGES_DIR = os.path.join(IMAGES_DIR, SUB_DIR)
    os.makedirs(IMAGES_DIR, exist_ok=True)
    os.environ["IMAGES_DIR"] = IMAGES_DIR

POSTGRE_HOST = os.environ.get('DB_HOST')
POSTGRE_DATABASE = os.environ.get('DB_NAME')
POSTGRE_USER = os.environ.get('DB_USER')
POSTGRE_PASSWORD = os.environ.get('DB_PASSWORD')
POSTGRE_PORT = os.environ.get('DB_PORT')
POSTGRE_DEFAULT_SCHEMA = os.environ.get('DB_DEFAULT_SCHEMA')

def _env_int(name, default):
    try:
        return int(os.environ.get(name))
    except (TypeError, ValueError):
        return default

CUT_AND_PREDICT = _env_int('CUT_AND_PREDICT', 1)
ENABLE_DETECTOR = _env_int('ENABLE_DETECTOR', 2)
ENABLE_DUAL_PREDICTION = _env_int('ENABLE_DUAL_PREDICTION', 1)
PROCESS_WORKERS_NUM = _env_int('PROCESS_WORKERS_NUM', 4)
WORKERS_PAGINA_LE = _env_int('WORKERS_PAGINA_LE', 3)
LE_PROCESS_WORKERS_NUM = _env_int('LE_PROCESS_WORKERS_NUM', 2)

os.environ["TF_CPP_MIN_LOG_LEVEL"] = "2"
os.environ["CUDA_VISIBLE_DEVICES"] = "-1"
os.environ["TF_ENABLE_ONEDNN_OPTS"] = "0"