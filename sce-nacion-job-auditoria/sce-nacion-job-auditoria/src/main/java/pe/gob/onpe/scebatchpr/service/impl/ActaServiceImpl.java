package pe.gob.onpe.scebatchpr.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pe.gob.onpe.scebatchpr.entities.orc.Acta;
import pe.gob.onpe.scebatchpr.entities.orc.Archivo;
import pe.gob.onpe.scebatchpr.repository.orc.ActaRepository;
import pe.gob.onpe.scebatchpr.repository.orc.ArchivoRepository;
import pe.gob.onpe.scebatchpr.service.ActaService;

@Service
public class ActaServiceImpl implements ActaService {

	@Autowired
	private ArchivoRepository archivoRepository;
	
	@Autowired
	private ActaRepository actaRepository;
	
	@Override
	@Transactional
	public void guardar(Archivo archivo, Long idActa) {
		Optional<Acta> op = this.actaRepository.findById(idActa);
		if(op.isPresent()){
			this.archivoRepository.save(archivo);
			Acta acta = op.get();
	        acta.setArchivoEscrutinioFirmado(archivo);
	        this.actaRepository.save(acta);
		}
	}

}
