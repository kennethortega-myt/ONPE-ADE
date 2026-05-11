package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.FiltroPuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.IVerificaVersionService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.Base64;

@PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
@RestController
@RequestMapping("verifica-version-nacion")
public class VerificaVersionController extends BaseController {

    Logger logger = LoggerFactory.getLogger(VerificaVersionController.class);

    private final IVerificaVersionService verificaVersionService;

    private final ITabLogTransaccionalService logService;

    private final UtilSceService utilSceService;

    public VerificaVersionController(TokenDecoder tokenDecoder, IVerificaVersionService verificaVersionService, UtilSceService utilSceService,ITabLogTransaccionalService logService) {
        super(tokenDecoder);
        this.verificaVersionService = verificaVersionService;
        this.utilSceService = utilSceService;
        this.logService = logService;
    }


    @GetMapping("")
    public ResponseEntity<String> version(
    ) {
        try {
            String resultado = this.utilSceService.getVersionSistema();
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }


    @PostMapping("/puesta-cero")
    public ResponseEntity<GenericResponse> puestaCero(@RequestBody FiltroPuestaCeroDTO filtro) {
        logger.info("Se ejecutó puestaCero()");

        GenericResponse response = new GenericResponse();
        try {
            boolean exito = verificaVersionService.puestaCero(filtro.getEsquema(), filtro.getUsuario());
            response.setSuccess(exito);
            response.setMessage(exito ? "Se realizó la puesta a cero de verificación de versión correctamente." : "No se pudo realizar la puesta a cero de la verificación de versión.");
            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), response.getMessage(),
                ConstantesComunes.VACIO, ConstantesComunes.VACIO, 0, 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/procesar")
    public ResponseEntity<GenericResponse> procesar(@RequestBody FiltroPuestaCeroDTO filtro) {
        GenericResponse genericResponse = new GenericResponse();
        try {
            boolean procesado = this.verificaVersionService.procesar(filtro.getUsuario(), filtro.getEsquema());

            genericResponse.setMessage(procesado
                ? "Se procesó la verificación de versión correctamente."
                : "Ya se ha procesado la Verificación de Versión.");
            genericResponse.setSuccess(true);

            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), genericResponse.getMessage(),
                ConstantesComunes.VACIO, ConstantesComunes.VACIO, 0, 0);

            return ResponseEntity.ok(genericResponse);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setSuccess(false);
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }


    @PostMapping("/reporte")
    public ResponseEntity<GenericResponse> reporte(@RequestBody FiltroPuestaCeroDTO filtro) {
        logger.info("Se ejecutó reporte() desde {}", this.getClass().getSimpleName());

        GenericResponse response = new GenericResponse();

        try {
            byte[] reporteBytes = verificaVersionService.reporteVerificaVersion(
                filtro.getNombre(),
                filtro.getAcronimo(),
                filtro.getUsuario(),
                filtro.getEsquema()
            );

            if (reporteBytes != null && reporteBytes.length > 0) {
                String encodedString = Base64.getEncoder().encodeToString(reporteBytes);
                response.setSuccess(true);
                response.setMessage("Se generó el reporte de verificación de versión correctamente.");
                response.setData(encodedString);
            } else {
                response.setSuccess(false);
                response.setMessage("No se pudo generar el reporte de verificación de versión; el contenido está vacío.");
            }

            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), response.getMessage(),
                ConstantesComunes.VACIO, ConstantesComunes.VACIO, 0, 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
