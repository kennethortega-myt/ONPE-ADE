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
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCargaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CierreActividadesService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("reapertura-autorizacion")
public class AutorizacionReaperturaController {

    Logger logger = LoggerFactory.getLogger(AutorizacionReaperturaController.class);

    private final TokenUtilService tokenUtilService;
    private final AutorizacionCargaService autorizacionImportacionService;
    private final CierreActividadesService cierreActividadesService;

    public AutorizacionReaperturaController(
            TokenUtilService tokenUtilService,
            AutorizacionCargaService autorizacionImportacionService,
            CierreActividadesService cierreActividadesService
    ) {
        this.tokenUtilService = tokenUtilService;
        this.autorizacionImportacionService = autorizacionImportacionService;
        this.cierreActividadesService = cierreActividadesService;
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
            String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_REAPERTURA_CC;
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.cierreActividadesService.solicitaAutorizacionReapertura(usr,ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }
}
