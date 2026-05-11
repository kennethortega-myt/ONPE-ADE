package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.scebackend.model.service.IAgrupacionPoliticaService;

@RequiredArgsConstructor
@Service
public class AgrupacionPoliticaService implements IAgrupacionPoliticaService {

	private final AgrupacionPoliticaRepository agrupacionPoliticaRepository;
	
	@Override
	@Transactional(transactionManager = "locationTransactionManager")
	public Map<String, Object> cargarCandidatos(String piEsquema, Integer poResultado, String poMensaje){
		return this.agrupacionPoliticaRepository.cargarCandidatos(piEsquema, poResultado, poMensaje);
	}
	
}

