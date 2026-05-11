import os
from logger_config import logger

from flask import Flask, jsonify, request
from flasgger import Swagger, swag_from
import base64

from modules import configuration
from modules.zip_models import generate_models_integrity_hash
import json

app = Flask(__name__) # NOSONAR - CSRF not applicable: stateless API
app.config.from_prefixed_env()
swagger = Swagger(app)
ROOT = os.environ.get('FLASK_APPLICATION_ROOT', '/')

@app.route(f'{ROOT}', methods=['GET'])  # hello world route
def hello():
  return "Models API"

@app.route(f'{ROOT}/compute-relative-coordinates', methods=['POST'])
@swag_from('apidocs/compute-relative-coordinates.yaml')
def compute_relative_coordinates():
  body = request.get_json()
  logger.info(f"Datos recibidos: {list(body.keys())}")
  b64_image = body['image']
  image = base64.b64decode(b64_image)
  areas = body['areas']
  abrev = body['abreviatura']

  logger.info(f"Abreviatura: {abrev}")
  logger.info(f"Procesando imagen con {len(areas)} áreas detectadas.")
  logger.info("Contenido de 'areas' recibido:\n" + json.dumps({"areas": areas}, indent=2))
  ans = configuration.compute_relative_coordinates(image, areas, abrev)
  logger.info(f"Coordenadas calculadas: {ans}")
  return jsonify(ans)

@app.route(f'{ROOT}/models-integrity-hash', methods=['POST'])
@swag_from('apidocs/models-integrity-hash.yaml')
def models_integrity_hash():
    try:
        logger.info(f"ROOT: {ROOT}")
        logger.info("Solicitando hash de integridad de modelos")
        hash_value = generate_models_integrity_hash()
        logger.info("Hash de modelos generado correctamente")
        logger.info(f"Hash: {hash_value}")

        return jsonify({
            "sha256": hash_value
        })
    except Exception as e:
        logger.exception(f"Error generando hash de integridad de modelos: {e}",exc_info=True)
        return jsonify({"message": "Error generando hash de integridad de modelos","error": str(e)}), 500


if __name__ == "__main__":
  logger.info("Iniciando la API...")
  app.run()
