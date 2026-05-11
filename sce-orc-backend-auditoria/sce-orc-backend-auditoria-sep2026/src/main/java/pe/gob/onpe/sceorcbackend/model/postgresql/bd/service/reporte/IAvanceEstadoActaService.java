package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.AvanceEstadoActaReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroAvanceEstadoActaDto;

public interface IAvanceEstadoActaService {

    public AvanceEstadoActaReporteDto getAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro);

    public byte[] getReporteAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro);
}
