package pe.gob.onpe.scebackend.model.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.OrcDetalleCatalogoEstructuraRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.IProcesoElectoralService;

import java.util.List;
import java.util.Optional;

@Service
public class ProcesoElectoralService implements IProcesoElectoralService {

    public static final Integer ID_TABLA_MAE_PROCESO_ELECTORAL = 1;

    public static final String C_COLUMNA_TIPO_AMBITO_ELECTORAL = "n_tipo_ambito_electoral";

    @Autowired
    private ProcesoElectoralRepository maeProcesoElectoralRepository;

    @Autowired
    private OrcDetalleCatalogoEstructuraRepository orcDetalleCatalogoEstructuraRepository;

    public List<ProcesoElectoral> findAll() {
        return this.maeProcesoElectoralRepository.findAll();
    }

    @Override
    public ProcesoElectoral findByActivo() {
        return null;
    }

    @Override
    public ProcesoAmbitoDto getTipoAmbito(String acronimo) {
        ProcesoAmbitoDto procesoAmb = new ProcesoAmbitoDto();

        return procesoAmb;
    }

    @Override
    @Transactional("locationTransactionManager")
    public ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso) {
        ProcesoAmbitoDto procesoAmb = null;
        Optional<ProcesoElectoral> procesoOp = this.maeProcesoElectoralRepository.findById(idProceso);
        if(procesoOp.isPresent()) {
            procesoAmb = new ProcesoAmbitoDto();
            ProcesoElectoral proceso = procesoOp.get();
            OrcDetalleCatalogoEstructura estructura = this.orcDetalleCatalogoEstructuraRepository.findByCatalogoIdAndColumnaAndCodigoI(ID_TABLA_MAE_PROCESO_ELECTORAL, C_COLUMNA_TIPO_AMBITO_ELECTORAL,
                    Math.toIntExact(proceso.getTipoAmbitoElectoral()));
            if(proceso!=null) {
                procesoAmb.setIdProceso(proceso.getId());
                procesoAmb.setNombreProceso(proceso.getNombre());
            }
            if(estructura!=null) {
                procesoAmb.setIdTipoAmbito(estructura.getCodigoI());
                procesoAmb.setNombreTipoAmbito(estructura.getCodigoS());
            }
        }
        return procesoAmb;
    }

	@Override
	public Optional<ProcesoElectoral> findByAcronimo(String acronimo) {
		return this.maeProcesoElectoralRepository.findByAcronimo(acronimo);
	}

}
