package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.omisos;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;

public interface IReporteOmisosService {
    byte[] reporteOmisos(ReporteOmisosRequestDto filtro) throws JRException;
}
