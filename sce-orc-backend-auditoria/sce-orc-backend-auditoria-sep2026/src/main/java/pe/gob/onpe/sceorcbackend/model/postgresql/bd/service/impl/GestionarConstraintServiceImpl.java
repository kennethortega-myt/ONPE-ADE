package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaTransmisionNacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.GestionarConstraintService;

@Service
public class GestionarConstraintServiceImpl implements GestionarConstraintService {

	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String dbSchema;
	
	@Autowired
	private ActaTransmisionNacionRepository generarSecuenciaRepository;
	
	@Override
	public void eliminarConstraintMiembroMesaSorteado() {
		this.generarSecuenciaRepository.eliminarConstraintMiembroMesaSorteado(dbSchema);
	}

	@Override
	public void crearConstraintMiembroMesaSorteado() {
		this.generarSecuenciaRepository.crearConstraintMiembroMesaSorteado(dbSchema);
	}
	
	@Override
	public void eliminarConstraintMiembroMesaCola() {
		this.generarSecuenciaRepository.eliminarConstraintMiembroMesaCola(dbSchema);
	}

	@Override
	public void crearConstraintMiembroMesaCola() {
		this.generarSecuenciaRepository.crearConstraintMiembroMesaCola(dbSchema);
	}
	
	
	@Override
	public void eliminarConstraintOmisoVotante() {
		this.generarSecuenciaRepository.eliminarConstraintOmisoVotante(dbSchema);
	}

	@Override
	public void crearConstraintOmisoVotante() {
		this.generarSecuenciaRepository.crearConstraintOmisoVotante(dbSchema);
	}
	

}
