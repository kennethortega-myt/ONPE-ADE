package pe.gob.onpe.scebatchpr.service.impl;

import java.util.List;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionDto;
import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionRequest;
import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionResponse;
import pe.gob.onpe.scebatchpr.dto.PrUpdatedDto;
import pe.gob.onpe.scebatchpr.dto.TramaSceDto;
import pe.gob.onpe.scebatchpr.dto.TramaSceRequest;
import pe.gob.onpe.scebatchpr.dto.TramaSceResponse;
import pe.gob.onpe.scebatchpr.enums.EstadoEnum;
import pe.gob.onpe.scebatchpr.repository.orc.ArchivoRepository;
import pe.gob.onpe.scebatchpr.service.MqTransmisionService;
import pe.gob.onpe.scebatchpr.service.TabPrTransmisionService;
import pe.gob.onpe.scebatchpr.utils.Constantes;
import pe.gob.onpe.scebatchpr.utils.ResponseUtils;
import pe.gob.onpe.scebatchpr.utils.SceConstantes;
import pe.gob.onpe.scebatchpr.utils.Utils;

@Service
public class MqTransmisionServiceImpl implements MqTransmisionService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MqTransmisionServiceImpl.class);

    @Value("${app.sce-batch-pr.transmision.send-queue}")
    private String requestQueue;
    
    @Value("${app.sce-batch-pr.transmision.reply-queue}")
    private String replyQueue;
    
    @Value("${app.sce-batch-pr.transmision.send-queue-file}")
    private String requestQueueFile;
    
    @Value("${app.sce-batch-pr.transmision.reply-queue-file}")
    private String replyQueueFile;
    
    @Value("${app.sce-batch-pr.transmision.reply.data.timeout}")
    private long replyDataTimeout;
    
    @Value("${app.sce-batch-pr.transmision.reply.file.timeout}")
    private long replyFileTimeout;
    
    private final TabPrTransmisionService tabPrTransmisionService;
    
	private final ArchivoRepository archivoRepository;
    
	private final RabbitTemplate rabbitTemplate;
	
    public MqTransmisionServiceImpl(
    		TabPrTransmisionService tabPrTransmisionService,
    		ArchivoRepository archivoRepository,
    		RabbitTemplate rabbitTemplate){
    	this.tabPrTransmisionService = tabPrTransmisionService;
    	this.archivoRepository = archivoRepository;
    	this.rabbitTemplate = rabbitTemplate;
    }
    
	@Override
	public void productorData(List<TramaSceDto> message) throws JsonProcessingException, InterruptedException {
		
		if(message.getFirst()==null){
			return;
		}
		
		String correlationId = String.format("%s_%s", Constantes.IDENTIFICADOR_CORR_DATA, Utils.uuidTime());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(correlationId);
        messageProperties.setReplyTo(replyQueue);
        CorrelationData correlationData = new CorrelationData(correlationId);
        LOG.info("*********************Producir Mensaje*****************************");
        
        message.forEach(x -> 
        		this.tabPrTransmisionService.actualizarCorrelativo(
        				x.getIdTransferencia(), 
        				correlationId)
    			
        	);

        TramaSceRequest request = new TramaSceRequest();
        request.setTramasSce(message);
        String messageJson = request.toJson();
        
        Message messageRequest = new Message(messageJson.getBytes(), messageProperties);

        LOG.info("Mensaje enviado para el acta {} de la transmision {}", message.getFirst().getIdActa(), message.getFirst().getIdTransferencia());
        rabbitTemplate.send(requestQueue, messageRequest, correlationData);
        
        LOG.info("Esperar la respuesta del consumidor");
        Message responseResponse = rabbitTemplate.receive(replyQueue, replyDataTimeout);
       
        if (responseResponse != null) {
        	LOG.info("Se recibió el mensaje dentro del tiempo de esperado");
        	String cadena = new String(responseResponse.getBody());
        	TramaSceResponse response = TramaSceResponse.getObject(cadena);
            List<PrUpdatedDto> updatePr = ResponseUtils.buildResponse(response);
            if(updatePr!=null) {
            	updatePr.forEach(x -> {
            			LOG.info("Se recepciono la transmision {} con el estado {}", x.getIdTransferencia(), x.getEstado());
            			this.tabPrTransmisionService.actualizarEstado(x.getIdTransferencia(), x.getEstado(), x.getMensaje());
            		}
            	);
            }
        } else {
        	LOG.info("No se recibió respuesta en el tiempo de espera.");
        	message.forEach(x -> {
        		LOG.info("No se recepciono la transmision {} en el tiempo esperado", x.getIdTransferencia());
            	this.tabPrTransmisionService.actualizarEstado(
            			x.getIdTransferencia(), 
            			EstadoEnum.SIN_CONFIRMAR.getValor(),
            			"No se recibió respuesta en el tiempo de espera");
            	}
            );
        }
        
        LOG.info("**************** Fin al producir mensaje*****************************");
	}


	@Override
	public void productorArchivos(ArchivoTransmisionRequest message)
			throws JsonProcessingException, InterruptedException {
		
		String correlationId = String.format("%s_%s", Constantes.IDENTIFICADOR_CORR_FILE, Utils.uuidTime());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(correlationId);
        messageProperties.setReplyTo(replyQueueFile);
        CorrelationData correlationData = new CorrelationData(correlationId);
        LOG.info("*********************Producir Archivo*****************************");
        this.print(message);
        String messageJson = message.toJson();
        Message messageRequest = new Message(messageJson.getBytes(), messageProperties);
        
        rabbitTemplate.send(requestQueueFile, messageRequest, correlationData);
        
        LOG.info("Esperar la respuesta del consumidor de respuesta de acuse de recibo de archivos");
        Message responseResponse = rabbitTemplate.receive(replyQueueFile, replyFileTimeout); 
        
        if(responseResponse!=null) {
        	LOG.info("Se recibió el archivo dentro del tiempo de espera.");
        	String cadena = new String(responseResponse.getBody());
        	ArchivoTransmisionResponse response = ArchivoTransmisionResponse.getObject(cadena);

			if(response.getArchivos()!=null) {
				response.getArchivos().stream().forEach(archivo -> {
						LOG.info("El archivo recepcionado es {}", archivo.getGuid());
					    this.archivoRepository.updateTransmision(
					    		archivo.getGuid(), 
					    		archivo.isTransmitido() ? SceConstantes.ACTIVO : SceConstantes.INACTIVO
					    );
				    }
				);
			}
        	
        }
        
	}
	
	private void print(ArchivoTransmisionRequest message){
		LOG.info("Archivo enviado del acta={}", message.getIdActa());
		
        
        if(message.getArchivos()!=null && !message.getArchivos().isEmpty()){
        	for(ArchivoTransmisionDto archivo:message.getArchivos()){
        		if(archivo!=null){
        			LOG.info("Archivo enviado={} de tipo {}", archivo.getGuid(), archivo.getTipoArchivo());
        		}
        	}
        } else {
        	LOG.info("No hay archivo de resolucion para enviar");
        }
        
	}
	
}
