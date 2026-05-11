package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.ActaContabilizadaResumenReporte;
import pe.gob.onpe.scebackend.model.dto.FiltroContabilizacionActa;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.service.IReporteContabilizacionVotoService;

@Service
public class ReporteContabilizacionVotoService implements IReporteContabilizacionVotoService {

    @Autowired
    private ActaRepository reportesRepository;

    @Override
    public ActaContabilizadaResumenReporte contabilizarVotosPorMesa(FiltroContabilizacionActa filtro) {
        return null;
    }

    @Override
    public byte[] getReporteContabilizarVotosPorMesa(FiltroContabilizacionActa filtro) {
        return new byte[0];
    }
}
