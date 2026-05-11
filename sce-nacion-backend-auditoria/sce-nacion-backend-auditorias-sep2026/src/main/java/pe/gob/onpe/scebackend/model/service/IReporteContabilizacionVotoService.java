package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.ActaContabilizadaResumenReporte;
import pe.gob.onpe.scebackend.model.dto.FiltroContabilizacionActa;

public interface IReporteContabilizacionVotoService {

    ActaContabilizadaResumenReporte contabilizarVotosPorMesa(FiltroContabilizacionActa filtro);

    byte[] getReporteContabilizarVotosPorMesa(FiltroContabilizacionActa filtro);
}
