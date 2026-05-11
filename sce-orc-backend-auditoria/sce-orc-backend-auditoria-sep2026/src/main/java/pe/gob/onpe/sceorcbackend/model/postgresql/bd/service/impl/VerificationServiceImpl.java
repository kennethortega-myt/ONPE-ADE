package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.ActaRandomClaimsDto;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.PositionAgrupolClaimsDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.AutorizacionCCRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.AutorizacionCCResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.dto.util.ResultRandom;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleTotalVote;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleVoteFooterItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleVoteItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaServiceGroup;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PadronElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificationService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationActaDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationDatetimeItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationDatetimeSectionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationDatetimeTotal;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationObservation;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationObservationSectionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationSignItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationSignSectionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVoteItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVotePreferenciaRowItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVotePreferencialItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVotePreferencialSectionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVoteRevocatoriaItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationVoteSectionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.exception.VerificationActaException;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ConstantesSecciones;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ModeloItem;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ModeloItemPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar.VotoContext;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar.VotoPreferencialContext;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.ConsultaErroresMateriales;
import pe.gob.onpe.sceorcbackend.utils.ProcessActaUtil;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;
import pe.gob.onpe.sceorcbackend.utils.TokenRandonUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class VerificationServiceImpl implements VerificationService {


  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schema;

  @Value("${orden.verificacion.actas}")
  private String propOrdenVerificacionActas;

  private static final Logger logger = LoggerFactory.getLogger(VerificationServiceImpl.class);

  private final ActaServiceGroup actaServiceGroup;

  private final ArchivoService archivoService;

  private final PadronElectoralService padronElectoralService;

  private final ITabLogService logService;

  private final UtilSceService utilSceService;

  private final ParametroService parametroService;

  private final AutorizacionCentroComputoService autorizacionCentroComputoService;

  private final StorageService storageService;

  public VerificationServiceImpl(
		  ActaServiceGroup actaServiceGroup, 
		  ArchivoService archivoService,
      PadronElectoralService padronElectoralService,
      ParametroService parametroService,
      ITabLogService logService,
      UtilSceService utilSceService,
          AutorizacionCentroComputoService autorizacionCentroComputoService,
          StorageService storageService) {
    this.actaServiceGroup = actaServiceGroup;
    this.archivoService = archivoService;
    this.padronElectoralService = padronElectoralService;
    this.parametroService = parametroService;
    this.logService = logService;
    this.utilSceService = utilSceService;
    this.autorizacionCentroComputoService = autorizacionCentroComputoService;
    this.storageService = storageService;

  }

  @Override
  @Transactional
  public GenericResponse<VerificationActaDTO> obtenerActaRandom(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException {


    VerificationActaDTO response = new VerificationActaDTO();

    logger.info("Buscando acta random para el usuario {}, eleccion {}.", tokenInfo.getNombreUsuario(), codigoEleccion);

    ResultRandom resultRandom = getActaRandomAleatoria(codigoEleccion, tokenInfo.getNombreUsuario());

    if (resultRandom.getIdActa() == null) {
      return new GenericResponse<>(false, "No existen registros o aún no están disponibles. Vuelva a intentarlo dentro de un momento.");
    }

    Long actaAlearotia = resultRandom.getIdActa();

    logger.info("Acta id {} obtenida, para el usuario {}, eleccion {}.", actaAlearotia, tokenInfo.getNombreUsuario(), codigoEleccion);

    boolean pasoUnaVez = resultRandom.isPasoUnaVez();

    Acta acta = obtenerActa(actaAlearotia);
    if (acta == null)
      return new GenericResponse<>(false, String.format("El acta aleatoria %d obtenida no existe.", actaAlearotia));

    String mensaje = "";

      if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA)) {

          acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA);
          acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);
          acta.setVerificador(tokenInfo.getNombreUsuario());

          mensaje = String.format(
                  "Se asignó para primera digitación el acta %s, al usuario %s",
                  SceUtils.getNumMesaAndCopia(acta),
                  tokenInfo.getNombreUsuario()
          );

      } else if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {

          acta.setVerificador2(tokenInfo.getNombreUsuario());

          mensaje = String.format(
                  "Se asignó para segunda digitación el acta %s, al usuario %s",
                  SceUtils.getNumMesaAndCopia(acta),
                  tokenInfo.getNombreUsuario()
          );
      }

    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    acta.setFechaModificacion(new Date());
    this.actaServiceGroup.getCabActaService().save(acta);

    setearZonasActaRandom(acta, codigoEleccion, response, tokenInfo.getNombreUsuario());

    Long idActa = 0L;

    if (pasoUnaVez) {
      if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {
        idActa = acta.getId();
        guardarAccionActa(acta,
                tokenInfo.getCodigoCentroComputo(),
                tokenInfo.getNombreUsuario(),
                ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_1ERA_VERI, 7);
      } else if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
        idActa = acta.getId();
        guardarAccionActa(acta,
                tokenInfo.getCodigoCentroComputo(),
                tokenInfo.getNombreUsuario(),
                ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_2DA_VERI, 9);
      }


    }

    if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA) || acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)){
      this.logService.registrarLog(
              tokenInfo.getNombreUsuario(),
              Thread.currentThread().getStackTrace()[1].getMethodName(), mensaje, tokenInfo.getCodigoCentroComputo(),ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
    }

    response.setToken(generarTokenActa(acta));
    response.setPropOrdenVerificacionActas(ConstantesComunes.COMPARAR_DIGITACION_CASO_1);//front siempre debe enviar 1

    return new GenericResponse<>(true, "Se obtuvo la información del acta random", response, List.of(idActa));
  }

  @Override
  @Transactional
  public GenericResponse<VerificationActaDTO> obtenerActaRandomConImagenes(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException {
    // Reutilizar toda la lógica existente de obtenerActaRandom
    GenericResponse<VerificationActaDTO> genericResponse = obtenerActaRandom(codigoEleccion, tokenInfo);

    if (!genericResponse.isSuccess() || genericResponse.getData() == null) {
      return genericResponse;
    }

    VerificationActaDTO response = genericResponse.getData();
    enrichSignSectionWithImages(response);

    return genericResponse;
  }

  /**
   * Enriquece TODAS las secciones del DTO con las imágenes en base64.
   * Estrategia eficiente: recolecta todos los fileId de todas las secciones,
   * 1 sola consulta batch a BD, conversión paralela a base64 con CompletableFuture,
   * y luego distribuye los resultados.
   */
  private void enrichSignSectionWithImages(VerificationActaDTO response) {
    try {
      // 1. Recolectar TODOS los fileId de todas las secciones
      List<Long> allFileIds = new ArrayList<>();
      collectSignSectionFileIds(response.getSignSection(), allFileIds);
      collectVoteSectionFileIds(response.getVoteSection(), allFileIds);
      collectObservationSectionFileIds(response.getObservationSection(), allFileIds);
      collectDatetimeSectionFileIds(response.getDateSectionResponse(), allFileIds);

      // Eliminar duplicados
      List<Long> uniqueFileIds = allFileIds.stream().distinct().collect(Collectors.toList());

      if (uniqueFileIds.isEmpty()) {
        return;
      }

      // 2. Una sola consulta batch a BD para obtener todos los Archivo
      List<Archivo> archivos = this.archivoService.findAllById(uniqueFileIds);

      // 3. Conversión paralela a base64 (lee de NFS, no de BD)
      Map<Long, String> base64Map = this.storageService.loadFilesToBase64Batch(archivos);

      // 4. Distribuir las imágenes en cada sección
      applySignSectionImages(response.getSignSection(), base64Map);
      applyVoteSectionImages(response.getVoteSection(), base64Map);
      applyObservationSectionImages(response.getObservationSection(), base64Map);
      applyDatetimeSectionImages(response.getDateSectionResponse(), base64Map);

    } catch (Exception e) {
      logger.error("Error al enriquecer secciones con imágenes: {}", e.getMessage());
    }
  }

  // ── Recolección de fileIds por sección ──

  private void collectSignSectionFileIds(VerificationSignSectionDTO section, List<Long> fileIds) {
    if (section == null) return;
    List<VerificationSignItem> items = Arrays.asList(
        section.getCountPresident(), section.getCountSecretary(), section.getCountThirdMember(),
        section.getInstallPresident(), section.getInstallSecretary(), section.getInstallThirdMember(),
        section.getVotePresident(), section.getVoteSecretary(), section.getVoteThirdMember()
    );
    for (VerificationSignItem item : items) {
      if (item != null && item.getFileId() != null) fileIds.add(item.getFileId());
    }
  }

  private void collectVoteSectionFileIds(VerificationVoteSectionDTO section, List<Long> fileIds) {
    if (section == null || section.getItems() == null) return;
    for (VerificationVoteItem voteItem : section.getItems()) {
      if (voteItem.getFileId() != null) fileIds.add(voteItem.getFileId());
      if (voteItem.getVotoPreferencial() != null) {
        for (VerificationVotePreferencialItem pref : voteItem.getVotoPreferencial()) {
          if (pref.getFileId() != null) fileIds.add(pref.getFileId());
        }
      }
    }
  }

  private void collectObservationSectionFileIds(VerificationObservationSectionDTO section, List<Long> fileIds) {
    if (section == null) return;
    if (section.getCount() != null && section.getCount().getFileId() != null) fileIds.add(section.getCount().getFileId());
    if (section.getInstall() != null && section.getInstall().getFileId() != null) fileIds.add(section.getInstall().getFileId());
    if (section.getVote() != null && section.getVote().getFileId() != null) fileIds.add(section.getVote().getFileId());
  }

  private void collectDatetimeSectionFileIds(VerificationDatetimeSectionDTO section, List<Long> fileIds) {
    if (section == null) return;
    if (section.getStart() != null && section.getStart().getFileId() != null) fileIds.add(section.getStart().getFileId());
    if (section.getEnd() != null && section.getEnd().getFileId() != null) fileIds.add(section.getEnd().getFileId());
    if (section.getTotal() != null) {
      VerificationDatetimeTotal total = section.getTotal();
      if (total.getFileId() != null) fileIds.add(total.getFileId());
      if (total.getFileIdNumber() != null) fileIds.add(total.getFileIdNumber());
      if (total.getFileIdNumberEscrutinio() != null) fileIds.add(total.getFileIdNumberEscrutinio());
    }
  }

  // ── Aplicación de imágenes base64 por sección ──

  private void applySignSectionImages(VerificationSignSectionDTO section, Map<Long, String> base64Map) {
    if (section == null) return;
    List<VerificationSignItem> items = Arrays.asList(
        section.getCountPresident(), section.getCountSecretary(), section.getCountThirdMember(),
        section.getInstallPresident(), section.getInstallSecretary(), section.getInstallThirdMember(),
        section.getVotePresident(), section.getVoteSecretary(), section.getVoteThirdMember()
    );
    for (VerificationSignItem item : items) {
      if (item != null && item.getFileId() != null) {
        item.setFilePngUrl(base64Map.get(item.getFileId()));
      }
    }
  }

  private void applyVoteSectionImages(VerificationVoteSectionDTO section, Map<Long, String> base64Map) {
    if (section == null || section.getItems() == null) return;
    for (VerificationVoteItem voteItem : section.getItems()) {
      if (voteItem.getFileId() != null) {
        voteItem.setFilePngUrl(base64Map.get(voteItem.getFileId()));
      }
      if (voteItem.getVotoPreferencial() != null) {
        for (VerificationVotePreferencialItem pref : voteItem.getVotoPreferencial()) {
          if (pref.getFileId() != null) {
            pref.setFilePngUrl(base64Map.get(pref.getFileId()));
          }
        }
      }
    }
  }

  private void applyObservationSectionImages(VerificationObservationSectionDTO section, Map<Long, String> base64Map) {
    if (section == null) return;
    if (section.getCount() != null && section.getCount().getFileId() != null) {
      section.getCount().setFilePngUrl(base64Map.get(section.getCount().getFileId()));
    }
    if (section.getInstall() != null && section.getInstall().getFileId() != null) {
      section.getInstall().setFilePngUrl(base64Map.get(section.getInstall().getFileId()));
    }
    if (section.getVote() != null && section.getVote().getFileId() != null) {
      section.getVote().setFilePngUrl(base64Map.get(section.getVote().getFileId()));
    }
  }

  private void applyDatetimeSectionImages(VerificationDatetimeSectionDTO section, Map<Long, String> base64Map) {
    if (section == null) return;
    if (section.getStart() != null && section.getStart().getFileId() != null) {
      section.getStart().setFilePngUrl(base64Map.get(section.getStart().getFileId()));
    }
    if (section.getEnd() != null && section.getEnd().getFileId() != null) {
      section.getEnd().setFilePngUrl(base64Map.get(section.getEnd().getFileId()));
    }
    if (section.getTotal() != null) {
      VerificationDatetimeTotal total = section.getTotal();
      if (total.getFileId() != null) {
        total.setFilePngTextoUrl(base64Map.get(total.getFileId()));
      }
      if (total.getFileIdNumber() != null) {
        total.setFilePngNumberUrl(base64Map.get(total.getFileIdNumber()));
      }
      if (total.getFileIdNumberEscrutinio() != null) {
        total.setFilePngNumberEscrutinioUrl(base64Map.get(total.getFileIdNumberEscrutinio()));
      }
    }
  }

  private void guardarAccionActa(Acta cabActa, String codigoCentroComputo ,String usuario, String tiempo, String accion, int orden) {
    Optional<Acta> optionalCabActa = this.actaServiceGroup.getCabActaService().findById(cabActa.getId());
    if (optionalCabActa.isPresent()) {
      DetActaAccion detActaAccion = DetActaAccion.builder()
              .fechaAccion(optionalCabActa.get().getFechaModificacion())
              .usuarioAccion(usuario)
              .codigoCentroComputo(codigoCentroComputo)
              .tiempo(tiempo)
              .activo(1)
              .accion(accion)
              .usuarioCreacion(usuario)
              .acta(cabActa)
              .orden(orden)
              .build();
      this.actaServiceGroup.getDetActaAccionService().save(detActaAccion);
    }
  }

  private void setearZonasActaRandom(Acta acta, String codigoEleccion, VerificationActaDTO response, String usuario)
      throws VerificationActaException {

    boolean esConvencional = Objects.equals(acta.getMesa().getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL);
    boolean esSTAE = Objects.equals(acta.getMesa().getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE);

    if (esConvencional || (esSTAE && (acta.getTipoTransmision() == null || !acta.getTipoTransmision()
        .equals(ConstantesComunes.TIPO_HOJA_STAE_TRANSMITIDA)))) {
      setZonasConvencional(response, codigoEleccion, acta, usuario);
    } else if (esSTAE) {
      setZonasSTAE(response, codigoEleccion, acta);
    }

  }

  private void setZonasConvencional(VerificationActaDTO response, String codigoEleccion, Acta acta, String usuario) throws VerificationActaException {

    List<DetActaRectangleDTO> detActaRectangles = this.actaServiceGroup.getDetActaRectangleService().findByActaId(acta.getId());
    if(detActaRectangles.isEmpty())
      throw new VerificationActaException(String.format("No existen registro de cortes para el acta %d.", acta.getId()));


      // Consultar autorización para ver nombres de agrupaciones
    AutorizacionCCRequestDto autorizacionRequest = new AutorizacionCCRequestDto(
              usuario,
              ConstantesComunes.TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA,  // "VAP"
              ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA,         // "AC"
              acta.getId()
    );
    AutorizacionCCResponseDto autorizacion = autorizacionCentroComputoService.recibirAutorizacion(autorizacionRequest);
    boolean autorizado = autorizacion.isAutorizado();

    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)){
      getSectionVotePreferencial(acta, this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId()), response, detActaRectangles, autorizado);
    }else{
      response.setVotePreferencialSection(null);
    }

    getSectionVoteConvencional(acta, codigoEleccion, response, detActaRectangles, autorizado);

    if (response.getVotePreferencialSection() != null && ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
      asignarVotoPreferencialAVotos(response, autorizado);
    }

    response.setVotePreferencialSection(null);

    boolean isAleatorio = false;
    ParametroDto parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_ORDEN_ALEATORIO_AGRUPOL);
    if(parametroDto !=null && parametroDto.getValor()!=null && parametroDto.getValor().toString().equals(ConstantesComunes.TEXT_TRUE)){
          isAleatorio = true;
    }


    if (isAleatorio && !autorizado) {
      List<VerificationVoteItem> agrupol = response.getVoteSection().getItems();


      List<VerificationVoteItem> agrupolOnly = new ArrayList<>(agrupol.stream().filter(e ->
                      e.getPosition() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue() &&
                      e.getPosition() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue() &&
                      e.getPosition() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()).toList());

      Collections.shuffle(agrupolOnly);//chocolateo de agrupaciones politicas

      List<VerificationVoteItem> agrupolFooterBNI = new ArrayList<>(agrupol.stream().filter(e ->
                      e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue() ||
                      e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue() ||
                      e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()).toList());

      Collections.shuffle(agrupolFooterBNI);//chocolateo de footer

      agrupolOnly.addAll(agrupolFooterBNI);

      response.getVoteSection().setItems(agrupolOnly);
    }


    getSectionSign(acta, response, detActaRectangles);

    getSectionObservation(acta, response, detActaRectangles);

    getSectionTimeInicioFinEscrutinio(acta, response, detActaRectangles);

    getSectionTotalCVAS(acta, response, detActaRectangles);

    response.setEstadoActa(acta.getEstadoActa());
    response.setSolucionTecnologica(ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_CONVENCIONAL);
  }

  private void setearZonasActaProcesamientoManual(Acta acta, String codigoEleccion, VerificationActaDTO response)
            throws VerificationActaException {


      if( ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)){
          getSectionVotePreferencialProcesamientoManual(acta, response);
      }else{
          response.setVotePreferencialSection(null);
      }

      getSectionVoteProcesamientoManual(acta, codigoEleccion, response);

      if (response.getVotePreferencialSection() != null &&
              ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
          asignarVotoPreferencialAVotos(response, false);
      }

      response.setVotePreferencialSection(null);

      getSectionSignProcesamientoManual(response);

      getSectionObservationProcesamientoManual(response);

      getSectionTimeProcesamientoManual(acta, response);

      getSectionTotalCVASProcesamientoManual(acta, response);

      response.setEstadoActa(acta.getEstadoActa());
      response.setSolucionTecnologica(ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_CONVENCIONAL);
  }

    private void getSectionTimeProcesamientoManual(Acta acta, VerificationActaDTO response)
            throws VerificationActaException {

        try {
            VerificationDatetimeSectionDTO sectionResponse = new VerificationDatetimeSectionDTO();

            VerificationDatetimeItem verificationDatetimeItemIniTime = new VerificationDatetimeItem();
            verificationDatetimeItemIniTime.setFileId(null);
            verificationDatetimeItemIniTime.setSystemValue(null);

            VerificationDatetimeItem verificationDatetimeItemFinTime = new VerificationDatetimeItem();
            verificationDatetimeItemFinTime.setFileId(null);
            verificationDatetimeItemFinTime.setSystemValue(null);

            // Asignar userValue si es segunda verificación
            asignarValoresTiempo(acta, verificationDatetimeItemIniTime, verificationDatetimeItemFinTime);

            sectionResponse.setStart(verificationDatetimeItemIniTime);
            sectionResponse.setEnd(verificationDatetimeItemFinTime);

            response.setDateSectionResponse(sectionResponse);

        } catch (Exception e) {
            throw new VerificationActaException(
                    "Error al obtener sección de fecha/hora para procesamiento manual: " + e.getMessage());
        }
    }

    private void getSectionTotalCVASProcesamientoManual(Acta acta, VerificationActaDTO response)
            throws VerificationActaException {

        try {
            VerificationDatetimeTotal verificationDatetimeTotal = new VerificationDatetimeTotal();

            // Sin datos del modelo
            verificationDatetimeTotal.setFileId(null);
            verificationDatetimeTotal.setTextSystemValue(null);
            verificationDatetimeTotal.setFileIdNumber(null);
            verificationDatetimeTotal.setNumberSystemValue(null);
            verificationDatetimeTotal.setFileIdNumberEscrutinio(null);
            verificationDatetimeTotal.setTextUserValue(null);
            verificationDatetimeTotal.setNumberUserValue(null);

            if (esTransmisionStaeTransmitida(acta)) {
                verificationDatetimeTotal.setNumberUserValue(acta.getCvas() == null ? ConstantesComunes.VALUE_CVAS_INCOMPLETA : acta.getCvas().toString());
            }

            response.getDateSectionResponse().setTotal(verificationDatetimeTotal);

        } catch (Exception e) {
            throw new VerificationActaException(
                    "Error al obtener total CVAS para procesamiento manual: " + e.getMessage());
        }
    }

    private void getSectionObservationProcesamientoManual(VerificationActaDTO response)
            throws VerificationActaException {

        try {
            VerificationObservationSectionDTO sectionResponse = new VerificationObservationSectionDTO();

            // Crear observaciones vacías
            VerificationObservation signAE = new VerificationObservation();
            signAE.setFileId(null);
            signAE.setSystemValue(null);

            VerificationObservation signAI = new VerificationObservation();
            signAI.setFileId(null);
            signAI.setSystemValue(null);

            VerificationObservation signAS = new VerificationObservation();
            signAS.setFileId(null);
            signAS.setSystemValue(null);

            sectionResponse.setCount(signAE);
            sectionResponse.setInstall(signAI);
            sectionResponse.setVote(signAS);
            sectionResponse.setNullityRequest(Boolean.FALSE);
            sectionResponse.setNoData(Boolean.FALSE);
            sectionResponse.setStatus(null);

            response.setObservationSection(sectionResponse);

        } catch (Exception e) {
            throw new VerificationActaException(
                    "Error al obtener sección de observaciones para procesamiento manual: " + e.getMessage());
        }
    }

    private void getSectionVotePreferencialProcesamientoManual(Acta acta, VerificationActaDTO response)
            throws VerificationActaException {
      try{
          int cantidadColumnas = this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId());

          List<VerificationVotePreferenciaRowItem> votos = new ArrayList<>();

          List<DetUbigeoEleccionAgrupacionPolitica> detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado =
                  getDetUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado(acta);

          for (DetUbigeoEleccionAgrupacionPolitica de : detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado) {
              votos.add(buildRowItemPreferencialManual(acta, de, cantidadColumnas));
          }

          VerificationVotePreferencialSectionDTO voteResponse = new VerificationVotePreferencialSectionDTO();
          voteResponse.setCantidadEscanios(cantidadColumnas);
          voteResponse.setToken(response.getToken());
          voteResponse.setItems(votos);
          response.setVotePreferencialSection(voteResponse);

      }catch(Exception e){
          throw new VerificationActaException(
                  "Error al obtener sección de votos preferenciales para procesamiento manual: " + e.getMessage());
      }
    }

    private VerificationVotePreferenciaRowItem buildRowItemPreferencialManual(
            Acta acta, DetUbigeoEleccionAgrupacionPolitica de, int cantidadColumnas) {

        VerificationVotePreferenciaRowItem rowItem = new VerificationVotePreferenciaRowItem();
        try {
            if (Objects.equals(de.getEstado(), ConstantesComunes.N_ACHURADO)) {
                rowItem.setPosition(de.getPosicion());
                rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
                rowItem.setItems(createVotosPreferencialesAchuradosItems(cantidadColumnas));

            } else if (Objects.equals(de.getEstado(), ConstantesComunes.N_PARTICIPA)) {
                List<VerificationVotePreferencialItem> items =
                        createVotosPreferencialesManualItems(acta, de.getPosicion(), cantidadColumnas);
                rowItem.setPosition(de.getPosicion());
                rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
                rowItem.setItems(items);
            }
        } catch (Exception e) {
            setItemsPreferencialesNoReconocidosProcesamientoManual(acta, rowItem, cantidadColumnas, de.getPosicion());
        }
        return rowItem;
    }

    private List<VerificationVotePreferencialItem> createVotosPreferencialesManualItems(
            Acta acta, Integer posicion, int cantidadColumnas) {

        List<VerificationVotePreferencialItem> items = new ArrayList<>();

        // Buscar si ya existe información guardada (para segunda verificación)
        Optional<DetActa> optionalDetActa = this.actaServiceGroup.getDetActaService()
                .getDetActa(acta.getId(), posicion.longValue());

        if (optionalDetActa.isPresent()) {
            // Crear items para cada escaño/curul
            for (int i = 1; i <= cantidadColumnas; i++) {
                VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
                item.setPosition(i);
                item.setFileId(null);
                item.setSystemValue(null);
                item.setUserValue(null);
                items.add(item);
            }
        } else {
            for (int i = 1; i <= cantidadColumnas; i++) {
                VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
                item.setPosition(i);
                item.setFileId(null);
                item.setSystemValue(null);
                item.setUserValue(null);
                items.add(item);
            }
        }

        return items;
    }

    private void setItemsPreferencialesNoReconocidosProcesamientoManual(
            Acta acta, VerificationVotePreferenciaRowItem rowItem,
            Integer cantidadColumnas, Integer nPosicion) {

        List<VerificationVotePreferencialItem> itemsNoReconocidos = new ArrayList<>();

        for (int i = 0; i < cantidadColumnas; i++) {
            VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
            item.setFileId(null);
            item.setPosition(i + 1);
            item.setSystemValue(null);
            item.setUserValue(null);
            itemsNoReconocidos.add(item);
        }

        rowItem.setPosition(nPosicion);
        rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, nPosicion));
        rowItem.setItems(itemsNoReconocidos);
    }

    private void getSectionSignProcesamientoManual(VerificationActaDTO response)
            throws VerificationActaException {

        try {
            VerificationSignSectionDTO sectionResponse = new VerificationSignSectionDTO();

            // Escrutinio
            sectionResponse.setCountPresident(createEmptyVerificationSignItem());
            sectionResponse.setCountSecretary(createEmptyVerificationSignItem());
            sectionResponse.setCountThirdMember(createEmptyVerificationSignItem());

            // Instalación
            sectionResponse.setInstallPresident(createEmptyVerificationSignItem());
            sectionResponse.setInstallSecretary(createEmptyVerificationSignItem());
            sectionResponse.setInstallThirdMember(createEmptyVerificationSignItem());

            // Sufragio
            sectionResponse.setVotePresident(createEmptyVerificationSignItem());
            sectionResponse.setVoteSecretary(createEmptyVerificationSignItem());
            sectionResponse.setVoteThirdMember(createEmptyVerificationSignItem());

            sectionResponse.setSystemStatus(null);
            sectionResponse.setStatus(null);

            response.setSignSection(sectionResponse);

        } catch (Exception e) {
            throw new VerificationActaException(
                    "Error al obtener sección de firmas para procesamiento manual: " + e.getMessage());
        }
    }

    private VerificationSignItem createEmptyVerificationSignItem() {
        VerificationSignItem item = new VerificationSignItem();
        item.setFileId(null);
        item.setSystemStatus(null);
        item.setUserStatus(null);
        return item;
    }

    private void getSectionVoteProcesamientoManual(Acta acta, String codigoEleccion, VerificationActaDTO response) {
        List<VerificationVoteItem> votos = new ArrayList<>();

        // Obtener agrupaciones políticas de la BD (sin Blancos, Nulos, Impugnados)
        List<DetUbigeoEleccionAgrupacionPolitica> agrupolOrdenadosSbni = agrupolOrdenadosSbni(acta);

        // Crear items de votos sin datos del modelo
        for (DetUbigeoEleccionAgrupacionPolitica agrupacion : agrupolOrdenadosSbni) {
            VerificationVoteItem item = new VerificationVoteItem();
            item.setPosition(agrupacion.getPosicion());
            item.setPositionToken(generarTokenPositionAgrupol(acta, agrupacion.getPosicion()));
            item.setEstado(agrupacion.getEstado());

            if (!Objects.equals(agrupacion.getEstado(), ConstantesComunes.N_ACHURADO)) {
                item.setNombreAgrupacionPolitica(agrupacion.getAgrupacionPolitica().getDescripcion());
            } else {
                setItemValuesToVerificationVoteItemConv(item, null, null, null);
            }

            votos.add(item);
        }

        // Agregar Blancos, Nulos, Impugnados
        if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            procesarVotosEspecialesProcesamientoManual(acta, votos);
        }

        VerificationVoteSectionDTO voteResponse = new VerificationVoteSectionDTO();
        voteResponse.setCantidadVotosPreferenciales(
                response.getVotePreferencialSection() != null ?
                        response.getVotePreferencialSection().getCantidadEscanios() : 0);
        voteResponse.setToken(response.getToken());
        voteResponse.setItems(votos);
        response.setVoteSection(voteResponse);
    }

    private void procesarVotosEspecialesProcesamientoManual(Acta acta, List<VerificationVoteItem> votos) {
        // Agregar Blancos, Nulos, Impugnados sin datos del modelo
        agregarVotoEspecialManual(acta, votos,
                ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue(),
                ConstantesComunes.DESC_AGRUPOL_VOTOS_BLANCOS);

        agregarVotoEspecialManual(acta, votos,
                ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue(),
                ConstantesComunes.DESC_AGRUPOL_VOTOS_NULOS);

        agregarVotoEspecialManual(acta, votos,
                ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue(),
                ConstantesComunes.DESC_AGRUPOL_VOTOS_IMPUGNADOS);
    }

    private void agregarVotoEspecialManual(Acta acta, List<VerificationVoteItem> votos, int position, String nombreAgrupacion) {
        VerificationVoteItem item = new VerificationVoteItem();
        item.setPosition(position);
        item.setPositionToken(generarTokenPositionAgrupol(acta, position));
        item.setFileId(null);
        item.setSystemValue(null);
        item.setNombreAgrupacionPolitica(nombreAgrupacion);
        item.setEstado(null);
        votos.add(item);
    }



  private void getSectionVoteConvencional(Acta acta, String codigoEleccion, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangles, boolean autorizado) {
    DetActaRectangleDTO archivoRect = findByAbreviatura(detActaRectangles, ConstantesSecciones.SECTION_ABREV_VOTE);
    if (archivoRect == null || archivoRect.getValues() == null) return;

    List<VerificationVoteItem> votos = new ArrayList<>();
    List<DetUbigeoEleccionAgrupacionPolitica> agrupolOrdenadosSbni = agrupolOrdenadosSbni(acta);
    ModeloItem[] votosModelo = getVotosModeloItem(archivoRect, agrupolOrdenadosSbni);

    for (int i = 0; i < agrupolOrdenadosSbni.size(); i++) {
      votos.add(crearItemVoto(acta, agrupolOrdenadosSbni.get(i), votosModelo, i, codigoEleccion, autorizado));
    }

    if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      procesarVotosEspeciales(acta, archivoRect, votos, autorizado);
    }

    VerificationVoteSectionDTO voteResponse = new VerificationVoteSectionDTO();
    voteResponse.setCantidadVotosPreferenciales(
        response.getVotePreferencialSection() != null ?
            response.getVotePreferencialSection().getCantidadEscanios() : 0);
    voteResponse.setToken(response.getToken());
    voteResponse.setItems(votos);
    voteResponse.setAutorizado(autorizado);

    response.setVoteSection(voteResponse);
  }

  private VerificationVoteItem crearItemVoto(Acta acta, DetUbigeoEleccionAgrupacionPolitica agrupacion,
                                             ModeloItem[] votosModelo, int indice, String codigoEleccion,
                                             boolean autorizado) {
    VerificationVoteItem item = new VerificationVoteItem();
    try {
      item.setPosition(agrupacion.getPosicion());
      item.setPositionToken(generarTokenPositionAgrupol(acta, agrupacion.getPosicion()));

      if(autorizado && agrupacion.getAgrupacionPolitica() != null){
          item.setNombreAgrupacionPolitica(agrupacion.getAgrupacionPolitica().getDescripcion());
      }

      if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
        if (Objects.equals(agrupacion.getEstado(), ConstantesComunes.N_ACHURADO)) {
          setItemValuesToVerificationVoteItemRevocatoriaAchurado(item);
        } else if (Objects.equals(agrupacion.getEstado(), ConstantesComunes.N_PARTICIPA)) {
          setItemValuesToVerificationVoteItemRevocatoriaParticipa(item, votosModelo[indice]);
        }
      } else {
        if (Objects.equals(agrupacion.getEstado(), ConstantesComunes.N_ACHURADO)) {
          setItemValuesToVerificationVoteItemConv(item, null, null, null);
        } else if (Objects.equals(agrupacion.getEstado(), ConstantesComunes.N_PARTICIPA)) {
          setItemValuesToVerificationVoteItemConv(item, votosModelo[indice].getIdArchivo1(), votosModelo[indice].getVotos1(), null);
        }
      }
    } catch (Exception e) {
      logger.error("No se encontró el item {} en el modelo.", item);
      setItemValuesToVerificationVoteItemConv(item, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.C_VALUE_ILEGIBLE);
    }
    return item;
  }

  private void procesarVotosEspeciales(Acta acta, DetActaRectangleDTO archivoRect, List<VerificationVoteItem> votos, boolean autorizado) {
    sectionVotosAgregarFooterV2(acta, archivoRect.getValues().getFooter(), votos, autorizado);
    sectionVotosAgregaFooterAchurados(acta, votos);
  }


  private void setItemValuesToVerificationVoteItemConv(VerificationVoteItem item, Long fileId, String systemValue, String userValue) {
    item.setFileId(fileId);
    item.setSystemValue(SceUtils.limpiarVotos(systemValue));
    item.setUserValue(userValue);
  }

  private void setItemValuesToVerificationVoteItemRevocatoriaAchurado(VerificationVoteItem item) {
    List<VerificationVoteRevocatoriaItem> votoRevocatoria =
            IntStream.rangeClosed(1, 6)
                    .mapToObj(i -> {
                      VerificationVoteRevocatoriaItem item1 = new VerificationVoteRevocatoriaItem();
                      item1.setPosition(i);
                      item1.setFileId(null);
                      item1.setSystemValue(ConstantesComunes.CVALUE_NULL);
                      item1.setUserValue(ConstantesComunes.CVALUE_NULL);
                      return item1;
                    })
                    .toList();

    item.setVotoRevocatoria(votoRevocatoria);
  }

  private void setItemValuesToVerificationVoteItemRevocatoriaParticipa(VerificationVoteItem verificationVoteItem, ModeloItem modeloItem) {
    List<VerificationVoteRevocatoriaItem> votoRevocatoria = new ArrayList<>();

    for (int i = ConstantesComunes.N_POSICION_CPR_VOTOS_SI; i <= ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS; i++) {
      VerificationVoteRevocatoriaItem item = createRevocatoriaItem(i, modeloItem);
      votoRevocatoria.add(item);
    }

    verificationVoteItem.setVotoRevocatoria(votoRevocatoria);
  }

  private VerificationVoteRevocatoriaItem createRevocatoriaItem(int position, ModeloItem modeloItem) {
    VerificationVoteRevocatoriaItem item = new VerificationVoteRevocatoriaItem();

    Map<Integer, Integer> positionMap = new HashMap<>();
    positionMap.put(ConstantesComunes.N_POSICION_CPR_VOTOS_BLANCOS, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue());
    positionMap.put(ConstantesComunes.N_POSICION_CPR_VOTOS_NULOS, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue());
    positionMap.put(ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue());

    item.setPosition(positionMap.getOrDefault(position, position));

    String votos = getVotosByPosition(position, modeloItem);
    Long fileId = getFileIdByPosition(position, modeloItem);

    item.setFileId(fileId);
    item.setSystemValue(SceUtils.limpiarVotos(votos));
    item.setUserValue(null);

    return item;
  }

  private String getVotosByPosition(int position, ModeloItem modeloItem) {
    if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_SI) {
      return modeloItem.getVotos1();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_NO) {
      return modeloItem.getVotos2();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_BLANCOS) {
      return modeloItem.getVotos3();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_NULOS) {
      return modeloItem.getVotos4();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS) {
      return modeloItem.getVotos5();
    } else {
      return StringUtils.EMPTY;
    }
  }

  private Long getFileIdByPosition(int position, ModeloItem modeloItem) {
    if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_SI) {
      return modeloItem.getIdArchivo1();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_NO) {
      return modeloItem.getIdArchivo2();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_BLANCOS) {
      return modeloItem.getIdArchivo3();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_NULOS) {
      return modeloItem.getIdArchivo4();
    } else if (position == ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS) {
      return modeloItem.getIdArchivo5();
    } else {
      return 0L;
    }
  }


  private void sectionVotosAgregarFooterV2(Acta acta, List<DetActaRectangleVoteFooterItem> footer, List<VerificationVoteItem> votos, boolean autorizado) {
    if (footer.isEmpty()) return;

    agregarVotoFooter(acta, footer, votos, 0, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue(),
            ConstantesComunes.DESC_AGRUPOL_VOTOS_BLANCOS, autorizado);  // Voto Blanco
    agregarVotoFooter(acta, footer, votos, 1, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue(),
            ConstantesComunes.DESC_AGRUPOL_VOTOS_NULOS, autorizado);   // Voto Nulo
    agregarVotoFooter(acta, footer, votos, 2, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue(),
            ConstantesComunes.DESC_AGRUPOL_VOTOS_IMPUGNADOS, autorizado); // Voto Impugnado
  }

  private void agregarVotoFooter(Acta acta, List<DetActaRectangleVoteFooterItem> footer, List<VerificationVoteItem> votos, int index, int position,
                                 String nombreAgrupacion, boolean autorizado) {
    if (index >= footer.size()) return;

    DetActaRectangleVoteFooterItem footerItem = footer.get(index);
    if (footerItem == null) return;

    VerificationVoteItem itemVoto = new VerificationVoteItem();
    itemVoto.setFileId(footerItem.getTotalVotos0().getArchivo());
    itemVoto.setPosition(position);
    itemVoto.setPositionToken(generarTokenPositionAgrupol(acta, position));
    itemVoto.setSystemValue( SceUtils.limpiarVotos(footerItem.getTotalVotos0().getPrediccion()) );

    if (ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION.equals(acta.getEstadoActa())) {
      itemVoto.setUserValue(null);
    }

    if(autorizado){
        itemVoto.setNombreAgrupacionPolitica(nombreAgrupacion);
    }

    votos.add(itemVoto);
  }


  private void sectionVotosAgregaFooterAchurados(Acta acta, List<VerificationVoteItem> votos) {

    List<VerificationVoteItem> lista1 = votos.stream().filter(e -> e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue()).toList();
    if (lista1.isEmpty()) {
      VerificationVoteItem item = new VerificationVoteItem();
      item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue());
      item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue()));
      item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
      item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
      item.setNombreAgrupacionPolitica(ConstantesComunes.DESC_AGRUPOL_VOTOS_BLANCOS);

      votos.add(item);
    }

    List<VerificationVoteItem> lista2 = votos.stream().filter(e -> e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue()).toList();
    if (lista2.isEmpty()) {
      VerificationVoteItem item = new VerificationVoteItem();
      item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue());
      item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue()));
      item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
      item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
      item.setNombreAgrupacionPolitica(ConstantesComunes.DESC_AGRUPOL_VOTOS_NULOS);

      votos.add(item);
    }

    List<VerificationVoteItem> lista3 = votos.stream().filter(e -> e.getPosition() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()).toList();
    if (lista3.isEmpty()) {
      VerificationVoteItem item = new VerificationVoteItem();
      item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue());
      item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()));
      item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
      item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
      item.setNombreAgrupacionPolitica(ConstantesComunes.DESC_AGRUPOL_VOTOS_IMPUGNADOS);

      votos.add(item);
    }

  }

  private ModeloItem[] getVotosModeloItem(DetActaRectangleDTO archivoRect, List<DetUbigeoEleccionAgrupacionPolitica> agrupolOrdenados) {
    List<DetActaRectangleVoteItem> detActaRectangleVoteItems = archivoRect.getValues().getBody();

    ModeloItem[] votosModelo = crearModeloItemsIlegibles(agrupolOrdenados.size());

    for (DetActaRectangleVoteItem item : detActaRectangleVoteItems) {
      Integer nro = item.getNro();
      if (nro == null || nro < 1 || nro > votosModelo.length) continue;

      ModeloItem modeloItem = new ModeloItem();

      for (int i = 0; i < 6; i++) {
        DetActaRectangleTotalVote voto = obtenerTotalVotos(item, i);
        if (voto != null) {
          switch (i) {
            case 0: modeloItem.setVotos1(voto.getPrediccion()); modeloItem.setIdArchivo1(voto.getArchivo()); break;
            case 1: modeloItem.setVotos2(voto.getPrediccion()); modeloItem.setIdArchivo2(voto.getArchivo()); break;
            case 2: modeloItem.setVotos3(voto.getPrediccion()); modeloItem.setIdArchivo3(voto.getArchivo()); break;
            case 3: modeloItem.setVotos4(voto.getPrediccion()); modeloItem.setIdArchivo4(voto.getArchivo()); break;
            case 4: modeloItem.setVotos5(voto.getPrediccion()); modeloItem.setIdArchivo5(voto.getArchivo()); break;
            case 5: modeloItem.setVotos6(voto.getPrediccion()); modeloItem.setIdArchivo6(voto.getArchivo()); break;
            default:
              // Opción segura: no hacer nada o loggear el error si es inesperado
              break;
          }
        }
      }

      if (item.getNro() <= (detActaRectangleVoteItems.size())) {
        votosModelo[item.getNro() - 1] = modeloItem;
      }
    }

    return votosModelo;
  }

  private ModeloItem[] crearModeloItemsIlegibles(int cantidad) {
    ModeloItem[] items = new ModeloItem[cantidad];

    for (int i = 0; i < cantidad; i++) {
      items[i] = new ModeloItem(
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE,
          ConstantesComunes.C_VALUE_ILEGIBLE, ConstantesComunes.N_ARCHIVO_ILEGIBLE
      );
    }

    return items;
  }

  private DetActaRectangleTotalVote obtenerTotalVotos(DetActaRectangleVoteItem item, int index) {
    switch (index) {
      case 0: return item.getTotalVotos0();
      case 1: return item.getTotalVotos1();
      case 2: return item.getTotalVotos2();
      case 3: return item.getTotalVotos3();
      case 4: return item.getTotalVotos4();
      case 5: return item.getTotalVotos5();
      default: return null;
    }
  }

  private void setZonasSTAE(VerificationActaDTO response, String codigoEleccion, Acta acta) throws VerificationActaException {

    List<DetActaRectangleDTO> detActaRectangles = this.actaServiceGroup.getDetActaRectangleService().findByActaId(acta.getId());
    if(detActaRectangles.isEmpty())
      throw new VerificationActaException(String.format("No existen registro de cortes para el acta %d.", acta.getId()));



    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)){
      getSectionVotePreferencialSTAE(acta, this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId()), response, detActaRectangles);
    }else{
      response.setVotePreferencialSection(null);
    }

    getSectionVoteSTAE(acta,codigoEleccion, response, detActaRectangles);


    if (response.getVotePreferencialSection() != null && ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
      asignarVotoPreferencialAVotos(response, false);
    }


    response.setVotePreferencialSection(null);//SETEAMOS A NULL, YA SE ASIGNO EN UNA SUBLISTA

    getSectionObservation(acta, response, detActaRectangles);
    getSectionTimeInicioFinEscrutinio(acta, response, detActaRectangles);
    getSectionTotalCVAS(acta, response, detActaRectangles);
    response.setEstadoActa(acta.getEstadoActa());
    response.setSolucionTecnologica(ConstantesComunes.SOLUCION_TECNOLOGICA_TEXT_STAE);

  }


  private void getSectionVoteSTAE(Acta acta, String codigoEleccion, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS) {
    DetActaRectangleDTO archivoRect = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_VOTE);

    List<VerificationVoteItem> votos = new ArrayList<>();
    if (archivoRect == null || archivoRect.getValues() == null)
      return;

    List<DetUbigeoEleccionAgrupacionPolitica> agrupPol = agrupolOrdenadosSbni(acta).stream()
        .filter(e -> e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue() &&
            e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue() &&
            e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue())
        .toList();

    ModeloItem[] votosModelo = getVotosModeloItem(archivoRect, agrupPol);

    boolean esRevocatoria = codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST);
    procesarVotosPorAgrupacionSTAE(acta, agrupPol, votosModelo, votos, esRevocatoria);

    if (!esRevocatoria) {
      sectionVotosAgregarFooterStae(acta, archivoRect.getValues().getFooter(), votos);
      sectionVotosAgregaFooterAchurados(acta, votos);
    }

    response.setVoteSection(getVerificationVoteSectionResponse(response, votos));
  }


  private void procesarVotosPorAgrupacionSTAE(Acta acta,
                                          List<DetUbigeoEleccionAgrupacionPolitica> agrupPol,
                                          ModeloItem[] votosModelo,
                                          List<VerificationVoteItem> votos,
                                          boolean esRevocatoria) {
    for (int i = 0; i < agrupPol.size(); i++) {
      DetUbigeoEleccionAgrupacionPolitica de = agrupPol.get(i);
      VerificationVoteItem item = new VerificationVoteItem();
      try {
        if (esRevocatoria) {
          processVoteItemStaeRevocatoria(item, de, votosModelo[i]);
        } else {
          processVoteItemStae(item, de, votosModelo[i], acta.getId());
        }
      } catch (Exception e) {
        if (esRevocatoria) {
          setItemValuesToVerificationVoteItemStaeRevocatoriaAchurada(item);
        } else {
          setItemValuesToVerificationVoteItemStae(item,
              ConstantesComunes.N_ARCHIVO_ILEGIBLE,
              ConstantesComunes.C_VALUE_ILEGIBLE,
              acta.getId(),
              de.getPosicion());
        }
      }
      votos.add(item);
    }
  }



  private VerificationVoteSectionDTO getVerificationVoteSectionResponse(VerificationActaDTO response, List<VerificationVoteItem> votos) {
    VerificationVoteSectionDTO voteResponse = new VerificationVoteSectionDTO();
    if (response.getVotePreferencialSection() != null) {
      voteResponse.setCantidadVotosPreferenciales(response.getVotePreferencialSection().getCantidadEscanios());
    } else {
      voteResponse.setCantidadVotosPreferenciales(0);
    }
    voteResponse.setToken(response.getToken());
    //Collections.shuffle(votos);//chocolateo no olvidar descomentar zoe catalina
    voteResponse.setItems(votos);
    return voteResponse;
  }

  private void sectionVotosAgregarFooterStae(Acta acta, List<DetActaRectangleVoteFooterItem> footer, List<VerificationVoteItem> votos) {
    for (DetActaRectangleVoteFooterItem itemVote : footer) {

      if (itemVote.getTotalVotos0() == null || itemVote.getItem() == null) {
        continue;
      }
      VerificationVoteItem item = new VerificationVoteItem();
      Optional<Archivo> optArchivo = this.archivoService.findById(itemVote.getTotalVotos0().getArchivo());
      if (itemVote.getItem().contains("VOTOS EN BLANCO")) {
        item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue());
        item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue()));
      } else if (itemVote.getItem().contains("VOTOS NULOS")) {
        item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue());
        item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue()));
      } else if (itemVote.getItem().contains("VOTOS IMPUGNADOS")) {
        item.setPosition(ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue());
        item.setPositionToken(generarTokenPositionAgrupol(acta, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()));
      } else if (itemVote.getItem().contains("TOTAL DE VOTOS EMITIDOS")) {
        item.setPosition(0);
        item.setPositionToken(generarTokenPositionAgrupol(acta, 0));
      }
      if (item.getPosition() != null && item.getPosition() != 0) {
        setItemValuesToVerificationVoteItemStae(item, optArchivo.map(Archivo::getId).orElse(null),
            itemVote.getTotalVotos0().getPrediccion(), acta.getId(), item.getPosition());
        votos.add(item);
      }
    }
  }

  private void processVoteItemStae(VerificationVoteItem item, DetUbigeoEleccionAgrupacionPolitica agrupacion, ModeloItem votoModelo,
      Long actaAleatoria) {
    if (agrupacion.getEstado() == 20) {
      setItemValuesToVerificationVoteItemStae(item, null, null, actaAleatoria, null);
    } else if (agrupacion.getEstado() == 1) {
      if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votoModelo.getVotos1())) {
        setItemValuesToVerificationVoteItemStae(item, ConstantesComunes.N_ARCHIVO_ILEGIBLE, ConstantesComunes.C_VALUE_ILEGIBLE, actaAleatoria, agrupacion.getPosicion());
      } else {
        Optional<Archivo> optionalArchivo = this.archivoService.findById(votoModelo.getIdArchivo1());
        optionalArchivo.ifPresent(
            archivo -> setItemValuesToVerificationVoteItemStae(item, archivo.getId(), votoModelo.getVotos1(), actaAleatoria,
                agrupacion.getPosicion()));
      }
    }
  }


  private void processVoteItemStaeRevocatoria(VerificationVoteItem item, DetUbigeoEleccionAgrupacionPolitica detUbigeoEleccionAgrupacionPolitica, ModeloItem votoModelo) {
    if (Objects.equals(detUbigeoEleccionAgrupacionPolitica.getEstado(), ConstantesComunes.N_ACHURADO))
      setItemValuesToVerificationVoteItemStaeRevocatoriaAchurada(item);
    else if (Objects.equals(detUbigeoEleccionAgrupacionPolitica.getEstado(), ConstantesComunes.N_PARTICIPA))
      setItemValuesToVerificationVoteItemStaeRevocatoriaParticipa(item, votoModelo);
  }

  private void setItemValuesToVerificationVoteItemStae(VerificationVoteItem item, Long fileId, String systemValue, Long actaAlearotia,
      Integer nPosicion) {

    String userValue = null;
    if (nPosicion != null) {
      Optional<DetActa> optDetActaSTAE = this.actaServiceGroup.getDetActaService().getDetActa(actaAlearotia, nPosicion);
      if (optDetActaSTAE.isPresent()) {
        userValue = optDetActaSTAE.get().getVotos() == null ? "0" : optDetActaSTAE.get().getVotos().toString();
      }
    }

    item.setFileId(fileId);
    item.setSystemValue(SceUtils.limpiarVotos(systemValue));
    item.setUserValue(userValue);
  }

  private void setItemValuesToVerificationVoteItemStaeRevocatoriaParticipa(VerificationVoteItem verificationVoteItem,ModeloItem modeloItem) {

    List<VerificationVoteRevocatoriaItem> votoRevocatoria = new ArrayList<>();

    for (int positionColumnaRevocatoria = ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_SI;
         positionColumnaRevocatoria <= ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_IMPUGNADOS;
         positionColumnaRevocatoria++) {

      VerificationVoteRevocatoriaItem item = createRevocatoriaStaeItem(positionColumnaRevocatoria, modeloItem);
      votoRevocatoria.add(item);
    }

    verificationVoteItem.setVotoRevocatoria(votoRevocatoria);
  }



  private VerificationVoteRevocatoriaItem createRevocatoriaStaeItem(int positionColumnaRevocatoria, ModeloItem modeloItem) {

    VerificationVoteRevocatoriaItem item = new VerificationVoteRevocatoriaItem();
    item.setPosition(positionColumnaRevocatoria);

    String votos = getVotosByPositionStae(positionColumnaRevocatoria, modeloItem);
    Long fileId = getFileIdByPositionStae(positionColumnaRevocatoria, modeloItem);

    if (votos.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
      item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
      item.setUserValue(ConstantesComunes.C_VALUE_ILEGIBLE);
    } else {
      item.setFileId(fileId);
      item.setSystemValue(SceUtils.limpiarVotos(votos));
      item.setUserValue(null);
    }

    return item;
  }

  private String getVotosByPositionStae(int positionColumnaRevocatoria, ModeloItem modeloItem) {
    return switch (positionColumnaRevocatoria) {
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_SI -> modeloItem.getVotos1();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_NO -> modeloItem.getVotos2();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_BLANCOS -> modeloItem.getVotos3();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_NULOS -> modeloItem.getVotos4();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_IMPUGNADOS -> modeloItem.getVotos5();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_TOTAL -> modeloItem.getVotos6();
      default -> throw new IllegalArgumentException("Invalid position getVotosByPositionStae: " + positionColumnaRevocatoria);
    };
  }

  private Long getFileIdByPositionStae(int position, ModeloItem modeloItem) {
    return switch (position) {
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_SI -> modeloItem.getIdArchivo1();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_NO -> modeloItem.getIdArchivo2();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_BLANCOS -> modeloItem.getIdArchivo3();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_NULOS -> modeloItem.getIdArchivo4();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_IMPUGNADOS -> modeloItem.getIdArchivo5();
      case ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_TOTAL -> modeloItem.getIdArchivo6();
      default -> throw new IllegalArgumentException("Invalid position getFileIdByPositionStae: " + position);
    };
  }



  private void setItemValuesToVerificationVoteItemStaeRevocatoriaAchurada(VerificationVoteItem item) {
    List<VerificationVoteRevocatoriaItem> votoRevocatoria =
            IntStream.rangeClosed(ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_SI, ConstantesComunes.POSICION_COLUMNA_REV_VOTOS_IMPUGNADOS)
                    .mapToObj(positioColumRev -> {
                      VerificationVoteRevocatoriaItem item1 = new VerificationVoteRevocatoriaItem();
                      item1.setPosition(positioColumRev);
                      item1.setFileId(null);
                      item1.setSystemValue(ConstantesComunes.CVALUE_NULL);
                      item1.setUserValue(ConstantesComunes.CVALUE_NULL);
                      return item1;
                    })
                    .toList();

    item.setVotoRevocatoria(votoRevocatoria);
  }

  private void getSectionVotePreferencialSTAE(Acta acta, int cantidadColumnas, VerificationActaDTO response,List<DetActaRectangleDTO> detActaRectangleDTOS) {

    DetActaRectangleDTO archivoRect = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_VOTE_PREFERENCIAL);
    if (archivoRect==null || archivoRect.getValues() == null) {
      return;
    }
    List<VerificationVotePreferenciaRowItem> votos = new ArrayList<>();

    List<DetUbigeoEleccionAgrupacionPolitica> detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado =
        getDetUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado(acta);

    List<DetActaRectangleVoteItem> detActaRectangleVoteItems = archivoRect.getValues().getBody();
    ModeloItemPreferencial[] votosModelo =
        new ModeloItemPreferencial[detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado.size()];
    Arrays.fill(votosModelo, new ModeloItemPreferencial());

    for (DetActaRectangleVoteItem item : detActaRectangleVoteItems) {
      ModeloItemPreferencial modeloItem = getModeloItemPreferencial(item);
      if (item.getNro() != null) {
        votosModelo[item.getNro() - 1] = modeloItem;
      }
    }

    int indice = 0;
    for (DetUbigeoEleccionAgrupacionPolitica de : detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado) {
      VerificationVotePreferenciaRowItem rowItem = new VerificationVotePreferenciaRowItem();

      try {
        if (Objects.equals(de.getEstado(), ConstantesComunes.N_ACHURADO)) {
          rowItem.setPosition(de.getPosicion());
          rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
          rowItem.setItems(createVotosPreferencialesAchuradosItems(cantidadColumnas));

        } else if (de.getEstado().equals(ConstantesComunes.N_PARTICIPA)) {
          ModeloItemPreferencial modeloItemPreferencial = votosModelo[indice];
          List<VerificationVotePreferencialItem> items2 =
              getVotosPreferencialesSTAE(acta.getId(), de.getPosicion(),
                  modeloItemPreferencial, cantidadColumnas);
          rowItem.setPosition(de.getPosicion());
          rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
          rowItem.setItems(items2);
        }
        votos.add(rowItem);
      } catch (Exception e) {
        setItemsPreferencialesNoReconocidosSTAE(acta, rowItem, cantidadColumnas, de.getPosicion());
      }
      indice = indice + 1;
    }

    VerificationVotePreferencialSectionDTO voteResponse = new VerificationVotePreferencialSectionDTO();
    voteResponse.setCantidadEscanios(cantidadColumnas);
    voteResponse.setToken(response.getToken());
    //Collections.shuffle(votos);// CHOCOLATEO no olvidar descomentar zoe catalina
    voteResponse.setItems(votos);
    response.setVotePreferencialSection(voteResponse);
  }

  private List<VerificationVotePreferencialItem> getVotosPreferencialesSTAE(Long actaAleatoria,
      Integer nPosicion,
      ModeloItemPreferencial modeloItemPreferencial,
      int cantidadScaniosCurules) {

    List<VerificationVotePreferencialItem> items2 = new ArrayList<>();
    Optional<DetActa> optionalDetActaStae = this.actaServiceGroup.getDetActaService()
        .getDetActa(actaAleatoria, nPosicion.longValue());

    if (optionalDetActaStae.isPresent()) {
      List<DetActaPreferencial> detActaPreferencialList =
          this.actaServiceGroup.getDetActaPreferencialService().findByDetActa(optionalDetActaStae.get());

      for (int i = 1; i <= cantidadScaniosCurules; i++) {
        String votostae = getVotoRegistradoSTAEDetActaPreferencial(detActaPreferencialList, i);
        String votosPreferencial = getVotosPreferencialPorIndice(modeloItemPreferencial, i);
        Long idArchivo = getIdArchivoPorIndice(modeloItemPreferencial, i);

        items2.add(getItemVpStae(i, idArchivo, votosPreferencial, votostae));

      }
    }
    return items2;
  }

  private Long getIdArchivoPorIndice(ModeloItemPreferencial modeloItemPreferencial, int indice) {
    if (indice < 1 || indice > 34) {
      return null;
    }
    try {
      Method metodo = pe.gob.onpe.sceorcbackend.utils.ModeloItemPreferencial.class.getMethod("getIdArchivo" + indice);
      return (Long) metodo.invoke(modeloItemPreferencial);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
      return null;
    }
  }

  private String getVotosPreferencialPorIndice(ModeloItemPreferencial modeloItemPreferencial, int indice) {
    if (indice < 1 || indice > 34) {
      return "";
    }

    try {
      Method metodo = ModeloItemPreferencial.class.getMethod(ConstantesComunes.REFLECT_NAME_METHOD_GET_VOTOS + indice);
      return (String) metodo.invoke(modeloItemPreferencial);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
      return "";
    }
  }

  private String getVotoRegistradoSTAEDetActaPreferencial(List<DetActaPreferencial> detActaPreferencialList, int lista) {

    String votoDefault = "0";
    DetActaPreferencial detActaPreferencial = null;

    List<DetActaPreferencial> detActaPrefere = detActaPreferencialList.stream().filter(e -> e.getLista() == lista).toList();
    if (!detActaPrefere.isEmpty()) {
      detActaPreferencial = detActaPrefere.getFirst();

      votoDefault = detActaPreferencial.getVotos() == null ? "0" : detActaPreferencial.getVotos().toString();
    }

    return votoDefault;

  }

  private void setItemsPreferencialesNoReconocidosSTAE(Acta acta, VerificationVotePreferenciaRowItem rowItem, Integer cantidadColumnas,
      Integer nPosicion) {
    Optional<DetActa> optDetActaSTAE = this.actaServiceGroup.getDetActaService().getDetActa(acta.getId(), nPosicion);

    if (optDetActaSTAE.isEmpty()) {
      return;
    }

    List<DetActaPreferencial> detActaPreferencialList =
        this.actaServiceGroup.getDetActaPreferencialService().findByDetActa(optDetActaSTAE.get());
    List<VerificationVotePreferencialItem> itemsNoReconocidos = new ArrayList<>();
    for (int i = 0; i < cantidadColumnas; i++) {
      String votostae = getVotoRegistradoSTAEDetActaPreferencial(detActaPreferencialList, i + 1);
      VerificationVotePreferencialItem item = getDefaultItemIlegibleSTAE(i + 1, votostae);
      itemsNoReconocidos.add(item);
    }
    rowItem.setPosition(nPosicion);
    rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, nPosicion));
    rowItem.setItems(itemsNoReconocidos);
  }

  private void asignarVotoPreferencialAVotos(VerificationActaDTO response, boolean autorizado) {
    VerificationVoteSectionDTO verificationVoteSectionResponse = response.getVoteSection();
    if (verificationVoteSectionResponse != null && verificationVoteSectionResponse.getItems() != null) {
      VerificationVotePreferencialSectionDTO verificationVotePreferencialSectionResponse = response.getVotePreferencialSection();
      for (VerificationVoteItem verificationVoteItem : verificationVoteSectionResponse.getItems()) {
        verificationVoteItem.setVotoPreferencial(
            getVerificationVotePreferencialItem(verificationVotePreferencialSectionResponse, verificationVoteItem.getPosition(), autorizado));

        if(verificationVoteItem.getVotoPreferencial() != null && !verificationVoteItem.getVotoPreferencial().isEmpty()){
            for(VerificationVotePreferencialItem votoPreferencial : verificationVoteItem.getVotoPreferencial()){
                votoPreferencial.setEstado(verificationVoteItem.getEstado());
            }
        }
        if (verificationVoteItem.getVotoPreferencial().isEmpty()) {
          verificationVoteItem.setVotoPreferencial(null);
        }
      }
    }
  }


  private List<VerificationVotePreferencialItem> getVerificationVotePreferencialItem(
      VerificationVotePreferencialSectionDTO verificationVotePreferencialSectionResponse, int posicion, boolean autorizado) {
    if (verificationVotePreferencialSectionResponse != null && verificationVotePreferencialSectionResponse.getItems() != null) {
      List<VerificationVotePreferenciaRowItem> items =
          verificationVotePreferencialSectionResponse.getItems().stream().filter(very -> very.getPosition() == posicion).toList();

      if (!items.isEmpty()) {
        List<VerificationVotePreferencialItem> votePreferencialItems = items.getFirst().getItems();
        if(!autorizado){
            votePreferencialItems.sort((a, b) -> {
                boolean aEmpty = (a.getSystemValue() == null || a.getSystemValue().isEmpty());
                boolean bEmpty = (b.getSystemValue() == null || b.getSystemValue().isEmpty());
                return Boolean.compare(aEmpty, bEmpty);
            });
        }

        return votePreferencialItems;
      }
    }
    return Collections.emptyList();
  }

  private List<DetUbigeoEleccionAgrupacionPolitica> agrupolOrdenadosSbni(Acta acta) {
    List<DetUbigeoEleccionAgrupacionPolitica> agrupol =
        this.actaServiceGroup.getDetUbigeoEleccionAgrupacionPoliticaService().findByUbigeoEleccion(acta.getUbigeoEleccion());

    List<DetUbigeoEleccionAgrupacionPolitica> agrupolSBNI = agrupol.stream()
            .filter(e -> e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue()
                    && e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue()
                    && e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()).toList();

    return agrupolSBNI.stream().sorted(Comparator.comparing(DetUbigeoEleccionAgrupacionPolitica::getPosicion)).toList();
  }

  private void getSectionVotePreferencial(Acta acta, int cantidadColumnas, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS, boolean autorizado) {

    DetActaRectangleDTO archivoRect = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_VOTE_PREFERENCIAL);
    if (archivoRect==null )
      return;

    if (archivoRect.getValues() == null) {
      return;
    }

    List<VerificationVotePreferenciaRowItem> votos = new ArrayList<>();

    List<DetUbigeoEleccionAgrupacionPolitica> detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado =
        getDetUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado(acta);

    List<DetActaRectangleVoteItem> detActaRectangleVoteItems = archivoRect.getValues().getBody();
    ModeloItemPreferencial[] votosModelo =
        new ModeloItemPreferencial[detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado.size()];
    Arrays.fill(votosModelo, new ModeloItemPreferencial());// Rellenar el array

    for (DetActaRectangleVoteItem item : detActaRectangleVoteItems) {
      ModeloItemPreferencial modeloItem = getModeloItemPreferencial(item);
      if (item.getNro() != null) {
        votosModelo[item.getNro() - 1] = modeloItem;
      }
    }

    int indice = 0;
    for (DetUbigeoEleccionAgrupacionPolitica de : detUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado) {
      VerificationVotePreferenciaRowItem rowItem = new VerificationVotePreferenciaRowItem();
      try {
        if (Objects.equals(de.getEstado(), ConstantesComunes.N_ACHURADO)) {
          rowItem.setPosition(de.getPosicion());
          rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
          rowItem.setItems(createVotosPreferencialesAchuradosItems(cantidadColumnas));
        } else if (Objects.equals(de.getEstado(), ConstantesComunes.N_PARTICIPA)) {
          ModeloItemPreferencial modeloItemPreferencial = votosModelo[indice];
          List<VerificationVotePreferencialItem> items2 = getVotosPreferenciales(modeloItemPreferencial, cantidadColumnas, autorizado);
          rowItem.setPosition(de.getPosicion());
          rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, de.getPosicion()));
          rowItem.setItems(items2);
        }
        votos.add(rowItem);
      } catch (Exception e) {
        setItemsPreferencialesNoReconocidos(acta, rowItem, cantidadColumnas, de.getPosicion());
      }
      indice = indice + 1;
    }

    VerificationVotePreferencialSectionDTO voteResponse = new VerificationVotePreferencialSectionDTO();
    voteResponse.setCantidadEscanios(cantidadColumnas);
    voteResponse.setToken(response.getToken());
    //Collections.shuffle(votos);//CHOCOLATEO no olvidar descomentar zoe catalina
    voteResponse.setItems(votos);
    response.setVotePreferencialSection(voteResponse);

  }

  private void setItemsPreferencialesNoReconocidos(Acta acta, VerificationVotePreferenciaRowItem rowItem,
      Integer cantidadColumnas, Integer nPosicion) {

    List<VerificationVotePreferencialItem> itemsNoReconocidos = new ArrayList<>();
    for (int i = 0; i < cantidadColumnas; i++) {
      VerificationVotePreferencialItem item = getDefaultItemIlegible(i + 1);
      itemsNoReconocidos.add(item);
    }
    rowItem.setPosition(nPosicion);
    rowItem.setTokenPosition(generarTokenPositionAgrupol(acta, nPosicion));
    rowItem.setItems(itemsNoReconocidos);
  }

  private List<VerificationVotePreferencialItem> getVotosPreferenciales(ModeloItemPreferencial modeloItemPreferencial, int cantidadScaniosCurules, boolean autorizado) {
    List<VerificationVotePreferencialItem> items2 = new ArrayList<>();

    // Iteramos desde 1 hasta la cantidad máxima de curules disponibles
    for (int i = 1; i <= cantidadScaniosCurules; i++) {
      String voto = getVotoByIndex(modeloItemPreferencial, i);
      Long idArchivo = getIdArchivoByIndex(modeloItemPreferencial, i);

      if (!autorizado && voto == null) {
        continue;
      }

      items2.add(getItemVp(i, idArchivo, voto != null ? voto : ""));
    }

    return items2;
  }

  private VerificationVotePreferencialItem getItemVp(int posicion, Long idArchivo, String votos) {
    VerificationVotePreferencialItem item =
        new VerificationVotePreferencialItem();
    item.setFileId(idArchivo);
    item.setPosition(posicion);
    item.setSystemValue(SceUtils.limpiarVotos(votos));
    item.setUserValue(null);
    return item;
  }

  private VerificationVotePreferencialItem getItemVpStae(int posicion, Long idArchivo, String votosModel, String votosRegistrados) {
    VerificationVotePreferencialItem item =
        new VerificationVotePreferencialItem();
    item.setFileId(idArchivo);
    item.setPosition(posicion);
    item.setSystemValue(SceUtils.limpiarVotos(votosModel));
    item.setUserValue(votosRegistrados);
    return item;
  }

  private VerificationVotePreferencialItem getDefaultItemIlegible(int posicion) {
    VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
    item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
    item.setPosition(posicion);
    item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
    item.setUserValue(ConstantesComunes.C_VALUE_ILEGIBLE);
    return item;
  }

  private VerificationVotePreferencialItem getDefaultItemIlegibleSTAE(int posicion, String votos) {
    VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
    item.setFileId(ConstantesComunes.N_ARCHIVO_ILEGIBLE);
    item.setPosition(posicion);
    item.setSystemValue(ConstantesComunes.C_VALUE_ILEGIBLE);
    item.setUserValue(votos);
    return item;
  }

  private String getVotoByIndex(ModeloItemPreferencial modeloItemPreferencial, int index) {
    if (index < 1 || index > 34) {
      return null;
    }
    try {
      Method metodo = ModeloItemPreferencial.class.getMethod(ConstantesComunes.REFLECT_NAME_METHOD_GET_VOTOS + index);
      return (String) metodo.invoke(modeloItemPreferencial);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
      return null;
    }
  }

  private Long getIdArchivoByIndex(ModeloItemPreferencial modeloItemPreferencial, int index) {
    if (index < 1 || index > 34) {
      return null;
    }

    try {
      Method metodo = ModeloItemPreferencial.class.getMethod("getIdArchivo" + index);
      return (Long) metodo.invoke(modeloItemPreferencial);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
      return null;
    }
  }

  private List<VerificationVotePreferencialItem> createVotosPreferencialesAchuradosItems(int cantidadColumnas) {
    List<VerificationVotePreferencialItem> items =
        new ArrayList<>();
    for (int i = 0; i < cantidadColumnas; i++) {
      Integer position = i + 1;
      items.add(createVotePreferencialItem(position));
    }
    return items;
  }

  private VerificationVotePreferencialItem createVotePreferencialItem(Integer position) {
    VerificationVotePreferencialItem item = new VerificationVotePreferencialItem();
    item.setFileId(null);
    item.setPosition(position);
    item.setSystemValue(null);
    item.setUserValue(null);
    return item;
  }

  private String generarTokenPositionAgrupol(Acta acta, Integer position) {
    PositionAgrupolClaimsDto claimsDto = null;
    if (acta != null) {
      claimsDto = new PositionAgrupolClaimsDto();
      claimsDto.setIdActa(acta.getId());
      claimsDto.setIdPosition(position);
    }
    return TokenRandonUtil.generateTokenPositionAgrupol(claimsDto);
  }

  private static ModeloItemPreferencial getModeloItemPreferencial(DetActaRectangleVoteItem item) {
    ModeloItemPreferencial modeloItem = new ModeloItemPreferencial();
    for (int i = 1; i <= 34; i++) {
      String methodNameVotos = ConstantesComunes.REFLECT_NAME_METHOD_GET_VOTOS + i;
      String methodNameArchivo = "setIdArchivo" + i;
      String methodNamePrediccion = "setVotos" + i;
      try {
        Method getVotosMethod = item.getClass().getMethod(methodNameVotos);
        Object votos = getVotosMethod.invoke(item);

        if (votos != null && item.getNro() != null) {
          Method getPrediccionMethod = votos.getClass().getMethod("getPrediccion");
          Method getArchivoMethod = votos.getClass().getMethod("getArchivo");
          Object prediccion = getPrediccionMethod.invoke(votos);
          Object archivo = getArchivoMethod.invoke(votos);

          Method setArchivoMethod = modeloItem.getClass().getMethod(methodNameArchivo, archivo.getClass());
          Method setPrediccionMethod = modeloItem.getClass().getMethod(methodNamePrediccion, prediccion.getClass());

          setPrediccionMethod.invoke(modeloItem, prediccion);
          setArchivoMethod.invoke(modeloItem, archivo);
        }
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
        logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e.getMessage());
      }
    }
    return modeloItem;
  }

  private List<DetUbigeoEleccionAgrupacionPolitica> getDetUbigeoEleccionAgrupacionPoliticasOrdenadosSinBlancoNuloImpugnado(Acta acta) {
    List<DetUbigeoEleccionAgrupacionPolitica> detUbigeoEleccionAgrupacionPoliticas =
        this.actaServiceGroup.getDetUbigeoEleccionAgrupacionPoliticaService().findByUbigeoEleccion(acta.getUbigeoEleccion());
    List<DetUbigeoEleccionAgrupacionPolitica> detUbigeoEleccionAgrupacionPoliticasOrdenados = detUbigeoEleccionAgrupacionPoliticas.stream()
        .sorted(Comparator.comparing(DetUbigeoEleccionAgrupacionPolitica::getPosicion)).toList();

    return detUbigeoEleccionAgrupacionPoliticasOrdenados.stream()
        .filter(e -> e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue() &&
            e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue() &&
            e.getPosicion() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue()).toList();
  }

  private void getSectionObservation(Acta acta, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS) throws VerificationActaException {
    try {

      VerificationObservationSectionDTO sectionResponse = new VerificationObservationSectionDTO();

      DetActaRectangleDTO archivoRectObsAE = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_OBSERVATION_COUNT);

      if(archivoRectObsAE == null)
        throw new VerificationActaException(String.format("No se obtuvo la sección de observación del acta de escrutinio del acta %d.", acta.getId()));


      DetActaRectangleDTO archivoRectObsAI = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_OBSERVATION_INSTALL);

      if(archivoRectObsAI == null)
        throw new VerificationActaException(String.format("No se obtuvo la sección de observación del acta de instalación del acta %d.", acta.getId()));


      DetActaRectangleDTO archivoRectObsAS = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_OBSERVATION_VOTE);

      if(archivoRectObsAS == null)
        throw new VerificationActaException(String.format("No se obtuvo la sección de observación del acta de sufragio del acta %d.", acta.getId()));


      VerificationObservation signAE = new VerificationObservation();
      signAE.setFileId(archivoRectObsAE.getArchivo());
      signAE.setSystemValue(null);

      VerificationObservation signAI = new VerificationObservation();
      signAI.setFileId(archivoRectObsAI.getArchivo());
      signAI.setSystemValue(null);

      VerificationObservation signAS = new VerificationObservation();
      signAS.setFileId(archivoRectObsAS.getArchivo());
      signAS.setSystemValue(null);

      sectionResponse.setCount(signAE);
      sectionResponse.setInstall(signAI);
      sectionResponse.setVote(signAS);
      sectionResponse.setNullityRequest(Boolean.FALSE);

      response.setObservationSection(sectionResponse);

    } catch (Exception e) {
      throw new VerificationActaException(e.getMessage());
    }

  }


  private void getSectionTimeInicioFinEscrutinio(Acta acta, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS) throws VerificationActaException {

    try {

      VerificationDatetimeSectionDTO sectionResponse = new VerificationDatetimeSectionDTO();

      DetActaRectangleDTO archivoRectIniTime= findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_START_TIME);

      if(archivoRectIniTime == null){
        throw new VerificationActaException(String.format("No se obtuvo la sección de hora de instalación del acta %d.", acta.getId()));
      }

      DetActaRectangleDTO archivoRectFinTime = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_FINISH_TIME);

      if(archivoRectFinTime == null){
        throw new VerificationActaException(String.format("No se obtuvo la sección de hora de escrutinio del acta %d.", acta.getId()));
      }


      VerificationDatetimeItem verificationDatetimeItemIniTime = new VerificationDatetimeItem();
      verificationDatetimeItemIniTime.setFileId(archivoRectIniTime.getArchivo());
      verificationDatetimeItemIniTime.setSystemValue(null);

      VerificationDatetimeItem verificationDatetimeItemFinTime = new VerificationDatetimeItem();
      verificationDatetimeItemFinTime.setFileId(archivoRectFinTime.getArchivo());
      verificationDatetimeItemFinTime.setSystemValue(null);


      asignarValoresTiempo(acta,verificationDatetimeItemIniTime, verificationDatetimeItemFinTime);

      sectionResponse.setStart(verificationDatetimeItemIniTime);

      sectionResponse.setEnd(verificationDatetimeItemFinTime);

      response.setDateSectionResponse(sectionResponse);

    } catch (Exception e) {
      throw new VerificationActaException(e.getMessage());
    }

}

  private void asignarValoresTiempo(Acta acta, VerificationDatetimeItem start, VerificationDatetimeItem end) {

    if(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      start.setUserValue(null);
      end.setUserValue(null);
    } else {
      start.setUserValue(acta.getHoraInstalacionManual());
      end.setUserValue(acta.getHoraEscrutinioManual());
    }

  }

  private void getSectionTotalCVAS(Acta acta, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS) throws VerificationActaException {

    try {
      DetActaRectangleDTO archivoRectCvasLetras = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_LETTERS);
      DetActaRectangleDTO archivoRectCvasNumeros = findByAbreviaturaAndTypes(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS, ConstantesComunes.LISTA_ABREV_ACTA_INSTALACION_SUFRAGIO);
      DetActaRectangleDTO archivoRectCvasNumerosEscrutinio = findByAbreviaturaAndTypes(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_CITIZENS_VOTED_NUMBERS, ConstantesComunes.LISTA_ABREV_ACTA_ESCRUTINIO);

      VerificationDatetimeTotal verificationDatetimeTotal = new VerificationDatetimeTotal();

      verificationDatetimeTotal.setFileId(archivoRectCvasLetras==null?ConstantesComunes.N_ARCHIVO_ILEGIBLE:archivoRectCvasLetras.getArchivo());
      verificationDatetimeTotal.setTextSystemValue(archivoRectCvasLetras==null?ConstantesComunes.VACIO:archivoRectCvasLetras.getTotalVotos());
      verificationDatetimeTotal.setFileIdNumber(archivoRectCvasNumeros==null?ConstantesComunes.N_ARCHIVO_ILEGIBLE:archivoRectCvasNumeros.getArchivo());
      verificationDatetimeTotal.setNumberSystemValue(archivoRectCvasNumeros==null?ConstantesComunes.VACIO:archivoRectCvasNumeros.getTotalVotos());
      verificationDatetimeTotal.setFileIdNumberEscrutinio(archivoRectCvasNumerosEscrutinio==null?ConstantesComunes.N_ARCHIVO_ILEGIBLE:archivoRectCvasNumerosEscrutinio.getArchivo());

      if (esTransmisionStaeTransmitida(acta)) {
        verificationDatetimeTotal.setNumberUserValue(acta.getCvas() == null ? ConstantesComunes.VALUE_CVAS_INCOMPLETA : acta.getCvas().toString());
      }

      response.getDateSectionResponse().setTotal(verificationDatetimeTotal);

    } catch (Exception e) {
      throw new VerificationActaException(e.getMessage());
    }

  }


  private boolean esTransmisionStaeTransmitida(Acta cabActa) {
    return cabActa.getTipoTransmision() != null && cabActa.getTipoTransmision().equals(ConstantesComunes.TIPO_HOJA_STAE_TRANSMITIDA);
  }



  private ResultRandom getActaRandomAleatoria(String codigoEleccion, String usuario) {

    boolean pasoUnaVez = Boolean.FALSE;
    ResultRandom resultRandom = new ResultRandom();
    Long actaAlearotia;

    List<Long> listActasAsignadas = this.actaServiceGroup.getCabActaService().findByEstadoActaAndVerificadorAndCodigoEleccion(
        ConstantesEstadoActa.ESTADO_ACTA_DIGITADA, ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION, usuario, codigoEleccion
    );

    if (!listActasAsignadas.isEmpty()) {

      actaAlearotia = listActasAsignadas.getFirst();

    } else {

      pasoUnaVez = Boolean.TRUE;
      actaAlearotia = this.actaServiceGroup.getCabActaService().getActaRandom(codigoEleccion, usuario);

    }

    resultRandom.setIdActa(actaAlearotia);
    resultRandom.setPasoUnaVez(pasoUnaVez);
    return resultRandom;

  }

    private ResultRandom getActaProcesamientoManualRandomAleatoria(String codigoEleccion, String usuario) {

        boolean pasoUnaVez = Boolean.FALSE;
        ResultRandom resultRandom = new ResultRandom();
        Long actaAlearotia;

        List<Long> listActasAsignadas = this.actaServiceGroup.getCabActaService().findByEstadoActaAndVerificadorAndCodigoEleccionAndDigitalizacion(
                ConstantesEstadoActa.ESTADO_ACTA_DIGITADA, ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION, usuario, codigoEleccion, ConstantesComunes.PROC_MANUAL_DIGITALIZACION_ESCRUTINIO
        );

        if(!listActasAsignadas.isEmpty()) {
            actaAlearotia = listActasAsignadas.getFirst();
        }else{
            pasoUnaVez = Boolean.TRUE;
            actaAlearotia = this.actaServiceGroup.getCabActaService().getActaProcesamientoManualRandom(codigoEleccion, usuario);
        }


        resultRandom.setIdActa(actaAlearotia);
        resultRandom.setPasoUnaVez(pasoUnaVez);
        return resultRandom;
    }

  private Acta obtenerActa(Long idActa) {
    Optional<Acta> actaRandom = this.actaServiceGroup.getCabActaService().findByIdForUpdate(idActa);
    return actaRandom.orElse(null);

  }

  private String generarTokenActa(Acta acta) {
    ActaRandomClaimsDto claims = null;
    if (acta != null) {
      claims = new ActaRandomClaimsDto();
      claims.setIdActa(acta.getId());

      boolean esConsulado = this.actaServiceGroup.getDetActaAccionService().existsByActaAccionTiempoAndUsuarioPrefix(
              acta.getId(),
              ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA,
              ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
              ConstantesComunes.USUARIO_CONSULADO);
      claims.setEsActaDeConsulado(esConsulado ? "SI" : "NO");

      if (acta.getMesa() != null) {
        claims.setNumMesa(acta.getMesa().getCodigo());
      }
      if (acta.getMesa() != null &&
          acta.getMesa().getLocalVotacion() != null) {
        claims.setLocalVotacion(acta.getMesa().getLocalVotacion().getNombre());
      }
      if (acta.getUbigeoEleccion() != null &&
          acta.getUbigeoEleccion().getUbigeo() != null) {
        claims.setUbigeo(acta.getUbigeoEleccion().getUbigeo().getCodigo());
        claims.setCopia(
            java.util.Optional.ofNullable(acta.getNumeroCopia()).orElse("") +
                java.util.Optional.ofNullable(acta.getDigitoChequeoEscrutinio()).orElse("")
        );
        claims.setDepartamento(acta.getUbigeoEleccion().getUbigeo().getDepartamento());
        claims.setProvincia(acta.getUbigeoEleccion().getUbigeo().getProvincia());
        claims.setDistrito(acta.getUbigeoEleccion().getUbigeo().getNombre());
        claims.setIdDetUbigeoEleccion(acta.getUbigeoEleccion().getId());
        claims.setIdEleccion(acta.getUbigeoEleccion().getEleccion().getCodigo());
        claims.setActaIdArchivoEscrutinio(acta.getArchivoEscrutinio().getId());
        claims.setActaIdArchivoInstalacionSufragio(acta.getArchivoInstalacionSufragio().getId());
      }
    }
    return TokenRandonUtil.generateToken(claims);
  }



  public DetActaRectangleDTO findByAbreviatura(List<DetActaRectangleDTO> detActaRectangleDTOS, String abreviatura) {
    if (detActaRectangleDTOS == null || abreviatura == null) {
      return null;
    }

    return detActaRectangleDTOS.stream()
            .filter(dto -> abreviatura.equals(dto.getAbreviatura()))
            .findFirst()
            .orElse(null);
  }

  /**
   * el tipo es de instalacion y sufragio
   * */
  public DetActaRectangleDTO findByAbreviaturaAndType(List<DetActaRectangleDTO> detActaRectangleDTOS, String abreviatura, String tipo) {
    if (detActaRectangleDTOS == null || abreviatura == null || tipo == null) {
      return null;
    }

    return detActaRectangleDTOS.stream()
        .filter(dto -> abreviatura.equals(dto.getAbreviatura()) && tipo.equals(dto.getType()))
        .findFirst()
        .orElse(null);
  }


  public DetActaRectangleDTO findByAbreviaturaAndTypes(List<DetActaRectangleDTO> detActaRectangleDTOS, String abreviatura, List<String> tipos) {
    if (detActaRectangleDTOS == null || abreviatura == null || tipos == null) {
      return null;
    }

    return detActaRectangleDTOS.stream()
        .filter(dto -> abreviatura.equals(dto.getAbreviatura()) && tipos.contains(dto.getType()))
        .findFirst()
        .orElse(null);
  }

  private void getSectionSign(Acta acta, VerificationActaDTO response, List<DetActaRectangleDTO> detActaRectangleDTOS) throws VerificationActaException {
    try {
      VerificationSignSectionDTO sectionResponse = new VerificationSignSectionDTO();

      // Mapear las abreviaturas a métodos setters de VerificationSignSectionDTO
      Map<String, BiConsumer<VerificationSignSectionDTO, VerificationSignItem>> sectionMapping = Map.of(
              ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_PRESIDENT, VerificationSignSectionDTO::setCountPresident,
              ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_SECRETARY, VerificationSignSectionDTO::setCountSecretary,
              ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_THIRD_MEMBER, VerificationSignSectionDTO::setCountThirdMember,
              ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_PRESIDENT, VerificationSignSectionDTO::setInstallPresident,
              ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_SECRETARY, VerificationSignSectionDTO::setInstallSecretary,
              ConstantesSecciones.SECTION_ABREV_SIGN_INSTALL_THIRD_MEMBER, VerificationSignSectionDTO::setInstallThirdMember,
              ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_PRESIDENT, VerificationSignSectionDTO::setVotePresident,
              ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_SECRETARY, VerificationSignSectionDTO::setVoteSecretary,
              ConstantesSecciones.SECTION_ABREV_SIGN_VOTE_THIRD_MEMBER, VerificationSignSectionDTO::setVoteThirdMember
      );

      for (Map.Entry<String, BiConsumer<VerificationSignSectionDTO, VerificationSignItem>> entry : sectionMapping.entrySet()) {
        String abreviatura = entry.getKey();
        BiConsumer<VerificationSignSectionDTO, VerificationSignItem> setter = entry.getValue();

        DetActaRectangleDTO detActaRectangleDTO = findByAbreviatura(detActaRectangleDTOS, abreviatura);
        if (detActaRectangleDTO == null) {
          throw new VerificationActaException(String.format(
                  "No está presente la sección %s del acta %d.", abreviatura, acta.getId()
          ));
        }

        VerificationSignItem verificationSignItem = createVerificationSignItem(detActaRectangleDTO);
        setter.accept(sectionResponse, verificationSignItem);
      }

      validarSignStatus(sectionResponse);
      response.setSignSection(sectionResponse);

    } catch (Exception e) {
      throw new VerificationActaException("Error al obtener las secciones de las firmas: " + e.getMessage());
    }
  }

  private VerificationSignItem createVerificationSignItem(DetActaRectangleDTO detActaRectangleDTO) {
    VerificationSignItem verificationSignItem = new VerificationSignItem();
    verificationSignItem.setFileId(detActaRectangleDTO.getArchivo());
    verificationSignItem.setSystemStatus(detActaRectangleDTO.getValid() != null
            ? detActaRectangleDTO.getValid().toString()
            : null);
    return verificationSignItem;
  }



  private void validarSignStatus(VerificationSignSectionDTO response) {
    int contFirmasEscrutinio = contarStatuses(response.getCountPresident(), response.getCountSecretary(), response.getCountThirdMember());
    int contFirmaInstall = contarStatuses(response.getInstallPresident(), response.getInstallSecretary(), response.getInstallThirdMember());
    int contFirmaSufragio = contarStatuses(response.getVotePresident(), response.getVoteSecretary(), response.getVoteThirdMember());

    if ((contFirmasEscrutinio == 3 && contFirmaInstall >= 2 && contFirmaSufragio >= 2) ||
        (contFirmasEscrutinio >= 2 && contFirmaInstall == 3 && contFirmaSufragio >= 2) ||
        (contFirmasEscrutinio >= 2 && contFirmaInstall >= 2 && contFirmaSufragio == 3)) {
      response.setSystemStatus(Boolean.toString(true));
      response.setStatus(1);
    } else {
      response.setSystemStatus(Boolean.toString(false));
      response.setStatus(0);
    }
  }

  private int contarStatuses(VerificationSignItem... items) {
    int count = 0;
    for (VerificationSignItem item : items) {
      if (item != null && item.getSystemStatus() != null && Boolean.parseBoolean(item.getSystemStatus())) {
        count++;
      }
    }
    return count;
  }

  @Override
  @Transactional
  public GenericResponse<Boolean> guardar(VerificationActaDTO request, TokenInfo tokenInfo) {

    long inicio = System.currentTimeMillis();

    try{
      Claims claims = TokenRandonUtil.decodeToken(request.getToken());
      @SuppressWarnings("unchecked")
      HashMap<String, Object> actaRamdon = (HashMap<String, Object>) claims.get("actaRandom");

      Long idActa = obtenerIdActa(actaRamdon.get("idActa"));
      if (idActa == null) {
        return new GenericResponse<>(false, "El acta no existe ", false);
      }

      logger.info("Guardando acta ID: {}, usuario: {}", idActa, tokenInfo.getNombreUsuario());

      Long idDetUbigeoEleccion = Long.parseLong(actaRamdon.get("idDetUbigeoEleccion").toString());
      Optional<Acta> actaRandom = this.actaServiceGroup.getCabActaService().findById(idActa);

      if (actaRandom.isPresent()) {
        Acta acta = actaRandom.get();
        String estadoActual = acta.getEstadoActa();

        validarEstadoActaAlGuardarDigitacion(acta, tokenInfo);

        procesarTipoTransmision(request, acta, idDetUbigeoEleccion, tokenInfo.getNombreUsuario());

        registrarAccionSegunOrdenVerificacion(request, acta, estadoActual, tokenInfo);

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                "guardar", "Acta "+SceUtils.getNumMesaAndCopia(acta)+" guardada correctamente.",
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
      }else{
          return new GenericResponse<>(false, "El acta a guardar no se encuentra registrada. ", false);
      }

      long fin = System.currentTimeMillis();
      logger.info("Acta ID: {} guardada en {} ms, usuario: {}", idActa, (fin - inicio), tokenInfo.getNombreUsuario());

      return new GenericResponse<>(true, "Acta guardada correctamente.", true, List.of(idActa));

    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw e;
    }

  }

  private void registrarAccionSegunOrdenVerificacion(VerificationActaDTO request, Acta acta, String estadoActual, TokenInfo tokenInfo) {
    String cc = tokenInfo.getCodigoCentroComputo();
    String usuario = tokenInfo.getNombreUsuario();
    String dtoProp = request.getPropOrdenVerificacionActas();

    if (ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(propOrdenVerificacionActas)) {
      if (estadoActual.equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {
        guardarAccionActa(acta, cc, usuario, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_1ERA_VERI, 8);
      } else if (estadoActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
        guardarAccionActa(acta, cc, usuario, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_2DA_VERI, 10);
      }
    } else if (ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)) {
      if (estadoActual.equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA) && ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(dtoProp)) {
        guardarAccionActa(acta, cc, usuario, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_1ERA_VERI, 8);
      } else if (estadoActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION) && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dtoProp)) {
        guardarAccionActa(acta, cc, usuario, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN, ConstantesComunes.DET_ACTA_ACCION_PROCESO_2DA_VERI, 10);
      }
    }
  }

  private void validarEstadoActaAlGuardarDigitacion(Acta acta, TokenInfo  tokenInfo) {

        String estadoActa = acta.getEstadoActa();

        if (estadoActa.equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {

            if(acta.getVerificador()==null) {
                throw new BadRequestException("El acta no tiene registrado un primer verificador.");
            }

            if(!acta.getVerificador().equals(ConstantesComunes.USER_MODELO) && !acta.getVerificador().equals(tokenInfo.getNombreUsuario())) {
                throw new BadRequestException(String.format("Solo el usuario %s, puede registrar la digitación del acta.", acta.getVerificador()));
              }

        } else if (estadoActa.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {

            if(acta.getVerificador2()==null){
                throw new BadRequestException("El acta no tiene registrado un segundo verificador.");
            }

            if(!acta.getVerificador2().equals(tokenInfo.getNombreUsuario())) {
                throw new BadRequestException(String.format("Solo el usuario %s, puede registrar la digitación del acta.", acta.getVerificador2()));
            }
        }else{
            throw new BadRequestException("El acta ya se encuentra en un estado procesado, no está disponible para su digitación.");
        }
    }

  @Override
  public PadronDto consultaPadronPorDni(String dni,String mesa, TokenInfo tokenInfo, Boolean omitirMesa) {

    if (dni.isEmpty())
      throw new BadRequestException("El campo DNI se encuentra vacío.");

    if (dni.length() != 8)
      throw new BadRequestException(String.format("El campo DNI %s debe tener 8 dígitos.", dni));

    Optional<PadronElectoral> optionalMaePadron = this.padronElectoralService.findByDocumentoIdentidad(dni);
    if (optionalMaePadron.isEmpty()) {
      throw new BadRequestException(String.format("El DNI %s no se encuentra registrado en el padrón electoral de este centro de cómputo.", dni));
    }

    if(mesa!=null && (Objects.isNull(omitirMesa) || !omitirMesa)){
      Optional<PadronElectoral> optionalMaePadron2 = this.padronElectoralService.findByDocumentoIdentidadAndMesa(dni, mesa);
      if (optionalMaePadron2.isEmpty()) {
        throw new BadRequestException(String.format("El DNI %s no pertenece a la mesa %s.", dni, mesa));
      }
    }


    PadronElectoral maePadron = optionalMaePadron.get();

    PadronDto padronDto = new PadronDto();
    padronDto.setIdPadron(maePadron.getId());
    padronDto.setNombres(maePadron.getNombres());
    padronDto.setApellidoMaterno(maePadron.getApellidoMaterno());
    padronDto.setApellidoPaterno(maePadron.getApellidoPaterno());
    return padronDto;
  }

    @Override
    @Transactional
    public GenericResponse<VerificationActaDTO> obtenerActaRandomParaProcesamientoManual(String codigoEleccion, TokenInfo tokenInfo) throws VerificationActaException {
      VerificationActaDTO response = new VerificationActaDTO();

      ResultRandom resultRandom = getActaProcesamientoManualRandomAleatoria(codigoEleccion,tokenInfo.getNombreUsuario());

      if(resultRandom.getIdActa() == null){
          return new GenericResponse<>(false, "No existen registros o aún no están disponibles. Vuelva a intentarlo dentro de un momento.");
      }

      Long idActa = resultRandom.getIdActa();
      boolean pasoUnaVez = resultRandom.isPasoUnaVez();

      Acta acta = obtenerActa(idActa);
      if (acta == null){
          return new GenericResponse<>(false, String.format("El acta aleatoria %d obtenida no existe.", idActa));
      }

      String mensaje = "";

        if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);
            acta.setVerificador(tokenInfo.getNombreUsuario());
            mensaje = "Se asignó para primera digitación manual el acta " +
                    SceUtils.getNumMesaAndCopia(acta) + ", al usuario " +
                    tokenInfo.getNombreUsuario();
        } else if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
            mensaje = "Se asignó para segunda digitación manual el acta " +
                    SceUtils.getNumMesaAndCopia(acta) + ", al usuario " +
                    tokenInfo.getNombreUsuario();
            acta.setVerificador2(tokenInfo.getNombreUsuario());
        }

        acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        acta.setFechaModificacion(new Date());
        this.actaServiceGroup.getCabActaService().save(acta);

        setearZonasActaProcesamientoManual(acta, acta.getUbigeoEleccion().getEleccion().getCodigo(), response);

        Long idActaRespuesta = 0L;

        if(pasoUnaVez){
            if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {
                idActaRespuesta = acta.getId();
                guardarAccionActa(acta,
                        tokenInfo.getCodigoCentroComputo(),
                        tokenInfo.getNombreUsuario(),
                        ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                        ConstantesComunes.DET_ACTA_ACCION_PROCESO_1ERA_VERI, 7);
            } else if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
                idActaRespuesta = acta.getId();
                guardarAccionActa(acta,
                        tokenInfo.getCodigoCentroComputo(),
                        tokenInfo.getNombreUsuario(),
                        ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                        ConstantesComunes.DET_ACTA_ACCION_PROCESO_2DA_VERI, 9);
            }
        }

        if (acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA) ||
                acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                    1
            );
        }

        response.setToken(generarTokenActa(acta));

        return new GenericResponse<>(
                true,
                "Se obtuvo la información del acta para procesamiento manual",
                response,
                List.of(idActaRespuesta)
        );
    }

    private void procesarTipoTransmision(VerificationActaDTO request, Acta cabActa, Long idDetUbigeoEleccion, String usuario) {
      Integer tipoTrasmision = cabActa.getTipoTransmision();
      if (tipoTrasmision == null) {
        return;
      }
      if (tipoTrasmision.equals(ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA) ||
          tipoTrasmision.equals(ConstantesComunes.TIPO_HOJA_STAE_NO_TRANSMITIDA) ||
          tipoTrasmision.equals(ConstantesComunes.TIPO_HOJA_CONVENCIOANL)) {
        guardarVerificacionActaConvencionalyStaeContingencia(request, cabActa, idDetUbigeoEleccion, usuario);
      }
    }


  private void guardarVerificacionActaConvencionalyStaeContingencia(VerificationActaDTO dto, Acta acta, Long idDetUbigeoEleccion, String usuario) {
    String codigoEleccion = acta.getUbigeoEleccion().getEleccion().getCodigo();
    String estadoActaActual = acta.getEstadoActa();

    prepararActa(acta, estadoActaActual, dto);

    VotoContext contexto = new VotoContext();
    contexto.setCabActa(acta);
    contexto.setCodigoEleccion(codigoEleccion);
    contexto.setEstadoActaActual(estadoActaActual);
    contexto.setUsuario(usuario);
    contexto.setIdDetUbigeoEleccion(idDetUbigeoEleccion);
    contexto.setPropOrdenVerificacionActas(dto.getPropOrdenVerificacionActas());

    long totalVotosCalculados = calcularTotalVotos(codigoEleccion, estadoActaActual, dto, acta);
    acta.setTotalVotos(totalVotosCalculados);

    if (dto.getVoteSection() == null || dto.getVoteSection().getItems() == null) return;

    Map<Integer, DetUbigeoEleccionAgrupacionPolitica> ubigeoAgrupMap = cargarUbigeoAgrupMap(idDetUbigeoEleccion);
    Map<Long, DetActa> detActaMap = cargarDetActaMap(acta);
    Map<String, DetActaPreferencial> prefMap = cargarPrefMap(detActaMap);

    procesarItemsVoto(dto.getVoteSection().getItems(), contexto, ubigeoAgrupMap, detActaMap, prefMap);

    procesarErroresMaterialesActa(acta, codigoEleccion, totalVotosCalculados, contexto);

    actualizarActaFinal(acta, estadoActaActual, usuario, contexto.getPropOrdenVerificacionActas());
  }

  private Map<Integer, DetUbigeoEleccionAgrupacionPolitica> cargarUbigeoAgrupMap(Long idDetUbigeoEleccion) {
    return actaServiceGroup.getDetUbigeoEleccionAgrupacionPoliticaService()
        .findByUbigeoEleccionId(idDetUbigeoEleccion)
        .stream()
        .collect(Collectors.toMap(DetUbigeoEleccionAgrupacionPolitica::getPosicion, d -> d));
  }

  private Map<Long, DetActa> cargarDetActaMap(Acta acta) {
    return actaServiceGroup.getDetActaService()
        .findByCabActa(acta)
        .stream()
        .filter(d -> d.getPosicion() != null)
        .collect(Collectors.toMap(DetActa::getPosicion, d -> d, (a, b) -> a));
  }

  private Map<String, DetActaPreferencial> cargarPrefMap(Map<Long, DetActa> detActaMap) {
    Map<String, DetActaPreferencial> prefMap = new HashMap<>();
    for (DetActa da : detActaMap.values()) {
      List<DetActaPreferencial> prefs = actaServiceGroup.getDetActaPreferencialService().findByDetActa(da);
      for (DetActaPreferencial p : prefs) {
        prefMap.put(da.getId() + "_" + p.getLista(), p);
      }
    }
    return prefMap;
  }

  private void procesarItemsVoto(List<VerificationVoteItem> items, VotoContext contexto,
      Map<Integer, DetUbigeoEleccionAgrupacionPolitica> ubigeoAgrupMap,
      Map<Long, DetActa> detActaMap, Map<String, DetActaPreferencial> prefMap) {

    for (VerificationVoteItem item : items) {
      DetActa detActa = inicializarDetActaBulk(item, contexto, ubigeoAgrupMap, detActaMap);
      if (detActa == null) continue;

      procesarYGuardarDetActa(item, detActa, contexto, prefMap);
    }
  }

  private void procesarYGuardarDetActa(VerificationVoteItem item, DetActa detActa, VotoContext contexto,
      Map<String, DetActaPreferencial> prefMap) {

    String votosSysmtem = limpiarValor(item.getSystemValue());
    String votosUser = limpiarValor(item.getUserValue());

    if (isEsCodigoEleccionValido(contexto.getCodigoEleccion())) {
      procesarVotosConvencional(contexto, detActa, votosSysmtem, votosUser, item);
    }

    detActa.setPosicion(item.getPosition().longValue());
    actaServiceGroup.getDetActaService().save(detActa);

    long totalVotosPrefeOpcion = 0;
    Long[] totalVotos = new Long[]{0L, 0L};

    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(contexto.getCodigoEleccion()) && item.getVotoPreferencial() != null) {
      totalVotosPrefeOpcion = procesarVotoPreferencialBulk(contexto, detActa, item, prefMap);
    } else if (contexto.getCodigoEleccion().equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      totalVotos = procesarVotoRevocatoria(contexto, detActa, item);
      totalVotosPrefeOpcion = totalVotos[0];
    }

    actualizarErroresMateriales(contexto, detActa, totalVotosPrefeOpcion, totalVotos[1]);

    detActa.setFechaModificacion(new Date());
    detActa.setUsuarioModificacion(contexto.getUsuario());
    actaServiceGroup.getDetActaService().save(detActa);
    contexto.getDetActaListToErrores().add(detActa);
  }

  private void procesarErroresMaterialesActa(Acta acta, String codigoEleccion, long totalVotosCalculados, VotoContext contexto) {
    if (isEsCodigoEleccionValido(codigoEleccion)) {
      acta.setEstadoErrorMaterial(ConsultaErroresMateriales.getErrMatANivelDeActa(acta, totalVotosCalculados));
      if (acta.getEstadoErrorMaterial() != null) {
        agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
      }
      ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(acta, contexto.getDetActaListToErrores());
      if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
        ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(acta, contexto.getDetActaPreferencialListToErrores());
      }
    } else {
      acta.setEstadoErrorMaterial(ConsultaErroresMateriales.getErrMatANivelDeActaRevocatoria(acta, totalVotosCalculados));
      guardarVeriConvencionalEstadoResolucionErrorMaterialRevocatoria(acta, contexto.getDetActaListToErrores(), contexto.getDetActaOpcionesListToErrores());
    }
  }


  private void prepararActa(Acta cabActa, String estadoActaActual, VerificationActaDTO dto) {
    boolean skipComparaciones = ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)
        && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dto.getPropOrdenVerificacionActas());


    if(ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)&&
        ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dto.getPropOrdenVerificacionActas()) &&
        estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)){
      skipComparaciones = false;
    }

    cabActa.setEstadoActaResolucion(null);
    cabActa.setEstadoErrorMaterial(null);
    setearFirmasEscrutinio(dto, cabActa);
    setearFirmasInstalacion(dto, cabActa);
    setearFirmasSugrafio(dto, cabActa);
    setearFirmasAutomatico(dto, cabActa, estadoActaActual);
    setearEstadoResolucionFirmas(dto, cabActa, estadoActaActual, skipComparaciones);
    if (dto.getVoteSection() != null && dto.getVoteSection().getStatus() != null)
      cabActa.setDigitacionVotos(dto.getVoteSection().getStatus());
    if (dto.getObservationSection() != null && dto.getObservationSection().getStatus() != null)
      cabActa.setDigitacionObserv(dto.getObservationSection().getStatus());
    setearEstadoResolucionObservaciones(dto, cabActa, estadoActaActual);
    setearEstadoResolucionDateSection(dto, cabActa, estadoActaActual, skipComparaciones);
    setearEstadoResolucionHoraSection(dto, cabActa);
  }

  private long calcularTotalVotos(String codigoEleccion, String estadoActaActual, VerificationActaDTO dto, Acta cabActa) {
    if (isEsCodigoEleccionValido(codigoEleccion)) {
      return getTotalVotosDeLasAgrupacionPoliticas(dto.getVoteSection());
    } else if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      Long cvas = estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)
          ? cabActa.getCvasV2() : cabActa.getCvasV1();
      return getTotalVotosCalculadosRevocatoria(dto.getVoteSection(), cvas);
    }
    return 0;
  }

  private String limpiarValor(String valor) {
    String item = SceUtils.limpiarVotos(valor);
    return item == null || item.equals(ConstantesComunes.VACIO) ? ConstantesComunes.NVALUE_ZERO + "" : item;
  }


  private void procesarVotosConvencional(VotoContext ctx, DetActa detActa, String votosSysmtem, String votosUser, VerificationVoteItem item) {
    if (!Objects.equals(detActa.getEstado(), ConstantesComunes.N_ACHURADO)) {
      if (!ctx.getEstadoActaActual().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
        setearVotosPrimeraVerificacionConvencionalyStaeContingencia(ctx.getCabActa(), detActa, votosSysmtem, votosUser, item, ctx.getPropOrdenVerificacionActas());
      } else {
        setearVotosSegundaVerificacionConvencionalyStaeContingencia(ctx.getCabActa(), detActa, votosUser, item);
      }
    }
  }

  private DetActa inicializarDetActaBulk(VerificationVoteItem item, VotoContext ctx,
      Map<Integer, DetUbigeoEleccionAgrupacionPolitica> ubigeoAgrupMap,
      Map<Long, DetActa> detActaMap) {

    DetUbigeoEleccionAgrupacionPolitica detUbi = ubigeoAgrupMap.get(item.getPosition());
    if (detUbi == null) return null;

    AgrupacionPolitica agrupacion = detUbi.getAgrupacionPolitica();

    DetActa detActa = detActaMap.get(item.getPosition().longValue());
    if (detActa == null) {
      detActa = new DetActa();
      detActa.setFechaCreacion(new Date());
      detActa.setUsuarioCreacion(ctx.getUsuario());
    }

    detActa.setEstado(detUbi.getEstado());
    detActa.setActa(ctx.getCabActa());
    detActa.setAgrupacionPolitica(new AgrupacionPolitica(agrupacion.getId()));
    return detActa;
  }

  private long procesarVotoPreferencialBulk(VotoContext ctx, DetActa detActa, VerificationVoteItem item,
      Map<String, DetActaPreferencial> prefMap) {
    VotoPreferencialContext vpctx = new VotoPreferencialContext();
    vpctx.setNEstado(detActa.getEstado());
    vpctx.setCabActa(ctx.getCabActa());
    vpctx.setDetActa(detActa);
    vpctx.setEstadoActaActual(ctx.getEstadoActaActual());
    vpctx.setUsuario(ctx.getUsuario());
    vpctx.setDetActaPreferencialList(ctx.getDetActaPreferencialListToErrores());
    vpctx.setTotalVotosAgrupaciones(ctx.getCabActa().getTotalVotos());
    vpctx.setPropOrdenVerificacionActas(ctx.getPropOrdenVerificacionActas());

    return guardarVotosPreferencialesBulk(vpctx, item.getVotoPreferencial(), prefMap);
  }

  private long guardarVotosPreferencialesBulk(VotoPreferencialContext ctx,
      List<VerificationVotePreferencialItem> votoPreferencialItems,
      Map<String, DetActaPreferencial> prefMap) {
    long total = 0;

    for (VerificationVotePreferencialItem itemPre : votoPreferencialItems) {

      DetActaPreferencial detActaPreferencial =
          obtenerOActualizarDetActaPreferencialBulk(ctx.getNEstado(), ctx.getCabActa(), ctx.getDetActa(), itemPre, ctx.getUsuario(), prefMap);

      String votosSysmtem = limpiarValor(itemPre.getSystemValue());
      String votosUser = limpiarValor(itemPre.getUserValue());

      if (!Objects.equals(ctx.getNEstado(), ConstantesComunes.N_ACHURADO)) {
        procesarPrimeraVerificacionVotosPreferenciales(ctx.getCabActa(), detActaPreferencial, votosSysmtem, votosUser, ctx.getEstadoActaActual(), ctx.getPropOrdenVerificacionActas());
        procesarSegundaVerificacionVotosPreferenciales(ctx.getCabActa(), detActaPreferencial, votosUser, ctx.getEstadoActaActual());
      }

      detActaPreferencial.setEstadoErrorMaterial(
          ConsultaErroresMateriales.getDetErrorMaterialPreferencial(ctx.getCabActa(), ctx.getDetActa(), detActaPreferencial));

      this.actaServiceGroup.getDetActaPreferencialService().save(detActaPreferencial);
      ctx.getDetActaPreferencialList().add(detActaPreferencial);
      total += (detActaPreferencial.getVotos() == null ? 0 : detActaPreferencial.getVotos());
    }

    return total;
  }

  private DetActaPreferencial obtenerOActualizarDetActaPreferencialBulk(Integer nEstado, Acta cabActa, DetActa detActa,
      VerificationVotePreferencialItem itemPre, String usuario,
      Map<String, DetActaPreferencial> prefMap) {

    String key = detActa.getId() + "_" + itemPre.getPosition();
    DetActaPreferencial detActaPreferencial = prefMap.get(key);

    if (detActaPreferencial == null) {
      detActaPreferencial = new DetActaPreferencial();
      detActaPreferencial.setUsuarioCreacion(usuario);
      detActaPreferencial.setFechaCreacion(new Date());
    }
    detActaPreferencial.setEstado(nEstado);
    detActaPreferencial.setDistritoElectoral(cabActa.getUbigeoEleccion().getUbigeo().getDistritoElectoral());
    detActaPreferencial.setDetActa(detActa);
    detActaPreferencial.setPosicion(detActa.getPosicion().intValue());
    detActaPreferencial.setLista(itemPre.getPosition());
    detActaPreferencial.setUsuarioModificacion(usuario);
    detActaPreferencial.setFechaModificacion(new Date());
    return detActaPreferencial;
  }

  private Long[] procesarVotoRevocatoria(VotoContext ctx, DetActa detActa, VerificationVoteItem item) {
    Long[] totalVotos = guardarVotosOpcion(detActa.getEstado(), ctx.getCabActa(), detActa, ctx.getEstadoActaActual(),
        item.getVotoRevocatoria(), ctx.getUsuario(), ctx.getDetActaOpcionesListToErrores());

    long totalVotosPrefeOpcion = totalVotos[0];
    if (ctx.getEstadoActaActual().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      detActa.setVotosManual2(totalVotosPrefeOpcion);
    } else {
      detActa.setVotosManual1(totalVotosPrefeOpcion);
    }
    detActa.setVotos(totalVotosPrefeOpcion);
    return totalVotos;
  }

  private void actualizarErroresMateriales(VotoContext ctx, DetActa detActa, long totalVotosPrefeOpcion, long totalVotosBNI) {
    if (isEsCodigoEleccionValido(ctx.getCodigoEleccion())) {
      detActa.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialAgrupol(ctx.getCabActa(), detActa, totalVotosPrefeOpcion));
    } else {
      detActa.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialDetOpcion(ctx.getCabActa(), totalVotosPrefeOpcion, totalVotosBNI));
      if (detActa.getEstadoErrorMaterial() != null && !detActa.getEstadoErrorMaterial().isEmpty()) {
        agregarEstadoResolucion(ctx.getCabActa(), ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
      }
    }
  }


  private void actualizarActaFinal(Acta cabActa, String estadoActaActual, String usuario, String dtoOrdenVerificacion) {
    boolean skipEstadoFinal = ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)
        && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dtoOrdenVerificacion);
    if (!skipEstadoFinal) {
      guardarVeriConvencionalEstadoFinalActa(estadoActaActual, cabActa);
    }

    if(ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)&&
        ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dtoOrdenVerificacion) &&
        estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)){
      guardarVeriConvencionalEstadoFinalActa(estadoActaActual, cabActa);
    }

    cabActa.setUsuarioModificacion(usuario);
    if (estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      cabActa.setVerificador2(usuario);
    } else {
      cabActa.setVerificador(usuario);
    }
    cabActa.setFechaModificacion(new Date());
    this.actaServiceGroup.getCabActaService().save(cabActa);
  }


  private static boolean isEsCodigoEleccionValido(String codigoEleccion) {
    return
        codigoEleccion.equals(ConstantesComunes.COD_ELEC_DIPUTADO) ||
            codigoEleccion.equals(ConstantesComunes.COD_ELEC_PAR) ||
            codigoEleccion.equals(ConstantesComunes.COD_ELEC_SENADO_UNICO) ||
            codigoEleccion.equals(ConstantesComunes.COD_ELEC_SENADO_MULTIPLE) ||
            codigoEleccion.equals(ConstantesComunes.COD_ELEC_PRE) ||
            codigoEleccion.equals(ConstantesComunes.COD_ELEC_DIST)||
                codigoEleccion.equals(ConstantesComunes.COD_ELEC_PROV)||
                codigoEleccion.equals(ConstantesComunes.COD_ELEC_CONSE)||
                codigoEleccion.equals(ConstantesComunes.COD_ELEC_REG);
  }

  private Long[] guardarVotosOpcion(Integer estado, Acta cabActa, DetActa detActa, String estadoActaActual,
                                    List<VerificationVoteRevocatoriaItem> votoRevocatoria,
                                    String usuario, List<DetActaOpcion> detActaOpcionesListToErrores) {

    Long totalVotosAutoridad = 0L;
    Long totalVotosBNI = 0L;

    for (VerificationVoteRevocatoriaItem item : votoRevocatoria) {
      DetActaOpcion opcion = obtenerOActualizarDetActaOpcion(detActa, item, usuario);
      String votosSistema = limpiarValor(item.getSystemValue());
      String votosUsuario = limpiarValor(item.getUserValue());

      if (!Objects.equals(estado, ConstantesComunes.N_ACHURADO)) {
        procesarVerificacionSegunEstado(estadoActaActual, cabActa, opcion, votosSistema, votosUsuario);
      }

      procesarErroresYGuardar(cabActa, opcion, detActaOpcionesListToErrores);

      totalVotosAutoridad += opcion.getVotos() != null ? opcion.getVotos() : 0;

      if (esBNI(item.getPosition())) {
        totalVotosBNI += opcion.getVotos() != null ? opcion.getVotos() : 0;
      }

      verificarImpugnadosOilegilibles(item, opcion, cabActa);
    }

    return new Long[]{totalVotosAutoridad, totalVotosBNI};
  }

  private void procesarVerificacionSegunEstado(String estadoActaActual, Acta cabActa, DetActaOpcion opcion, String votosSistema, String votosUsuario) {
    if (estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      procesarSegundaVerificacionVotosOpciones(cabActa, opcion, votosUsuario);
    } else {
      procesarPrimeraVerificacionVotosOpciones(cabActa, opcion, votosSistema, votosUsuario);
    }
  }

  private void procesarErroresYGuardar(Acta cabActa, DetActaOpcion opcion, List<DetActaOpcion> listaErrores) {
    opcion.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialOpcion(cabActa, opcion));

    if (opcion.getEstadoErrorMaterial() != null && !opcion.getEstadoErrorMaterial().isEmpty()) {
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
    }

    opcion.setActivo(ConstantesComunes.ACTIVO);
    this.actaServiceGroup.getDetActaOpcionService().save(opcion);
    listaErrores.add(opcion);
  }

  private boolean esBNI(Integer position) {
    return Objects.equals(position, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS.intValue()) ||
        Objects.equals(position, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS.intValue()) ||
        Objects.equals(position, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS.intValue());
  }

  private void verificarImpugnadosOilegilibles(VerificationVoteRevocatoriaItem item, DetActaOpcion opcion, Acta cabActa) {
    boolean esImpugnado = item.getPosition().longValue() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS;

    if (esImpugnado && opcion.getVotos() != null && opcion.getVotos() > 0) {
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
    }

    if (esImpugnado && ConstantesComunes.C_VALUE_ILEGIBLE.equals(opcion.getIlegible())) {
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
    }
  }



  private void setearVotosSegundaVerificacionConvencionalyStaeContingencia(
      Acta cabActa, DetActa detActa, String votosUser, VerificationVoteItem item) {

    String votosManualVerificador = obtenerVotosManualVerificador(detActa);
    String votoAutomatico = obtenerVotoAutomatico(detActa);

    boolean esCoincidente = Objects.equals(votosManualVerificador, votosUser) ||
        Objects.equals(votoAutomatico, votosUser);

    if (esCoincidente) {
      procesarCoincidencia(detActa, cabActa, votosUser);
      if (esImpugnado(item, detActa)) {
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
      }
      return;
    }

    cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
    procesarNoCoincidencia(detActa, cabActa, votosUser);
  }

  private String obtenerVotosManualVerificador(DetActa detActa) {
    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActa.getIlegiblev1())) {
      return ConstantesComunes.C_VALUE_ILEGIBLE;
    }
    return detActa.getVotosManual1() != null ? detActa.getVotosManual1().toString() : null;
  }

  private String obtenerVotoAutomatico(DetActa detActa) {
    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActa.getIlegibleAutomatico())) {
      return ConstantesComunes.C_VALUE_ILEGIBLE;
    }
    return detActa.getVotosAutomatico() != null ? detActa.getVotosAutomatico().toString() : null;
  }

  private void procesarCoincidencia(DetActa detActa, Acta cabActa, String votosUser) {
    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votosUser)) {
      detActa.setVotos(null);
      detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActa.setVotosManual2(null);
      detActa.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
    } else {
      detActa.setVotos(Long.valueOf(votosUser));
      detActa.setVotosManual2(Long.valueOf(votosUser));
      detActa.setIlegible(null);
      detActa.setIlegiblev2(null);
    }
  }

  private void procesarNoCoincidencia(DetActa detActa, Acta cabActa, String votosUser) {
    detActa.setVotos(null);
    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votosUser)) {
      detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActa.setVotosManual2(null);
      detActa.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
    } else {
      detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActa.setVotosManual2(Long.valueOf(votosUser));
      detActa.setIlegiblev2(null);
    }
    agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
  }

  private boolean esImpugnado(VerificationVoteItem item, DetActa detActa) {
    return item.getPosition().longValue() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS &&
        detActa.getVotos() != null && detActa.getVotos() > 0;
  }




  private void guardarVeriConvencionalEstadoFinalActa(String estadoActaActual, Acta cabActa) {
    //actualizacion final del acta
    if (estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      guardarVeriConvencionalEstadoFinalActa1era(cabActa);
    } else {
      guardarVeriConvencionalEstadoFinalActa2da(cabActa);
    }
  }

  private void guardarVeriConvencionalEstadoFinalActa1era(Acta cabActa) {
    if (cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR)) {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);
    } else {
      if (cabActa.getEstadoActaResolucion() == null || cabActa.getEstadoActaResolucion().isEmpty()) {
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA);
        cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
      } else {
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
        cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
      }
    }
  }

  private void guardarVeriConvencionalEstadoFinalActa2da(Acta cabActa) {
    if (cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
      cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);
    } else {
      if (cabActa.getEstadoActaResolucion() == null || cabActa.getEstadoActaResolucion().isEmpty()) {
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA);
        cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
      } else {
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
        cabActa.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
      }
    }
  }



  private void guardarVeriConvencionalEstadoResolucionErrorMaterialRevocatoria(Acta acta, List<DetActa> detActaList, List<DetActaOpcion> detActaOpcions) {


    if (acta.getEstadoErrorMaterial() != null) {
      agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
    }

    String hashSetErroresMaterialesDetActaTotal = detActaList.stream()
        .map(DetActa::getEstadoErrorMaterial)
        .filter(estado -> estado != null && !estado.isEmpty())
        .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
        .filter(s -> !s.isEmpty())
        .distinct()
        .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

    if(!hashSetErroresMaterialesDetActaTotal.isEmpty()){
      agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
    }

    String hashSetErroresMaterialesDetActaOpcionTotal = detActaOpcions.stream()
        .map(DetActaOpcion::getEstadoErrorMaterial)
        .filter(estado -> estado != null && !estado.isEmpty())
        .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
        .filter(s -> !s.isEmpty())
        .distinct()
        .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

    if(hashSetErroresMaterialesDetActaOpcionTotal.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I)){
      removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
    }


  }





  private void setearVotosPrimeraVerificacionConvencionalyStaeContingencia(Acta cabActa, DetActa detActa,
                                                                           String votosSysmtem, String votosUser, VerificationVoteItem item,
                                                                           String dtoOrdenVerificacion) {

    // Skip comparaciones: properties=CASO_2 y dto=CASO_1 (front primera vez)
    boolean skipComparaciones = ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)
        && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dtoOrdenVerificacion);

    detActa.setVotosAutomatico(null);
    if (votosSysmtem != null) {
      if (votosSysmtem.isEmpty()) {
        detActa.setVotosAutomatico(ConstantesComunes.NVALUE_ZERO);
      } else if (votosSysmtem.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        detActa.setVotosAutomatico(null);
        detActa.setIlegibleAutomatico(ConstantesComunes.C_VALUE_ILEGIBLE);
      } else {
        try {
          Long numero = Long.parseLong(votosSysmtem);
          detActa.setVotosAutomatico(numero);
        } catch (Exception excepcion) {
          detActa.setVotosAutomatico(null);
          detActa.setIlegibleAutomatico(ConstantesComunes.C_VALUE_ILEGIBLE);
        }
      }
    }

    if (votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      if (skipComparaciones) {
        // Solo guardar manual1 como ilegible, sin comparar
        detActa.setVotosManual1(null);
        detActa.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);
      } else {
        handleIlegibleVotos(cabActa, detActa);
      }
    } else {
      detActa.setVotosManual1(votosUser.equals(ConstantesComunes.VACIO) ? ConstantesComunes.NVALUE_ZERO : Long.valueOf(votosUser));
      detActa.setIlegiblev1(null);
      if (skipComparaciones) {
        // Solo guardar manual1, sin comparar con automatico
        detActa.setVotos(null);
        detActa.setIlegible(null);
      } else {
        handleVotosIguales(cabActa, detActa);
        if (item.getPosition().longValue() == ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS && detActa.getVotos() != null
            && detActa.getVotos() > 0) {
          agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
        }
      }
    }
  }

  private void handleIlegibleVotos(Acta cabActa, DetActa detActa) {

    if (detActa.getVotosAutomatico() == null) {//quiere decir modelo ilegible
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
      detActa.setVotosManual1(null);
      detActa.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActa.setVotos(null);
      detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
    } else {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
      detActa.setVotos(null);
      detActa.setIlegible(null);
      detActa.setVotosManual1(null);
      detActa.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);
    }
  }

  private void handleVotosIguales(Acta cabActa, DetActa detActa) {
    if (Objects.equals(detActa.getVotosAutomatico(), detActa.getVotosManual1())) {
      detActa.setVotos(detActa.getVotosManual1());
      detActa.setIlegible(null);
    } else {
      detActa.setVotos(null);
      detActa.setIlegible(null);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
    }
  }

  private long getTotalVotosDeLasAgrupacionPoliticas(VerificationVoteSectionDTO voteSection) {
    long total = 0;
    if (voteSection != null && voteSection.getItems() != null) {
      for (VerificationVoteItem item : voteSection.getItems()) {
        if (item.getPosition() == null || item.getPosition() == 0) {
          continue;
        }
        String votosUser = item.getUserValue();
        votosUser = SceUtils.removeZerosLeft(votosUser);

        if (votosUser == null || votosUser.equals(ConstantesComunes.VACIO) || votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
          votosUser = ConstantesComunes.CVALUE_ZERO;
        }
        total += Long.parseLong(votosUser);
      }
    }
    return total;
  }


  private long getTotalVotosCalculadosRevocatoria(VerificationVoteSectionDTO voteSection, Long cvas) {
    if (voteSection == null || voteSection.getItems() == null) {
      return 0;
    }

    List<Long> listaVotosCalculados = voteSection.getItems().stream()
        .filter(item -> item.getPosition() != null && item.getPosition() != 0)
        .map(this::calcularTotalVotosItemRevocatoria)
        .toList();

    if (listaVotosCalculados.isEmpty()) {
      return 0;
    }

    if (listaVotosCalculados.stream().distinct().count() > 1) {
      return (cvas == null) ? 0 : cvas;
    }

    return listaVotosCalculados.get(0);
  }

  private long calcularTotalVotosItemRevocatoria(VerificationVoteItem item) {
    return item.getVotoRevocatoria().stream()
        .map(VerificationVoteRevocatoriaItem::getUserValue)
        .map(SceUtils::removeZerosLeft)
        .map(v -> (v == null || v.isEmpty() || v.equals(ConstantesComunes.C_VALUE_ILEGIBLE))
            ? ConstantesComunes.CVALUE_ZERO
            : v)
        .mapToLong(Long::parseLong)
        .sum();
  }



  private void setearEstadoResolucionHoraSection(VerificationActaDTO request, Acta cabActa) {

    if (request.getDateSectionResponse() != null) {
      if (request.getDateSectionResponse().getStart() != null) {
        cabActa.setHoraInstalacionAutomatico(request.getDateSectionResponse().getStart().getSystemValue());
        cabActa.setHoraInstalacionManual(request.getDateSectionResponse().getStart().getUserValue());
      }
      if (request.getDateSectionResponse().getEnd() != null) {
        cabActa.setHoraEscrutinioAutomatico(request.getDateSectionResponse().getEnd().getSystemValue());
        cabActa.setHoraEscrutinioManual(request.getDateSectionResponse().getEnd().getUserValue());
      }
    }
  }

  private void setearEstadoResolucionDateSection(VerificationActaDTO request, Acta cabActa, String estadoActaActual, boolean skipComparaciones) {
    if (request.getDateSectionResponse() == null || request.getDateSectionResponse().getTotal() == null) {
      return;
    }
    String cvasSysmtem = request.getDateSectionResponse().getTotal().getNumberSystemValue();
    String cvasUser = request.getDateSectionResponse().getTotal().getNumberUserValue();

    if (!estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      setearEstadoResolucionDateSectionPrimeraVerificacion(cvasSysmtem, cvasUser, cabActa, skipComparaciones);
    } else {
      setearEstadoResolucionDateSectionSegundaVerificacion(cvasUser, cabActa);
    }
  }

  private void setearEstadoResolucionDateSectionPrimeraVerificacion(String cvasSysmtem, String cvasUser, Acta cabActa, boolean skipComparaciones) {

    if (cvasSysmtem != null) {
      try {
        cabActa.setCvasAutomatico(cvasSysmtem.isEmpty() ? ConstantesComunes.NVALUE_NULL : Long.valueOf(cvasSysmtem));
      } catch (Exception e) {
        cabActa.setCvasAutomatico(ConstantesComunes.NVALUE_NULL);
      }
    } else {
      cabActa.setCvasAutomatico(ConstantesComunes.NVALUE_NULL);
    }
    if (cvasUser.equals(ConstantesComunes.VACIO)) {
      cvasUser = ConstantesComunes.VALUE_CVAS_INCOMPLETA;
    }

    if (cvasUser.equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA)) {

      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
      cabActa.setCvasV1(null);
      cabActa.setIlegibleCvasV1(null);

    } else if (cvasUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS);
      cabActa.setCvasV1(null);
      cabActa.setIlegibleCvasV1(ConstantesComunes.C_VALUE_ILEGIBLE);

    } else {
      cabActa.setCvasV1(Long.valueOf(cvasUser));
      cabActa.setIlegibleCvasV1(null);
    }

    if (!skipComparaciones) {
      setearEstadoResolucionDateSectionPrimeraVerificacionComparacion(cabActa);
    }
  }

  private void setearEstadoResolucionDateSectionPrimeraVerificacionComparacion(Acta cabActa) {
    if (Objects.equals(cabActa.getCvasV1(), cabActa.getCvasAutomatico())) {
      if (cabActa.getCvasV1() == null) {
        if (cabActa.getIlegibleCvasV1() == null) {
          cabActa.setCvas(ConstantesComunes.NVALUE_NULL);
          cabActa.setIlegibleCvas(ConstantesComunes.CVALUE_NULL);
        } else {
          cabActa.setCvas(ConstantesComunes.NVALUE_NULL);
          cabActa.setIlegibleCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
        }
      } else {
        cabActa.setCvas(cabActa.getCvasV1());
        cabActa.setIlegibleCvas(cabActa.getIlegibleCvasV1());
      }
    } else {
      cabActa.setCvas(null);
      cabActa.setIlegibleCvas(null);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
    }
  }

  private void setearEstadoResolucionDateSectionSegundaVerificacion(String cvasUser, Acta cabActa) {

    //2DO VERIFICACION
    String cvasManual1erVerificador = null;

    if (cvasUser.equals(ConstantesComunes.VACIO)) {
      cvasUser = ConstantesComunes.VALUE_CVAS_INCOMPLETA;
    }

    if (cabActa.getIlegibleCvasV1() != null && cabActa.getIlegibleCvasV1().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      cvasManual1erVerificador = ConstantesComunes.C_VALUE_ILEGIBLE;
    }

    if (cabActa.getCvasV1() == null && cabActa.getIlegibleCvasV1() == null) {
      cvasManual1erVerificador = ConstantesComunes.VALUE_CVAS_INCOMPLETA;
    }

    if (cabActa.getCvasV1() != null) {
      cvasManual1erVerificador = cabActa.getCvasV1() + "";
    }

    setearEstadoResolucionDateSectionSegundaVerificacionComparacion(cvasManual1erVerificador, cvasUser, cabActa);

  }

  private void setearEstadoResolucionDateSectionSegundaVerificacionComparacion(String cvasManual1erVerificador, String cvasUser, Acta cabActa) {

    String cvasAutomatico = null;

    if (cabActa.getCvasAutomatico() != null) {
      cvasAutomatico = cabActa.getCvasAutomatico() + "";
    }

    if (Objects.equals(cvasManual1erVerificador, cvasUser) || Objects.equals(cvasAutomatico, cvasUser)) {

      if (Objects.equals(cvasUser, ConstantesComunes.VALUE_CVAS_INCOMPLETA)) {
        cabActa.setCvas(null);
        cabActa.setIlegibleCvas(null);
        cabActa.setCvasV2(null);
        cabActa.setIlegibleCvasV2(null);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
      } else if (cvasUser !=null && cvasUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        cabActa.setCvas(null);
        cabActa.setCvasV2(null);
        cabActa.setIlegibleCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
        cabActa.setIlegibleCvasV2(ConstantesComunes.C_VALUE_ILEGIBLE);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS);
      } else {
        cabActa.setCvas(Long.parseLong(cvasUser));
        cabActa.setCvasV2(Long.parseLong(cvasUser));
        cabActa.setIlegibleCvas(null);
        cabActa.setIlegibleCvasV2(null);
      }
    } else {
      //SINO COINCIDEN MARCAN COMO INCOMPLETA
      cabActa.setCvas(null);
      cabActa.setIlegibleCvas(null);

      if (cvasUser.equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA)) {
        cabActa.setCvasV2(null);
        cabActa.setIlegibleCvasV2(null);
      } else if (cvasUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        cabActa.setCvasV2(null);
        cabActa.setIlegibleCvasV2(ConstantesComunes.C_VALUE_ILEGIBLE);
      } else {
        cabActa.setCvasV2(Long.parseLong(cvasUser));
        cabActa.setIlegibleCvasV2(null);
      }

      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);

      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
    }
  }

  private void setearEstadoResolucionObservaciones(VerificationActaDTO request, Acta cabActa, String estadoActaActual) {
    //OBSERVACIONES
    if (request.getObservationSection() == null) {
      return;
    }

    setearEstadoResolucionObservacionesSolicitudNulidad(request, cabActa, estadoActaActual);

    //CHECK SIN DATOS
    setearEstadoResolucionObservacionesSinDatos(request, cabActa, estadoActaActual);

  }

  private void setearEstadoResolucionObservacionesSinDatos(VerificationActaDTO request, Acta cabActa, String estadoActaActual) {
    if (request.getObservationSection().getNoData() == null) {
      return;
    }

    if (!estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      //PRIMERA VERIFICACION
      if (Boolean.TRUE.equals(request.getObservationSection().getNoData())) {//SI REALIZO EL CHECK
        cabActa.setDigitacionSinDatosManual(ConstantesComunes.NVALUE_UNO);
        cabActa.setDigitacionSinDatosManualV1(ConstantesComunes.NVALUE_UNO);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
      } else {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
        cabActa.setDigitacionSinDatosManual(ConstantesComunes.NVALUE_ZERO);
        cabActa.setDigitacionSinDatosManualV1(ConstantesComunes.NVALUE_ZERO);
      }
    } else {
      //SEGUNDA VERIFICACION
      if (Boolean.TRUE.equals(request.getObservationSection().getNoData())) {//SI REALIZO EL CHECK
        cabActa.setDigitacionSinDatosManual(ConstantesComunes.NVALUE_UNO);
        cabActa.setDigitacionSinDatosManualV2(ConstantesComunes.NVALUE_UNO);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
      } else {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
        cabActa.setDigitacionSinDatosManual(ConstantesComunes.NVALUE_ZERO);
        cabActa.setDigitacionSinDatosManualV2(ConstantesComunes.NVALUE_ZERO);
      }

      if (Objects.equals(cabActa.getDigitacionSinDatosManualV1(), cabActa.getDigitacionSinDatosManualV2())) {
        cabActa.setDigitacionSinDatosManual(cabActa.getDigitacionSinDatosManualV2());
      } else {
        cabActa.setDigitacionSinDatosManual(null);
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      }
    }

  }

  private void setearEstadoResolucionObservacionesSolicitudNulidad(VerificationActaDTO request, Acta cabActa, String estadoActaActual) {
    //CHECK SOLICITUD DE NULIDAD
    if (request.getObservationSection().getNullityRequest() == null) {
      return;
    }

    if (!estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      //PRIMERA VERIFICACION
      if (Boolean.TRUE.equals(request.getObservationSection().getNullityRequest())) {//SI REALIZO EL CHECK
        cabActa.setDigitacionSolicitudNulidadManual(ConstantesComunes.NVALUE_UNO);
        cabActa.setDigitacionSolicitudNulidadManualV1(ConstantesComunes.NVALUE_UNO);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
      } else {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
        cabActa.setDigitacionSolicitudNulidadManual(ConstantesComunes.NVALUE_ZERO);
        cabActa.setDigitacionSolicitudNulidadManualV1(ConstantesComunes.NVALUE_ZERO);
      }
    } else {
      //SEGUNDA VERIFICACION
      if (Boolean.TRUE.equals(request.getObservationSection().getNullityRequest())) {//SI REALIZO EL CHECK
        cabActa.setDigitacionSolicitudNulidadManual(ConstantesComunes.NVALUE_UNO);
        cabActa.setDigitacionSolicitudNulidadManualV2(ConstantesComunes.NVALUE_UNO);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
      } else {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
        cabActa.setDigitacionSolicitudNulidadManual(ConstantesComunes.NVALUE_ZERO);
        cabActa.setDigitacionSolicitudNulidadManualV2(ConstantesComunes.NVALUE_ZERO);
      }

      if (Objects.equals(cabActa.getDigitacionSolicitudNulidadManualV1(), cabActa.getDigitacionSolicitudNulidadManualV2())) {
        cabActa.setDigitacionSolicitudNulidadManual(cabActa.getDigitacionSolicitudNulidadManualV2());
      } else {
        cabActa.setDigitacionSolicitudNulidadManual(null);
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      }
    }
  }

  private void setearFirmasEscrutinio(VerificationActaDTO request, Acta cabActa) {
    // Presidente
    if (request.getSignSection() != null &&
        request.getSignSection().getCountPresident() != null &&
        request.getSignSection().getCountPresident().getSystemStatus() != null) {
      cabActa.setEscrutinioFirmaMm1Automatico(request.getSignSection().getCountPresident().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Secretaria
    if (request.getSignSection() != null &&
        request.getSignSection().getCountSecretary() != null &&
        request.getSignSection().getCountSecretary().getSystemStatus() != null) {
      cabActa.setEscrutinioFirmaMm2Automatico(request.getSignSection().getCountSecretary().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Tercer miembro
    if (request.getSignSection() != null &&
        request.getSignSection().getCountThirdMember() != null &&
        request.getSignSection().getCountThirdMember().getSystemStatus() != null) {
      cabActa.setEscrutinioFirmaMm3Automatico(request.getSignSection().getCountThirdMember().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }
  }

  private void setearFirmasInstalacion(VerificationActaDTO request, Acta cabActa) {
    // Presidente
    if (request.getSignSection() != null &&
        request.getSignSection().getInstallPresident() != null &&
        request.getSignSection().getInstallPresident().getSystemStatus() != null) {
      cabActa.setInstalacionFirmaMm1Automatico(request.getSignSection().getInstallPresident().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Secretaria
    if (request.getSignSection() != null &&
        request.getSignSection().getInstallSecretary() != null &&
        request.getSignSection().getInstallSecretary().getSystemStatus() != null) {
      cabActa.setInstalacionFirmaMm2Automatico(request.getSignSection().getInstallSecretary().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Tercer miembro
    if (request.getSignSection() != null &&
        request.getSignSection().getInstallThirdMember() != null &&
        request.getSignSection().getInstallThirdMember().getSystemStatus() != null) {
      cabActa.setInstalacionFirmaMm3Automatico(request.getSignSection().getInstallThirdMember().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }
  }

  private void setearFirmasSugrafio(VerificationActaDTO request, Acta cabActa) {
    // Presidente
    if (request.getSignSection() != null &&
        request.getSignSection().getVotePresident() != null &&
        request.getSignSection().getVotePresident().getSystemStatus() != null) {
      cabActa.setSufragioFirmaMm1Automatico(request.getSignSection().getVotePresident().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Secretaria
    if (request.getSignSection() != null &&
        request.getSignSection().getVoteSecretary() != null &&
        request.getSignSection().getVoteSecretary().getSystemStatus() != null) {
      cabActa.setSufragioFirmaMm2Automatico(request.getSignSection().getVoteSecretary().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }

    // Tercer miembro
    if (request.getSignSection() != null &&
        request.getSignSection().getVoteThirdMember() != null &&
        request.getSignSection().getVoteThirdMember().getSystemStatus() != null) {
      cabActa.setSufragioFirmaMm3Automatico(request.getSignSection().getVoteThirdMember().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }
  }

  private void setearFirmasAutomatico(VerificationActaDTO request, Acta cabActa, String estadoActaActual) {
    //FIRMAS
    if (request.getSignSection() != null && request.getSignSection().getSystemStatus() != null && !estadoActaActual.equals(
        ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      cabActa.setDigitacionFirmasAutomatico(request.getSignSection().getSystemStatus().equals(ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    }
  }

  private void setearEstadoResolucionFirmas(VerificationActaDTO request, Acta cabActa, String estadoActaActual, boolean skipComparaciones) {
    if (request.getSignSection() == null) {
      return;
    }
    if (skipComparaciones) {
      // Solo guardar valores del usuario, sin comparar
      if (request.getSignSection().getUserStatus() != null) {
        cabActa.setDigitacionFirmasManualV1(Objects.equals(request.getSignSection().getUserStatus(), ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
      } else {
        cabActa.setDigitacionFirmasManualV1(cabActa.getDigitacionFirmasAutomatico());
      }
      return;
    }
    if (request.getSignSection().getUserStatus() != null) {
      setearEstadoResolucionFirmasUsuarioSelecciono(request, cabActa, estadoActaActual);
    } else {
      setearEstadoResolucionFirmasUsuarioNoSelecciono(cabActa, estadoActaActual);
    }
  }

  private void setearEstadoResolucionFirmasUsuarioNoSelecciono(Acta cabActa, String estadoActaActual) {
    // EL USUARIO NO HA DECIDIDO NADA - PRIMERA VERIFICACION
    if (!estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      cabActa.setDigitacionFirmasManual(cabActa.getDigitacionFirmasAutomatico());
      cabActa.setDigitacionFirmasManualV1(cabActa.getDigitacionFirmasAutomatico());
      if (cabActa.getDigitacionFirmasAutomatico() == 0L) {
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      }
    } else {//SEGUNDA VERIFICACION
      cabActa.setDigitacionFirmasManual(cabActa.getDigitacionFirmasAutomatico());
      cabActa.setDigitacionFirmasManualV2(cabActa.getDigitacionFirmasAutomatico());
      if (Objects.equals(cabActa.getDigitacionFirmasManualV1(), cabActa.getDigitacionFirmasManualV2())||
          Objects.equals(cabActa.getDigitacionFirmasAutomatico(), cabActa.getDigitacionFirmasManualV2())) {
        //SI AMBOS COINCIDEN
        if (cabActa.getDigitacionFirmasManualV2() == 1L) {
          removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
        } else if (cabActa.getDigitacionFirmasManualV2() == 0L) {
          agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
        }
      } else {
        //SINO COINCIDEN DEBE QUEDAR SIN FIRMAS
        cabActa.setDigitacionFirmasManual(0L);
        cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      }
    }
  }

  private void setearEstadoResolucionFirmasUsuarioSelecciono(VerificationActaDTO request, Acta cabActa, String estadoActaActual) {
    //PRIMERA VERIFICACION
    if (!isSegundaVerificacion(estadoActaActual)) {
      setearEstadoResolucionFirmasUsuarioSelecciono1erVerificacion(request, cabActa);
    } else {
      setearEstadoResolucionFirmasUsuarioSelecciono2daVerificacion(request, cabActa);
    }
  }

  private boolean isSegundaVerificacion(String estadoActaActual) {
    return estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
  }

  private void setearEstadoResolucionFirmasUsuarioSelecciono2daVerificacion(VerificationActaDTO request, Acta cabActa) {
    //2DO VERIFICADOR
    Long digitacionFirmasManual1erVerificador = cabActa.getDigitacionFirmasManualV1();
    Long digitacionFirmasManual2doVerificador = Objects.equals(request.getSignSection().getUserStatus(), ConstantesComunes.TEXT_TRUE) ? 1L : 0L;
    cabActa.setDigitacionFirmasManualV2(digitacionFirmasManual2doVerificador);

    if (Objects.equals(digitacionFirmasManual1erVerificador, digitacionFirmasManual2doVerificador) ||
        Objects.equals(cabActa.getDigitacionFirmasAutomatico()==null?0:cabActa.getDigitacionFirmasAutomatico(), digitacionFirmasManual2doVerificador)) {
      //SI AMBOS COINCIDEN
      if (digitacionFirmasManual2doVerificador == 1L) {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      } else {
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      }

    } else {
      //SINO COINCIDEN DEBE QUEDAR SIN FIRMAS
      cabActa.setDigitacionFirmasManual(0L);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
    }
  }

  private void setearEstadoResolucionFirmasUsuarioSelecciono1erVerificacion(VerificationActaDTO request, Acta cabActa) {
    cabActa.setDigitacionFirmasManualV1(Objects.equals(request.getSignSection().getUserStatus(), ConstantesComunes.TEXT_TRUE) ? 1L : 0L);
    if (Objects.equals(cabActa.getDigitacionFirmasAutomatico(), cabActa.getDigitacionFirmasManualV1())) {
      cabActa.setDigitacionFirmasManual(cabActa.getDigitacionFirmasManualV1());
      if (cabActa.getDigitacionFirmasManualV1() == 1L) {
        removerEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      } else if (cabActa.getDigitacionFirmasManualV1() == 0L) {
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
      }
    } else {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
      cabActa.setDigitacionFirmasManual(null);
    }
  }

  private void removerEstadoResolucion(Acta cabActa, String estadoAEliminar) {
    if (estadoAEliminar == null || estadoAEliminar.isEmpty()) return;

    if (cabActa.getEstadoActaResolucion() != null && cabActa.getEstadoActaResolucion().contains(estadoAEliminar)) {
      if(cabActa.getEstadoActaResolucion().length()==1){
        cabActa.setEstadoActaResolucion(null);
      }else{
        if(cabActa.getEstadoActaResolucion().startsWith(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES))
          cabActa.setEstadoActaResolucion(cabActa.getEstadoActaResolucion().replace(estadoAEliminar+ConstantesComunes.SEPARADOR_ERRORES, ConstantesComunes.VACIO));
        else
          cabActa.setEstadoActaResolucion(cabActa.getEstadoActaResolucion().replace(ConstantesComunes.SEPARADOR_ERRORES+estadoAEliminar, ConstantesComunes.VACIO));
      }
    }
  }

  private void agregarEstadoResolucion(Acta cabActa, String estadoResolucion) {
    if (cabActa.getEstadoActaResolucion() == null || cabActa.getEstadoActaResolucion().isEmpty()) {
      cabActa.setEstadoActaResolucion(estadoResolucion);
    } else if (!cabActa.getEstadoActaResolucion().contains(estadoResolucion)) {
      cabActa.setEstadoActaResolucion(cabActa.getEstadoActaResolucion().concat( ConstantesComunes.SEPARADOR_ERRORES + estadoResolucion));
    }
  }

  private void procesarPrimeraVerificacionVotosPreferenciales(Acta cabActa, DetActaPreferencial detActaPreferencial, String votosSysmtem,
                                                              String votosUser, String estadoActaActual,
                                                              String dtoOrdenVerificacion) {

    if (estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      return;
    }

    boolean skipComparaciones = ConstantesComunes.COMPARAR_DIGITACION_CASO_2.equals(propOrdenVerificacionActas)
        && ConstantesComunes.COMPARAR_DIGITACION_CASO_1.equals(dtoOrdenVerificacion);

    if (votosSysmtem != null) {
      if (votosSysmtem.isEmpty()) {
        detActaPreferencial.setVotosAutomatico(ConstantesComunes.NVALUE_ZERO);
        detActaPreferencial.setIlegibleAutomatico(null);
      } else {
        try {
          Long numero = Long.parseLong(votosSysmtem);
          detActaPreferencial.setVotosAutomatico(numero);
          detActaPreferencial.setIlegibleAutomatico(null);
        } catch (Exception excepcion) {
          detActaPreferencial.setVotosAutomatico(null);
          detActaPreferencial.setIlegibleAutomatico(ConstantesComunes.C_VALUE_ILEGIBLE);
        }
      }
    } else {
      detActaPreferencial.setVotosAutomatico(null);
      detActaPreferencial.setIlegibleAutomatico(ConstantesComunes.C_VALUE_ILEGIBLE);
    }

    if (votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      if (skipComparaciones) {
        // Solo guardar manual1 como ilegible, sin comparar
        detActaPreferencial.setVotosManual1(null);
        detActaPreferencial.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);
      } else {
        setearIlegibleVotosPreferencial(cabActa, detActaPreferencial);
      }
    } else {
      if (skipComparaciones) {
        // Solo guardar manual1, sin comparar
        detActaPreferencial.setVotosManual1(Long.valueOf(votosUser));
        detActaPreferencial.setIlegiblev1(null);
        detActaPreferencial.setVotos(null);
        detActaPreferencial.setIlegible(null);
      } else {
        setearVotosPreferenciales(cabActa, detActaPreferencial, votosUser);
      }
    }
  }


  private void procesarPrimeraVerificacionVotosOpciones(Acta cabActa, DetActaOpcion detActaOpcion, String votosSysmtem,
                                                              String votosUser) {

    if (votosSysmtem != null) {
      if (votosSysmtem.isEmpty()) {
        detActaOpcion.setVotosAutomatico(ConstantesComunes.NVALUE_ZERO);
      } else {
        try {
          Long numero = Long.parseLong(votosSysmtem);
          detActaOpcion.setVotosAutomatico(numero);
        } catch (Exception excepcion) {
          detActaOpcion.setVotosAutomatico(null);
        }
      }
    } else {
      detActaOpcion.setVotosAutomatico(null);
    }

    if (votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      setearIlegibleVotosOpciones(cabActa, detActaOpcion);
    } else {
      setearVotosOpciones(cabActa, detActaOpcion, votosUser);
    }
  }

  private void procesarSegundaVerificacionVotosPreferenciales(Acta cabActa, DetActaPreferencial detActaPreferencial, String votosUser,
      String estadoActaActual) {

    if (!estadoActaActual.equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION)) {
      return;
    }

    String votosManualVerificador = null;

    if (detActaPreferencial.getIlegiblev1() != null && detActaPreferencial.getIlegiblev1().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      votosManualVerificador = ConstantesComunes.C_VALUE_ILEGIBLE;
    }

    if (detActaPreferencial.getVotosManual1() != null) {
      votosManualVerificador = detActaPreferencial.getVotosManual1() + "";
    }

    String votoAutomatico = null;
    if(detActaPreferencial.getVotosAutomatico()!=null)
      votoAutomatico = detActaPreferencial.getVotosAutomatico()+"";

    //las dos combinaciones restantes
    if (Objects.equals(votosManualVerificador, votosUser) || Objects.equals(votoAutomatico, votosUser)) {
      //Si son iguales ambos verificadores
      if (votosUser!=null && votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        detActaPreferencial.setVotos(null);
        detActaPreferencial.setVotosManual2(null);
        detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaPreferencial.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      } else {
        detActaPreferencial.setVotos(Long.valueOf(votosUser));
        detActaPreferencial.setVotosManual2(Long.valueOf(votosUser));
        detActaPreferencial.setIlegible(null);
        detActaPreferencial.setIlegiblev2(null);
      }
    } else {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      if (votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        detActaPreferencial.setVotos(null);
        detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaPreferencial.setVotosManual2(null);
        detActaPreferencial.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      } else {
        //Son diferentes, lo marcamos como ilegible a nivel de detalle
        detActaPreferencial.setVotos(null);
        detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaPreferencial.setVotosManual2(Long.valueOf(votosUser));
        detActaPreferencial.setIlegiblev2(null);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      }
    }
  }

  private void procesarSegundaVerificacionVotosOpciones(Acta cabActa, DetActaOpcion detActaOpcion, String votosUser) {

    String votosManualVerificador = null;

    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActaOpcion.getIlegiblev1())) {
      votosManualVerificador = ConstantesComunes.C_VALUE_ILEGIBLE;
    }else if (detActaOpcion.getVotosManual1() != null) {
      votosManualVerificador = detActaOpcion.getVotosManual1() + "";
    }

    String votoAutomatico = null;

    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActaOpcion.getIlegibleAutomatico())) {
      votoAutomatico = ConstantesComunes.C_VALUE_ILEGIBLE;
    }else if (detActaOpcion.getVotosAutomatico() != null) {
      votoAutomatico = detActaOpcion.getVotosAutomatico() + "";
    }

    //las dos combinaciones restantes
    if (Objects.equals(votosManualVerificador, votosUser) || Objects.equals(votoAutomatico, votosUser)) {
      //Si son iguales ambos verificadores
      if (votosUser!=null && votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        detActaOpcion.setVotos(null);
        detActaOpcion.setVotosManual2(null);
        detActaOpcion.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaOpcion.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      } else {
        detActaOpcion.setVotos(Long.valueOf(votosUser));
        detActaOpcion.setVotosManual2(Long.valueOf(votosUser));
        detActaOpcion.setIlegible(null);
        detActaOpcion.setIlegiblev2(null);
      }
    } else {
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR);
      if (votosUser.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        detActaOpcion.setVotos(null);
        detActaOpcion.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaOpcion.setVotosManual2(null);
        detActaOpcion.setIlegiblev2(ConstantesComunes.C_VALUE_ILEGIBLE);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      } else {
        //Son diferentes, lo marcamos como ilegible a nivel de detalle
        detActaOpcion.setVotos(null);
        detActaOpcion.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
        detActaOpcion.setVotosManual2(Long.valueOf(votosUser));
        detActaOpcion.setIlegiblev2(null);
        agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
      }
    }
  }

  private void setearIlegibleVotosPreferencial(Acta cabActa, DetActaPreferencial detActaPreferencial) {
    // Valores comunes
    detActaPreferencial.setVotos(null);
    detActaPreferencial.setVotosManual1(null);
    detActaPreferencial.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);

    if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActaPreferencial.getIlegibleAutomatico())) {
      detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
    } else {
      detActaPreferencial.setIlegible(null);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
    }

    agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
  }


  private void setearIlegibleVotosOpciones(Acta cabActa, DetActaOpcion detActaOpcion) {
    cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);
    detActaOpcion.setVotos(null);
    detActaOpcion.setIlegible(null);
    detActaOpcion.setVotosManual1(null);
    detActaOpcion.setIlegiblev1(ConstantesComunes.C_VALUE_ILEGIBLE);
    agregarEstadoResolucion(cabActa, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
  }

  private void setearVotosPreferenciales(Acta cabActa, DetActaPreferencial detActaPreferencial, String votosUser) {
    detActaPreferencial.setVotosManual1(Long.valueOf(votosUser));
    detActaPreferencial.setIlegiblev1(null);
    if (Objects.equals(detActaPreferencial.getVotosAutomatico(), detActaPreferencial.getVotosManual1())) {
      detActaPreferencial.setVotos(detActaPreferencial.getVotosManual1());
      detActaPreferencial.setIlegible(null);
    } else {
      detActaPreferencial.setVotos(null);
      detActaPreferencial.setIlegible(null);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);

    }
  }

  private void setearVotosOpciones(Acta cabActa, DetActaOpcion detActaOpcion, String votosUser) {
    detActaOpcion.setVotosManual1(Long.valueOf(votosUser));
    detActaOpcion.setIlegiblev1(null);
    if (Objects.equals(detActaOpcion.getVotosAutomatico(), detActaOpcion.getVotosManual1())) {
      detActaOpcion.setVotos(detActaOpcion.getVotosManual1());
      detActaOpcion.setIlegible(null);
    } else {
      detActaOpcion.setVotos(null);
      detActaOpcion.setIlegible(null);
      cabActa.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION);

    }
  }


  private DetActaOpcion obtenerOActualizarDetActaOpcion(DetActa detActa,
                                                        VerificationVoteRevocatoriaItem itemPre,
                                                        String usuario) {
    Optional<DetActaOpcion> optionalDetActaOpcion =
        this.actaServiceGroup.getDetActaOpcionService().getDetActaOpcionByDetActaAndPosicion(detActa, itemPre.getPosition());
    DetActaOpcion detActaOpcion;

    if (optionalDetActaOpcion.isEmpty()) {
      detActaOpcion = new DetActaOpcion();
      detActaOpcion.setUsuarioCreacion(usuario);
      detActaOpcion.setFechaCreacion(new Date());
    } else {
      detActaOpcion = optionalDetActaOpcion.get();
    }
    detActaOpcion.setDetActa(detActa);
    detActaOpcion.setPosicion(Long.valueOf(itemPre.getPosition())); //hace referencia a la columan SI NO  B N I
    detActaOpcion.setUsuarioModificacion(usuario);
    detActaOpcion.setFechaModificacion(new Date());
    return detActaOpcion;
  }


  private Long obtenerIdActa(Object idActaO) {
    return switch (idActaO) {
      case Long value -> value;
      case Integer valueInt -> valueInt.longValue();
      default -> null;
    };
  }

}
