package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.exeption.ResponseHelperException;
import pe.gob.onpe.scebackend.model.dto.AutorizacionDto;
import pe.gob.onpe.scebackend.model.dto.EstadoAprobacionDto;
import pe.gob.onpe.scebackend.model.dto.TipoAutorizacionDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionFilterRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponseAlternative;
import pe.gob.onpe.scebackend.model.service.AutorizacionGenericaService;
import pe.gob.onpe.scebackend.model.service.IAutorizacionService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/autorizacion")
public class AutorizacionController extends BaseController{

    private final IAutorizacionService autorizacionService;

    private final AutorizacionGenericaService autorizacionGenericaService;

    private final TokenUtilService tokenUtilService;

    public AutorizacionController(TokenDecoder tokenDecoder,
            IAutorizacionService autorizacionService,
            AutorizacionGenericaService autorizacionGenericaService,
            TokenUtilService tokenUtilService) {
        super(tokenDecoder);
        this.autorizacionService = autorizacionService;
        this.autorizacionGenericaService = autorizacionGenericaService;
        this.tokenUtilService = tokenUtilService;
    }

    @PostMapping("/consulta")
    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    public ResponseEntity<GenericResponse> consultaAutorizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam String tipo) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String usr = tokenInfo.getNombreUsuario();
        GenericResponse genericResponse = new GenericResponse();

        try {
            AutorizacionNacionResponseDto autorizacionNacion = this.autorizacionGenericaService
                    .getAutorizacionNacion(usr, tipo);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage("Servicio no disponible");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(genericResponse);
        }
    }

    @PostMapping("/solicitar")
    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    public ResponseEntity<GenericResponse> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam String tipo) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String usr = tokenInfo.getNombreUsuario();
        GenericResponse genericResponse = new GenericResponse();

        try {
            Boolean autorizacionNacion = this.autorizacionGenericaService.solicitaAutorizacionNacion(usr, tipo);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(genericResponse);
        }
    }

    @PostMapping("/list-autorizacion")
    public ResponseEntity<GenericResponseAlternative<List<AutorizacionDto>>> listAutorizaciones(@RequestBody(required = false) AutorizacionFilterRequestDto filter) {
        try{
            List<AutorizacionDto> listAutorizacionDto = this.autorizacionService.listAutorizaciones(filter);
            return ResponseHelperException.createSuccessResponse("Operación realizada con éxito", listAutorizacionDto);
        }
        catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AutorizacionController.listarAutorizaciones");
        }
    }

    @GetMapping("/list-tipo-autorizacion")
    public ResponseEntity<GenericResponseAlternative<List<TipoAutorizacionDto>>> listTiposAutorizacion() {
        try{
            List<TipoAutorizacionDto> listTipoAutorizacionDto = this.autorizacionService.listTiposAutorizacionActivos();
            return ResponseHelperException.createSuccessResponse("Operación realizada con éxito", listTipoAutorizacionDto);
        }
        catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AutorizacionController.listTiposAutorizacion");
        }
    }

    @GetMapping("/list-estado-aprobacion")
    public ResponseEntity<GenericResponseAlternative<List<EstadoAprobacionDto>>> listEstadosAprobacion() {
        try{
            List<EstadoAprobacionDto> listEstadoAprobacionDto = this.autorizacionService.listEstadosAprobacionActivos();
            return ResponseHelperException.createSuccessResponse("Operación realizada con éxito", listEstadoAprobacionDto);
        }
        catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AutorizacionController.listEstadosAprobacion");
        }
    }

    @PostMapping("/aprobar-autorizacion")
    public ResponseEntity<GenericResponse> aprobarAutorizacion(
            @RequestBody AutorizacionRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        LoginUserHeader user = getUserLogin(authorization);
        return this.autorizacionService.aprobarAutorizacion(filtro,user.getUsuario());
    }

    @PostMapping("/rechazar-autorizacion")
    public ResponseEntity<GenericResponse> rechazarAutorizacion(
            @RequestBody AutorizacionRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        LoginUserHeader user = getUserLogin(authorization);
        return this.autorizacionService.rechazarAutorizacion(filtro,user.getUsuario());
    }

    @PatchMapping("/recibir-autorizacion")
    public ResponseEntity<AutorizacionNacionResponseDto> recibirAutorizacion(@RequestBody AutorizacionNacionRequestDto request) {
        return this.autorizacionService.recibirAutorizacion(request);
    }

    @PatchMapping("/crear-solicitud-autorizacion")
    public ResponseEntity<GenericResponse> crearSolicitudAutorizacion(@RequestBody AutorizacionNacionRequestDto request) {
        return this.autorizacionService.crearSolicitudAutorizacion(request);
    }
}