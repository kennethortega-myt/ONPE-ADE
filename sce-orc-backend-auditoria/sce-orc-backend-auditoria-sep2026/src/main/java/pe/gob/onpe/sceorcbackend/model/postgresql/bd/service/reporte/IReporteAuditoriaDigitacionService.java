package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteAuditoriaDigitacionActaRequestDto;

import java.sql.SQLException;

public interface IReporteAuditoriaDigitacionService {
    byte[] reporteAuditoriaDigitacion(ReporteAuditoriaDigitacionActaRequestDto filtro) throws JRException, SQLException;
}
