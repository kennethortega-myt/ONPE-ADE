package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTotalEnviadaJEERequestDto;

public interface IReporteTotalActasEnviadasJEEService {
	byte[] reporte(ReporteTotalEnviadaJEERequestDto filtro, String authorization) throws JRException, SQLException;
}
