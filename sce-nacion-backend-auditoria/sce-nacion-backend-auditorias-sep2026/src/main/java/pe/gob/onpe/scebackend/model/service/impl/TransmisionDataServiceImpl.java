package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;
import pe.gob.onpe.scebackend.model.service.TransmisionDataService;
import pe.gob.onpe.scebackend.model.service.ITransmisionService;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;

import pe.gob.onpe.scebackend.model.orc.repository.PuestaCeroActaRepositoryCustom;

import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;

@Service
@Slf4j
public class TransmisionDataServiceImpl implements TransmisionDataService {

	@Autowired
	private ITransmisionService transmisionService;
	
	@Autowired
	private PuestaCeroActaRepositoryCustom puestaCeroActaRepositoryCustom;
	
	@Autowired
	private ActaRepository actaRepository;
	
	@Override
	@Transactional(
			transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class,
			propagation = Propagation.REQUIRES_NEW)
	public void recibirTransmision(TransmisionDto transmisionDto, String esquema, String correlationId, String cc,
			Integer orden) throws Exception {
		this.transmisionService.recibirTransmision(transmisionDto, esquema, correlationId, cc, orden);
	}

	@Override
	@Transactional(
			transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class)
	public void recibirReseteo(TransmisionNacionRequestDto request, 
			String esquema, 
			String correlationId, 
			String cc,
			String usuario) throws Exception {
		
		Optional<Acta> actaOp = actaRepository.findById(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
		if(actaOp.isPresent()){
		List<TransmisionDto> actasOrdenadas = request.getActasTransmitidas()
				    .stream()
				    .sorted(Comparator.comparingLong(TransmisionDto::getIdTransmision))
				    .collect(Collectors.toList());
			
			for (int i = 1; i <= actasOrdenadas.size(); i++) {
				TransmisionDto actaTras = actasOrdenadas.get(i - 1);
				this.transmisionService.recibirTransmision(actaTras, esquema, correlationId, cc, i);
			}
		} else {
			throw new Exception("El id de acta no existe");
		}
		
		
	}
	
	@Override
	@Transactional(
			transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class)
	public void executarPuestaCero(TransmisionNacionRequestDto request, 
			String esquema, 
			String correlationId, 
			String cc,
			String usuario) throws Exception {
		
		Optional<Acta> actaOp = actaRepository.findById(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
		if(actaOp.isPresent()){
			Acta acta = actaOp.get();
			
			log.info("Puesta cero del acta {} con mesa {} y eleccion {}", 
					acta.getId(),
					acta.getMesa().getCodigo(),
					acta.getUbigeoEleccion().getEleccion().getId().intValue()
					);
			
			Map<String, Object> resultado = this.puestaCeroActaRepositoryCustom.puestaCeroActa(
					esquema,
					acta.getMesa().getCodigo(),
					acta.getUbigeoEleccion().getEleccion().getId().intValue(),
					usuario);
			
			Integer resul = (Integer) resultado.get("po_resultado");
			
			if(resul!=1){
				throw new Exception("No se pudo realizar la puesta cero del acta");
			}
			
			log.info("Resultado de pc: {}", resul);
			
			
		} else {
			throw new Exception("El id de acta no existe");
		}
		
		
	}

}
