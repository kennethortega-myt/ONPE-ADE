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
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificaVersionService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import java.util.Base64;

@RestController
@RequestMapping("/verifica-version")
public class VerificaVersionController {

    Logger logger = LoggerFactory.getLogger(VerificaVersionController.class);

    private final TokenUtilService tokenUtilService;
    private final VerificaVersionService verificaVersionService;

    private final ITabLogService logService;

    public VerificaVersionController(TokenUtilService tokenUtilService,
                                     VerificaVersionService verificaVersionService,
                                     ITabLogService logService) {
        this.tokenUtilService = tokenUtilService;
        this.verificaVersionService = verificaVersionService;
        this.logService = logService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/puesta-cero")
    public ResponseEntity<GenericResponse<String>> puestaCeroVerificaOrc(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse<String> genericResponse = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            this.verificaVersionService.puestaCero(tokenInfo.getNombreUsuario());
            String mensaje = "La puesta a cero de la verificación de versión se realizó correctamente.";
            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            genericResponse.setMessage(mensaje);
            genericResponse.setSuccess(Boolean.TRUE);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error("Error /puesta-cero", e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/procesar-orc")
    public ResponseEntity<GenericResponse<String>> procesarVerificaVersionOrc(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse<String> response = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
            String mensaje = verificaVersionService.procesarOrc(tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getAbrevProceso());
            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            response.setSuccess(true);
            response.setMessage(mensaje);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error /procesar", e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/reporte")
    public ResponseEntity<GenericResponse<String>> reporteVerificaVersion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        GenericResponse<String> genericResponse = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            byte[] reporte = this.verificaVersionService.reporteVerificaVersion(
                    tokenInfo.getAbrevProceso(),
                    tokenInfo.getCodigoCentroComputo(),
                    tokenInfo.getNombreCentroComputo(),
                    tokenInfo.getNombreUsuario());
            if (reporte != null) {
                String encodedString = Base64.getEncoder().encodeToString(reporte);
                genericResponse.setSuccess(Boolean.TRUE);
                genericResponse.setMessage("El reporte de verificación de versión se generó correctamente.");

                this.logService.registrarLog(
                        tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        genericResponse.getMessage(),
                        tokenInfo.getCodigoCentroComputo(),
                        ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

                genericResponse.setData(encodedString);
            } else {
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage("Error al generar el reporte.");
            }
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }


}
