package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteComparacionOmisosAusentismoRequestDto;

public interface IReporteComparacionOmisosAusentismoService {
    byte[] reporteComparacionOmisosAusentismo(ReporteComparacionOmisosAusentismoRequestDto filtro) throws JRException;
}
