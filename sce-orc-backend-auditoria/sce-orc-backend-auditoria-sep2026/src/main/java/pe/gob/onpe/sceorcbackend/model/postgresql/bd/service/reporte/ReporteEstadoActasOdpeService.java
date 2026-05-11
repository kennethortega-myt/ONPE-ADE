package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.EstadoActasOdpeReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroEstadoActasOdpeDto;

public interface ReporteEstadoActasOdpeService {
	
	byte[] reporteEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro);
	EstadoActasOdpeReporteDto getListaEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro);
}
