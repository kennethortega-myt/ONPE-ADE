package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;

public interface IReporteMesasSinOmisosService {
    byte[] reporteMesasSinOmisos(ReporteMesasSinOmisosRequestDto filtro) throws JRException;
}
