import cv2
import numpy as np
from models.imageutils import ImageFromMemory
from models import actasmodel
from PIL import Image, ExifTags
import io
from logger_config import logger
from util import constantes
from util.imagen_util import detect_flujo_by_dimensions

ABREV_EXTRANJERO = {
    # Escrutinio extranjero
    constantes.ABREV_ACTA_ESCRUTINIO_EXTRANJERO,
    constantes.ABREV_ACTA_ESCRUTINIO_HORIZONTAL_EXTRANJERO,
    constantes.ABREV_ACTA_INSTALACION_SUFRAGIO_EXTRANJERO,

    # Mesa de miembros
    constantes.ABREV_HOJA_ASISTENCIA_MM_EXTRANJERO,
    constantes.ABREV_RELACION_MM_NO_SORTEADOS_EXTRANJERO,
}

ABREV_STAE = {
    # Escrutinio STAE
    constantes.ABREV_ACTA_ESCRUTINIO_STAE,
    constantes.ABREV_ACTA_ESCRUTINIO_STAE_HORIZONTAL,
    constantes.ABREV_ACTA_INSTALACION_STAE,
    constantes.ABREV_ACTA_SUFRAGIO_STAE,

    # Mesa de miembros
    constantes.ABREV_HOJA_ASISTENCIA_MM_STAE,
    constantes.ABREV_RELACION_MM_NO_SORTEADOS_STAE,
}

def fix_exif_rotation(image_bytes):
    with Image.open(io.BytesIO(image_bytes)) as img:
        try:
            for orientation in ExifTags.TAGS.keys():
                if ExifTags.TAGS[orientation] == 'Orientation':
                    break
            exif = img._getexif()
            if exif is not None:
                orientation_value = exif.get(orientation, None)
                if orientation_value == 3:
                    img = img.rotate(180, expand=True)
                elif orientation_value == 6:
                    img = img.rotate(270, expand=True)
                elif orientation_value == 8:
                    img = img.rotate(90, expand=True)
        except Exception as e:
            logger.info(f"No EXIF orientation found: {e}")

        return cv2.cvtColor(np.array(img), cv2.COLOR_RGB2BGR)

def compute_relative_coordinates(image_bytes, areas, abrev=""):
  image = fix_exif_rotation(image_bytes)
  image_loader = ImageFromMemory(image)
  if abrev in ABREV_EXTRANJERO: 
    is_convencional = constantes.FLUJO_EXTRANJERO
  elif abrev in ABREV_STAE:
    is_convencional = constantes.FLUJO_STAE
  else:
    is_convencional = detect_flujo_by_dimensions(image)

  rectangles = []
  for x in areas:
    top_left = (x['topLeft']['x'], x['topLeft']['y'])
    bottom_right = (x['bottomRight']['x'], x['bottomRight']['y'])
    rectangles.append((x['name'], top_left, bottom_right))

  ans = [{
    "name": x[0],
    "topLeft": {
      "x": x[1][0],
      "y": x[1][1]
    },
    "bottomRight": {
      "x": x[2][0],
      "y": x[2][1]
    }
  } for x in actasmodel.compute_relative_coordinates(image_loader, rectangles, is_convencional)]
  return ans
