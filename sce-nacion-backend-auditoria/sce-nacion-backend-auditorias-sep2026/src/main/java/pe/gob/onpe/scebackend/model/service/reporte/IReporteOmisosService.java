package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;

public interface IReporteOmisosService {
    byte[] reporteOmisos(ReporteOmisosRequestDto filtro) throws JRException;
}
