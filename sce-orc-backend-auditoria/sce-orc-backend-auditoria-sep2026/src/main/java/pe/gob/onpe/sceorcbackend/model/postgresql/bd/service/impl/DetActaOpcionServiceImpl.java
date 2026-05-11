package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaOpcionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaOpcionService;

@Service
public class DetActaOpcionServiceImpl implements DetActaOpcionService {

	private final DetActaOpcionRepository detActaOpcionRepository;

	public DetActaOpcionServiceImpl(DetActaOpcionRepository detActaOpcionRepository){
			this.detActaOpcionRepository = detActaOpcionRepository;
	}
	
	@Override
	public void save(DetActaOpcion k) {
		this.detActaOpcionRepository.save(k);
	}

	@Override
	public void saveAll(List<DetActaOpcion> k) {
		this.detActaOpcionRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.detActaOpcionRepository.deleteAll();
	}

	@Override
	public List<DetActaOpcion> findAll() {
		return this.detActaOpcionRepository.findAll();
	}

	@Override
	public Optional<DetActaOpcion> findById(Integer id) {
		return this.detActaOpcionRepository.findById(id);
	}

	@Override
	public Optional<DetActaOpcion> getDetActaOpcionByDetActaAndPosicion(DetActa detActa, Integer posicion) {
		return this.detActaOpcionRepository.findByDetActaAndPosicion(detActa, posicion);
	}

	@Override
	public List<DetActaOpcion> findByDetActa(DetActa detActa) {
		return this.detActaOpcionRepository.findByDetActa(detActa);
	}

	@Override
	public List<DetActaOpcion> findByDetActaOrderByPosicion(DetActa detActa) {
		return this.detActaOpcionRepository.findByDetActaOrderByPosicion(detActa);
	}

	@Override
	public Long obtenerSumaVotosPrimerDetActa(Long actaId) {
		return this.detActaOpcionRepository.obtenerSumaVotosPrimerDetActa(actaId);
	}

	@Override
	public void anularDetActaOpcionContabilizada(Long actaId, Long valorPasarNulos, Long posicionNulos) {
		this.detActaOpcionRepository.anularDetActaOpcionContabilizada(actaId, valorPasarNulos, posicionNulos);
	}

	@Override
	public void deleteAllInBatch() {
		this.detActaOpcionRepository.deleteAllInBatch();
	}
}
