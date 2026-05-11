import os
from PIL import Image
import numpy as np
import torch
from rfdetr import RFDETRBase
from torchvision.ops import nms
from logger_config import logger
from util import constantes

import threading
__RFDETR_INSTANCE      = None
__RFDETR_INSTANCE_LOCK = threading.Lock()
RFDET_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'bin', 'checkpoint_best_ema.pth')

CLASSES = [str(i) for i in range(13)]

# Tile config
TILE_W = 512
TILE_H = 512
STRIDE_W = 384
STRIDE_H = 384
PADDING = 96

SCORE_THR = 0.75
NMS_IOU = 0.5

__RFDETR_INSTANCE = None

def load_rfdetr_model():
    global __RFDETR_INSTANCE
    if __RFDETR_INSTANCE is None:
        with __RFDETR_INSTANCE_LOCK:
            if __RFDETR_INSTANCE is None:
                logger.info("Cargando modelo RF-DETR.", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
                model = RFDETRBase(pretrain_weights=RFDET_PATH)
                model.optimize_for_inference(
                    compile=False,
                    batch_size=8,
                    dtype=torch.float32
                )
                __RFDETR_INSTANCE = model
    return __RFDETR_INSTANCE

def next_multiple_32(x):
    return int(np.ceil(x / 32) * 32)

def _prepare_padded_image(img_np):
    original_h, original_w = img_np.shape[:2]

    target_w = next_multiple_32(max(original_w, TILE_W))
    target_h = next_multiple_32(max(original_h, TILE_H))

    padded = np.ones((target_h, target_w, 3), dtype=np.uint8) * 255
    padded[:original_h, :original_w] = img_np

    return Image.fromarray(padded), original_w, original_h


def _compute_tiles(w, h):
    xs = list(range(0, w, STRIDE_W))
    ys = list(range(0, h, STRIDE_H))

    if xs[-1] + TILE_W < w:
        xs.append(max(0, w - TILE_W))
    if ys[-1] + TILE_H < h:
        ys.append(max(0, h - TILE_H))

    return xs, ys

def _generate_tiles(image, xs, ys, w, h):
    tiles = []
    meta = []

    for x in xs:
        for y in ys:
            x2 = min(x + TILE_W, w)
            y2 = min(y + TILE_H, h)

            px1 = max(0, x - PADDING)
            py1 = max(0, y - PADDING)
            px2 = min(w, x2 + PADDING)
            py2 = min(h, y2 + PADDING)

            crop = image.crop((px1, py1, px2, py2))
            tiles.append(crop)
            meta.append((x, y, x2, y2, px1, py1))

    return tiles, meta

def _collect_detections_batched(model, image, xs, ys, w, h, threshold, batch_size=8):

    tiles, meta = _generate_tiles(image, xs, ys, w, h)

    boxes_all, scores_all, classes_all = [], [], []

    for i in range(0, len(tiles), batch_size):
        batch = tiles[i:i + batch_size]
        batch_meta = meta[i:i + batch_size]

        dets_batch = model.predict(batch, threshold=threshold)

        for dets, (x, y, x2, y2, px1, py1) in zip(dets_batch, batch_meta):

            if not hasattr(dets, "xyxy"):
                continue

            for (bx1, by1, bx2, by2), score, cls_id in zip(
                dets.xyxy, dets.confidence, dets.class_id
            ):
                X1 = int(bx1 + px1)
                Y1 = int(by1 + py1)
                X2 = int(bx2 + px1)
                Y2 = int(by2 + py1)

                if X1 >= x and Y1 >= y and X2 <= x2 and Y2 <= y2:
                    boxes_all.append([X1, Y1, X2, Y2])
                    scores_all.append(float(score))
                    classes_all.append(int(cls_id))

    return boxes_all, scores_all, classes_all

def _apply_nms_and_clip(boxes_all, scores_all, classes_all, original_w, original_h):
    boxes = torch.tensor(boxes_all, dtype=torch.float32)
    scores = torch.tensor(scores_all)
    classes = torch.tensor(classes_all, dtype=torch.int64)

    keep_indices = []
    for c in classes.unique().tolist():
        idxs = (classes == c).nonzero(as_tuple=True)[0]
        keep = nms(boxes[idxs], scores[idxs], NMS_IOU)
        keep_indices.extend(idxs[keep].tolist())

    final = []
    for i in keep_indices:
        x1, y1, x2, y2 = boxes_all[i]

        if x1 >= original_w or y1 >= original_h:
            continue

        final.append((
            min(x1, original_w - 1),
            min(y1, original_h - 1),
            min(x2, original_w - 1),
            min(y2, original_h - 1),
        ))

    return final


def evaluate_image_np_tiled(img_np, seccion_total = False, threshold = SCORE_THR):
    model = load_rfdetr_model()

    if seccion_total:
        image, original_w, original_h = _prepare_padded_image(img_np)
        W, H = image.size
    else:
        image = Image.fromarray(img_np)
        original_h, original_w = img_np.shape[:2]
        W, H = original_w, original_h

    xs, ys = _compute_tiles(W, H)
    boxes_all, scores_all, classes_all = _collect_detections_batched(
    model, image, xs, ys, W, H, threshold
    )

    if not boxes_all:
        return []

    final_bb = _apply_nms_and_clip(boxes_all, scores_all, classes_all, original_w, original_h)
    return final_bb
