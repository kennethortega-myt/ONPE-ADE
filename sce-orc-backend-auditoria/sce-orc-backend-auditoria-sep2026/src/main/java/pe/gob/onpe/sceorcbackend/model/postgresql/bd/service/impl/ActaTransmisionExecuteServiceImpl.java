package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.queue.TransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;

@Service
public class ActaTransmisionExecuteServiceImpl implements ActaTransmisionExecuteService {

	@Autowired
	private RabbitMqSender rabbitMqSender;
	
	
	@Override
	public void sincronizar(Long idActa, String proceso, TransmisionNacionEnum estadoEnum, String usuario) {
			this.rabbitMqSender.sendProcessTransmision(
					TransmisionQueue.builder()
					.idActa(idActa)
					.estadoEnum(estadoEnum)
					.usuario(usuario)
					.proceso(proceso)
					.build()
					);
	}
	
	@Override
	public void sincronizar(List<Long> idActas, String proceso, TransmisionNacionEnum estadoEnum, String usuario) {
			for(Long idActa:idActas){
				this.rabbitMqSender.sendProcessTransmision(
						TransmisionQueue.builder()
						.idActa(idActa)
						.estadoEnum(estadoEnum)
						.usuario(usuario)
						.proceso(proceso)
						.build()
						);
			}
	}


}
