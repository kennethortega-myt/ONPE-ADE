package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.response.trazabilidad.ActaHistorialResumenProjection;
import pe.gob.onpe.scebackend.model.dto.response.trazabilidad.ItemHistory;

public interface TrazabilidadService {

    ItemHistory switchItemHistoryByEstado(ActaHistorialResumenProjection item);
}
