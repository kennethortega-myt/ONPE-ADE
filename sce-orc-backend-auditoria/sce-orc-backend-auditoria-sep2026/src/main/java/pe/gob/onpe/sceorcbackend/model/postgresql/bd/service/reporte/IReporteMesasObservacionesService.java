package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.request.ReporteMesasObservacionesRequestDto;

public interface IReporteMesasObservacionesService {
    byte[] reporteMesasObservaciones(ReporteMesasObservacionesRequestDto filtro) throws JRException;
}
