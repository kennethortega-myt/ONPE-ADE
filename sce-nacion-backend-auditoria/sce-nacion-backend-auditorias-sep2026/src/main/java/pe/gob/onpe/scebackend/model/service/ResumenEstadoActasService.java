package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.reportes.EstadoActasOdpeReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroEstadoActasOdpeDto;

public interface ResumenEstadoActasService {
    byte[] reporteResumenEstadoActas(FiltroEstadoActasOdpeDto filtro);
    EstadoActasOdpeReporteDto getListaResumenEstadoActas(FiltroEstadoActasOdpeDto filtro);
}
