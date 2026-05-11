package pe.gob.onpe.scebackend.model.service.impl.comun;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebackend.model.dto.request.comun.EleccionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.comun.EleccionDto;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.service.comun.IEleccionCustomService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EleccionCustomService implements IEleccionCustomService {

    private final EleccionRepository iEleccionRepositoryCustom;

    @Override
    public List<EleccionDto> obtenerEleccionPorProcesoElectoralId(EleccionRequestDto filtro) {
        return iEleccionRepositoryCustom
        		.obtenerEleccionPorProcesoElectoralId(filtro.getEsquema(), filtro.getIdProcesoElectoral())
        		.stream()
        		.map(this::llenarDatos).toList();
    }
    
    private EleccionDto llenarDatos(Map<String, Object> rs) {
        EleccionDto dato = new EleccionDto();
        Short pref = (Short) rs.get("n_preferencial");
        
        dato.setId((Integer) rs.get("n_eleccion"));
        dato.setCodigo((String) rs.get("c_codigo"));
        dato.setNombre((String) rs.get("c_nombre"));
        dato.setNombreVista((String) rs.get("c_nombre_vista"));
        dato.setPreferencial(pref.intValue());
        return dato;
    }
}
