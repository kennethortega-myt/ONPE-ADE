package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteAuditoriaDigitacionActaRequestDto;

import java.sql.SQLException;

public interface IReporteAuditoriaDigitacionService {
    byte[] reporteAuditoriaDigitacion(ReporteAuditoriaDigitacionActaRequestDto filtro) throws JRException, SQLException;
}
