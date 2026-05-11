package pe.gob.onpe.scebackend.model.service.reporte;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteOmisosDto;
import java.util.List;

public interface ReporteOmisosHandler {
    List<ReporteOmisosDto> obtenerReporte(ReporteOmisosRequestDto filtro);
    String getNombreColumna();
    Integer getTipoReporteActorElectoral();
    String getTituloReporte();
    String getNombreReporteResumen();
    String getNombreReporteDetalle();
}
