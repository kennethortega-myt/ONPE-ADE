package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.AmbitoElectoralRequestDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.AmbitoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.AmbitoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IAmbitoElectoralNacionService;

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
        		.map(this::mapearAmbitoElectoral).toList();
    }

    @Override
    public List<AmbitoElectoralDto> listarAmbitoElectoralPorEleccion(AmbitoElectoralRequestDto filtro) {
        return ambitoElectoralNacionRepository
        		.listarAmbitoElectoralPorEleccion(filtro.getEsquema(), filtro.getIdEleccion())
        		.stream()
        		.map(this::mapearAmbitoElectoral).toList();
    }
    
    private AmbitoElectoralDto mapearAmbitoElectoral(Map <String, Object> ambitoMap) {
    	AmbitoElectoralDto ambito = new AmbitoElectoralDto();
    	Integer id = (Integer) ambitoMap.get("n_ambito_electoral_pk");
    	ambito.setId(id.longValue()) ;
    	ambito.setCodigo((String) ambitoMap.get("c_codigo"));
		ambito.setNombre((String) ambitoMap.get("c_nombre"));
		return ambito;
    }

}
