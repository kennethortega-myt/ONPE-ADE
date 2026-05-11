package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRelacionPuestaCeroRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteRelacionPuestoCeroDto;

import java.util.List;

public interface IReporteRelacionPuestaCeroService {
    byte[] reporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro) throws JRException;
    List<ReporteRelacionPuestoCeroDto> consultaReporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro);
}
