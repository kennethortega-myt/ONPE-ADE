package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.reportes.EstadoActasOdpeReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroEstadoActasOdpeDto;

public interface ReporteEstadoActasOdpeService {
	
	byte[] reporteEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro);
	EstadoActasOdpeReporteDto getListaEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro);
}
