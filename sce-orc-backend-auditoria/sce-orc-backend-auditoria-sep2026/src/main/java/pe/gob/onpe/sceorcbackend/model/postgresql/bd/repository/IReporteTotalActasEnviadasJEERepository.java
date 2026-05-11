package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;

import java.sql.SQLException;
import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;

public interface IReporteTotalActasEnviadasJEERepository {
	List<ReporteTotalActasEnviadasJEEDto> listarReporteTotalActasEnviadasJEE(ReporteTotalEnviadaJEERequestDto filtro) throws SQLException;
}
