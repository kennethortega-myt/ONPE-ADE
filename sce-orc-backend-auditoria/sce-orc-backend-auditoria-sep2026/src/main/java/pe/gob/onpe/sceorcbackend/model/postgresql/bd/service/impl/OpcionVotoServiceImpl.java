package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OpcionVoto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OpcionVotoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OpcionVotoService;

@Service
public class OpcionVotoServiceImpl implements OpcionVotoService {

	private final OpcionVotoRepository opcionVotoRepository;
	
	 public OpcionVotoServiceImpl(OpcionVotoRepository  opcionVotoRepository){
	        this.opcionVotoRepository = opcionVotoRepository;
	    }
	
	@Override
	public void save(OpcionVoto k) {
		this.opcionVotoRepository.save(k);
	}

	@Override
	public void saveAll(List<OpcionVoto> k) {
		this.opcionVotoRepository.saveAll(k);
	}

	@Override
	public void deleteAll() {
		this.opcionVotoRepository.deleteAll();
	}

	@Override
	public List<OpcionVoto> findAll() {
		return this.opcionVotoRepository.findAll();
	}

}
