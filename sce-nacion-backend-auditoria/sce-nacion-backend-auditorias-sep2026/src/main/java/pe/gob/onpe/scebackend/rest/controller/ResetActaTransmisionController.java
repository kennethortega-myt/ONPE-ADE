package pe.gob.onpe.scebackend.rest.controller;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.exeption.AuthTransmisionException;
import pe.gob.onpe.scebackend.exeption.PcRunningException;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.transmision.TransmisionResponseDto;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.TokenValidadorService;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import pe.gob.onpe.scebackend.model.service.TransmisionDataService;

@RestController
@Validated
@RequestMapping("/reseteo-transmision")
public class ResetActaTransmisionController {

	Logger logger = LoggerFactory.getLogger(ResetActaTransmisionController.class);
	
	@Autowired
	private IConfiguracionProcesoElectoralService confProcesoService;
	
	@Autowired
	private TokenValidadorService tokenValidadorService;
	
	@Autowired
	private TransmisionDataService transmisionDataService;
	
	@PatchMapping({"/recibir-transmision", "/recibir-transmision/"})
    public ResponseEntity<TransmisionResponseDto> recibirActa(
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestHeader("codigocc") String cc,
			@RequestBody TransmisionNacionRequestDto request) {
        
    	String correlationId = null;
    	
    	
    	try {
    	
	    	boolean autenticacion = this.tokenValidadorService.validarToken(authorization, cc);
	    	
	    	if (!autenticacion) {
	    		throw new AuthTransmisionException(
	    		        "Token invalido"
	    		);
	    	}
	    	
	    	logger.info("Se procesa el id de transmision {}", 
	    			request
	    			.getActasTransmitidas()
	    			.getFirst()
	    			.getIdTransmision());
	    	
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
	
			logger.info("esquema: {}",procesoElectoralConfig.getNombreEsquemaPrincipal());
			
			this.transmisionDataService.executarPuestaCero	(
        			request, 
        			procesoElectoralConfig.getNombreEsquemaPrincipal(), 
        			correlationId, 
        			cc, 
        			request.getActasTransmitidas().getFirst().getUsuarioTransmision());
			
        	this.transmisionDataService.recibirReseteo(
        			request, 
        			procesoElectoralConfig.getNombreEsquemaPrincipal(), 
        			correlationId, 
        			cc, 
        			request.getActasTransmitidas().getFirst().getUsuarioTransmision());
        	
        	TransmisionResponseDto response = TransmisionResponseDto
    				.builder()
    				.correlationId(correlationId)
    				.idActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa())
    				.estado(ConstanteTransmision.ESTADO_TRANSMISION_OK)
    				.exitoso(true)
    				.mensaje("Transmision exitosa")
    				.build();
        	
        	return ResponseEntity.status(HttpStatus.OK)
                    .body(response);
            
        } catch (PcRunningException e) {
        	TransmisionResponseDto response = new TransmisionResponseDto();
        	response.setExitoso(false);
        	response.setIdActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        } catch (AuthTransmisionException e) {
        	TransmisionResponseDto response = new TransmisionResponseDto();
        	response.setExitoso(false);
        	response.setIdActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            response.setMensaje("Token no autorizado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        } catch (TimeoutException e) {
        	TransmisionResponseDto response = new TransmisionResponseDto();
        	response.setExitoso(false);
        	response.setIdActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
        	response.setMensaje("Timeout - El procesamiento tomó demasiado tiempo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        } catch (Exception e) {
        	TransmisionResponseDto response = new TransmisionResponseDto();
        	response.setExitoso(false);
        	response.setIdActa(request.getActasTransmitidas().getFirst().getActaTransmitida().getIdActa());
        	response.setMensaje(e.getMessage());
        	response.setEstado(ConstanteTransmision.ESTADO_TRANSMISION_ERROR);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
        } 
    }
	
}
