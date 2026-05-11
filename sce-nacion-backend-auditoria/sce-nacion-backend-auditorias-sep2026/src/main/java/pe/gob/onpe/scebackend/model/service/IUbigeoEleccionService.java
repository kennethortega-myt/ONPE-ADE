package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.UbigeoNacionDTO;

public interface IUbigeoEleccionService {

	// para monitoreo
	UbigeoNacionDTO ubigeos(Long idEleccion);
	
}
