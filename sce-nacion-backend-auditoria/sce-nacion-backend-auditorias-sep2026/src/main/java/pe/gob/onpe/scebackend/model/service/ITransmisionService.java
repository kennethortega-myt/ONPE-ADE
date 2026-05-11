package pe.gob.onpe.scebackend.model.service;


import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;

public interface ITransmisionService {
	
	void recibirTransmision(TransmisionDto transmisionDto, 
			String esquema, 
			String correlationId, 
			String cc,
			Integer orden) throws Exception;
	
	TransmisionResponseDto recibirTransmisionPc(TransmisionDto actasDto, String esquema);
}
