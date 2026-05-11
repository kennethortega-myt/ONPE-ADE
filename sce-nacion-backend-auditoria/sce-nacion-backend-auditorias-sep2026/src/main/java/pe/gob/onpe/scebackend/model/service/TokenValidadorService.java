package pe.gob.onpe.scebackend.model.service;

public interface TokenValidadorService {

	boolean validarToken(String tokenBearer, String cc);
	
}
