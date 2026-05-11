package pe.gob.onpe.scebatchpr.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionRequest;
import pe.gob.onpe.scebatchpr.dto.TramaSceDto;

public interface MqTransmisionService {

	public void productorData(List<TramaSceDto> message) throws JsonProcessingException, InterruptedException; 
	public void productorArchivos(ArchivoTransmisionRequest message) throws JsonProcessingException, InterruptedException; 
	
}
