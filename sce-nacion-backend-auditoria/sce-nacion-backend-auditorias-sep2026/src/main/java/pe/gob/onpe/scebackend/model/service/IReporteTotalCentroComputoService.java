package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.AvanceEstadoActaReporteDto;
import pe.gob.onpe.scebackend.model.dto.FiltroAvanceEstadoActaDto;

public interface IReporteTotalCentroComputoService {
    AvanceEstadoActaReporteDto getAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro);

    byte[] getReporteAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro);
}
