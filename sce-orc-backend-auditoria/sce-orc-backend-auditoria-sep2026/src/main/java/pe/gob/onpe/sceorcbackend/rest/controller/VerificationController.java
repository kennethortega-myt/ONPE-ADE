package pe.gob.onpe.sceorcbackend.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores.VerificationLe;
import pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa.VerificationMm;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.dto.queue.PredictionActaRequest;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificationService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificationServiceListaElectores;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificationServiceMiembrosMesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationActaDTO;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/verification")
public class VerificationController {
	
    Logger logger = LoggerFactory.getLogger(VerificationController.class);

    @Value("${orden.verificacion.actas}")
    private String propOrdenVerificacionActas;

    private final VerificationService verificationService;
    private final VerificationServiceMiembrosMesa verificationServiceMiembrosMesa;
    private final TokenUtilService tokenUtilService;
    private final VerificationServiceListaElectores  verificationServiceListaElectores;
    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;
    private final CabActaService cabActaService;
    private final RabbitMqSender rabbitMqSender;


    public VerificationController(VerificationService verificationService,
                                  TokenUtilService tokenUtilService,
                                  VerificationServiceListaElectores verificationServiceListaElectores,
                                  VerificationServiceMiembrosMesa verificationServiceMiembrosMesa,
                                  ActaTransmisionExecuteService actaTransmisionNacionStrategyService,
                                  CabActaService cabActaService,
                                  RabbitMqSender rabbitMqSender) {
        this.verificationService = verificationService;
        this.tokenUtilService = tokenUtilService;
        this.verificationServiceListaElectores = verificationServiceListaElectores;
        this.verificationServiceMiembrosMesa = verificationServiceMiembrosMesa;
        this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
        this.cabActaService = cabActaService;
        this.rabbitMqSender = rabbitMqSender;
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/getRandomActa")
    public ResponseEntity<GenericResponse<VerificationActaDTO>> getRandomActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("codigoEleccion") String codigoEleccion) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<VerificationActaDTO> genericResponse = verificationService.obtenerActaRandom(codigoEleccion, tokenInfo);
            if(genericResponse.isSuccess() && genericResponse.getActasId()!=null && !genericResponse.getActasId().isEmpty()){
                    Long idActa = genericResponse.getActasId().getFirst();
                    if(idActa!=0L) {
                        Optional<Acta> optActa = this.cabActaService.findById(idActa);
                        if(optActa.isPresent()) {
                            if(optActa.get().getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA))
                                sincronizar(idActa, TransmisionNacionEnum.PRIMERA_VERI_TRANSMISION, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
                            else if(optActa.get().getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION))
                                sincronizar(idActa, TransmisionNacionEnum.A_SEGUNDA_VERI_TRANSMISION, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());

                        }
                    }
                }

            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

        } catch (Exception e) {
            logger.error("Error /getRandomActa",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/getRandomActaWithImages")
    public ResponseEntity<GenericResponse<VerificationActaDTO>> getRandomActaWithImages(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("codigoEleccion") String codigoEleccion) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<VerificationActaDTO> genericResponse = verificationService.obtenerActaRandomConImagenes(codigoEleccion, tokenInfo);
            if(genericResponse.isSuccess() && genericResponse.getActasId()!=null && !genericResponse.getActasId().isEmpty()){
                    Long idActa = genericResponse.getActasId().getFirst();
                    if(idActa!=0L) {
                        Optional<Acta> optActa = this.cabActaService.findById(idActa);
                        if(optActa.isPresent()) {
                            if(optActa.get().getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA))
                                sincronizar(idActa, TransmisionNacionEnum.PRIMERA_VERI_TRANSMISION, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
                            else if(optActa.get().getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION))
                                sincronizar(idActa, TransmisionNacionEnum.A_SEGUNDA_VERI_TRANSMISION, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
                        }
                    }
                }

            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

        } catch (Exception e) {
            logger.error("Error /getRandomActaWithImages",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/getActaRandomProcesamientoManual")
    public ResponseEntity<GenericResponse<VerificationActaDTO>> getActaRandomProcesamientoManual(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("codigoEleccion") String codigoEleccion) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<VerificationActaDTO> genericResponse =
                    verificationService.obtenerActaRandomParaProcesamientoManual(codigoEleccion, tokenInfo);

            if (genericResponse.isSuccess() &&
                    genericResponse.getActasId() != null &&
                    !genericResponse.getActasId().isEmpty()) {

                Long idActaRespuesta = genericResponse.getActasId().getFirst();
                if (idActaRespuesta != 0L) {
                    Optional<Acta> optActa = this.cabActaService.findById(idActaRespuesta);
                    if (optActa.isPresent()) {
                        Acta acta = optActa.get();
                        if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {
                            sincronizar(idActaRespuesta,
                                    TransmisionNacionEnum.PRIMERA_VERI_TRANSMISION,
                                    tokenInfo.getNombreUsuario(),
                                    tokenInfo.getAbrevProceso());
                        } else if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
                            sincronizar(idActaRespuesta,
                                    TransmisionNacionEnum.A_SEGUNDA_VERI_TRANSMISION,
                                    tokenInfo.getNombreUsuario(),
                                    tokenInfo.getAbrevProceso());
                        }
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

        } catch (Exception e) {
            logger.error("Error /getActaProcesamientoManual", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/getRandomListaElectores")
    public ResponseEntity<GenericResponse<VerificationLe>> getRandomListaElectores(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam("reprocesar") boolean reprocesar,
            @RequestParam(name = "tipoDenuncia", required = false) String tipoDenuncia) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<VerificationLe> response = this.verificationServiceListaElectores.
                    getRandomListaElectores(tokenInfo, reprocesar);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/getRandomMiembrosMesa")
    public ResponseEntity<GenericResponse<VerificationMm>> getRandomMiembrosMesa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam("reprocesar") boolean reprocesar,
            @RequestParam(name = "tipoDenuncia", required = false) String tipoDenuncia) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<VerificationMm> response = verificationServiceMiembrosMesa.getRandomMiembrosMesa(tokenInfo,
                    reprocesar, tipoDenuncia);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/saveListaElectores")
    public ResponseEntity<GenericResponse<Boolean>> saveListaElectores(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody VerificationLe request, @RequestParam("reprocesar") boolean reprocesar) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> response = verificationServiceListaElectores.saveListaElectores(request, tokenInfo, reprocesar);
            if(response.isSuccess()){
                sincronizarMesa(request.getMesaId(),TransmisionNacionEnum.OMISOS_LISTA_ELECTORES_VOTANTES ,tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/rechazarListaElectores")
    public ResponseEntity<GenericResponse<Boolean>> rechazarListaElectores(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("mesaId") Long mesaId) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> response = verificationServiceListaElectores.rechazarListaElectores(mesaId, tokenInfo);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/saveMiembrosMesa")
    public ResponseEntity<GenericResponse<Boolean>> saveMiembrosMesa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody VerificationMm request, @RequestParam("reprocesar") boolean reprocesar) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> response = verificationServiceMiembrosMesa.saveMiembrosMesa(request, tokenInfo, reprocesar);

            if(response.isSuccess()){
                sincronizarMesa(request.getMesaId(),TransmisionNacionEnum.OMISOS_MIEMBRO_MESA ,tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
            }

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/rechazarMiembrosMesa")
    public ResponseEntity<GenericResponse<Boolean>> rechazarMiembrosMesa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("mesaId") Long mesaId) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> response = verificationServiceMiembrosMesa.rechazarMiembrosMesa(mesaId, tokenInfo);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK,e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/consulta-padron")
    public ResponseEntity<GenericResponse<PadronDto>> consultaPadron(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("dni") String dni,
            @RequestParam(value = "mesa", required = false) String mesa,
            @RequestParam(value = "omitirMesa", required = false) Boolean omitirMesa) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseHelperException.createSuccessResponse(String.format("Se obtuvo correctamente los datos del DNI %s.",dni), verificationService.consultaPadronPorDni(dni, mesa, tokenInfo, omitirMesa));
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/saveActa")
    public ResponseEntity<GenericResponse<Boolean>> saveActa (
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody VerificationActaDTO request) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Boolean> respuesta =  this.verificationService.guardar(request, tokenInfo);
            if(respuesta.isSuccess()) {
                if (!respuesta.getActasId().isEmpty()){
                    Long actaId = respuesta.getActasId().getFirst();
                    Optional<Acta> optActa = this.cabActaService.findById(actaId);

                    if(request.getPropOrdenVerificacionActas() == null){
                        request.setPropOrdenVerificacionActas(ConstantesComunes.COMPARAR_DIGITACION_CASO_1);
                    }

                    // Sincronizar: si properties=CASO_1 siempre, si properties=CASO_2 solo cuando dto=1 y acta en SEGUNDA_VERIFICACION
                    if (ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(propOrdenVerificacionActas)) {
                        logger.info("[SYNC] Sincronizando acta (CASO_1) actaId={}, usuario={}", actaId, tokenInfo.getNombreUsuario());
                        optActa.ifPresent(acta -> sincronizarActa(acta, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso()));
                    } else if (ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(request.getPropOrdenVerificacionActas())
                        && optActa.isPresent()
                        && (ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION.equals(optActa.get().getEstadoActa())||
                            ConstantesEstadoActa.ESTADO_ACTA_PROCESADA.equals(optActa.get().getEstadoActa())||
                            ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(optActa.get().getEstadoActa())||
                             ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR.equals(optActa.get().getEstadoActa())
                        )) {
                        logger.info("[SYNC] Sincronizando acta (CASO_2, 2da veri) actaId={}, usuario={}", actaId, tokenInfo.getNombreUsuario());
                        optActa.ifPresent(acta -> sincronizarActa(acta, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso()));
                    }


                    // Enviar a cola de prediccion solo una vez: properties=CASO_2, dto=CASO_1, acta en DIGITADA
                    if (ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)
                        && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(request.getPropOrdenVerificacionActas())
                        && optActa.isPresent()
                        && ConstantesEstadoActa.ESTADO_ACTA_DIGITADA.equals(optActa.get().getEstadoActa())) {
                        logger.info("[PREDICTION] Enviando acta a cola prediction actaId={}, usuario={}", actaId, tokenInfo.getNombreUsuario());
                        request.setPropOrdenVerificacionActas(propOrdenVerificacionActas);//eso va al MODELO y este registrara, se envia TIPO 2 LO CONFIGURADO
                        PredictionActaRequest predictionRequest = PredictionActaRequest.builder()
                                .actaId(actaId)
                                .nombreUsuario(tokenInfo.getNombreUsuario())
                                .codigoCentroComputo(tokenInfo.getCodigoCentroComputo())
                                .abrevProceso(tokenInfo.getAbrevProceso())
                                .body(request)
                                .build();


                        Acta acta  = optActa.get();
                        acta.setVerificador(ConstantesComunes.USER_MODELO);//temporal para que no sea atendido por otro
                        cabActaService.save(acta);


                        this.rabbitMqSender.sendProcessActaPrediction(predictionRequest);
                        if (logger.isDebugEnabled()) {
                            try {
                                logger.debug("[PREDICTION] JSON enviado a cola: {}", new ObjectMapper().writeValueAsString(predictionRequest));
                            } catch (Exception e) {
                                logger.debug("[PREDICTION] Request enviado a cola: {}", predictionRequest);
                            }
                        }
                        logger.info("[PREDICTION] Acta enviada a cola prediction actaId={}", actaId);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(respuesta);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(respuesta);
            }
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Ocurrió un error al guardar el acta.", null));
        }
    }

    private void sincronizarActa(Acta cabActa, String usuario, String proceso) {
        switch (cabActa.getEstadoActa()) {
            case ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION ->
                sincronizar(cabActa.getId(), TransmisionNacionEnum.SEGUNDA_VERI_TRANSMISION, usuario, proceso);
            case ConstantesEstadoActa.ESTADO_ACTA_PROCESADA ->
                sincronizar(cabActa.getId(), TransmisionNacionEnum.PROC_NORMAL_VERI_TRANSMISION, usuario, proceso);
            case ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO ->
                sincronizar(cabActa.getId(), TransmisionNacionEnum.PROC_OBS_VERI_TRANSMISION, usuario, proceso);
            case ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR ->
                sincronizar(cabActa.getId(), TransmisionNacionEnum.PROC_POR_CORREGIR_VERI_TRANSMISION, usuario, proceso);
            default -> sincronizar(cabActa.getId(), TransmisionNacionEnum.VERIFICACION_TRANSMISION, usuario, proceso);
        }
    }

    private void sincronizar(Long idActa, TransmisionNacionEnum transmisionNacionEnum, String usuario, String proceso) {
        try {
            this.actaTransmisionNacionStrategyService.sincronizar(idActa, proceso, transmisionNacionEnum, usuario);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
        }
    }

    private void sincronizarMesa(Long idMesa, TransmisionNacionEnum transmisionNacionEnum, String usuario, String proceso) {
        try {
            List<Acta> actaList = this.cabActaService.findByMesaId(idMesa);
            if(!actaList.isEmpty()){
                this.actaTransmisionNacionStrategyService.sincronizar(actaList.getFirst().getId(), proceso, transmisionNacionEnum, usuario);
            }
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
        }
    }



}
