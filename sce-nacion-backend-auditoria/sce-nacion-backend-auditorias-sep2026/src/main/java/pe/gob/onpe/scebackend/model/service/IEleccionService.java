package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.EleccionDto;



public interface IEleccionService {

	// para monitoreo
	List<EleccionDto> listByProcesoId(Long id);
	List<EleccionDto> listEleccPreferencialByProcesoId(Long id);
	
}
