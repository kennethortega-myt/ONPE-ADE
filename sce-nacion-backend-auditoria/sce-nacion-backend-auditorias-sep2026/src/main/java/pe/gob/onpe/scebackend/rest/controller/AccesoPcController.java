package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.exeption.ResponseHelperException;
import pe.gob.onpe.scebackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.scebackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponseAlternative;
import pe.gob.onpe.scebackend.model.service.IAccesoPcService;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@RestController
@Validated
@RequestMapping("/acceso-pc-nacion")
public class AccesoPcController extends BaseController {
    private final IConfiguracionProcesoElectoralService confProcesoService;
    private final IAccesoPcService accesoPcService;

    public AccesoPcController(IConfiguracionProcesoElectoralService confProcesoService,
                              TokenDecoder tokenDecoder,
                              IAccesoPcService accesoPcService
                              ) {
        super(tokenDecoder);
        this.confProcesoService = confProcesoService;
        this.accesoPcService = accesoPcService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @GetMapping("/paginado")
    public ResponseEntity<GenericResponseAlternative<Page<AccesoPcResponse>>> listarPaginado(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Page<AccesoPcResponse> accesos = accesoPcService.listarTodosPaginado(page, size);
            return ResponseHelperException.createSuccessResponse("Operación realizada con éxito", accesos);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e, "AccesoPcController.listarPaginado");
        }
    }

    @PostMapping("/autorizacion/consulta")
    public ResponseEntity<GenericResponseAlternative<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestBody AccesoPcRequest accesoPcRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        try{
            LoginUserHeader user = getUserLogin(authorization);
            AutorizacionNacionResponseDto autorizacionNacion = this.accesoPcService.getAutorizacionNacion(user.getUsuario(), accesoPcRequest);
            return ResponseHelperException.createSuccessResponse("Consulta realizada con éxito", autorizacionNacion);
        }catch(Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AccesoPcController.consultaAutorizacionNacion");
        }
    }

    @PostMapping("/autorizacion/solicitar")
    public ResponseEntity<GenericResponseAlternative<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AccesoPcRequest accesoPcRequest
    ) {
        try{
            LoginUserHeader user = getUserLogin(authorization);
            Boolean autorizacionNacion = this.accesoPcService.solicitarAutorizacion(user.getUsuario(), accesoPcRequest);
            return ResponseHelperException.createSuccessResponse("Solicitud realizada con éxito", autorizacionNacion);

        }catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AccesoPcController.solicitaAutorizacionNacion");
        }
    }

    @PostMapping("/actualizar-estado-acceso")
    public ResponseEntity<GenericResponseAlternative<Boolean>> actualizarEstadoAccesoPc(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AccesoPcRequest accesoPcRequest) {
        try{
            LoginUserHeader user = getUserLogin(authorization);
            Boolean resultado = this.accesoPcService.actualizarEstado(accesoPcRequest, user.getUsuario());
            return ResponseHelperException.createSuccessResponse("Se actualizó el estado del acceso pc seleccionado.", resultado);
        }catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e, "AccesoPcController.actualizarEstadoAccesoPc");
        }
    }
    
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> reporteListadoPcs(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    	LoginUserHeader user = getUserLogin(authorization);
        
        byte[] resultado = this.accesoPcService.getReportePcs(user.getUsuario());
        
        return getPdfResponse(resultado);
            
    }
}
