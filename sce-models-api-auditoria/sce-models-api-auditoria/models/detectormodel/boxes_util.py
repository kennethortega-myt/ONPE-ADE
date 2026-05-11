from logger_config import logger
from util import constantes
import numpy as np
from models.detectormodel.new_evaluate_image import evaluate_image_np_tiled
from models.detectormodel.contour_detector import evaluate_image_find_contour

def iou_xyxy(a, b):
    ax1, ay1, ax2, ay2 = a
    bx1, by1, bx2, by2 = b

    inter_x1 = max(ax1, bx1)
    inter_y1 = max(ay1, by1)
    inter_x2 = min(ax2, bx2)
    inter_y2 = min(ay2, by2)

    inter_w = max(0, inter_x2 - inter_x1)
    inter_h = max(0, inter_y2 - inter_y1)
    inter_area = inter_w * inter_h

    area_a = (ax2 - ax1) * (ay2 - ay1)
    area_b = (bx2 - bx1) * (by2 - by1)

    if area_a == 0 or area_b == 0:
        return 0.0

    return inter_area / float(area_a + area_b - inter_area)

def box_area(box):
    return (box[2] - box[0]) * (box[3] - box[1])

def area_xyxy(box):
    x1, y1, x2, y2 = box
    return max(0, x2 - x1) * max(0, y2 - y1)

def intersection_xyxy(box_a, box_b):
    xa = max(box_a[0], box_b[0])
    ya = max(box_a[1], box_b[1])
    xb = min(box_a[2], box_b[2])
    yb = min(box_a[3], box_b[3])

    if xb <= xa or yb <= ya:
        return 0

    return (xb - xa) * (yb - ya)


def coverage_ratio(logic_box, model_boxes):
    """
    Retorna cuánto del area del logic_box está cubierto
    por uno o varios model_boxes.
    """

    logic_area = area_xyxy(logic_box)
    if logic_area == 0:
        return 0.0

    total_intersection = 0

    for mbox in model_boxes:
        total_intersection += intersection_xyxy(logic_box, mbox)

    # Evitar sobreconteo (cap al área lógica)
    total_intersection = min(total_intersection, logic_area)

    return total_intersection / logic_area


def union_box(boxes):
    """Une múltiples bounding boxes en uno solo."""
    xs1 = [b[0] for b in boxes]
    ys1 = [b[1] for b in boxes]
    xs2 = [b[2] for b in boxes]
    ys2 = [b[3] for b in boxes]
    return (min(xs1), min(ys1), max(xs2), max(ys2))

def validate_against_logic(logic_boxes, model_boxes, coverage_thresh):
    """
    Ejecuta validación normal + validación con unión (si aplica).
    Devuelve True si pasa.
    """
    coverage_ok = True
    for lbox in logic_boxes:
        if coverage_ratio(lbox, model_boxes) < coverage_thresh:
            coverage_ok = False
            break

    if coverage_ok:
        return True
    if len(model_boxes) >= 2:
        merged_box = union_box(model_boxes)

        coverage_ok_merge = True
        for lbox in logic_boxes:
            if coverage_ratio(lbox, [merged_box]) < coverage_thresh:
                coverage_ok_merge = False
                break

        if coverage_ok_merge:
            return True

    return False

def split_box_by_cells(box, matriz_info, threshold=0.25):
    """
    Divide una caja si invade >= threshold de otra celda.
    Retorna lista de (cell_key, clipped_box).
    """
    splits = []
    total_area = box_area(box)

    if total_area == 0:
        return splits

    bx1, by1, bx2, by2 = box

    for r in range(len(matriz_info)):
        for c in range(len(matriz_info[r])):

            cx1, cy1, cx2, cy2 = matriz_info[r][c]

            ix1 = max(bx1, cx1)
            iy1 = max(by1, cy1)
            ix2 = min(bx2, cx2)
            iy2 = min(by2, cy2)

            if ix2 <= ix1 or iy2 <= iy1:
                continue

            inter_area = (ix2 - ix1) * (iy2 - iy1)

            if inter_area / total_area >= threshold:
                splits.append(((r, c), (ix1, iy1, ix2, iy2)))
    return splits


