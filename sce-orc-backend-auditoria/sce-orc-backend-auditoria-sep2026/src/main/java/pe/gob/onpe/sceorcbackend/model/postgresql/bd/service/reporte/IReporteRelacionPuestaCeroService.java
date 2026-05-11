package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteRelacionPuestaCeroRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteRelacionPuestoCeroDto;

public interface IReporteRelacionPuestaCeroService {

	byte[] reporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro) throws JRException;
    List<ReporteRelacionPuestoCeroDto> consultaReporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro);
}
