package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.ProcesoAmbitoDto;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;

import java.util.List;
import java.util.Optional;

public interface IProcesoElectoralService {

    List<ProcesoElectoral> findAll();

    ProcesoElectoral findByActivo();

    ProcesoAmbitoDto getTipoAmbito(String acronimo);

    ProcesoAmbitoDto getTipoAmbitoPorIdProceso(Long idProceso);

    Optional<ProcesoElectoral> findByAcronimo(String acronimo);
	
}
