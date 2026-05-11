package pe.gob.onpe.sceorcbackend.model.service.kafka.producer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.model.dto.queue.EnvioTransmisionQueue;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionMapperService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision.TransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Service
@Slf4j
public class TransmisionProducerKafka {

	private final KafkaTemplate<String, TransmisionRequestDto> kafkaTemplate;
	
	private final ActaTransmisionDataService actaTransmisionDataService;
	
	private final ActaTransmisionMapperService actaTransmisionMapperService;
	
	private final CentroComputoService centroComputoService;

    @Value("${kafka.topic.request}")
    private String topic;
    
    @Value("${kafka.topic.response}")
    private String topicResponse;
    
    public TransmisionProducerKafka(
    		KafkaTemplate<String, TransmisionRequestDto> kafkaTemplate,
    		ActaTransmisionDataService actaTransmisionDataService,
    		ActaTransmisionMapperService actaTransmisionMapperService,
    		CentroComputoService centroComputoService) {
        this.kafkaTemplate = kafkaTemplate;
        this.actaTransmisionDataService = actaTransmisionDataService;
        this.actaTransmisionMapperService = actaTransmisionMapperService;
        this.centroComputoService = centroComputoService;
    }

    public void enviarActa(EnvioTransmisionQueue mensaje) {

        Optional<ActaTransmisionNacion> actaTransmitida =
                actaTransmisionDataService.findByIdActaTransmiion(mensaje.getIdTransmision());

        if (actaTransmitida.isEmpty()) {
            log.info("No existe la transmisión con id: {}", mensaje.getIdTransmision());
            return;
        }

        ActaTransmisionNacion transmision = actaTransmitida.get();

        try {

            log.info("Se ejecuta la transmisión id {}", transmision.getId());

            actaTransmisionDataService.ejecutandose(transmision.getId());

            TransmisionRequestDto request = new TransmisionRequestDto();
            request.setProceso(mensaje.getProceso());
            request.setActasTransmitidas(
                    actaTransmisionDataService.adjuntar(
                            actaTransmisionMapperService.mapperRequest(List.of(transmision)))
            );

            String cc = centroComputoService.getCentroComputoActual()
                    .orElseThrow()
                    .getCodigo();

            String correlationId = UUID.randomUUID().toString();
            String key = cc + "-" + transmision.getIdActa();
            String replyToTopic = topicResponse;
            String orden = transmision.getOrden().toString();
            String proceso = mensaje.getProceso();

            ProducerRecord<String, TransmisionRequestDto> mensajeNacion =
                    new ProducerRecord<>(topic, key, request);

            mensajeNacion.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));
            mensajeNacion.headers().add(new RecordHeader("reply-to", replyToTopic.getBytes()));
            mensajeNacion.headers().add(new RecordHeader(SceConstantes.TENANT_HEADER, proceso.getBytes()));
            mensajeNacion.headers().add(new RecordHeader(SceConstantes.HEADER_ORDEN, orden.getBytes()));
            mensajeNacion.headers().add(new RecordHeader(SceConstantes.HEADER_CODIGO_CC, cc.getBytes()));


            kafkaTemplate.send(mensajeNacion)
	            .thenAccept(result -> {
	                log.info("Mensaje enviado correctamente a partición {}", 
	                    result.getRecordMetadata().partition());
	            })
	            .exceptionally(ex -> {
	                log.error("Error enviando mensaje: {}", ex.getMessage());
	                actaTransmisionDataService.actualizarFallido(transmision.getId(), ex.getMessage());
	                return null;
	            });

        } catch (Exception e) {
            actaTransmisionDataService.actualizarFallido(transmision.getId(), e.getMessage());
        }
    }
    

	
}
