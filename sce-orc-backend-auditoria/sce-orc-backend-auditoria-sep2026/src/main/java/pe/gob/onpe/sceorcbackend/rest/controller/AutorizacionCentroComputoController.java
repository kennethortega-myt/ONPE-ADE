package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.AutorizacionCCRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.AutorizacionCCResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.ConsultaEstadoSolicitudResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCentroComputoService;
import pe.gob.onpe.sceorcbackend.rest.controller.reporte.BaseController;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/autorizacion-cc")
public class AutorizacionCentroComputoController extends BaseController {

    Logger logger = LoggerFactory.getLogger(AutorizacionCentroComputoController.class);

    private final TokenUtilService tokenUtilService;
    private final AutorizacionCentroComputoService autorizacionCentroComputoService;

    public AutorizacionCentroComputoController(AutorizacionCentroComputoService autorizacionCentroComputoService,
                                               TokenUtilService tokenUtilService) {
        this.autorizacionCentroComputoService = autorizacionCentroComputoService;
        this.tokenUtilService = tokenUtilService;
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/recibir-autorizacion")
    public ResponseEntity<GenericResponse<AutorizacionCCResponseDto>> recibirAutorizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AutorizacionCCRequestDto autorizacionCCRequestDto
    ) {
        try{
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            autorizacionCCRequestDto.setUsuario(tokenInfo.getNombreUsuario());

            AutorizacionCCResponseDto autorizacionCCResponseDto = autorizacionCentroComputoService.recibirAutorizacion(autorizacionCCRequestDto);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, autorizacionCCResponseDto);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/crear-solicitud-autorizacion")
    public ResponseEntity<GenericResponse<Boolean>> crearSolicitudAutorizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AutorizacionCCRequestDto autorizacionCCRequestDto
    ) {
        try{
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            autorizacionCCRequestDto.setUsuario(tokenInfo.getNombreUsuario());

            boolean resultado = autorizacionCentroComputoService.crearSolicitudAutorizacion(autorizacionCCRequestDto, tokenInfo.getCodigoCentroComputo());
            String mensaje="";
            if (resultado){
                mensaje = ConstantesComunes.MENSAJE_SOLICITUD_USUARIO + autorizacionCCRequestDto.getUsuario() + " realizada. Vuelva a consultar tras la aprobación del administrador.";
            }else{
                mensaje = "Ya existe una autorización para esta solicitud";
            }

            return ResponseHelperException.createSuccessResponse(mensaje, resultado);

        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/consultar-estado-solicitud")
    public ResponseEntity<GenericResponse<ConsultaEstadoSolicitudResponseDto>> consultarEstadoSolicitud(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody AutorizacionCCRequestDto autorizacionCCRequestDto
    ) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            autorizacionCCRequestDto.setUsuario(tokenInfo.getNombreUsuario());

            ConsultaEstadoSolicitudResponseDto responseDto =
                    autorizacionCentroComputoService.consultarEstadoSolicitud(autorizacionCCRequestDto);
            return ResponseHelperException.createSuccessResponse(ConstantesComunes.TEXTO_OPERACION_EXITOSA, responseDto);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseHelperException.handleCommonExceptions(e);
        }
    }
}
