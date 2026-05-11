package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCargaService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("importacion-padron-autorizacion")
public class AutorizacionImportacionPadronController {

Logger logger = LoggerFactory.getLogger(AutorizacionImportacionPadronController.class);
	
	private final TokenUtilService tokenUtilService;
	
	private final AutorizacionCargaService autorizacionImportacionService;
	
	public AutorizacionImportacionPadronController(
            TokenUtilService tokenUtilService,
            AutorizacionCargaService autorizacionImportacionService
	) {
		this.tokenUtilService = tokenUtilService;
		this.autorizacionImportacionService = autorizacionImportacionService;
	}

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/check")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
		TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
		String ccc = tokenInfo.getCodigoCentroComputo();
        String proceso = tokenInfo.getAbrevProceso();
        String usr = tokenInfo.getNombreUsuario();
        String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_IMPORTACION_PADRONES;
        logger.info("Proceso: {}", proceso);

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.autorizacionImportacionService.getAutorizacionNacion(usr, ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
	@PostMapping("/solicitar")
    public ResponseEntity<GenericResponse<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        try{
        	TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        	String ccc = tokenInfo.getCodigoCentroComputo();
        	String usr = tokenInfo.getNombreUsuario();
        	String proceso = tokenInfo.getAbrevProceso();
        	String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_IMPORTACION_PADRONES;
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.autorizacionImportacionService.solicitaAutorizacionImportacion(usr, ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }
	
}
