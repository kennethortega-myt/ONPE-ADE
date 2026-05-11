package pe.gob.onpe.sceorcbackend.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.ReprocesarMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCargaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reprocesar-mesa")
public class ReprocesarMesaController {

    private final TokenUtilService tokenUtilService;

    private final AutorizacionCargaService autorizacionImportacionService;

    private final MesaService mesaService;

    public ReprocesarMesaController(
            TokenUtilService tokenUtilService,
            AutorizacionCargaService autorizacionImportacionService, MesaService mesaService
    ) {
        this.tokenUtilService = tokenUtilService;
        this.autorizacionImportacionService = autorizacionImportacionService;
        this.mesaService = mesaService;
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/check")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String ccc = tokenInfo.getCodigoCentroComputo();
        String proceso = tokenInfo.getAbrevProceso();
        String usr = tokenInfo.getNombreUsuario();
        String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_MESA;
        log.info("Proceso: {}", proceso);

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.autorizacionImportacionService.getAutorizacionNacion(usr, ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            log.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/solicitar")
    public ResponseEntity<GenericResponse<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        try{
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            String ccc = tokenInfo.getCodigoCentroComputo();
            String usr = tokenInfo.getNombreUsuario();
            String proceso = tokenInfo.getAbrevProceso();
            String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_REPROCESAR_MESA;
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.autorizacionImportacionService.solicitaAutorizacionImportacion(usr, ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            log.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/save")
    public ResponseEntity<GenericResponse<Boolean>> guardarReprocesos(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody List<ReprocesarMesaResponseDto> data
            ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            this.mesaService.procesarReprocesarMesa(data, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo());
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            log.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }
}
