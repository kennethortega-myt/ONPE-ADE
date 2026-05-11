package pe.gob.onpe.scebatchpr.service;

import pe.gob.onpe.scebatchpr.entities.orc.Archivo;

public interface ActaService {

	void guardar(Archivo archivo, Long idActa);
	
}
