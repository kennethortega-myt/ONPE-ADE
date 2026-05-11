package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.transmision.TransmisionResponseDto;

public interface ActaTransmisionHttpService {

	TransmisionResponseDto transmitir(Long idTransmision, String proceso, String usuarioTransmision);
	
	
	
}
