package pe.gob.onpe.scebatchpr.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;


public interface TransmisionMqService {

	void enviarTramaSce()  throws IOException, InterruptedException ;
	void enviarArchivos()  throws JsonProcessingException, InterruptedException ;
	
}
