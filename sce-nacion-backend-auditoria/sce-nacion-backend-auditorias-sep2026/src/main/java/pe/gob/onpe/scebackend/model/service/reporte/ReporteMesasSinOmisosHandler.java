package pe.gob.onpe.scebackend.model.service.reporte;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasSinOmisosDto;

import java.util.List;

public interface ReporteMesasSinOmisosHandler {
    List<ReporteMesasSinOmisosDto> obtenerReporte(ReporteMesasSinOmisosRequestDto filtro);
    Integer getTipoReporteActorElectoral();
    String getTituloReporte();

}
