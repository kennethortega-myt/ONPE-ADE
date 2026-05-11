import numpy as np
import math
from models.imageutils import ImagesCache, ImagesLoader, cache_result
import cv2

class OrientationDetector(ImagesCache):
  def __init__(self, image_loader: ImagesLoader):
    super().__init__()
    self.image_loader = image_loader

    self.h, self.w = self.image_loader.get_image().shape[:2] / np.float64(10)
    self.kernel_size = int(self.w * 0.048)

    self.param_filter_max_std = 20
    self.param_filter_max_avg = 127

  @cache_result
  def get_thumbnail(self):
    return cv2.resize(self.image_loader.get_image(), (0, 0), fx=0.1, fy=0.1)

  @cache_result
  def get_average(self):
    return np.average(self.get_thumbnail(), axis=-1)

  @cache_result
  def get_std_dev(self):
    return np.max(self.get_thumbnail(), axis=-1) - np.min(self.get_thumbnail(), axis=-1)

  @cache_result
  def get_filtered(self):
    mask = np.logical_or(self.get_average() >= self.param_filter_max_avg,
                         self.get_std_dev() >= self.param_filter_max_std)
    filtered = self.get_thumbnail().copy()
    filtered[mask] = [255, 255, 255]
    return filtered

  @cache_result
  def get_blurred(self):
    gray_image = cv2.cvtColor(self.get_filtered(), cv2.COLOR_BGR2GRAY)
    _, binary_image = cv2.threshold(gray_image, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return binary_image

  def get_orientation(self):
    edges = cv2.Canny(self.get_blurred(), 10, 50)

    lines = cv2.HoughLinesP(edges, 1, np.pi / 180, 10, minLineLength=50, maxLineGap=5)
    image = self.get_thumbnail().copy()

    st = []
    for [[x1, y1, x2, y2]] in lines:
      dy = math.fabs(y1 - y2)
      dx = math.fabs(x1 - x2)
      st.append(round(np.degrees(np.arctan2(dy, dx)), 0))
      cv2.line(image, (x1, y1), (x2, y2), (0, 255, 0), 2)

    self.save_image('lines', image)
    angle = np.argmax(np.bincount(st))

    return "horizontal" if angle < 45 else "vertical"
