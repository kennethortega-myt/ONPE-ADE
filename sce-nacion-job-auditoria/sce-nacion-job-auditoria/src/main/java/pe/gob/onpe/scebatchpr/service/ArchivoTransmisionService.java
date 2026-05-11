package pe.gob.onpe.scebatchpr.service;

import java.util.List;

import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionRequest;

public interface ArchivoTransmisionService {

	List<ArchivoTransmisionRequest> listarArchivosPendientes();
	
}
