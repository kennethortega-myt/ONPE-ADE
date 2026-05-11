package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;

import java.sql.SQLException;
import java.util.List;

public interface IReporteTotalActasEnviadasJEERepository {
    List<ReporteTotalActasEnviadasJEEDto> listarReporteTotalActasEnviadasJEE(ReporteTotalEnviadaJEERequestDto filtro) throws SQLException;
}
