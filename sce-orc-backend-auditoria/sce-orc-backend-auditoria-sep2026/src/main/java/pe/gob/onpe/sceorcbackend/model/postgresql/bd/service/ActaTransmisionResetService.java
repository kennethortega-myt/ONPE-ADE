package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.EnvioRequest;

public interface ActaTransmisionResetService {

	boolean enviarConPrioridad(EnvioRequest requestEnvio);
	
}
