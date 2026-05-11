package pe.gob.onpe.scebackend.rest.controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.exeption.AuthTransmisionException;
import pe.gob.onpe.scebackend.exeption.PcRunningException;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.TokenValidadorService;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


@RestController
@Validated
@RequestMapping("/transmision")
public class ActaTransmisionController {

	Logger logger = LoggerFactory.getLogger(ActaTransmisionController.class);
	
	@Autowired
	@Qualifier("transmisionInputChannel")
    private MessageChannel transmisionInputChannel;
	
	@Autowired
	private IConfiguracionProcesoElectoralService confProcesoService;
	
	@Autowired
	private TokenValidadorService tokenValidadorService;
	
	private final Map<String, CompletableFuture<TransmisionResponseDto>> futuresPendientes = new ConcurrentHashMap<>();
	
    
	@PatchMapping({"/recibir-transmision", "/recibir-transmision/"})
    public ResponseEntity<TransmisionResponseDto> recibirActa(
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestHeader("codigocc") String cc,
			@RequestHeader("orden") String orden,
			@RequestBody TransmisionNacionRequestDto request) {
        
    	String correlationId = null;
    	TransmisionResponseDto response = new TransmisionResponseDto();
    	
    	try {
    	
	    	boolean autenticacion = this.tokenValidadorService.validarToken(authorization, cc);
	    	
	    	if (!autenticacion) {
	    		throw new AuthTransmisionException(
	    		        "Token invalido"
	    		);
	    	}
	    	
	    	logger.info("Se procesa el id de transmision {} con el orden {}", 
	    			request
	    			.getActasTransmitidas()
	    			.getFirst()
	    			.getIdTransmision(), 
	    			orden);
	    	
	        // Generar ID único para esta solicitud
	        correlationId = UUID.randomUUID().toString();
	        
	        String proceso = request.getProceso();
	        ConfiguracionProcesoElectoral procesoElectoralConfig = this.confProcesoService.findByProceso(proceso);
	        
	        if(procesoElectoralConfig.getEtapa()!=null 
	        		&& procesoElectoralConfig.getEtapa().equals(ConstantesComunes.ETAPA_SIN_CARGA)){
	        	logger.info("En el esquema {} aun no se ha hecho la carga, se ignora la transmision", 
	        			procesoElectoralConfig.getNombreEsquemaPrincipal());
	        	throw new PcRunningException(
	    		        "Se esta realizando la puesta cero, se ignora la transmision"
	    		);
	        	
			}

	        
	        Long idTransmision = request
        			.getActasTransmitidas()
        			.getFirst()
        			.getIdTransmision();
	        Long idActa = request
        			.getActasTransmitidas()
        			.getFirst()
        			.getActaTransmitida()
        			.getIdActa();
	
			logger.info("esquema: {}",procesoElectoralConfig.getNombreEsquemaPrincipal());
			
			
        	response.setIdActa(idActa);
        	response.setIdTransmision(idTransmision);
        	response.setCorrelationId(correlationId);
	        
	        // Crear futuro para esperar la respuesta
	        CompletableFuture<TransmisionResponseDto> future = new CompletableFuture<>();
	        futuresPendientes.put(correlationId, future);
	        
	        TransmisionDto acta = request.getActasTransmitidas().getFirst();
	        
	        // Crear mensaje con headers
	        Message<TransmisionDto> mensaje = MessageBuilder
	            .withPayload(acta)
	            .setHeader("correlationId", correlationId)
	            .setHeader("fechaRecepcion", new Date())
	            .setHeader("proceso", proceso)
	            .setHeader("esquema", procesoElectoralConfig.getNombreEsquemaPrincipal())
	            .setHeader("codigoCc", cc)
	            .setHeader("idActa", idActa)
	            .setHeader("idTransmision",idTransmision)
	            .setHeader("orden", Integer.parseInt(orden))
	            .build();
	        
	        // Enviar a procesar
	        transmisionInputChannel.send(mensaje);
        
            // ESPERAR RESPUESTA (máximo 60 minutos)
        	TransmisionResponseDto resultado = future.get(60, TimeUnit.MINUTES);
        	logger.info("transmision resultado {}:", resultado);
            
            if (resultado.isExitoso()) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(resultado);
            }
            
        } catch (PcRunningException e) {
        	response.setExitoso(false);
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        } catch (AuthTransmisionException e) {
        	response.setExitoso(false);
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            response.setMensaje("Token no autorizado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
        } catch (TimeoutException e) {
        	response.setExitoso(false);
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
        	response.setMensaje("Timeout - El procesamiento tomó demasiado tiempo");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(response);
        } catch (Exception e) {
        	response.setExitoso(false);
        	response.setMensaje(e.getMessage());
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
        } finally {
        	if(correlationId!=null){
        		futuresPendientes.remove(correlationId);
        	}
        }
    }
	
	@ServiceActivator(inputChannel = "transmisionOutputChannel")
    public void recibirRespuesta(Message<TransmisionResponseDto> mensaje) {
        TransmisionResponseDto respuesta = mensaje.getPayload();
        String correlationId = respuesta.getCorrelationId();
        
        logger.info("Respuesta recibida para correlationId: {}", correlationId);
        
        CompletableFuture<TransmisionResponseDto> future = futuresPendientes.get(correlationId);
        if (future != null) {
            future.complete(respuesta);
            logger.info("Future completado para correlationId: {}", correlationId);
        } else {
            logger.warn("No se encontró future para correlationId: {}", correlationId);
        }
    }
    
}
