package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.ActaConArchivosNull;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.ActaConArchivosNullResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregirListItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaReprocesadaListIItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ArchivosActaDTO;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ReprocesarService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.TrazabilidadDto;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("actas")
public class ActaController {

    Logger logger = LoggerFactory.getLogger(ActaController.class);

    private final TokenUtilService tokenUtilService;

    private final CabActaService actaService;

    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;
 
    private final ReprocesarService reprocesarService;

    public ActaController(
                          TokenUtilService tokenUtilService,
                          CabActaService cabActaServicePostgress,
                          ActaTransmisionExecuteService actaTransmisionNacionStrategyService,
            ReprocesarService reprocesarService
    ) {
        this.tokenUtilService = tokenUtilService;
        this.actaService = cabActaServicePostgress;
        this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
        this.reprocesarService = reprocesarService;
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/sin-archivos")
    public ResponseEntity<ActaConArchivosNullResponse> getActasConArchivosNull() {
        ActaConArchivosNullResponse response = new ActaConArchivosNullResponse();
        List<ActaConArchivosNull> actasConArchivosNull = actaService.actasConArchivosNull();
        response.setActaConArchivosNull(actasConArchivosNull);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_REPORTES)
    @GetMapping("/trazabilidad/{mesa-copia-dig}")
    public ResponseEntity<GenericResponse<TrazabilidadDto>> trazabilidadActa(@PathVariable("mesa-copia-dig") String mesaCopiaDigito) {
        GenericResponse<TrazabilidadDto> genericResponse = new GenericResponse<>();
        try {
            genericResponse = this.actaService.trazabilidadActa(mesaCopiaDigito);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_REPORTES)
    @GetMapping("/trazabilidad/porMesa/{mesa}")
    public ResponseEntity<GenericResponse<List<TrazabilidadDto>>> trazabilidadActaPorMesa(@PathVariable("mesa") String nroMesa) {
        GenericResponse<List<TrazabilidadDto>> genericResponse = new GenericResponse<>();
        try {
            genericResponse = this.actaService.trazabilidadActaPorMesa(nroMesa);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    /**
     * Lista las actas por corregir por usuario
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/actasPorCorregir")
    public ResponseEntity<List<ActaPorCorregirListItem>> actasPorCorregir(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

        return ResponseEntity.status(HttpStatus.OK).body(this.actaService.listarActasPorCorregirPorUsuario(tokenInfo));
    }
    
    /**
     * Obtiene la información a mostrar de un acta por corregir, como ubigeo, electores hábiles
     * y sus tres digitaciones; las dos primeras son del UserValue1 y UserValue2 respectivamente
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/actasPorCorregir/{acta-id}")
    public ResponseEntity<ActaPorCorregir> actasPorCorregirInfo(
            @PathVariable("acta-id") Long actaId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actaService.actasPorCorregirInfo(actaId));
    }

    /**
     * Lista los archivos segun tipo de solución y tipo de transmisión
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/getArchivos")
    public ResponseEntity<List<Long>> listarArchivosPorActa(
            @RequestParam(value = "mesa", required = false) String mesa,
            @RequestParam(value = "codigoEleccion", required = false) String codigoEleccion,
            @RequestParam(value = "actaId", required = false) Long actaId) {

        return ResponseEntity.ok(
                this.actaService.listarArchivosPorActa(actaId, mesa, codigoEleccion)
        );
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/get-archivos-por-solucion/{acta-id}")
    public ResponseEntity<GenericResponse<ArchivosActaDTO>> listarArchivosPorSolucion(@PathVariable("acta-id") Long actaId) {
        return ResponseHelperException.createSuccessResponse("Se listó correctamente los archivos.", this.actaService.listarArchivosPorSolucion(actaId));
    }


    /**
     * Valida la tercera digitación (ES LA QUE MANDARÁ). Retorna las observaciones de SIN DATOS,
     * SIN FIRMA , ERROR MATERIAL
     * Si el usuario acepta procede con el registro, y se puede guardar como Acta Observada y Acta Normal
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/actasPorCorregir/validar")
    public ResponseEntity<List<String>> validarActaPorCorregir(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ActaPorCorregir actaPorCorregir) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actaService.validarActasPorCorregir(actaPorCorregir));
    }

    /**
     * Registra el acta por corregir, considerando la 3ra Digitacion (Terceros valores)
     * y se puede registrar como actas Observada u Procesada
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/actasPorCorregir/registrar")
    public ResponseEntity<Boolean> registrarActasPorCorregir(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ActaPorCorregir actaPorCorregir) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            String saved = this.actaService.registrarActasPorCorregir(actaPorCorregir, tokenInfo);
            if(saved.equals("1")){
                sincronizar(List.of(actaPorCorregir.getActa().getActaId()), tokenInfo, TransmisionNacionEnum.PROC_NORMAL_VERI_TRANSMISION);
            } else if(saved.equals("2")){
                sincronizar(List.of(actaPorCorregir.getActa().getActaId()), tokenInfo, TransmisionNacionEnum.PROC_OBS_VERI_TRANSMISION);
            }

            return ResponseEntity.status(HttpStatus.OK).body(true);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }



    private void sincronizar(List<Long> idActas, TokenInfo tokenInfo, TransmisionNacionEnum transmisionNacionEnum) {
        try {
            this.actaTransmisionNacionStrategyService.sincronizar(idActas, tokenInfo.getAbrevProceso(), transmisionNacionEnum, tokenInfo.getNombreUsuario());
        } catch (Exception e) {
            logger.info(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
        }
    }

    /**
     * Valida  la existencia del acta y si tiene los estados finales C_ESTADO_ACTA D-L
     * para realizarle reprocesamiento
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/reprocesarActa/{mesa-copia-dig}")
    public ResponseEntity<GenericResponse<ActaReprocesadaListIItem>> reprocesarActaValidar(
            @PathVariable("mesa-copia-dig") String mesaCopiaDigito) {
    	
        GenericResponse<ActaReprocesadaListIItem> genericResponse = new GenericResponse<>();
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.actaService.validarReprocesamientoActa(mesaCopiaDigito));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    /**
     * Realiza el reprocesamiento del acta, cambia a estados
     */
    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/reprocesarActa")
    public ResponseEntity<GenericResponse<Boolean>> reprocesarActas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody List<ActaReprocesadaListIItem> actasReprocesarList) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> genericResponse = this.actaService.reprocesarActas(actasReprocesarList, tokenInfo);
            if(genericResponse.isSuccess() && !genericResponse.getActasId().isEmpty()) {
            	
            	List<Long> actaIdsLong = genericResponse.getActasId();

                sincronizar(actaIdsLong, tokenInfo, TransmisionNacionEnum.REPROCESAMIENTO_ACTA);
            }
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, ConstantesComunes.MSJ_ERROR + e.getMessage(), false));
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/puesta-cero-por-acta")
    public ResponseEntity<GenericResponse<Boolean>> puestaCeroPorActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("mesa") String mesa,
            @RequestParam("codigoEleccion") String codigoEleccion) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            return ResponseEntity.status(HttpStatus.OK).body(this.actaService.puestaCeroPorActa(mesa, codigoEleccion, tokenInfo.getNombreUsuario()));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            genericResponse.setSuccess(false);
            genericResponse.setMessage(ConstantesComunes.MSJ_ERROR + e.getMessage());
            genericResponse.setData(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/rechazarActaEnVerificacion")
    public ResponseEntity<GenericResponse<Boolean>> rechazarActaEnVerificacion (
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("mesa") String mesa,
            @RequestParam("codigoEleccion") String codigoEleccion) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> genericResponse =  this.actaService.rechazarActaEnVerificacion(tokenInfo, mesa, codigoEleccion);
            if(genericResponse.isSuccess() && !genericResponse.getActasId().isEmpty()) {
                Long actaId = genericResponse.getActasId().getFirst();
                Optional<Acta> optActa = this.actaService.findById(actaId);
                // No sincronizar si es ubigeo extranjero (código empieza con "9")
                boolean esExtranjero = optActa.isPresent()
                        && optActa.get().getMesa().getLocalVotacion().getUbigeo().getCodigo().startsWith("9")
                        && this.actaService.getDetActaAccionService().existsByActaAccionTiempoAndUsuarioPrefix(
                                actaId,
                                ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA,
                                ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                                ConstantesComunes.USUARIO_CONSULADO);
                if (!esExtranjero) {
                    sincronizar(List.of(actaId), tokenInfo, TransmisionNacionEnum.RECHAZAR_EN_VERIFICACION_TRANSMISION);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Error "+e.getMessage(), false));
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @GetMapping("/listReprocesar")
    public ResponseEntity<GenericResponse<List<ActaReprocesadaListIItem>>> listReprocesar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse<List<ActaReprocesadaListIItem>> genericResponse = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            return ResponseEntity.status(HttpStatus.OK).body(this.actaService.listReprocesar(tokenInfo.getNombreUsuario()));
        } catch (Exception e) {
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/updateReprocesar")
    public ResponseEntity<GenericResponse<Boolean>> actualizarlistProcesar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody List<ActaReprocesadaListIItem> actasReprocesarList) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            return ResponseEntity.status(HttpStatus.OK).body(this.actaService.reprocesarListActas(actasReprocesarList, tokenInfo.getNombreUsuario()));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/reprocesarActa/autorizacion/consulta")
    public ResponseEntity<GenericResponse<AutorizacionNacionResponseDto>> consultaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
    	TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    	String ccc = tokenInfo.getCodigoCentroComputo();
    	String usr = tokenInfo.getNombreUsuario();
    	String proceso = tokenInfo.getAbrevProceso();
        logger.info("Proceso: {}", proceso);

        try{
            GenericResponse<AutorizacionNacionResponseDto> genericResponse = new GenericResponse<>();
            AutorizacionNacionResponseDto autorizacionNacion = this.reprocesarService.getAutorizacionNacion(usr, ccc, proceso);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/reprocesarActa/autorizacion/solicitar")
    public ResponseEntity<GenericResponse<Boolean>> solicitaAutorizacionNacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam(name = "tipo", required = false) String tipo
    ) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    	String ccc = tokenInfo.getCodigoCentroComputo();
    	String usr = tokenInfo.getNombreUsuario();
    	String proceso = tokenInfo.getAbrevProceso();

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            Boolean autorizacionNacion = this.reprocesarService.solicitaAutorizacionReprocesar(usr, ccc, proceso);
            genericResponse.setData(autorizacionNacion);
            genericResponse.setSuccess(true);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }
}
