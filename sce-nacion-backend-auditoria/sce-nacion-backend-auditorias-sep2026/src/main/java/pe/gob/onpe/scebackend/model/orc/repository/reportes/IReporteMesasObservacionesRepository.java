package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasObservacionesDto;

import java.util.List;

public interface IReporteMesasObservacionesRepository {
    List<ReporteMesasObservacionesDto> listarReporteMesasObservaciones(ReporteMesasObservacionesRequestDto filtro);
}
