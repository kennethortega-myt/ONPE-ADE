package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTotalEnviadaJEERequestDto;

import java.sql.SQLException;

public interface IReporteTotalActasEnviadasJEEService {
    byte[] reporte(ReporteTotalEnviadaJEERequestDto filtro, String authorization) throws JRException, SQLException;
}
