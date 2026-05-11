package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.service.TokenValidadorService;



@Service
public class TokenValidadorServiceImpl implements TokenValidadorService {
	
	private final CentroComputoRepository centroComputoRepository;
	
	public TokenValidadorServiceImpl(CentroComputoRepository centroComputoRepository) {
		super();
		this.centroComputoRepository = centroComputoRepository;
	}

	@Override
	public boolean validarToken(String tokenBearer, String cc) {
		boolean exitoso = false;
		if (tokenBearer != null && tokenBearer.startsWith("Bearer ")) {
            String tokenIn = tokenBearer.substring(7); // Elimina el prefijo "Bearer "
            Optional<CentroComputo> cep = centroComputoRepository.findByCodigo(cc);
            if(cep.isPresent()){
            	String tokenSaved = cep.get().getApiTokenBackedCc();
            	return tokenIn.equals(tokenSaved);
            }
		}
		return exitoso;
	}

}
