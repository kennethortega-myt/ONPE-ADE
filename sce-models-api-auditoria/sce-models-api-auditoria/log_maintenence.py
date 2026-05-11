import os
import zipfile
import shutil
from datetime import datetime, timedelta
import time

BASE_LOG_DIR = os.environ.get("LOG_DIR", "logs")
if not BASE_LOG_DIR:
    BASE_LOG_DIR = "logs"
os.makedirs(BASE_LOG_DIR, exist_ok=True)
    
PREFIX = "sce-models-api-"


def zip_folder(folder_path):
    zip_path = f"{folder_path}.zip"

    if os.path.exists(zip_path):
        return

    print(f"[LOG] Zipping {folder_path}")

    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, _, files in os.walk(folder_path):
            for file in files:
                full_path = os.path.join(root, file)
                arcname = os.path.relpath(full_path, folder_path)
                zipf.write(full_path, arcname)

    shutil.rmtree(folder_path)
    print(f"[LOG] Removed {folder_path}")


def cleanup_old_zips():
    now = datetime.now()

    for item in os.listdir(BASE_LOG_DIR):
        if not item.startswith(PREFIX) or not item.endswith(".zip"):
            continue

        try:
            date_str = item.replace(PREFIX, "").replace(".zip", "")
            file_date = datetime.strptime(date_str, "%Y-%m-%d")

            if now - file_date > timedelta(days=30):
                os.remove(os.path.join(BASE_LOG_DIR, item))
                print(f"[LOG] Deleted old zip {item}")

        except Exception:
            continue


def process():
    now = datetime.now()

    for item in os.listdir(BASE_LOG_DIR):
        full_path = os.path.join(BASE_LOG_DIR, item)

        if not item.startswith(PREFIX):
            continue

        if os.path.isdir(full_path):
            try:
                date_str = item.replace(PREFIX, "")
                folder_date = datetime.strptime(date_str, "%Y-%m-%d")

                if folder_date.date() < now.date():
                    zip_folder(full_path)

            except Exception:
                continue

    cleanup_old_zips()


def main():
    while True:
        process()
        time.sleep(3600)  # cada hora


if __name__ == "__main__":
    main()