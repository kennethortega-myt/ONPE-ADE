package pe.gob.onpe.sceorcbackend.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AccesoPcService;
import pe.gob.onpe.sceorcbackend.rest.controller.reporte.BaseController;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
@RestController
@Validated
@RequestMapping("/acceso-pc")
@RequiredArgsConstructor
public class AccesoPcController extends BaseController {

    private final AccesoPcService accesoPcService;
    private final TokenUtilService tokenUtilService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<AccesoPcResponse>>> listarTodos() {
        try {
            List<AccesoPcResponse> accesos = accesoPcService.listarTodos();
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, accesos);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @GetMapping("/paginado")
    public ResponseEntity<GenericResponse<Page<AccesoPcResponse>>> listarPaginado(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Page<AccesoPcResponse> accesos = accesoPcService.listarTodosPaginado(page, size);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, accesos);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<GenericResponse<List<AccesoPcResponse>>> listarPorUsuario(
            @PathVariable String usuario) {
        try {
            List<AccesoPcResponse> accesos = accesoPcService.listarPorUsuario(usuario);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, accesos);
        } catch (Exception e) {
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @PostMapping("/autorizacion/consulta")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestBody AccesoPcRequest accesoPcRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String cc = tokenInfo.getCodigoCentroComputo();
        String usr = tokenInfo.getNombreUsuario();
        String abrevProceso = tokenInfo.getAbrevProceso();
        try{
            AutorizacionNacionResponseDto autorizacionNacion = this.accesoPcService.getAutorizacionNacion(usr, cc, accesoPcRequest, abrevProceso);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, autorizacionNacion);
        }catch(Exception e){
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @PostMapping("/autorizacion/solicitar")
    public ResponseEntity<GenericResponse<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AccesoPcRequest accesoPcRequest
    ) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String ccc = tokenInfo.getCodigoCentroComputo();
        String nombreCentroComputo = tokenInfo.getNombreCentroComputo();
        String usr = tokenInfo.getNombreUsuario();
        String abrevProceso = tokenInfo.getAbrevProceso();

        try{
            Boolean autorizacionNacion = this.accesoPcService.solicitarAutorizacion(usr, ccc, nombreCentroComputo, abrevProceso, accesoPcRequest);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, autorizacionNacion);

        }catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @PostMapping("/actualizar-estado-acceso")
    public ResponseEntity<GenericResponse<Boolean>> actualizarEstadoAccesoPc(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AccesoPcRequest accesoPcRequest) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        try{
            Boolean resultado = this.accesoPcService.actualizarEstado(accesoPcRequest, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo());
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, resultado);
        }catch (Exception e){
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }
    
    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> reporteListadoPcs(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        
        byte[] resultado = this.accesoPcService.getReportePcs(tokenInfo.getAbrevProceso(), 
        						tokenInfo.getNombreUsuario(), 
        						tokenInfo.getCodigoCentroComputo(), 
        						tokenInfo.getNombreCentroComputo());
        
        return getPdfResponse(resultado);
            
    }
}
