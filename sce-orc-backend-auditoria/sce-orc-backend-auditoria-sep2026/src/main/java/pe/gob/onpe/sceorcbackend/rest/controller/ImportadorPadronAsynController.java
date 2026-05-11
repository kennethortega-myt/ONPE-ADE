package pe.gob.onpe.sceorcbackend.rest.controller;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.impl.ImportarPadronBdAsyncServiceImpl;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.util.List;


@Controller
public class ImportadorPadronAsynController {

    Logger logger = LoggerFactory.getLogger(ImportadorPadronAsynController.class);

    private final ImportarPadronBdAsyncServiceImpl importar;

    private final TokenDecoder tokenDecoder;
    
    private final ITabLogService logService;
    
    public ImportadorPadronAsynController(
    		ImportarPadronBdAsyncServiceImpl importar,
    		TokenDecoder tokenDecoder,
    		ITabLogService logService) {
    	this.importar = importar;
    	this.tokenDecoder = tokenDecoder;
    	this.logService = logService;
    }


    @MessageMapping("/importar-padron-ws")
    public String recibirMensaje(SimpMessageHeaderAccessor headerAccessor) throws Exception {
    	
        List<String> values = headerAccessor.getNativeHeader(SceConstantes.AUTHORIZATION_HEADER);

        String proceso = null;
        String perfil = null;
        String centroc = null;
        String usr = null;

        if (values != null) {
            String authorizationHeader = values.get(0);
            String token = authorizationHeader.substring(SceConstantes.LENGTH_BEARER); // remove prefix "Bearer "
            Claims claims = this.tokenDecoder.decodeToken(token);
            proceso = claims.get("apr", String.class);
            perfil = claims.get("per", String.class);
            centroc = claims.get("ccc", String.class);
            usr = claims.get("usr", String.class);
            
            this.logService.registrarLog(
            		usr,
            		"pe.gob.onpe.sceorcbackend.rest.controller.ImportadorPadronAsynController.recibirMensaje",
            		this.getClass().getName(),
                    "El usuario realizó una importación de padrones",
            		centroc,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
            		1);
            
            
        } // end

        logger.info("****************************");
        logger.info("Proceso: {}", proceso);
        logger.info("perfil: {}", perfil);
        logger.info("Cc: {}", centroc);
        logger.info("****************************");

        this.importar.migrar(centroc, usr, proceso);

        return "";
    }

}
