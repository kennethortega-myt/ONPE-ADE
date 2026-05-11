package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.EleccionRequestDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.EleccionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.EleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IEleccionCustomService;


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
        Integer id = (Integer) rs.get("n_eleccion");
        
        dato.setId(id.longValue());
        dato.setCodigo((String) rs.get("c_codigo"));
        dato.setNombre((String) rs.get("c_nombre"));
        dato.setNombreVista((String) rs.get("c_nombre_vista"));
        dato.setPreferencial(pref.intValue());
        return dato;
    }
}
