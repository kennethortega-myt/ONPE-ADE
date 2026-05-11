package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PuestaCero;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroTransmision;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.AutorizacionDto;
import pe.gob.onpe.sceorcbackend.rest.controller.reporte.BaseController;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetParametroRepository;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/puesta-cero")
public class PuestaCeroController extends BaseController {

    Logger logger = LoggerFactory.getLogger(PuestaCeroController.class);

    private final PuestaCeroService puestaCeroService;

    private final TokenUtilService tokenUtilService;

    private final CentroComputoService centroComputoService;
    private final ITabLogService logService;

    private final StorageService storageService;

    private final PuestaCeroTransmision puestaCeroTransmision;

    private final DetParametroRepository detParametroRepository;

    public PuestaCeroController(PuestaCeroService puestaCeroService,
                                TokenUtilService tokenUtilService,
                                PuestaCeroTransmision puestaCeroTransmision,
                                CentroComputoService centroComputoService,
                                ITabLogService logService,
                                DetParametroRepository detParametroRepository,
                                StorageService storageService
    ) {
        this.puestaCeroService = puestaCeroService;
        this.tokenUtilService = tokenUtilService;
        this.puestaCeroTransmision = puestaCeroTransmision;
        this.centroComputoService = centroComputoService;
        this.logService = logService;
        this.detParametroRepository = detParametroRepository;
        this.storageService = storageService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @GetMapping("/list-autorizacion")
    public ResponseEntity<GenericResponse<List<AutorizacionDto>>> listarAutorizacionPuestaCero() {
        GenericResponse<List<AutorizacionDto>> genericResponse = this.puestaCeroService.listarAutorizaciones();
        return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/autorizacion")
    public ResponseEntity<GenericResponse<TabAutorizacion>> autoizacionPuestaCero(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam(name = "tipo", required = false) String tipo
    ) {
        try{
            GenericResponse<TabAutorizacion> genericResponse;
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            genericResponse = puestaCeroService.registrarAutorizacion(tokenInfo.getCodigoCentroComputo(),tokenInfo.getNombreUsuario(), tipo, tokenInfo.getAbrevProceso());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/approve-autorizacion/{idAutorizacion}")
    public ResponseEntity<GenericResponse<TabAutorizacion>> aprobarAutorizacionPuestaCero(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable("idAutorizacion") Long id
    ) {
        try{
            GenericResponse<TabAutorizacion> genericResponse;
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            genericResponse = puestaCeroService.aprobarAutorizacion(id, tokenInfo.getNombreUsuario());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/rechazar-autorizacion/{idAutorizacion}")
    public ResponseEntity<GenericResponse<TabAutorizacion>> rechazarAutorizacionPuestaCero(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable("idAutorizacion") Long id
    ) {
        try{
            GenericResponse<TabAutorizacion> genericResponse;
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            genericResponse = puestaCeroService.rechazarAutorizacion(id, tokenInfo.getNombreUsuario());
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/reporte")
    public ResponseEntity<GenericResponse<String>> getReportePuestaCero(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        byte[] reporte = this.puestaCeroService.reportePuestaCeroCentroComputo(tokenInfo.getAbrevProceso(), tokenInfo.getCodigoCentroComputo(),
                tokenInfo.getNombreCentroComputo(), tokenInfo.getNombreUsuario());
        return getPdfResponse(reporte);
    }


    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/general")
    public ResponseEntity<GenericResponse<String>> puestaCeroGneral(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idAutorizacion") String idAutorizacion) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        try {
            actualizarParametroIntegridad(ConstantesComunes.TEXT_TRUE, tokenInfo.getNombreUsuario());
            puestaCeroService.puestaCeroGeneral(tokenInfo, idAutorizacion);
            this.eliminarArchivosRepositorio();
            String mensaje = idAutorizacion.equals("0") ? "Se realizó la puesta a cero del centro de cómputo." :
                "Se realizó la puesta a cero con autorización nación.";
            Integer tipoAutorizacion = idAutorizacion.equals("0") ?
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO : ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI;
            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    tokenInfo.getCodigoCentroComputo(),
                    tipoAutorizacion, 1);
            return ResponseHelperException.createSuccessResponse("Se realizó la puesta a cero en centro de cómputo.");
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        } finally {
            actualizarParametroIntegridad(ConstantesComunes.TEXT_FALSE, tokenInfo.getNombreUsuario());
        }
    }

    private void eliminarArchivosRepositorio() {
        try {
            logger.info("Eliminando archivos repositorio");
            storageService.deleteAllFilesRepositoryAsyncSubCarpeta();
            logger.info("Archivos del repositorio: directorio renombrado y recreado, eliminación en segundo plano");
        } catch (IOException e) {
            throw new InternalServerErrorException(
                    "Error eliminando archivos del repositorio", e
            );
        }
    }

    private void actualizarParametroIntegridad(String valorParametro, String usuario) {
        try {
            this.detParametroRepository.actualizarValorPorParametroYNombre(
                valorParametro, new Date(), usuario,
                ConstantesParametros.CAB_PARAM_EJECUCION_PUESTA_CERO,
                ConstantesParametros.DET_PARAM_EJECUCION_PUESTA_CERO
            );
            logger.info("Parámetro de ejecución de puesta a cero actualizado a: {}", valorParametro);
        } catch (Exception ex) {
            logger.error("Error al actualizar parámetro de ejecución de puesta a cero : {}", ex.getMessage(), ex);
        }
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/transmision-puesta-cero")
    public ResponseEntity<GenericResponse<String>> transmisionPuestaCero(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse<String> genericResponse = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            this.puestaCeroService.desactivarPuestaCeroMasivo();

            PuestaCero puestaCero = new PuestaCero();
            puestaCero.setActivo(ConstantesComunes.ACTIVO);
            puestaCero.setFechaCreacion(new Date());
            puestaCero.setUsuarioCreacion(tokenInfo.getNombreUsuario());
            puestaCero.setFechaEjecucion(puestaCero.getFechaCreacion());

            Optional<CentroComputo> optionalCentroComputo = this.centroComputoService.findByCodigo(tokenInfo.getCodigoCentroComputo());
            if(optionalCentroComputo.isPresent()) {
                puestaCero.setCentroComputo(optionalCentroComputo.get());
                this.puestaCeroService.save(puestaCero);
            }

            logger.info("Inicio transmisión de la puesta cero a nación.");
            this.puestaCeroTransmision.sincronizar(
                    tokenInfo.getAbrevProceso(),
                    tokenInfo.getCodigoCentroComputo(),
                    tokenInfo.getNombreUsuario(), 
                    puestaCero.getFechaEjecucion(),
                    true);
            logger.info("Fin transmisión de la puesta cero a nación.");

            genericResponse.setMessage("Se realizó la transmisión de la puesta cero a nación correctamente.");
            genericResponse.setSuccess(Boolean.TRUE);
            return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Ocurrió un error al transmitir la puesta cero a nación: "+e.getMessage());
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @PostMapping("/confirm-from-cc")
    public ResponseEntity<GenericResponse<Boolean>> confirmarPuestaCeroDesdeCC(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        try{
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> genericResponse = puestaCeroService.confirmarPuestaCeroDesdeCC(tokenInfo);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

}
