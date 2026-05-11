package pe.gob.onpe.sceorcbackend.rest.controller;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.CierreCentroComputoRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CierreActividadesService;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@RestController
@RequestMapping("cierre-actividades")
@Validated
public class CierreActividadesController {
    private final TokenUtilService tokenUtilService;
    private final CierreActividadesService cierreActividadesService;
    private final TokenDecoder tokenDecoder;

    public CierreActividadesController(TokenUtilService tokenUtilService,
                                       CierreActividadesService cierreActividadesService,
                                       TokenDecoder tokenDecoder
                                       ){
        this.tokenUtilService = tokenUtilService;
        this.tokenDecoder = tokenDecoder;
        this.cierreActividadesService = cierreActividadesService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/cerrar-cc")
    @Operation(summary = "Cerrar centro de cómputo", description = "Permite cerrar un centro de cómputo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta procesada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido o sin autorización"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GenericResponse<CierreCentroComputoResponse>> cerrarCC(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody @Valid CierreCentroComputoRequest request){
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            CierreCentroComputoResponse resultado = this.cierreActividadesService.cerrarCC(
                    request.getUsuario(),
                    tokenInfo.getCodigoCentroComputo(),
                    request.getMotivo(),
                    request.getClave());
            return ResponseHelperException.createSuccessResponse("Centro de cómputo cerrado correctamente", resultado);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "cerrar-cc");
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/reabrir-cc")
    @Operation(summary = "Reabrir centro de cómputo", description = "Permite reabrir un centro de cómputo previamente cerrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Centro reabierto exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para realizar la operación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GenericResponse<ReaperturaCentroComputoResponse>> reabrirCC(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("conAutorizacionNacion") boolean conAutorizacionNacion) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            ReaperturaCentroComputoResponse resultado = this.cierreActividadesService.reabrirCC(
                    tokenInfo.getNombreUsuario(),
                    tokenInfo.getCodigoCentroComputo(),
                    conAutorizacionNacion
            );

            return ResponseHelperException.createSuccessResponse("Centro de cómputo reabierto correctamente", resultado);

        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "reabrir-cc");
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/validar-usuario-reapertura")
    @Operation(summary = "Validar usuario para la reapertura", description = "Verifica si el usuario que realizo el cierre del centro de computo es el mismo que esta realizando la reapertura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Es el mismo usuario"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para realizar la operación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GenericResponse<ValidarUsuarioReaperturaResponse>> validarUsuarioReapertura(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            ValidarUsuarioReaperturaResponse resultado = this.cierreActividadesService.validarUsuarioReapertura(
                    tokenInfo.getNombreUsuario(),
                    tokenInfo.getCodigoCentroComputo()
            );

            return ResponseHelperException.createSuccessResponse("Validación de usuario para la reapertura correctamente", resultado);

        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "validar-usuario-reapertura");
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @GetMapping("/estado-cc")
    @Operation(summary = "Consultar estado del centro de cómputo", description = "Permite consultar si un centro de cómputo está cerrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<GenericResponse<EstadoCentroComputoResponse>> consultarEstadoCC(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            EstadoCentroComputoResponse resultado = this.cierreActividadesService.consultarEstadoCC(
                    tokenInfo.getCodigoCentroComputo()
            );

            return ResponseHelperException.createSuccessResponse("Consulta realizada correctamente", resultado);

        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "estado-cc");
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/reapertura/autorizacion/consulta")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        String token = authorization.substring(SceConstantes.LENGTH_BEARER); // Remueve el prefijo "Bearer "
        Claims claims = this.tokenDecoder.decodeToken(token);
        String proceso = claims.get("apr", String.class);
        String cc = claims.get("ccc", String.class);
        String usr = claims.get("usr", String.class);

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.cierreActividadesService.getAutorizacionNacion(usr, proceso, cc);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/check")
    public ResponseEntity<GenericResponse<Boolean>> consultaAutorizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String ccc = tokenInfo.getCodigoCentroComputo();
        String proceso = tokenInfo.getAbrevProceso();
        String usr = tokenInfo.getNombreUsuario();
        String tipoAutorizacion = ConstantesAutorizacion.TIPO_AUTORIZACION_REAPERTURA_CC;

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.cierreActividadesService.consultaAutorizacion(usr, ccc, proceso, tipoAutorizacion);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }
}
