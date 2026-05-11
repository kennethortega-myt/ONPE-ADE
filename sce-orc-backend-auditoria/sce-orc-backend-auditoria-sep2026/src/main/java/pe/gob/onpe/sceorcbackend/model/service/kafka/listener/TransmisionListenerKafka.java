package pe.gob.onpe.sceorcbackend.model.service.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;

@Component
@Slf4j
public class TransmisionListenerKafka {

	@Autowired
	private ActaTransmisionDataService actaTransmisionDataService;

	@KafkaListener(topics = "${kafka.topic.response}")
	public void escucharRespuestas(@Payload TransmisionResponseDto respuesta,
			@Header(name = "correlationId", required = false) byte[] correlationIdBytes) {

		String correlationId = correlationIdBytes != null ? new String(correlationIdBytes) : null;

		log.info("¡RESPUESTA RECIBIDA! CorrelationId: {}", correlationId);

		this.actaTransmisionDataService.actualizarEstado(
				respuesta.getIdTransmision(), 
				respuesta.getEstado(), 
				respuesta.getMensaje());

	}

}