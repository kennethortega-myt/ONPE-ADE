package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteComparacionOmisosAusentismoRequestDto;

public interface IReporteComparacionOmisosAusentismoService {
    byte[] reporteComparacionOmisosAusentismo(ReporteComparacionOmisosAusentismoRequestDto filtro) throws JRException;
}
