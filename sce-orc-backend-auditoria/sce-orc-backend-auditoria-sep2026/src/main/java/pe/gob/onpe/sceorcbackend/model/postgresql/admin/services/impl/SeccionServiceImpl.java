package pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.repository.SeccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.SeccionService;

@Service
public class SeccionServiceImpl implements SeccionService {

	private final SeccionRepository seccionRepository;
	
	public SeccionServiceImpl(
			SeccionRepository adminSeccionRepository) {
		this.seccionRepository = adminSeccionRepository;
	}
	
	@Override
	@Transactional
	public void save(Seccion k) {
		this.seccionRepository.save(k);
	}

	@Override
	@Transactional
	public void saveAll(List<Seccion> k) {
		this.seccionRepository.saveAll(k);
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.seccionRepository.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Seccion> findAll() {
		return this.seccionRepository.findAll();
	}

	@Override
	public Optional<Seccion> findByAbreviatura(String abreviatura) {
		return seccionRepository.findByAbreviatura(abreviatura);
	}

	@Override
	public Optional<Seccion> findByNombre(String nombre) {
		return seccionRepository.findByNombre(nombre);
	}

	@Override
	public List<Seccion> findIdsByAbreviaturas(List<String> abreviaturas) {
		return seccionRepository.findIdsByAbreviaturas(abreviaturas);
	}
}
