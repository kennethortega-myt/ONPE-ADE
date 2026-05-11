package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.omisos;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;

import java.util.List;

public interface ReporteOmisosHandler {
    List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro);
    String getNombreColumna();
    Integer getTipoReporteActorElectoral();
    String getTituloReporte();
    String getNombreReporteResumen();
    String getNombreReporteDetalle();
}
