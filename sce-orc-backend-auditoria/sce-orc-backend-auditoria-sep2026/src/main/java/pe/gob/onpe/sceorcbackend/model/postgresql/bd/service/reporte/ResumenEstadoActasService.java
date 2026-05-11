package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import pe.gob.onpe.sceorcbackend.model.dto.EstadoActasOdpeReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroEstadoActasOdpeDto;

public interface ResumenEstadoActasService {
    byte[] reporteResumenEstadoActas(FiltroEstadoActasOdpeDto filtro);
    EstadoActasOdpeReporteDto getResumenListaEstadoActas(FiltroEstadoActasOdpeDto filtro);
}
