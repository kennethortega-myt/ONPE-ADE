package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionPushService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@RestController
@RequestMapping("transmision")
public class TransmisionController {

	Logger logger = LoggerFactory.getLogger(TransmisionController.class);
	
	private final TokenDecoder tokenDecoder;
	
	private final ActaTransmisionPushService actaTransmisionPushService;
	
	private final ITabLogService logService;
	
	
	public TransmisionController(
			ActaTransmisionPushService actaTransmisionPushService,
            TokenDecoder tokenDecoder,
            ITabLogService logService) {
		this.actaTransmisionPushService = actaTransmisionPushService;
		this.tokenDecoder = tokenDecoder;
		this.logService = logService;
	}
	
	@GetMapping("/acta/{idActa}")
    public ResponseEntity<Boolean> tramsmitirActa(
    		@PathVariable("idActa") Long idActa, 
    		@RequestHeader(SceConstantes.AUTHORIZATION_HEADER) String authorizationHeader) {
		
        String token = authorizationHeader.substring(SceConstantes.LENGTH_BEARER); // Remueve el prefijo "Bearer "
        Claims claims = this.tokenDecoder.decodeToken(token);
        String proceso = claims.get("apr", String.class);
        String perfil = claims.get("per", String.class);
        String centroc = claims.get("ccc", String.class);
        String usr = claims.get("usr", String.class);

        this.logService.registrarLog(
                usr,
                "pe.gob.onpe.sceorcbackend.rest.controller.TransmisionController.tramsmitirActa",
                        "El usuario realizo una transmision manual",
                centroc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI, 1);
        
        logger.info("Proceso: {}", proceso);
        logger.info("perfil:{}", perfil);
        logger.info("Cc: {}", centroc);
        logger.info("id acta a transmitir: {}", idActa);

        boolean resultado = this.actaTransmisionPushService.empujar(idActa, proceso, usr);
        
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }
	
	@GetMapping("/acta/verificar-ejecutados/{idActa}")
    public ResponseEntity<Boolean> verificarEjecutados(
    		@PathVariable Long idActa, 
    		@RequestHeader(SceConstantes.AUTHORIZATION_HEADER) String authorizationHeader) {
		

        boolean resultado = this.actaTransmisionPushService.verificarTransmisionesEjecucion(idActa);
        
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }

	
	
}
