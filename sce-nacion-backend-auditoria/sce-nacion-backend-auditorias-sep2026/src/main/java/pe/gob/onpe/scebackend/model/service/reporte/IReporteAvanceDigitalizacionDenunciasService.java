package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;

public interface IReporteAvanceDigitalizacionDenunciasService {
    byte[] reporteAvanceDigitalizacionDenuncias(ReporteMesasObservacionesRequestDto filtro) throws JRException;
}
