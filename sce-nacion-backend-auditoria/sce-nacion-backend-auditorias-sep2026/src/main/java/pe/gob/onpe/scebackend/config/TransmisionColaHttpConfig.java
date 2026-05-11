package pe.gob.onpe.scebackend.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.service.TransmisionDataService;
import pe.gob.onpe.scebackend.multitenant.CurrentTenantId;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;

@Configuration
@Slf4j
public class TransmisionColaHttpConfig {

	private TransmisionDataService transmisionService;
	
	// Mapa para almacenar canales creados dinamicamente
    private final Map<String, MessageChannel> canalesDinamicos = new ConcurrentHashMap<>();
    
    // Mapa para almacenar canales de respuesta
    private final Map<String, DirectChannel> canalesRespuesta = new ConcurrentHashMap<>();
	
	
	public TransmisionColaHttpConfig(
			TransmisionDataService transmisionService){
		this.transmisionService = transmisionService;
	}
	
	// === 1. CANAL DE ENTRADA PRINCIPAL ===
    @Bean
    public MessageChannel transmisionInputChannel() {
        return new DirectChannel();
    }
    
    // === 2. CANAL DE RESPUESTA GLOBAL ===
    @Bean
    public MessageChannel transmisionOutputChannel() {
        return new DirectChannel();
    }
	
    
    @Bean
    @ServiceActivator(inputChannel = "transmisionInputChannel")
    public MessageHandler enrutadorPorActa() {
        return message -> {
        	TransmisionDto transmisionDto = (TransmisionDto) message.getPayload();
            Long idActa = transmisionDto.getActaTransmitida().getIdActa();
            String cc = (String) message.getHeaders().get("codigoCc");
            String esquema = (String) message.getHeaders().get("esquema");
            Integer orden = (Integer) message.getHeaders().get("orden");
            String correlationId = (String) message.getHeaders().get("correlationId");
            String channelName = "actaQueue-" + idActa + "-" +cc;
            String responseChannelName = "responseChannel-" + correlationId;
            DirectChannel responseChannel = new DirectChannel();
            canalesRespuesta.put(correlationId, responseChannel);
            
            // Conectar el responseChannel al transmisionOutputChannel
            responseChannel.subscribe(message1 -> {
                // Reenviar al output channel global
                transmisionOutputChannel().send(message1);
            });
            
            // Crear nuevo mensaje con los headers necesarios
            Message<TransmisionDto> nuevoMensaje = MessageBuilder
            	.withPayload(transmisionDto)
                .setHeader("responseChannel", responseChannel)
                .setHeader("responseChannelName", responseChannelName)
                .setHeader("correlationId", correlationId)
                .setHeader("esquema", esquema)
                .setHeader("orden", orden)
                .setHeader("codigoCc", cc)
                .build();
            
            // Obtener o crear canal especifico
            MessageChannel channel = getOrCreateExecutorChannel(channelName);
            
            // Reenviar mensaje al canal específico
            channel.send(nuevoMensaje);
           
        };
    }
    
    // === 3. MÉTODO PARA CREAR/OBTENER CANALES DINÁMICOS ===
	 // computeIfAbsent hace lo siguiente:
	 // - Si existe el canal, lo DEVUELVE
	 // - Si NO existe, lo CREA y lo guarda
    private MessageChannel getOrCreateExecutorChannel(String channelName) {
        return canalesDinamicos.computeIfAbsent(channelName, name -> {

            // 1 hilo => secuencial por acta.
            // Cola acotada => evitas que reviente memoria si llegan miles.
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    1, 1,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100) // capacidad cola por acta (ajusta)
            );
            executor.allowCoreThreadTimeOut(true);

            ExecutorChannel nuevoCanal = new ExecutorChannel(executor);

            registrarProcesadorParaCanal(name, nuevoCanal);

            log.info("Creado nuevo canal (1 hilo) para: {}", name);
            return nuevoCanal;
        });
    }
    
    // === 4. REGISTRAR PROCESADOR PARA CANAL ESPECÍFICO  ===
    private void registrarProcesadorParaCanal(String channelName, ExecutorChannel channel) {
        MessageHandler procesador = message -> {
            String correlationId = null;
            TransmisionDto acta = null;
            try {
            	
            	log.info("Se inicio el procesamiento en el canal {}", channelName);
            	
            	acta = (TransmisionDto) message.getPayload();
                correlationId = (String) message.getHeaders().get("correlationId");
                String esquema = (String) message.getHeaders().get("esquema");
                String cc = (String) message.getHeaders().get("codigoCc");
                Integer orden = (Integer) message.getHeaders().get("orden");
                MessageChannel responseChannel = (MessageChannel) message.getHeaders().get("responseChannel");
                
                CurrentTenantId.set(acta.getAcronimoProceso());

                transmisionService.recibirTransmision(
                		acta, 
                		esquema,
                		correlationId,
                		cc,
                		orden);
                
                TransmisionResponseDto resultado = TransmisionResponseDto
        				.builder()
        				.correlationId(correlationId)
        				.idActa(acta.getActaTransmitida().getIdActa())
        				.idTransmision(acta.getIdTransmision())
        				.estado(ConstanteTransmision.ESTADO_TRANSMISION_OK)
        				.exitoso(true)
        				.mensaje("Transmision exitosa")
        				.build();

                
                // Enviar respuesta por el canal correspondiente
                if (responseChannel != null) {
                    Message<TransmisionResponseDto> mensajeRespuesta = MessageBuilder
                        .withPayload(resultado)
                        .setHeader("correlationId", correlationId)
                        .build();
                    
                    responseChannel.send(mensajeRespuesta);
                }
                
            } catch (Exception e) {
                log.error("Error procesando: " + e.getMessage());
                
                // Enviar respuesta de ERROR
                if (correlationId != null && acta!=null) {
                	TransmisionResponseDto respuesta = new TransmisionResponseDto();
                	respuesta.setIdActa(acta.getActaTransmitida().getIdActa());
                	respuesta.setIdTransmision(acta.getIdTransmision());
                    respuesta.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
                    respuesta.setMensaje("Error: " + e.getMessage());
                    respuesta.setCorrelationId(correlationId);
                    
                    MessageChannel responseChannel = (MessageChannel) message.getHeaders().get("responseChannel");
                    if (responseChannel != null) {
                        Message<TransmisionResponseDto> mensajeRespuesta = MessageBuilder
                            .withPayload(respuesta)
                            .setHeader("correlationId", correlationId)
                            .build();
                        responseChannel.send(mensajeRespuesta);
                    }
                }
            } finally {
                if (correlationId != null) {
                    canalesRespuesta.remove(correlationId);
                }
            }
        };
        
        channel.subscribe(procesador);
    }
    
	
}
