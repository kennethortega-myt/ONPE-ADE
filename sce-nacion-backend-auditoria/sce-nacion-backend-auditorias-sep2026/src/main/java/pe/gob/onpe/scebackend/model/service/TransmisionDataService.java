package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;

public interface TransmisionDataService {

	
	void recibirTransmision(TransmisionDto transmisionDto, 
			String esquema, 
			String correlationId, 
			String cc,
			Integer orden) throws Exception;
	
	void recibirReseteo(TransmisionNacionRequestDto request, 
			String esquema, 
			String correlationId, 
			String cc,
			String usuario) throws Exception;

	void executarPuestaCero(TransmisionNacionRequestDto request, String esquema, String correlationId, String cc,
			String usuario) throws Exception;
	
	
}
