package pe.gob.onpe.scebackend.model.service.impl.comun;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.comun.AmbitoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.AmbitoElectoralDto;
import pe.gob.onpe.scebackend.model.orc.repository.AmbitoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.comun.IAmbitoElectoralNacionService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AmbitoElectoralNacionService implements IAmbitoElectoralNacionService {

    private final AmbitoElectoralRepository ambitoElectoralNacionRepository;

    @Override
    public List<AmbitoElectoralDto> listarAmbitoElectoralPorCentroComputo(AmbitoElectoralRequestDto filtro) {
    	return ambitoElectoralNacionRepository
    				.listarAmbitoElectoralPorCentroComputo(filtro.getEsquema(), filtro.getIdCentroComputo())
    				.stream()
    				.map( this::mapearAmbitoElectoral).toList();
    	
    }
    
    @Override
    public List<AmbitoElectoralDto> listarAmbitoElectoralPorEleccion(AmbitoElectoralRequestDto filtro) {
    	return ambitoElectoralNacionRepository
    			.listarAmbitoElectoralPorEleccion(filtro.getEsquema(), filtro.getIdEleccion())
    			.stream()
				.map( this::mapearAmbitoElectoral).toList();
    }
    
    private AmbitoElectoralDto mapearAmbitoElectoral(Map <String, Object> ambitoMap) {
    	AmbitoElectoralDto ambito = new AmbitoElectoralDto();
    	ambito.setId((Integer) ambitoMap.get("n_ambito_electoral_pk"));
    	ambito.setCodigo((String) ambitoMap.get("c_codigo"));
		ambito.setNombre((String) ambitoMap.get("c_nombre"));
		return ambito;
    }
}
