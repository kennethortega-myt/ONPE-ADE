package pe.gob.onpe.scebatchpr.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebatchpr.repository.orc.TabPrTransmisionRepository;
import pe.gob.onpe.scebatchpr.service.TabPrTransmisionService;
import pe.gob.onpe.scebatchpr.utils.Utils;

@Service
public class TabPrTransmisionServiceImpl implements TabPrTransmisionService{

	private static final Logger LOG = LoggerFactory.getLogger(TabPrTransmisionServiceImpl.class);

	private TabPrTransmisionRepository repository;
	
	public TabPrTransmisionServiceImpl(
			TabPrTransmisionRepository repository){
		this.repository = repository;
	}

	@Override
	@Transactional
	public int actualizarEstado(Long idTransferencia, Integer newState, String mensaje) {
		LOG.info("Se procede actualizar el estado de la transferencia");
		return repository.actualizarEstado(idTransferencia, newState, Utils.truncate(mensaje,255));
	}

	@Override
	@Transactional
	public int actualizarCorrelativo(Long idTransferencia, String correlativo) {
		return repository.actualizarCorrelativo(idTransferencia, correlativo);
	}

	@Override
	@Transactional
	public int actualizarEnviadoPorCorrelativo(String correlativo, Integer newState) {
		return repository.actualizarEnviadoPorCorrelativo(correlativo, newState);
	}

}
