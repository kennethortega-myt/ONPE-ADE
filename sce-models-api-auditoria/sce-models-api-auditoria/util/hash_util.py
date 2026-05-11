from pathlib import Path
import hashlib
from typing import Union

def sha256_file(file_input: Union[str, Path, bytes],chunk_size: int = 1024 * 1024,is_bytes: bool = False) -> str:
    """
    Calcula el SHA256 de un archivo.
    """
    sha256 = hashlib.sha256()

    if is_bytes:
        sha256.update(file_input)
        return sha256.hexdigest()

    path = Path(file_input)

    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(chunk_size), b""):
            sha256.update(chunk)

    return sha256.hexdigest()
