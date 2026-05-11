package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaTransmisionNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.GenerarSecuenciaService;

@Service
public class GenerarSecuenciaServiceImpl implements GenerarSecuenciaService {

	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String dbSchema;
	
	@Autowired
	private ActaTransmisionNacionRepository generarSecuenciaRepository;
	
	@Override
	public void resetearSecuencias() {
		this.generarSecuenciaRepository.resetearSecuencias(dbSchema);
	}
	
}
