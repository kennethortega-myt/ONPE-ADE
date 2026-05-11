package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import java.io.IOException;

import org.springframework.core.io.ByteArrayResource;

public interface InstaladorService {

	ByteArrayResource descargar() throws IOException;

	ByteArrayResource descargar(String subPath) throws IOException;
	
}
