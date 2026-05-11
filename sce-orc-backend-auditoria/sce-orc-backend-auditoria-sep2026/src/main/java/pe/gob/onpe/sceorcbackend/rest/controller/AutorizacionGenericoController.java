package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionGenericaService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("autorizacion-generico")
public class AutorizacionGenericoController {

    Logger logger = LoggerFactory.getLogger(AutorizacionGenericoController.class);

    private final TokenUtilService tokenUtilService;
    private final AutorizacionGenericaService  autorizacionGenericaService;

    public AutorizacionGenericoController(TokenUtilService tokenUtilService, AutorizacionGenericaService autorizacionGenericaService) {
        this.tokenUtilService = tokenUtilService;
        this.autorizacionGenericaService = autorizacionGenericaService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/consulta")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam(name = "tipo") String tipo
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String ccc = tokenInfo.getCodigoCentroComputo();
        String usr = tokenInfo.getNombreUsuario();
        String proceso = tokenInfo.getAbrevProceso();
        logger.info("Proceso: {}", proceso);

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.autorizacionGenericaService.getAutorizacionNacion(usr, ccc, proceso, tipo);
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
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam(name = "tipo") String tipo
    ) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String ccc = tokenInfo.getCodigoCentroComputo();
        String usr = tokenInfo.getNombreUsuario();
        String proceso = tokenInfo.getAbrevProceso();

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.autorizacionGenericaService.solicitaAutorizacionNacion(usr, ccc, proceso, tipo);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }
}
