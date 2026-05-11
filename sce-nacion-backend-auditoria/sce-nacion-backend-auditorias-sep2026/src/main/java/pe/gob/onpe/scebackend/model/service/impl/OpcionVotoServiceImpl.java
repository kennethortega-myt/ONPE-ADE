package pe.gob.onpe.scebackend.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.orc.repository.OpcionVotoRepository;
import pe.gob.onpe.scebackend.model.service.IOpcionVotoService;

@Service
public class OpcionVotoServiceImpl implements IOpcionVotoService {

	@Autowired
	private OpcionVotoRepository opcionVotoRepository;
	
}
