package pe.gob.onpe.scebatchpr.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.InputStream;

public interface FirmaDigitalDocumentoService {

	InputStream firmarArchivo(String pathPdf) throws JsonProcessingException;
	
}
