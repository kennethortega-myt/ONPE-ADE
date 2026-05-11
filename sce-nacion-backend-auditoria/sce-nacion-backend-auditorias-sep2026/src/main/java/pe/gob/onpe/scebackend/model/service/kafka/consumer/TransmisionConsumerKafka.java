package pe.gob.onpe.scebackend.model.service.kafka.consumer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.ext.pr.service.EnvioTramaSceService;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.ITransmisionService;
import pe.gob.onpe.scebackend.multitenant.CurrentTenantId;
import pe.gob.onpe.scebackend.utils.TextTruncatorUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransmisionConsumerKafka {

	private final KafkaTemplate<String, TransmisionResponseDto> kafkaTemplate;
	
	private final ITransmisionService transmisionService;
	
	private final IConfiguracionProcesoElectoralService confProcesoService;
	
    @KafkaListener(
            topics = "${kafka.topic.request}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirTransmision(
            ConsumerRecord<String, TransmisionNacionRequestDto> record, 
            Acknowledgment acknowledgment) {

        log.info("Mensaje recibido en NACION con key {}", record.key());


        String correlationId = getHeader(record, "correlationId");
        String replyTo = getHeader(record, "reply-to");
        String codigoCc = getHeader(record, "codigocc");
        String orden = getHeader(record, "orden");

        if (replyTo == null || correlationId == null) {
            log.error("Mensaje sin reply-to o correlationId");
            return;
        }

        TransmisionNacionRequestDto request = record.value();
        
        TransmisionResponseDto respuesta = null;
        try {
        	
        	String proceso = request.getProceso();
        	ConfiguracionProcesoElectoral procesoElectoralConfig = this.confProcesoService.findByProceso(proceso);
	        
	        if(procesoElectoralConfig.getEtapa()!=null && procesoElectoralConfig.getEtapa().equals(ConstantesComunes.ETAPA_SIN_CARGA)){
	        	log.info("En el esquema {} aun no se ha hecho la carga, se ignora la transmision", 
	        			procesoElectoralConfig.getNombreEsquemaPrincipal());
	        	throw new Exception(
	    		        "Se esta realizando la carga inicial, se ignora la transmision"
	    		);
	        	
			}

	        CurrentTenantId.set(proceso);
	        
	        transmisionService.recibirTransmision(
            		request.getActasTransmitidas().getFirst(), 
            		procesoElectoralConfig.getNombreEsquemaPrincipal(),
            		correlationId,
            		codigoCc,
            		Integer.valueOf(orden));
            
	        respuesta = TransmisionResponseDto
    				.builder()
    				.correlationId(correlationId)
    				.idActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa())
    				.idTransmision(request.getActasTransmitidas().getFirst().getIdTransmision())
    				.estado(ConstanteTransmision.ESTADO_TRANSMISION_OK)
    				.exitoso(true)
    				.mensaje("Transmision exitosa")
    				.build();

            log.info("Transmisión procesada correctamente id {}", respuesta.getIdTransmision());
            

        } catch (Exception e) {
        	log.error("Error", e);
        	respuesta = new TransmisionResponseDto();
        	respuesta.setIdActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
        	respuesta.setIdTransmision(request.getActasTransmitidas().getFirst().getIdTransmision());
            respuesta.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            respuesta.setExitoso(false);
            respuesta.setCorrelationId(correlationId);
            respuesta.setMensaje(String.format("Error %s : %s", correlationId, TextTruncatorUtil.truncateTo300Chars(e.getMessage())));
        } finally {
        	CurrentTenantId.clear();
        }
        
        ProducerRecord<String, TransmisionResponseDto> responseRecord = 
                new ProducerRecord<>(replyTo, record.key(), respuesta);
        responseRecord.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));

        acknowledgment.acknowledge();
        
        kafkaTemplate.send(responseRecord)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error("Error enviando respuesta: {}", ex.getMessage());
                    } else {
                        log.info("Respuesta enviada a {}", replyTo);
                    }
                });
    }

    private String getHeader(ConsumerRecord<?, ?> record, String key) {
        Header header = record.headers().lastHeader(key);
        return header != null
                ? new String(header.value(), StandardCharsets.UTF_8)
                : null;
    }
	
}
