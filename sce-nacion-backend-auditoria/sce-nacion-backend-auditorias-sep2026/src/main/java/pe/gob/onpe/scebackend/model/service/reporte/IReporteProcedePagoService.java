package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteProcedePagoRequestDto;

public interface IReporteProcedePagoService {
    byte[] reporteProcedePago(ReporteProcedePagoRequestDto filtro) throws JRException;
}
