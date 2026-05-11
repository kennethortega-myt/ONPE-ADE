import cv2
import os
from logger_config import logger
from util import constantes
class ImagesCache:
  def __init__(self):
    self.__stored_images = {}

  def has_image_type(self, image_type):
    logger.info(f"Ejecutando has_image_type para {image_type}...")
    return image_type in self.__stored_images

  def save_image(self, image_type, image, log_queue = "default"):
    logger.info(f"Ejecutando save_image para {image_type}...", queue = log_queue)
    self.__stored_images[image_type] = image

  def get_image_by_type(self, image_type):
    if image_type in self.__stored_images:
      return self.__stored_images[image_type]

    return None

  def write_images(self, folder_dest):
    logger.info(f"Ejecutando write_images en {folder_dest}...")
    if not os.path.exists(folder_dest):
      os.mkdir(folder_dest)

    for name, image in self.__stored_images.items():
      if isinstance(image, (list)):
        for i, img in enumerate(image):
          cv2.imwrite(os.path.join(folder_dest, f'{name}_{i}.png'), img)
      else:
        cv2.imwrite(os.path.join(folder_dest, name + '.png'), image)


def cache_result(result_code):
  def decorator(func):
    # assert funct is a method of ImagesCache extended class
    def wrapper(*args, **kwargs):
      self = args[0]
      log_queue = kwargs.pop("log_queue", "default")
      cached = self.get_image_by_type(result_code)
      if cached is not None:
        return cached

      ans = func(*args, log_queue=log_queue, **kwargs)
      self.save_image(result_code, ans, log_queue = log_queue)
      return ans

    return wrapper

  if callable(result_code):
    _func = result_code
    result_code = _func.__name__
    return decorator(_func)

  return decorator




class ImagesLoader:
  def __init__(self, rotate=False, **kwargs):
    self.rotate = rotate
    self.image = None

  def get_original_image(self):
    ans = self.load_image()
    if self.rotate:
      ans = cv2.rotate(ans, cv2.ROTATE_90_CLOCKWISE)
    return ans

  def load_image(self):
    logger.info("Ejecutando load_image...")
    raise NotImplementedError()

  def get_image(self):
    if self.image is None:
      self.image = self.get_original_image()
    return self.image
  
  def set_image(self, img):
    self.image = img


class ImageFromDisk(ImagesLoader):
  def __init__(self, image_path, log_queue="default", **kwargs):
    logger.info(f"Ejecutando __init__ de ImageFromDisk para {image_path}...", queue = log_queue)
    super().__init__(**kwargs)
    self.__image_path = image_path

  def load_image(self):
    return cv2.imread(self.__image_path)


class ImageFromMemory(ImagesLoader):
  def __init__(self, image, **kwargs):
    super().__init__(**kwargs)
    self.__original_image = image

  def load_image(self):
    return self.__original_image.copy()

class ImageFromArray(ImagesLoader):
    def __init__(self, image_np, **kwargs):
        super().__init__(**kwargs)
        self.__image = image_np

    def load_image(self):
        return self.__image