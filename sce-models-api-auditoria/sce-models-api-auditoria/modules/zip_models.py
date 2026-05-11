from pathlib import Path
import hashlib
from util.hash_util import sha256_file

BASE_DIR = Path(__file__).resolve().parent.parent
MODELS_BASE_DIR = BASE_DIR / "models"


def generate_models_integrity_hash() -> str:
    """
    Genera un hash determinístico de integridad de modelos,
    basado en el contenido de cada archivo.
    """
    model_files = sorted([
        MODELS_BASE_DIR / "detectormodel/bin/checkpoint_best_ema.pth",
        MODELS_BASE_DIR / "binarymodel/bin/best_model_balanced_mobilenetv3_291025.safetensors",
        MODELS_BASE_DIR / "binarymodel/bin/best_model_balanced_spinalvgg_291025.safetensors",
        MODELS_BASE_DIR / "mnistmodel/bin/efficient_capsnet_MNIST_last_train.h5",
    ])

    sha256 = hashlib.sha256()

    for file_path in model_files:
        if not file_path.exists():
            raise FileNotFoundError(f"No existe el modelo: {file_path}")

        file_hash = sha256_file(file_path)
        sha256.update(file_hash.encode("utf-8"))

    return sha256.hexdigest()
