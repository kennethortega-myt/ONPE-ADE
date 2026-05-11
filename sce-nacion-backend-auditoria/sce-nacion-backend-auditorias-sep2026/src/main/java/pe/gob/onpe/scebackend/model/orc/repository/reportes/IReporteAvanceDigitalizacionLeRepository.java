package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionLeDto;

import java.util.List;

public interface IReporteAvanceDigitalizacionLeRepository {
    List<ReporteAvanceDigitalizacionLeDto> listarReporteAvanceDigitalizacionLe(ReporteMesasObservacionesRequestDto filtro);
}
