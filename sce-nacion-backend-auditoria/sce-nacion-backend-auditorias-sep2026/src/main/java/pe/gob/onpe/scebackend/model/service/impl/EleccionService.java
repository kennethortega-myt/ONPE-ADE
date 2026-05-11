package pe.gob.onpe.scebackend.model.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.EleccionDto;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.service.IEleccionService;

@Service
public class EleccionService implements IEleccionService {

	@Autowired
    EleccionRepository  eleccionRepository;

	// para nacion
    @Override
    @Transactional("locationTransactionManager")
	public List<EleccionDto> listByProcesoId(Long id) {
    	List<Eleccion> elecciones = this.eleccionRepository.findByProcesoElectoralId(id);
    
    	List<EleccionDto> eleccionesDto = elecciones.parallelStream()
    		    .map(eleccion -> {
    		        EleccionDto eleccionDto = new EleccionDto();
    		        eleccionDto.setId(eleccion.getId());
    		        eleccionDto.setNombre(eleccion.getNombre());
								eleccionDto.setCodigo(eleccion.getCodigo());
								eleccionDto.setActivo(eleccion.getActivo());
    		        return eleccionDto;
    		    })
    		    .collect(Collectors.toList());
    	
    	return eleccionesDto;
    	
	}

	 @Override
    @Transactional("locationTransactionManager")
	public List<EleccionDto> listEleccPreferencialByProcesoId(Long id) {
    	List<Eleccion> elecciones = this.eleccionRepository.findEleccPreferencialByProcesoElectoralId(id);
    
    	List<EleccionDto> eleccionesDto = elecciones.parallelStream()
    		    .map(eleccion -> {
    		        EleccionDto eleccionDto = new EleccionDto();
    		        eleccionDto.setId(eleccion.getId());
    		        eleccionDto.setNombre(eleccion.getNombre());
								eleccionDto.setCodigo(eleccion.getCodigo());
								eleccionDto.setActivo(eleccion.getActivo());
    		        return eleccionDto;
    		    })
    		    .collect(Collectors.toList());
    	
    	return eleccionesDto;
    	
	}

}
