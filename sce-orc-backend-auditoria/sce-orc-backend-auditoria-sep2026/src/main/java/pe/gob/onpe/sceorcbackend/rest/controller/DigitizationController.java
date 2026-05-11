package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.queue.ApprovedActa;
import pe.gob.onpe.sceorcbackend.model.dto.queue.NewActa;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveModelo;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationRejectMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.DigitizationListActasItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationSummaryResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion.DigtalDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.model.service.DigitalizacionService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/digitization")
public class DigitizationController {

  Logger logger = LoggerFactory.getLogger(DigitizationController.class);

  private final CabActaService cabActaService;
  
  private final CabActaCelesteService cabActaCelesteService;

  private final TokenUtilService tokenUtilService;

  private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;

  private final RabbitMqSender rabbitMqSender;

  private final ModeloService modeloService;

  private final DigitalizacionService digitalizacionService;

  public DigitizationController(
      CabActaService cabActaService,
      CabActaCelesteService cabActaCelesteService,
      TokenUtilService tokenUtilService,
      ActaTransmisionExecuteService actaTransmisionNacionStrategyService, 
      RabbitMqSender rabbitMqSender,
      ModeloService modeloService,
      DigitalizacionService digitalizacionService) {
    this.cabActaService = cabActaService;
    this.cabActaCelesteService = cabActaCelesteService;
    this.tokenUtilService = tokenUtilService;
    this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
    this.rabbitMqSender = rabbitMqSender;
    this.modeloService = modeloService;
    this.digitalizacionService = digitalizacionService;

  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @PostMapping("/uploadActaDigitization")
  public ResponseEntity<GenericResponse<DigtalDTO>> uploadDocumentDigitization(
          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
          @RequestParam("file") MultipartFile file,
          @RequestParam(value = "code") String codeBar) {

    TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
    logger.info("[DIGTAL-CTRL] Recibido uploadActaDigitization codeBar={}, usuario={}, fileSize={}", codeBar, tokenInfo.getNombreUsuario(), file.getSize());
    DigtalDTO digtalDTO = digitalizacionService.digitalizarActa(file, codeBar, tokenInfo);

    if (digtalDTO != null) {
      if (!digtalDTO.isRedigitalizado()) {
        enviarNewActa(NewActa.from(
                digtalDTO.getIdActa(),
                digtalDTO.getIdArchivo(),
                digtalDTO.getTipoArchivo(),
                tokenInfo
        ));

        enviarNewActa(NewActa.from(
                digtalDTO.getIdActa(),
                digtalDTO.getIdArchivoAis(),
                digtalDTO.getTipoArchivoAis(),
                tokenInfo
        ));
      } else {
        logger.info("[DIGTAL-CTRL] Acta redigitalizada, no se envía a cola. actaId={}", digtalDTO.getIdActa());
      }
    }

    return ResponseHelperException.createSuccessResponse(
            String.format("Se digitalizó correctamente el acta %s.", codeBar));
  }

  private void enviarNewActa(NewActa queueMessage) {
    if (queueMessage.getFileId() == null || queueMessage.getType() == null) {
      logger.warn("Archivo o tipoArchivo nulo para actaId={}, no se enviará a la cola",
              queueMessage.getActaId());
      return;
    }

    logger.info("Enviando a la cola validación {}", queueMessage);
    rabbitMqSender.sendNewActa(queueMessage);
  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @PostMapping("/uploadActaCeleste")
  public ResponseEntity<GenericResponse<DigtalDTO>> digitalizarActaCeleste(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "code") String codeBar) {

	  if (file == null || file.isEmpty()) {
	        throw new BadRequestException("Archivo requerido.");
	  }
	  
	  if (!StringUtils.hasText(codeBar)) {
	        throw new BadRequestException("El código de acta es requerido.");
	  }
	    
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      DigtalDTO dto = this.digitalizacionService.digitalizarActaCeleste(file, codeBar, tokenInfo);
      
      if (dto != null) {
    	  enviarNewActaCeleste(NewActa.from(dto.getIdActa(), dto.getIdArchivo(), dto.getTipoArchivo(), tokenInfo));
    	  enviarNewActaCeleste(NewActa.from(dto.getIdActa(), dto.getIdArchivoAis(), dto.getTipoArchivoAis(), tokenInfo));
      }
      
      return ResponseHelperException.createSuccessResponse(
    	String.format("Se digitalizó correctamente el acta celeste %s.", codeBar));
  }
  
  private void enviarNewActaCeleste(NewActa queueMessage) {
	    if (queueMessage.getFileId() == null || queueMessage.getType() == null) {
	      logger.warn("Archivo o tipoArchivo nulo para actaId={}, no se enviará a la cola",
	              queueMessage.getActaId());
	      return;
	    }

	    logger.info("Enviando a la cola validación {}", queueMessage);
	    rabbitMqSender.sendNewActaCeleste(queueMessage);
  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @PostMapping("/uploadListaElectores")
  @Transactional
  public ResponseEntity<GenericResponse<Mesa>> uploadDigitalizacionListaElectoresDigitization(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "mesa") String nroMesa) {

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    this.digitalizacionService.digitalizarListaElectores(file, nroMesa, tokenInfo);
    return ResponseHelperException.createSuccessResponse(String.format("Se digitalizó correctamente la lista de electores de la mesa %s.", nroMesa));
  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @PostMapping("/uploadMiembrosDeMesa")
  @Transactional
  public ResponseEntity<GenericResponse<Mesa>> uploadMiembrosDeMesaDigitization(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "mesa") String nroMesa) {

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    this.digitalizacionService.digitalizarMiembrosDeMesa(file, nroMesa, tokenInfo);
    return ResponseHelperException.createSuccessResponse(String.format("Se digitalizó correctamente la hoja de asistencia de la mesa %s.", nroMesa));
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @GetMapping("/summary")
  public ResponseEntity<DigitizationSummaryResponse> summary(@RequestParam(value = "codigoEleccion") String codigoEleccion) {
    return ResponseEntity.status(HttpStatus.OK).body(this.cabActaService.summary(codigoEleccion));
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @GetMapping("/summary/celeste")
  public ResponseEntity<DigitizationSummaryResponse> summaryCeleste(@RequestParam(value = "codigoEleccion") String codigoEleccion) {
    return ResponseEntity.status(HttpStatus.OK).body(this.cabActaCelesteService.summaryCeleste(codigoEleccion));
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @GetMapping("/listActas")
  public ResponseEntity<List<DigitizationListActasItem>> listActas(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("codigoEleccion") String codigoEleccion,
      @RequestParam(name = "status", required = false, defaultValue = "all") String status,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(name = "limit", required = false, defaultValue = "2") int limit) {

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    try{
      return ResponseEntity.status(HttpStatus.OK).body(this.cabActaService.listActas(codigoEleccion, tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), status, offset, limit));
    }catch (Exception e){
      logger.error("Error al listar actas para control ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
    }
  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @GetMapping("/listActasSceScanner")
  public ResponseEntity<List<ActaScanProjection>> listActasSceScanner(
          @RequestParam("codigoEleccion") String codigoEleccion,
          @RequestParam("estadoDigitalizacion") String estadoDigitalizacion) {

    return ResponseEntity.status(HttpStatus.OK).body(this.cabActaService.listActasSceScanner(codigoEleccion, estadoDigitalizacion));
  }

  @PreAuthorize(RoleAutority.SCE_SCANNER)
  @GetMapping("/listActasCelesteSceScanner")
  public ResponseEntity<List<ActaScanProjection>> listActasCelesteSceScanner(
          @RequestParam("codigoEleccion") String codigoEleccion,
          @RequestParam("estadoDigitalizacion") String estadoDigitalizacion) {

    return ResponseEntity.status(HttpStatus.OK).body(this.cabActaCelesteService.listActasCelesteSceScanner(codigoEleccion, estadoDigitalizacion));
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @GetMapping("/listActas/celeste")
  public ResponseEntity<List<DigitizationListActasItem>> listActasCeleste(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("codigoEleccion") String codigoEleccion,
      @RequestParam(name = "status", required = false, defaultValue = "all") String status,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(name = "limit", required = false, defaultValue = "2") int limit) {

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    try{
      return ResponseEntity.status(HttpStatus.OK).body(this.cabActaCelesteService.listActasCeleste(codigoEleccion, tokenInfo.getNombreUsuario(), status, offset, limit));
    }catch (Exception e){
      logger.error("Error al listar actas para control ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
    }
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/finalizar-atencion-control-digtal")
  public ResponseEntity<GenericResponse<String>> finalizarAtencionControlDigitalizacion(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("codigoEleccion") String codigoEleccion
  ) {
    GenericResponse<String> genericResponse = new GenericResponse<>();

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      genericResponse = this.cabActaService.finalizarAtencionControlDigitalizacion(codigoEleccion, tokenInfo);
      return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
    } catch (Exception e) {
      logger.error("Error al finalizar la atención", e);
      genericResponse.setMessage(e.getMessage());
      genericResponse.setData(null);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/approveMesa")
  public ResponseEntity<GenericResponse<Boolean>> approveMesa(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam(name = "electionId", required = false, defaultValue = "10") String electionId,
      @RequestBody DigitizationApproveMesaRequest request) {
    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

      this.cabActaService.approveMesa(request, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso(),
          tokenInfo.getCodigoCentroComputo());

      // Enviar a la cola de aprobación
      Optional<Acta> optionalActa = this.cabActaService.findById(request.getActaId());

      optionalActa.ifPresent(acta -> {
        sendQueueProcess(
                acta.getId(),
                acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId() : 0L,
                acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId() : 0L,
                tokenInfo.getNombreUsuario(),
                tokenInfo.getCodigoCentroComputo()
        );
        logger.info("Acta {} enviada a la cola de aprobación.", acta.getId());

      });

      sincronizarAprobado(request.getActaId(), tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());


      return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(true, "Acta aprobada correctamente."));
    } catch (BadRequestException e) {
      logger.error("Error de validación en approveMesa:", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResponse<>(false, e.getMessage()));
    }  catch (Exception e) {
      logger.error("Error en el servive approveMesa:",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
    }
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/approveMesaCeleste")
  public ResponseEntity<GenericResponse<Boolean>> approveMesaCeleste(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam(name = "electionId", required = false, defaultValue = "10") String electionId,
      @RequestBody DigitizationApproveMesaRequest request) {
    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

      this.cabActaCelesteService.approveMesa(request, tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso(),
          tokenInfo.getCodigoCentroComputo());
      
      return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(true, "Acta aprobada correctamente."));
    } catch (BadRequestException e) {
      logger.error("Error de validación en approveMesa:", e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResponse<>(false, e.getMessage()));
    }  catch (Exception e) {
      logger.error("Error en el servive approveMesa:",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
    }
  }

  @PostMapping("/approveMesaModelo")
  public ResponseEntity<GenericResponse<Boolean>> approveMesaModelo(@RequestBody DigitizationApproveModelo request) {
    try {

      logger.info("/approveMesaModelo {}",request);

      GenericResponse<Boolean> genericResponse =  this.modeloService.approveMesaModelo(request);

      if(genericResponse.isSuccess()) {
        //enviar a la cola de aprobación
        Optional<Acta> optionalActa = this.cabActaService.findById(request.getActaId());
        optionalActa.ifPresent(acta -> sendQueueProcess(acta.getId(),
                acta.getArchivoEscrutinio()!=null?acta.getArchivoEscrutinio().getId():0L,
                acta.getArchivoInstalacionSufragio()!=null?acta.getArchivoInstalacionSufragio().getId():0L,
                request.getUsuario(),
                request.getCodigoCc()));

        //sincronizar
        sincronizarAprobado(request.getActaId(), request.getUsuario(),request.getAbrevProceso());
      }

      return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
    } catch (Exception e) {
      logger.error("Error:",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
    }
  }


  private void sendQueueProcess(Long idActa, Long archivoAE, Long archivoAIS, String usuario, String codigoCentroComputo) {
    ApprovedActa queueMessage = new ApprovedActa();
    queueMessage.setActaId(idActa);
    queueMessage.setFileId1(archivoAE);
    queueMessage.setFileId2(archivoAIS);
    queueMessage.setCodUsuario(usuario);
    queueMessage.setCodCentroComputo(codigoCentroComputo);
    logger.info("Enviando process-acta {}",idActa);
    this.rabbitMqSender.sendProcessActa(queueMessage);
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/liberar-acta")
  public ResponseEntity<GenericResponse<String>> liberarActa(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("actaId") String actaId) {
    try {
      Long actaIdLo = Long.parseLong(actaId);
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse<String> genericResponse =
          this.cabActaService.liberarActaControlDigitalizacion(actaIdLo, tokenInfo.getNombreUsuario());
      return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
    }

  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/validar-control-digtal-acta")
  public ResponseEntity<GenericResponse<DigitizationListActasItem>> bloquearActa(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestBody DigitizationListActasItem digitizationListActasItem) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse<DigitizationListActasItem> genericResponse =
          this.cabActaService.bloquearActaControlDigitalizacion(digitizationListActasItem, tokenInfo.getNombreUsuario());
      return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(genericResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage()));
    }
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/rejectMesa")
  public ResponseEntity<Boolean> rejectMesa(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                            @RequestParam("codigoEleccion") String codigoEleccion, @RequestBody DigitizationRejectMesaRequest request) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      this.cabActaService.rejectActa(codigoEleccion, request, tokenInfo);

      sincronizarRechazoMesa(request.getActaId(), tokenInfo);

      return ResponseEntity.status(HttpStatus.OK).body(true);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }

  @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
  @PostMapping("/rejectMesaCeleste")
  public ResponseEntity<Boolean> rejectMesaCeleste(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                            @RequestParam("codigoEleccion") String codigoEleccion, @RequestBody DigitizationRejectMesaRequest request) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      this.cabActaCelesteService.rejectActa(codigoEleccion, request, tokenInfo);

      return ResponseEntity.status(HttpStatus.OK).body(true);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }


  private void sincronizarAprobado(Long actaId, String usuario, String proceso) {
    try {
      this.actaTransmisionNacionStrategyService.sincronizar(
          actaId,
          proceso,
          TransmisionNacionEnum.ACTA_APROBADA_TRANSMISION,
          usuario
      );
    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
    }
  }

  private void sincronizarRechazoMesa(Long actaId, TokenInfo tokenInfo) {
    try {
      this.actaTransmisionNacionStrategyService.sincronizar(
          actaId,
          tokenInfo.getAbrevProceso(),
          TransmisionNacionEnum.RECHAZAR_EN_DIGITALIZACION,
          tokenInfo.getNombreUsuario()
      );
    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
    }
  }



}
