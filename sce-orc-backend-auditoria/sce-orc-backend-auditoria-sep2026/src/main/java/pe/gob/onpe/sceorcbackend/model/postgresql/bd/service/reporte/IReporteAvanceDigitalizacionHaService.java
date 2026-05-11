package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.request.ReporteMesasObservacionesRequestDto;

public interface IReporteAvanceDigitalizacionHaService {
    byte[] reporteAvanceDigitalizacionHa(ReporteMesasObservacionesRequestDto filtro) throws JRException;
}
