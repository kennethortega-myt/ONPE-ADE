package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.orc.entities.TransmisionControl;
import pe.gob.onpe.scebackend.model.orc.repository.TransmisionControlRepository;
import pe.gob.onpe.scebackend.model.service.TransmisionControlService;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


@Service
public class TransmisionControlServiceImpl implements TransmisionControlService {


	private final TransmisionControlRepository transmisionControlRepository;
	
	public TransmisionControlServiceImpl(
			TransmisionControlRepository transmisionControlRepository){
		this.transmisionControlRepository = transmisionControlRepository;
	}
	

	@Override
	@Transactional(value = "locationTransactionManager", readOnly = true)
	public Integer getOrden(String codigoCc, Long idActa) {
	    return transmisionControlRepository.findByCodigoCcAndIdActa(codigoCc, idActa)
	            .map(TransmisionControl::getOrden)
	            .orElse(0);
	}
	
	@Override
	@Transactional(value = "locationTransactionManager")
	public void actualizarOrden(String codigoCc, Long idActa, Integer ordenRecibido){
		Optional<TransmisionControl> tc = transmisionControlRepository.findByCodigoCcAndIdActa(codigoCc, idActa);
		TransmisionControl transmisionControl = null;
		if(!tc.isPresent()){
			transmisionControl = new TransmisionControl();
			transmisionControl.setCodigoCc(codigoCc);
			transmisionControl.setIdActa(idActa);
			transmisionControl.setOrden(ordenRecibido);
			transmisionControl.setActivo(SceConstantes.ACTIVO);
			transmisionControl.setAudFechaCreacion(new Date());
			transmisionControl.setAudUsuarioCreacion(ConstantesComunes.USUARIO_SYSTEM);
		} else {
			transmisionControl = tc.get();
			transmisionControl.setOrden(ordenRecibido);
			transmisionControl.setAudFechaModificacion(new Date());
			transmisionControl.setAudUsuarioModificacion(ConstantesComunes.USUARIO_SYSTEM);
		}
		transmisionControlRepository.save(transmisionControl);
	}
	
}
