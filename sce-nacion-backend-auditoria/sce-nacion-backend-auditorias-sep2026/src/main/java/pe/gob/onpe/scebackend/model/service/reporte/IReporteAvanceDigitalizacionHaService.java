package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;

public interface IReporteAvanceDigitalizacionHaService {
    byte[] reporteAvanceDigitalizacionHa(ReporteMesasObservacionesRequestDto filtro) throws JRException;
}
