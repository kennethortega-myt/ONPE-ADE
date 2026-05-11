package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.sceorcbackend.model.dto.queue.EnvioTransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionHttpService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionPushService;
import pe.gob.onpe.sceorcbackend.model.service.kafka.producer.TransmisionProducerKafka;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionResetService;

import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.EnvioRequest;


@Service
public class ActaTransmisionPushServiceImpl implements ActaTransmisionPushService {

	@Autowired
	private ActaTransmisionDataService actaTransmisionDataService;
	
	@Autowired
	private ActaTransmisionHttpService actaTransmisionHttpService;
	
	@Autowired
	private ActaTransmisionResetService actaTransmisionResetService;
	
	@Autowired
	private TransmisionProducerKafka transmisionProducerService;
	
	@Value("${app.orc.transmision.mq.kafka}")
    private boolean habilitarKafka;
	
	
	@Override
	public boolean empujar(Long idActa, String proceso, String usr) {
		if(habilitarKafka){
			return this.empujarKafka(idActa, proceso, usr);
		} else {
			return this.empujarMq(idActa, proceso, usr);
		}
	}
	
	private boolean empujarMq(Long idActa, String proceso, String usr) {
		TransmisionResponseDto resultado = null;
		List<ActaTransmisionNacion> transmisiones = actaTransmisionDataService.findByIdActaConTransmisionesOrdenadas(idActa);
		for(ActaTransmisionNacion transmision:transmisiones){
			resultado = actaTransmisionHttpService.transmitir(transmision.getId(), proceso, usr);
			if(resultado==null || !resultado.isExitoso()){
				return false;
			}
		}
		// Si todas fueron exitosas, retorna true
	    return true;
	}
	

	private boolean empujarKafka(Long idActa, String proceso, String usr) {
		boolean resultado = true;
		List<ActaTransmisionNacion> transmisiones = actaTransmisionDataService.findByIdActaConTransmisionesOrdenadas(idActa);
		for(ActaTransmisionNacion transmision:transmisiones){
			this.transmisionProducerService.enviarActa(
					EnvioTransmisionQueue.builder()
					.idTransmision(transmision.getId())
					.exitoso(true)
					.proceso(proceso)
					.usuario(usr)
					.build()
					);
		}
		return resultado;
	}

	
	@Override
	@Transactional(readOnly = true)
	public boolean verificarTransmisionesEjecucion(Long idActa){
		Integer cantidadE = this.actaTransmisionDataService.contarEnEjecucion(idActa);
		return cantidadE>0;
	}

	@Override
	public boolean resetear(Long idActa, String proceso, String usr) {
		List<ActaTransmisionNacion> transmisiones = actaTransmisionDataService.findByIdActaTodasTransmisionesOrdenadas(idActa);
		EnvioRequest request = new EnvioRequest();
		request.setIdActa(idActa);
		request.setProceso(proceso);
		request.setTransmisiones(transmisiones);
		return this.actaTransmisionResetService.enviarConPrioridad(request);
		
	}

}