def evaluate_merge_bboxes(
    bboxes_logic,
    bboxes_model,
    matriz_info,
    image_np,
    coverage_thresh=0.8
):

    try:
        final_boxes = []
        pipe_status_per_cell = {}

        logic_map = _split_logic_boxes(bboxes_logic, matriz_info, image_np)
        model_map = _split_model_boxes(bboxes_model, matriz_info)

        for i in range(len(matriz_info)):
            for j in range(len(matriz_info[i])):
                cell_key = (i, j)

                cell_result = _process_single_cell(
                    cell_key,
                    logic_map,
                    model_map,
                    matriz_info,
                    image_np,
                    coverage_thresh
                )

                final_boxes.extend(cell_result["boxes"])
                pipe_status_per_cell[cell_key] = cell_result["status"]
        return final_boxes, pipe_status_per_cell

    except Exception as e:
        logger.info(f"Se detecto un error en el reforzamiento de la deteccion de trazos = {e} | UTILIZANDO LEGACY... ", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
        return _handle_legacy_fallback(
            bboxes_logic,
            bboxes_model,
            matriz_info
        )

def _split_logic_boxes(bboxes_logic, matriz_info, image_np):
    new_map = {
        (i, j): []
        for i in range(len(matriz_info))
        for j in range(len(matriz_info[i]))
    }

    for box in bboxes_logic:
        splits = split_box_by_cells(box, matriz_info, threshold=0.25)

        if len(splits) > 1:
            for cell_key, clipped in splits:
                refined = _refine_split_logic_box(
                    clipped,
                    image_np
                )
                new_map[cell_key].extend(refined)
        else:
            for cell_key, clipped in splits:
                new_map[cell_key].append(clipped)

    return new_map

def _refine_split_logic_box(clipped, image_np):
    cx1, cy1, cx2, cy2 = clipped

    roi = image_np[cy1:cy2, cx1:cx2]
    if roi.size == 0:
        return [clipped]

    refined_local = evaluate_image_find_contour(
        roi,
    )

    if not refined_local:
        return [clipped]

    refined_global = [
        (rx1 + cx1, ry1 + cy1, rx2 + cx1, ry2 + cy1)
        for rx1, ry1, rx2, ry2 in refined_local
    ]

    return refined_global

def _split_model_boxes(bboxes_model, matriz_info):
    new_map = {
        (i, j): []
        for i in range(len(matriz_info))
        for j in range(len(matriz_info[i]))
    }

    for box in bboxes_model:
        splits = split_box_by_cells(box, matriz_info, threshold=0.25)
        for cell_key, clipped in splits:
            new_map[cell_key].append(clipped)

    return new_map

def _process_single_cell(
    cell_key,
    logic_map,
    model_map,
    matriz_info,
    image_np,
    coverage_thresh
):
    logic_boxes = logic_map.get(cell_key, [])
    model_boxes = model_map.get(cell_key, [])

    if not logic_boxes:
        return {"boxes": [], "status": True}

    # Intento directo
    direct = _try_model_direct(
        logic_boxes,
        model_boxes,
        coverage_thresh
    )

    if direct:
        return {"boxes": model_boxes, "status": True}

    # Fallback
    fallback_boxes = _run_model_fallback(
        cell_key,
        matriz_info,
        image_np
    )

    if fallback_boxes and len(fallback_boxes) >= len(logic_boxes) and validate_against_logic(
        logic_boxes,
        fallback_boxes,
        coverage_thresh
    ):
        return {"boxes": fallback_boxes, "status": True}

    return {"boxes": [], "status": False}

def _try_model_direct(logic_boxes, model_boxes, coverage_thresh):
    if not model_boxes:
        return False

    if len(model_boxes) < len(logic_boxes):
        return False

    return validate_against_logic(
        logic_boxes,
        model_boxes,
        coverage_thresh
    )

def _run_model_fallback(
    cell_key,
    matriz_info,
    image_np
):
    row_i, col_j = cell_key
    x1, y1, x2, y2 = matriz_info[row_i][col_j]

    cell_img = image_np[y1:y2, x1:x2]

    refined_local = evaluate_image_np_tiled(
        cell_img,
        seccion_total=True,
        threshold=0.35
    )

    return [
        (rx1 + x1, ry1 + y1, rx2 + x1, ry2 + y1)
        for rx1, ry1, rx2, ry2 in refined_local
    ]

def _handle_legacy_fallback(bboxes_logic, bboxes_model, matriz_info):
    final_boxes = list(bboxes_model)

    for lbox in bboxes_logic:
        if not any(iou_xyxy(lbox, mbox) > 0.15 for mbox in bboxes_model):
            final_boxes.append(lbox)

    pipe_status = {
        (i, j): True
        for i in range(len(matriz_info))
        for j in range(len(matriz_info[i]))
    }

    return final_boxes, pipe_status


def boxes_overlap(b1, b2, threshold=0.25, padding=1):
        """Devuelve True si hay solapamiento (o igualdad) mayor al umbral."""
        x11, y11, x12, y12 = b1
        x21, y21, x22, y22 = b2

        # Normalizar coordenadas
        x11, x12 = sorted([x11, x12])
        y11, y12 = sorted([y11, y12])
        x21, x22 = sorted([x21, x22])
        y21, y22 = sorted([y21, y22])

        x11 -= padding; y11 -= padding; x12 += padding; y12 += padding
        x21 -= padding; y21 -= padding; x22 += padding; y22 += padding

        inter_x1 = max(x11, x21)
        inter_y1 = max(y11, y21)
        inter_x2 = min(x12, x22)
        inter_y2 = min(y12, y22)

        inter_area = max(0, inter_x2 - inter_x1) * max(0, inter_y2 - inter_y1)
        if inter_area == 0:
            return False

        area1 = (x12 - x11) * (y12 - y11)
        area2 = (x22 - x21) * (y22 - y21)
        overlap_ratio = inter_area / min(area1, area2)

        same_box = abs(x11 - x21) <= 1 and abs(y11 - y21) <= 1 and abs(x12 - x22) <= 1 and abs(y12 - y22) <= 1

        return same_box or (overlap_ratio > threshold)

def filter_empty_bboxes(bboxes, img_binary):
    filtered = []
    removed = 0

    for (x1, y1, x2, y2) in bboxes:
        box_crop = img_binary[y1:y2, x1:x2]

        if box_crop.size == 0:
            removed += 1
            continue
        white_pixels = int(np.sum(box_crop == 255))

        if white_pixels == 0:
            removed += 1
            continue
        filtered.append((x1, y1, x2, y2))
    
    logger.info(f"Bboxes eliminados por tener 0 pixeles blancos: {removed}", queue = constantes.QUEUE_LOGGER_VALUE_PROCESS)
    return filtered

def merge_group(base, others, used, index, threshold = 0.25, padding = 1):
        x1, y1, x2, y2 = base
        for j, other in enumerate(others):
            if index != j and not used[j] and boxes_overlap(base, other, threshold, padding):
                x1 = min(x1, other[0])
                y1 = min(y1, other[1])
                x2 = max(x2, other[2])
                y2 = max(y2, other[3])
                used[j] = True
                return (x1, y1, x2, y2), True
        return base, False

def merge_boxes_global(boxes, threshold=0.25, padding=1):
    if not boxes:
        return []
    merged = list({tuple(map(int, b)) for b in boxes})
    merged_flag = True

    while merged_flag:
        merged_flag = False
        used = [False] * len(merged)
        new_merged = []

        for i, box in enumerate(merged):
            if used[i]:
                continue

            current = box
            used[i] = True

            updated = True
            while updated:
                current, updated = merge_group(current, merged, used, i, threshold, padding)
                merged_flag |= updated

            new_merged.append(current)
        merged = list({tuple(map(int, b)) for b in new_merged})
    return merged

def process_seccion_total(bboxes, filtered_img, overlap_threshold = 0.25):
    if not bboxes:
        return {}

    logger.info(f"Total bboxes originales: {len(bboxes)}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    if filtered_img is not None:
        bboxes = filter_empty_bboxes(bboxes, filtered_img)

    merged_bboxes = merge_boxes_global(bboxes, overlap_threshold)
    logger.info(f"Total bboxes luego de merge: {len(merged_bboxes)}", queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)
    return merged_bboxes

def inter(a, b):
    ax1, ay1, ax2, ay2 = a
    bx1, by1, bx2, by2 = b

    ix1 = max(ax1, bx1)
    iy1 = max(ay1, by1)
    ix2 = min(ax2, bx2)
    iy2 = min(ay2, by2)

    if ix2 > ix1 and iy2 > iy1:
        return (ix1, iy1, ix2, iy2), (ix2 - ix1) * (iy2 - iy1)

    return None, 0

def evaluate_bb_in_tables(bboxes, matriz_info, filtered_img):
    if not bboxes:
        return [], []

    bboxes = filter_empty_bboxes(bboxes, filtered_img)
    merged_bboxes = merge_boxes_global(bboxes)

    col_left, col_right = matriz_info[0]
    frontera = (col_left[2] + col_right[0]) // 2

    H, W = filtered_img.shape[:2]
    left_cell = (0, 0, frontera, H)
    right_cell = (frontera, 0, W, H)

    frag_total = []
    frag_pref = []

    def process_left(inter_right):
        gx1, gy1, gx2, gy2 = inter_right
        lx1 = max(gx1 - frontera, 0)
        lx2 = max(gx2 - frontera, 0)
        frag_pref.append({
            "global": inter_right,
            "local": (lx1, gy1, lx2, gy2)
        })

    def process_right(inter_left):
        frag_total.append({"global": inter_left})

    for box in merged_bboxes:
        inter_left, area_left = inter(box, left_cell)
        inter_right, area_right = inter(box, right_cell)

        if area_left == 0 and area_right == 0:
            continue

        if area_left > area_right:
            if inter_right:
                process_left(inter_right)
        else:
            if inter_left:
                process_right(inter_left)

    return frag_total, frag_pref

def flatten_cells(table_data):
    cells = []
    for i, row in enumerate(table_data):
        for j, cell in enumerate(row):
            if isinstance(cell, (list, tuple)) and len(cell) >= 4:
                cx1, cy1, cx2, cy2 = map(int, cell)
                cells.append((i, j, cx1, cy1, cx2, cy2))
    return cells

def cells_intersections(box, cells):
    bx1, by1, bx2, by2 = box
    best_cell = None
    best_area = 0
    inters = []

    for i, j, cx1, cy1, cx2, cy2 in cells:
        ix1 = max(bx1, cx1)
        iy1 = max(by1, cy1)
        ix2 = min(bx2, cx2)
        iy2 = min(by2, cy2)

        w = max(0, ix2 - ix1)
        h = max(0, iy2 - iy1)
        area = w * h

        if area > 0:
            inters.append((i, j, cx1, cy1, cx2, cy2, ix1, iy1, ix2, iy2, area))
            if area > best_area:
                best_area = area
                best_cell = (i, j, cx1, cy1, cx2, cy2)

    return best_cell, inters

def cells_assign_local(cell_map, best_cell, box):
    i, j, cx1, cy1, _, _ = best_cell
    bx1, by1, bx2, by2 = box

    cell_map.setdefault((i, j), []).append({
        "type": "LOCAL",
        "local": (bx1 - cx1, by1 - cy1, bx2 - cx1, by2 - cy1),
        "global": box
    })

def cells_assign_fragments(cell_map, inters, best_cell, min_cell_coverage=0.25):
    fragments = 0

    for i, j, cx1, cy1, cx2, cy2, ix1, iy1, ix2, iy2, inter_area in inters:
        if best_cell and (i, j) == best_cell[:2]:
            continue

        cell_area = (cx2 - cx1) * (cy2 - cy1)

        if cell_area == 0:
            continue

        coverage_ratio = inter_area / cell_area

        if coverage_ratio >= min_cell_coverage:
            cell_map.setdefault((i, j), []).append({
                "type": "LOCAL",
                "local": (ix1 - cx1, iy1 - cy1, ix2 - cx1, iy2 - cy1),
                "global": (ix1, iy1, ix2, iy2)
            })
            continue

        cell_map.setdefault((i, j), []).append({
            "type": "FRAGMENT",
            "local": (ix1 - cx1, iy1 - cy1, ix2 - cx1, iy2 - cy1),
            "global": (ix1, iy1, ix2, iy2)
        })
        fragments += 1
    return fragments

def _assign_bboxes_to_cells_sort(cell_bboxes_map):
    for key, items in cell_bboxes_map.items():
        items.sort(key=lambda e: e["local"][0] + (e["local"][2] - e["local"][0]) / 2)
        cell_bboxes_map[key] = items


def assign_bboxes_to_cells(bboxes, table_data, filtered_img=None, overlap_threshold=0.85):
    if not bboxes:
        return {}

    logger.info(f"Total bboxes originales: {len(bboxes)}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    if filtered_img is not None:
        bboxes = filter_empty_bboxes(bboxes, filtered_img)

    merged_bboxes = merge_boxes_global(bboxes, overlap_threshold)

    logger.info(f"Total bboxes luego de merge: {len(merged_bboxes)}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    cells = flatten_cells(table_data)

    cell_bboxes_map = {}
    assigned_count = 0
    fragment_count = 0

    for box in merged_bboxes:
        best_cell, inters = cells_intersections(box, cells)

        if not inters:
            continue

        if best_cell:
            cells_assign_local(cell_bboxes_map, best_cell, box)
            assigned_count += 1

        fragment_count += cells_assign_fragments(
            cell_bboxes_map, inters, best_cell
        )

    logger.info(f"LOCAL assigned: {assigned_count}, FRAGMENTS created: {fragment_count}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    total = sum(len(v) for v in cell_bboxes_map.values())
    logger.info(f"Total bboxes asignados (incl fragments): {total}",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    _assign_bboxes_to_cells_sort(cell_bboxes_map)

    logger.info("Bboxes dentro de cada celda ordenados por X-center.",queue=constantes.QUEUE_LOGGER_VALUE_PROCESS)

    return cell_bboxes_map