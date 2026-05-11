import cv2
import numpy as np

def merge_close_contours(bboxes, max_dist=12):
    merged = []
    used = set()

    for i in range(len(bboxes)):
        if i in used:
            continue

        x1, y1, w1, h1 = bboxes[i]
        bb1 = np.array([x1, y1, x1 + w1, y1 + h1])

        group = [i]

        for j in range(i + 1, len(bboxes)):
            if j in used:
                continue

            x2, y2, w2, h2 = bboxes[j]
            bb2 = np.array([x2, y2, x2 + w2, y2 + h2])

            dx = max(0, max(bb1[0] - bb2[2], bb2[0] - bb1[2]))
            dy = max(0, max(bb1[1] - bb2[3], bb2[1] - bb1[3]))
            dist = np.hypot(dx, dy)

            if dist < max_dist:
                group.append(j)
                used.add(j)

        xs = []
        ys = []
        xe = []
        ye = []
        for g in group:
            x, y, w, h = bboxes[g]
            xs.append(x)
            ys.append(y)
            xe.append(x + w)
            ye.append(y + h)

        merged.append((
            min(xs),
            min(ys),
            max(xe) - min(xs),
            max(ye) - min(ys),
        ))
        for g in group:
            used.add(g)
    return merged

def _evaluate_image_find_contour_gray(img):
    if len(img.shape) == 3:
        return cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
    return img.copy()

def _evaluate_image_find_contour_is_valid(cnt, idx, hierarchy):
    area = cv2.contourArea(cnt)
    if area < 50 or area > 4000:
        return False

    _, _, w, h = cv2.boundingRect(cnt)
    aspect = w / float(h)
    if h < 5:
        return False
    if aspect < 0.1 or aspect > 5:
        return False

    hull = cv2.convexHull(cnt)
    hull_area = cv2.contourArea(hull)
    if hull_area == 0 or area / hull_area < 0.15:
        return False

    return hierarchy[0][idx][3] == -1

def _evaluate_image_find_contour_to_box(cnt):
    x, y, w, h = cv2.boundingRect(cnt)
    return x, y, w, h

def evaluate_image_find_contour(img):
    gray = _evaluate_image_find_contour_gray(img)

    _, bw = cv2.threshold(gray, 128, 255, cv2.THRESH_BINARY)
    contours, hierarchy = cv2.findContours(bw, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    raw_boxes = [
        _evaluate_image_find_contour_to_box(cnt)
        for idx, cnt in enumerate(contours)
        if _evaluate_image_find_contour_is_valid(cnt, idx, hierarchy)
    ]

    merged_boxes = merge_close_contours(raw_boxes, max_dist=14)
    final_boxes = []

    for (x, y, w, h) in merged_boxes:
        final_boxes.append((x, y, x+w, y+h))

    return final_boxes