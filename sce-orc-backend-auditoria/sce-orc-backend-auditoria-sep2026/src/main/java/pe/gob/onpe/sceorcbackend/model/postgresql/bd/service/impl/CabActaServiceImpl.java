package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.firma.service.FirmaActaAprobadaService;
import pe.gob.onpe.sceorcbackend.model.dto.ActaConArchivosNull;
import pe.gob.onpe.sceorcbackend.model.dto.MonitoreoNacionBusquedaDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationApproveMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.DigitizationRejectMesaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationSummaryResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.MonitoreoListActaItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.ReturnMonitoreoActas;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregirListItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaReprocesadaListIItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.AgrupolPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ArchivosActaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.DigitizationListActasItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ItemPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.VotoOpcionPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.VotoPreferencialPorCorregir;
import pe.gob.onpe.sceorcbackend.model.dto.util.ProcesoResult;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DetTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaTransmisionNacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaAccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.VwActaMonitoreo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ActaPorTransmitirDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaPreferencialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetUbigeoEleccionAgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.VwActaMonitoreoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTrazabilidadService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaAccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaOpcionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaRectangleService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.TabAutorizacionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.ActaScanProjection;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar.ErroresActaWrapper;
import pe.gob.onpe.sceorcbackend.model.service.TrazabilidadService;
import pe.gob.onpe.sceorcbackend.utils.ActaDTO;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesMensajes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesMonitoreo;
import pe.gob.onpe.sceorcbackend.utils.ConsultaErroresMateriales;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.ProcessActaUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;
import pe.gob.onpe.sceorcbackend.utils.StringCustomUtils;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.ConstantesEstadosTrazabilidad;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.InfoActa;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.ItemHistory;
import pe.gob.onpe.sceorcbackend.utils.trazabilidad.TrazabilidadDto;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

@Service
public class CabActaServiceImpl implements CabActaService {

  Logger logger = LoggerFactory.getLogger(CabActaServiceImpl.class);
  private static final String STATUS_PENDIENTE = "Pendiente";

  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String schema;
  private final SecureRandom secureRandom = new SecureRandom();
  private final ArchivoService archivoService;
  private final ActaRepository cabActaRepository;
  private final DetActaRepository detActaRepository;
  private final DetActaResolucionRepository detActaResolucionRepository;
  private final DetActaFormatoRepository detActaFormatoRepository;
  private final TabAutorizacionService tabAutorizacionService;
  private final TabResolucionRepository tabResolucionRepository;
  private final DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository;
  private final MesaRepository mesaRepository;
  private final DetActaRectangleService detActaRectangleService;
  private final DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService;
  private final DetActaAccionService detActaAccionService;
  private final ActaTrazabilidadService actaTrazabilidadService;
  private final UsuarioService usuarioService;
  private final DetActaPreferencialRepository detActaPreferencialRepository;
  private final DetActaOpcionService detActaOpcionService;
  private final ITabLogService logService;
  private final UtilSceService utilSceService;
  private final TrazabilidadService trazabilidadService;
  private final OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService;
  private final VwActaMonitoreoRepository vwActaMonitoreoRepository;
  private final FirmaActaAprobadaService  firmaActaAprobadaService;


  public CabActaServiceImpl(ActaRepository cabActaRepository,
      DetActaResolucionRepository detActaResolucionRepository,
     DetActaFormatoRepository detActaFormatoRepository,
                            TabAutorizacionService tabAutorizacionService,
      DetActaRectangleService detActaRectangleService,
      MesaRepository mesaRepository,
      DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService,
      DetActaAccionService detActaAccionService,
      ActaTrazabilidadService actaTrazabilidadService,
      UsuarioService usuarioService,
      DetActaPreferencialRepository detActaPreferencialRepository,
      DetActaRepository detActaRepository,
      DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository, 
      ArchivoService archivoService,TabResolucionRepository tabResolucionRepository,
      DetActaOpcionService detActaOpcionService,
      ITabLogService logService,
      UtilSceService utilSceService, 
      TrazabilidadService trazabilidadService,
      OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService,
      VwActaMonitoreoRepository vwActaMonitoreoRepository,
      FirmaActaAprobadaService  firmaActaAprobadaService) {
    this.cabActaRepository = cabActaRepository;
    this.detActaRepository = detActaRepository;
    this.detUbigeoEleccionAgrupacionPoliticaRepository = detUbigeoEleccionAgrupacionPoliticaRepository;
    this.detActaRectangleService = detActaRectangleService;
    this.tabAutorizacionService=tabAutorizacionService;
    this.mesaRepository = mesaRepository;
    this.detActaResolucionRepository = detActaResolucionRepository;
    this.detActaFormatoRepository = detActaFormatoRepository;
    this.detTipoEleccionDocumentoElectoralService = detTipoEleccionDocumentoElectoralService;
    this.detActaAccionService = detActaAccionService;
    this.actaTrazabilidadService = actaTrazabilidadService;
    this.usuarioService = usuarioService;
    this.detActaPreferencialRepository = detActaPreferencialRepository;
    this.archivoService = archivoService;
    this.tabResolucionRepository = tabResolucionRepository;
    this.detActaOpcionService = detActaOpcionService;
    this.logService = logService;
    this.utilSceService = utilSceService;
    this.trazabilidadService = trazabilidadService;
    this.detalleCatalogoEstructuraService = detalleCatalogoEstructuraService;
    this.vwActaMonitoreoRepository = vwActaMonitoreoRepository;
    this.firmaActaAprobadaService = firmaActaAprobadaService;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Acta> findById(Long id) {
    return this.cabActaRepository.findById(id);
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public Optional<Acta> findByIdForUpdate(Long id) {
    return this.cabActaRepository.findByIdForUpdate(id);
  }

  @Override
  public List<Acta> findByMesaId(Long idMesa) {
    return this.cabActaRepository.findByMesa_IdOrderByIdAsc(idMesa);
  }


  private void guardarDetAccion(Long idActa, String codigoCentroComputo, String usuario, Date fecha, String tiempo, String accion, int orden) {

    DetActaAccion detActaAccion = DetActaAccion.builder()
            .fechaAccion(fecha)
            .usuarioAccion(usuario)
            .codigoCentroComputo(codigoCentroComputo)
            .tiempo(tiempo)
            .activo(1)
            .accion(accion)
            .acta(new Acta(idActa))
            .usuarioCreacion(usuario)
            .orden(orden)
            .build();
    this.detActaAccionService.save(detActaAccion);

  }

  @Override
  public Long countByEstadoDigitalizacionNot(String estadoDigitalizacion) {
    return this.cabActaRepository.countByEstadoDigitalizacionNot(estadoDigitalizacion);
  }

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public Long getActaRandom(String codigoEleccion, String usuario) {

    Optional<Long> optionalRandomActa = this.cabActaRepository.findActaDisponibleConModeloProcesado(
            codigoEleccion,
            List.of(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA),
            ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA, ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION,
            usuario);

    if (optionalRandomActa.isEmpty())
      return null;

    logger.info("Se le asigno al usuario  {}, la siguiente acta {}.", usuario, optionalRandomActa.get());

    return optionalRandomActa.get();

  }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Long getActaProcesamientoManualRandom(String codigoEleccion, String usuario) {

        List<Long> idActasDisponibles = this.cabActaRepository.findActasParaProcesamientoManualDisponibles(codigoEleccion,
                List.of(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA),
                ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA, ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION, usuario);

        if (idActasDisponibles.isEmpty())
            return null;

        return idActasDisponibles.stream()
                .skip(secureRandom.nextInt(idActasDisponibles.size()))
                .findFirst()
                .orElse(null);
    }

  @Override
  @Transactional
  public List<DigitizationListActasItem> listActas(String codigoEleccion, String codigoCentroComputo, String usuario, String status, int offset, int limit) {

    List<DigitizationListActasItem> listActasResponse = new ArrayList<>();

    List<Acta> listActasDigitalizadasAsignadasActual = getActasDigitalizadasAAsignar(usuario, codigoEleccion);

    for (Acta cabActa : listActasDigitalizadasAsignadasActual) {

      if (cabActa.getAsignado() == null || cabActa.getAsignado() == 0) {
        Date fecha = new Date();
        guardarAsignacionActaListActas(cabActa, usuario, fecha);
        guardarDetAccion(cabActa.getId(), codigoCentroComputo, usuario, fecha, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_DIGTAL, 2);
      }
      listActasResponse.add(createDigitizationListActasItem(cabActa));
    }

    return listActasResponse;

  }

  DigitizationListActasItem createDigitizationListActasItem(Acta cabActa) {

    DigitizationListActasItem item = new DigitizationListActasItem();
    item.setActaId(cabActa.getId());
    item.setMesa(cabActa.getMesa().getCodigo());
    item.setUbigeo(cabActa.getUbigeoEleccion().getUbigeo().getCodigo());
    item.setEstado(cabActa.getEstadoDigitalizacion());
    item.setFecha(cabActa.getFechaCreacion());
    item.setDigitalizacionEscrutinio(cabActa.getDigitalizacionEscrutinio());
    item.setObservacionDigtalAe(cabActa.getObservDigEscrutinio());
    item.setDigitalizacionInstalacionSufragio(cabActa.getDigitalizacionInstalacionSufragio());
    item.setObservacionDigtalAis(cabActa.getObservDigInstalacionSufragio());

    applyArchivo(item::setActa1FileId, item::setActa1Status,
            cabActa.getArchivoEscrutinio(),
            cabActa.getDigitalizacionEscrutinio());

    applyArchivo(item::setActa2FileId, item::setActa2Status,
            cabActa.getArchivoInstalacionSufragio(),
            cabActa.getDigitalizacionInstalacionSufragio()) ;

    String mesa = item.getMesa()+ ConstantesComunes.GUION_MEDIO;
    String copia = cabActa.getNumeroCopia() != null ? cabActa.getNumeroCopia() : "";
    item.setNroActaEscrutinio(mesa + copia + (cabActa.getDigitoChequeoEscrutinio() != null ? cabActa.getDigitoChequeoEscrutinio() : ""));
    item.setNroActaInstalacionSufragio(mesa + copia + (cabActa.getDigitoChequeoInstalacion() != null ? cabActa.getDigitoChequeoInstalacion() : ""));

    boolean esConsulado = this.detActaAccionService.existsByActaAccionTiempoAndUsuarioPrefix(
            cabActa.getId(),
            ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA,
            ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
            ConstantesComunes.USUARIO_CONSULADO);
    item.setEsActaDeConsulado(esConsulado ? "SI" : "NO");

    return item;

  }

  private void applyArchivo(LongConsumer setFileId, Consumer<String> setStatus,
                            Archivo archivo, Long digitalizacion) {
    if (archivo != null) {
      setFileId.accept(archivo.getId());
      setStatus.accept(this.mapNDigitalizacion(digitalizacion));
    } else {
      setStatus.accept(STATUS_PENDIENTE);
    }
  }


  private String mapNDigitalizacion(Long n) {
    if (n == null) {
      return ConstantesComunes.TEXT_DIGITALIZACION_REDIGITALIZAR;
    }

    if (n.equals(ConstantesComunes.N_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE)) {
      return ConstantesComunes.TEXT_DIGITALIZACION_VALIDADO_AUTOMATICAMENTE;
    }

    if (n.equals(ConstantesComunes.N_DIGITALIZACION_PROCESAMIENTO_MANUAL)) {
      return ConstantesComunes.TEXT_DIGITALIZACION_PROCESAMIENTO_MANUAL;
    }

    if (n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR)
            || n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR_2)
            || n.equals(ConstantesComunes.N_DIGITALIZACION_REDIGITALIZAR_4)) {
      return ConstantesComunes.TEXT_DIGITALIZACION_REDIGITALIZAR;
    }

    return n.toString();
  }

  @Override
  public DigitizationSummaryResponse summary(String codigoEleccion) {

    DigitizationSummaryResponse ans = new DigitizationSummaryResponse();

    List<String> estadosDigtalRechazados = List.of(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA,
            ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);

    List<String> estadosDigtalAprabadas = List.of (
            ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA,
            ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);

    Object[] outerResult = this.cabActaRepository.getDigitalizationSummary (
            codigoEleccion,
            ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA,                    // "D"
            estadosDigtalAprabadas, // Lista de aprobadas
            estadosDigtalRechazados,                                                                   // Lista de rechazadas
            ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,        // "P"
            ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA,                       // "S"
            ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA,                        // "O"
            ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA,                      // "N"
            ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA,             // "S"
            Arrays.asList(ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA,                       // "S"
                    ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA,                        // "O"
                    ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA)                                        // Estados a excluir para solo pendientes
    );

    if (outerResult == null || outerResult.length == 0 || !(outerResult[0] instanceof Object[] innerResult)) {
      return new DigitizationSummaryResponse(0, 0, 0,0,0);
    }

    ans.setPending(Objects.isNull(innerResult[0]) ? 0 : ((Long) innerResult[0]).intValue());
    ans.setDigitalizados(Objects.isNull(innerResult[1]) ? 0 : ((Long) innerResult[1]).intValue());
    ans.setApproved(Objects.isNull(innerResult[2]) ? 0 :((Long) innerResult[2]).intValue());
    ans.setRejected(Objects.isNull(innerResult[3]) ? 0 : ((Long) innerResult[3]).intValue());
    ans.setNoInstaladosExtSin(Objects.isNull(innerResult[4]) ? 0 : ((Long) innerResult[4]).intValue());
    return ans;
  }

  @Override
  public GenericResponse<String> liberarActaControlDigitalizacion(Long actaIdLo, String usuario) {

    GenericResponse<String> genericResponse = new GenericResponse<>(false, "");
    Optional<Acta> optionalCabActa = this.cabActaRepository.findById(actaIdLo);
    if (optionalCabActa.isPresent()) {
      Acta acta = optionalCabActa.get();
      if (acta.getEstadoDigitalizacion() != null && acta.getEstadoDigitalizacion()
          .equals(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO)) {
        acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);

        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());
        this.cabActaRepository.save(acta);
        genericResponse.setSuccess(true);
        genericResponse.setMessage("El acta fue liberada correctamente.");
      } else {
        genericResponse.setSuccess(false);
        genericResponse.setMessage("El acta tiene un estado no permitido.");
      }
    } else {
      genericResponse.setSuccess(false);
      genericResponse.setMessage(String.format("El acta %d no se encuentra registrada en la BD.", actaIdLo));
    }
    return genericResponse;
  }

  @Override
  @Transactional
  public void approveMesa(DigitizationApproveMesaRequest request, String usuario, String proceso, String cc) {

    try{
      Optional<Acta> optionalActa = this.cabActaRepository.findById(request.getActaId());

      if (optionalActa.isEmpty()) {
        throw new BadRequestException("Acta no encontrada");
      }

      Acta acta = optionalActa.get();

      if (Objects.equals(acta.getMesa().getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL)){
        acta.setTipoTransmision(ConstantesComunes.TIPO_HOJA_CONVENCIOANL);
      }

      validaRequestApproveMesa(acta, request);

      Date fecha = new Date();

      actualizarActaApproveMesa(acta, usuario, fecha);

      guardarDetAccion(acta.getId(), cc, usuario, fecha, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN,
              ConstantesComunes.DET_ACTA_ACCION_PROCESO_CONTROL_DIGTAL, 2);

      actualizarActasUsuario(usuario);

      guardarDetAccion(acta.getId(), cc, usuario, fecha, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                ConstantesComunes.DET_ACTA_ACCION_PROCESO_MODELO_PROCESAR, 3);

      //this.firmaActaAprobadaService.firmar(acta, usuario);
      
      this.logService.registrarLog(usuario,
          Thread.currentThread().getStackTrace()[1].getMethodName(),
              String.format("El acta %s fue aprobada en control de digitalización por el usuario %s.",
                      SceUtils.getNumMesaAndCopia(acta), usuario), cc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

    }catch (Exception e){
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      throw e;
    }

  }

  @Override
  public GenericResponse<String> finalizarAtencionControlDigitalizacion(String codigoEleccion, TokenInfo tokenInfo) {

    GenericResponse<String> genericResponse = new GenericResponse<>(false, "");

    List<Acta> listaActasPorUsuarioAsignado = this.cabActaRepository.findByUsuarioAsignado(tokenInfo.getNombreUsuario());

    List<Acta> listaActasBloqueadasPorEleccion = listaActasPorUsuarioAsignado.stream().filter(acta ->
        acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA) && acta.getUbigeoEleccion().getEleccion()
            .getCodigo().equals(codigoEleccion)
    ).toList();

    if (!listaActasBloqueadasPorEleccion.isEmpty()) {
      listaActasBloqueadasPorEleccion.forEach(cabActa -> {
        cabActa.setAsignado(0);
        cabActa.setUsuarioAsignado(null);
        cabActa.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        cabActa.setFechaModificacion(new Date());
      });
      this.cabActaRepository.saveAll(listaActasBloqueadasPorEleccion);
      genericResponse.setSuccess(true);
      genericResponse.setMessage(String.format("Las actas asociadas al usuario %s, fueron liberadas.", tokenInfo.getNombreUsuario()));
    } else {
      genericResponse.setSuccess(false);
      genericResponse.setMessage(String.format("No existen actas asociadas al usuario %s para finalizar su atención.", tokenInfo.getNombreUsuario()));
    }


    this.logService.registrarLog(
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            genericResponse.getMessage(),
            tokenInfo.getCodigoCentroComputo(),
            ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,1);

    return genericResponse;
  }

  @Override
  @Transactional
  public void rejectActa(String codigoEleccion, DigitizationRejectMesaRequest request, TokenInfo tokenInfo) {

    Optional<Acta> optionalCabActa = this.cabActaRepository.findById(request.getActaId());
    if (optionalCabActa.isEmpty()) {
      logger.error(ConstantesComunes.MENSAJE_LOG_ERROR_ACTA_NO_EXISTE_PARA_ELECION, request.getActaId());
      return;
    }
    Acta acta = optionalCabActa.get();

    Archivo archivoAE = acta.getArchivoEscrutinio();
    Archivo archivoAIS = acta.getArchivoInstalacionSufragio();
    if(archivoAE!=null){
      archivoAE.setActivo(ConstantesComunes.INACTIVO);
      this.archivoService.save(archivoAIS);
    }
    if(archivoAIS!=null){
      archivoAIS.setActivo(ConstantesComunes.INACTIVO);
      this.archivoService.save(archivoAIS);
    }

    acta.setDigitalizacionEscrutinio(0L);
    acta.setDigitalizacionInstalacionSufragio(0L);
    acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
    acta.setFechaModificacion(new Date());
    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    acta.setAsignado(null);
    acta.setUsuarioAsignado(null);
    acta.setArchivoEscrutinio(null);
    acta.setArchivoInstalacionSufragio(null);
    this.cabActaRepository.save(acta);
    Usuario tabUsuario = this.usuarioService.findByUsername(tokenInfo.getNombreUsuario());
    tabUsuario.setActasAsignadas((tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas()) - 1); //un acta atendida
    tabUsuario.setActasAtendidas((tabUsuario.getActasAtendidas() == null ? 0 : tabUsuario.getActasAtendidas()) + 1); //Un acta atendida
    this.usuarioService.save(tabUsuario);

    this.logService.registrarLog(
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            String.format("Se rechazó el acta %s, en control de digitalización de actas por el usuario %s.", SceUtils.getNumMesaAndCopia(acta), tokenInfo.getNombreUsuario()),
            tokenInfo.getCodigoCentroComputo(),
            ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
            1
    );

  }

  @Override
  @Transactional
  public GenericResponse<Boolean> reprocesarActas(List<ActaReprocesadaListIItem> actasReprocesarList, TokenInfo tokenInfo) {

    GenericResponse<Boolean> genericResponse = new GenericResponse<>();
    StringBuilder mensajeResponse = new StringBuilder();

    List<Long> longIdActaList = new ArrayList<>();

    for (ActaReprocesadaListIItem actaItem : actasReprocesarList) {
      Optional<Acta> optionalCabActa = this.cabActaRepository.findById(actaItem.getActaId());

      if (optionalCabActa.isPresent()) {
        Acta cabActa = optionalCabActa.get();
        if (!cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA) &&
                !cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION) &&
                !cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_NORMAL) &&
                !cabActa.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_ANULADA)) {
          mensajeResponse.append(String.format("El acta %s-%s-%s, no tiene un estado Normal o Procesada por Resolución.", actaItem.getMesa(), actaItem.getCopia(), actaItem.getDigitoChequeo()));
        }

        this.reprocesar(cabActa, tokenInfo.getNombreUsuario());
        this.cabActaRepository.save(cabActa);
        longIdActaList.add(actaItem.getActaId());


        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                String.format("Se reprocesó el acta %s, por el usuario %s.", SceUtils.getNumMesaAndCopia(cabActa), tokenInfo.getNombreUsuario()),
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                1);

      } else {
        mensajeResponse.append("El acta ").append(actaItem.getMesa()).append(actaItem.getCopia()).append(actaItem.getDigitoChequeo()).append(", no esta registrada en BD.");
      }
    }

    genericResponse.setSuccess(true);
    genericResponse.setMessage(mensajeResponse.isEmpty() ? "Se realizó el reprocesamiento de todas las actas." : mensajeResponse.toString());
    genericResponse.setData(true);
    genericResponse.setActasId(longIdActaList);

    return genericResponse;
  }


  public void reprocesar(Acta acta, String usuario) {
    if(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA) && acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA)){
      acta.setEstadoActaResolucion(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA);
      if(acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)){
        acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
      }
    }else if(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA) && acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA)){
      acta.setEstadoActaResolucion(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA);
      if(acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)){
        acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
      }
    }else {
      acta.setEstadoActaResolucion(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO);
      if(acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)){
        acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
      }
    }
    acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
    acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);
    acta.setUsuarioModificacion(usuario);
    acta.setFechaModificacion(new Date());
  }

  @Override
  public GenericResponse<ActaReprocesadaListIItem> validarReprocesamientoActa(String mesaCopiaDigito) {

    GenericResponse<ActaReprocesadaListIItem> genericResponse = new GenericResponse<>();

    ActaInfo actaInfo = this.utilSceService.validarActa(mesaCopiaDigito, ConstantesComunes.VACIO, Boolean.FALSE);
    Mesa mesa = actaInfo.getMesa();
    BarCodeInfo barcodeInfo = actaInfo.getBarCodeInfo();
    Acta acta = actaInfo.getActa();

    if (!acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA) &&
            !acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION) &&
            !acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_NORMAL) &&
            !acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_ANULADA)) {
      return new GenericResponse<>(false, String.format("El acta %s no se encuentra en un estado Procesado Normal o Por Resolución.", mesaCopiaDigito));
    }
    ActaReprocesadaListIItem actaReprocesadaListIItem = new ActaReprocesadaListIItem();

    actaReprocesadaListIItem.setActaId(acta.getId());
    actaReprocesadaListIItem.setEleccion(actaInfo.getNombreEleccion());
    actaReprocesadaListIItem.setMesa(mesa.getCodigo());
    actaReprocesadaListIItem.setCopia(barcodeInfo.getNroCopia());
    actaReprocesadaListIItem.setDigitoChequeo(barcodeInfo.getDigitoChequeo());
    actaReprocesadaListIItem.setEstadoActa(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION) ? "Procesada por Resolución" : "Procesada Normal");
    genericResponse.setSuccess(true);
    genericResponse.setData(actaReprocesadaListIItem);
    genericResponse.setMessage("El acta se encuentra en un estado para realizar reprocesamiento.");

    return genericResponse;

  }

  @Override
  public GenericResponse<DigitizationListActasItem> bloquearActaControlDigitalizacion(DigitizationListActasItem digitizationListActasItem,
      String usuario) {
    GenericResponse<DigitizationListActasItem> genericResponse = new GenericResponse<>(false, "");
    Optional<Acta> optionalCabActa = this.cabActaRepository.findById(digitizationListActasItem.getActaId());
    if (optionalCabActa.isPresent()) {
      Acta acta = optionalCabActa.get();
      if (acta.getEstadoDigitalizacion() != null && acta.getEstadoDigitalizacion()
          .equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
        acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
        digitizationListActasItem.setEstado(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());
        this.cabActaRepository.save(acta);
        genericResponse.setSuccess(true);
        genericResponse.setData(digitizationListActasItem);
        genericResponse.setMessage("El acta fue validado por el usuario " + usuario + " para su control de digitalización.");

      } else if (acta.getEstadoDigitalizacion() != null && acta.getEstadoDigitalizacion()
          .equals(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO)) {
        genericResponse.setSuccess(false);
        genericResponse.setMessage("El acta siendo revisada por el usuario " + acta.getUsuarioModificacion() + ".");
      } else {
        genericResponse.setSuccess(false);
        genericResponse.setMessage("El acta tiene un estado no permitido.");
      }
    } else {
      genericResponse.setSuccess(false);
      genericResponse.setMessage(String.format("El acta %s, no se encuentra registrado en la BD.", digitizationListActasItem.getActaId()));
    }
    return genericResponse;
  }

  @Override
  public int reseteaValores(String estadoActa, String estadoComputo, String estadoDigitalizacion, String usuario, Date fechaModificacion) {
    return this.cabActaRepository.reseteaValores(estadoActa, estadoComputo, estadoDigitalizacion, usuario, fechaModificacion);
  }

  @Override
  public GenericResponse<TrazabilidadDto>   trazabilidadActa(String mesaCopiaDigito) {
    GenericResponse<TrazabilidadDto> genericResponse = new GenericResponse<>();
    String nroMesa = mesaCopiaDigito.substring(0, 6);
    String copia = mesaCopiaDigito.substring(6, 8);
    String digito = mesaCopiaDigito.substring(8,9);

    ProcesoResult<Acta, DetTipoEleccionDocumentoElectoral> cabActaProcesoResult = validarCabActaTrazabilidad(nroMesa, copia, digito);
    if (cabActaProcesoResult.isObservaciones()) {
      return new GenericResponse<>(false, cabActaProcesoResult.getMensajeObservacion());
    }

    Acta acta = cabActaProcesoResult.getData();

    TrazabilidadDto trazabilidadDto = new TrazabilidadDto();

    List<ItemHistory> itemHistories = new ArrayList<>();

    trazabilidadDto.setInfoActa(getInfoActaTrazabilidad(acta,nroMesa, cabActaProcesoResult.getData2()));

    getItemHistoriesTrazabilidad(acta, itemHistories);


    List<ItemHistory> itemHistoriesFinal = itemHistories.stream().filter(item -> Objects.equals(item.getActivo(), ConstantesComunes.ACTIVO) &&
            !item.getDescripcionEstado().equals("ACTA PARA SEGUNDA DIGITACIÓN")).toList();

    trazabilidadDto.setHistory(itemHistoriesFinal);
    genericResponse.setSuccess(true);
    genericResponse.setData(trazabilidadDto);
    genericResponse.setMessage("Se generó la trazabilidad del acta " + mesaCopiaDigito);

    return genericResponse;
  }

  @Override
  public GenericResponse<List<TrazabilidadDto>> trazabilidadActaPorMesa(String nroMesa) {
    GenericResponse<List<TrazabilidadDto>> genericResponse = new GenericResponse<>();
    ProcesoResult<List<Acta>, DetTipoEleccionDocumentoElectoral> cabActaProcesoResult =
            validarListCabActaTrazabilidad(nroMesa);
    if (cabActaProcesoResult.isObservaciones()) {
      return new GenericResponse<>(false, cabActaProcesoResult.getMensajeObservacion());
    }
    List<Acta> actaList = cabActaProcesoResult.getData();
    List<TrazabilidadDto> trazabilidadDtoList = new ArrayList<>();
    actaList.forEach(acta -> {
      TrazabilidadDto trazabilidadDto = new TrazabilidadDto();
      List<ItemHistory> itemHistories = new ArrayList<>();
      trazabilidadDto.setInfoActa(getInfoActaTrazabilidad(acta, nroMesa,null));
      getItemHistoriesTrazabilidad(acta, itemHistories);
      List<ItemHistory> itemHistoriesFinal = itemHistories.stream().filter(item ->  Objects.equals(item.getActivo(), ConstantesComunes.ACTIVO) &&
              !item.getDescripcionEstado().equals("ACTA PARA SEGUNDA DIGITACIÓN")).toList();
      trazabilidadDto.setHistory(itemHistoriesFinal);
      trazabilidadDtoList.add(trazabilidadDto);
    });
    genericResponse.setSuccess(true);
    genericResponse.setData(trazabilidadDtoList );
    genericResponse.setMessage("Se generó la trazabilidad de la mesa " + nroMesa);
    return genericResponse;
  }

  @Override
  @Transactional
  public List<ActaPorCorregirListItem> listarActasPorCorregirPorUsuario(TokenInfo tokenInfo) {
    var actas = Optional.ofNullable(
                    cabActaRepository.findByEstadoActaAndUsuarioCorreccion(
                            ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR,
                            tokenInfo.getNombreUsuario()
                    )
            )
            .filter(list -> !list.isEmpty())
            .orElseGet(() -> {
              var libres = cabActaRepository.findByEstadoActaAndUsuarioCorreccionIsNull(
                      ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR
              );
              Collections.shuffle(libres);
              return libres;
            });

    var seleccionados = actas.stream()
            .limit(ConstantesComunes.N_DISTRIBUCION_ACTAS_POR_CORREGIR)
            .toList();

    var fechaActual = new Date();

    var nuevos = seleccionados.stream()
            .filter(a -> a.getUsuarioCorreccion() == null)
            .peek(a -> {
              a.setUsuarioCorreccion(tokenInfo.getNombreUsuario());
              a.setFechaUsuarioCorreccion(fechaActual);
              a.setUsuarioModificacion(tokenInfo.getNombreUsuario());
              a.setFechaModificacion(fechaActual);
              guardarDetAccion(a.getId(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), fechaActual, ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI, ConstantesComunes.DET_ACTA_ACCION_PROCESO_POR_CORREGIR, 2);
                  })
            .toList();

    if (!nuevos.isEmpty()) {
      cabActaRepository.saveAll(nuevos);
    }

    return seleccionados.stream()
            .map(this::mapToListItem)
            .toList();

  }

  private ActaPorCorregirListItem mapToListItem(Acta acta) {
    return new ActaPorCorregirListItem(
            acta.getId(),
            acta.getMesa().getCodigo(),
            acta.getNumeroCopia(),
            acta.getDigitoChequeoEscrutinio(),
            acta.getUbigeoEleccion().getEleccion().getNombre()
    );
  }


  @Override
  public ActaPorCorregir actasPorCorregirInfo(Long actaId) {
    ActaPorCorregir actaPorCorregir = new ActaPorCorregir();
    Optional<Acta> optionalCabActa = this.cabActaRepository.findById(actaId);

    if (optionalCabActa.isPresent()) {
      Acta acta = optionalCabActa.get();
      String codigoEleccion = acta.getUbigeoEleccion().getEleccion().getCodigo();
      //INFO ACTA
      actaPorCorregir.setActa(getActaPorCorregirListItem(acta));
      //AGRUPACIONES POLITICAS
      List<DetActa> detActaList = this.detActaRepository.findByActa(acta);
      List<AgrupolPorCorregir> agrupolPorCorregirList = getAgrupolPorCorregirList(acta,codigoEleccion, detActaList);
      actaPorCorregir.setAgrupacionesPoliticas(agrupolPorCorregirList);
      //cvas
      actaPorCorregir.setCvas(getItemPorCorregirCvas(acta));
      actaPorCorregir.setObservaciones(getItemPorCorregirListObs(acta, actaPorCorregir.getCvas()));
    }

    return actaPorCorregir;
  }

  @Override
  public List<String> validarActasPorCorregir(ActaPorCorregir actaPorCorregir) {

    List<String> mensajes = new ArrayList<>();

    if(actaPorCorregir.getActa().getCodigoEleccion() !=null && !actaPorCorregir.getActa().getCodigoEleccion().equals(ConstantesComunes.COD_ELEC_REV_DIST))
      validarActasPorCorregirAgrupacionesPoliticas(actaPorCorregir.getAgrupacionesPoliticas(), mensajes);
    else
      validarActasPorCorregirAutoridades(actaPorCorregir.getAgrupacionesPoliticas(), mensajes);

    validarActasPorCorregirCVAS(actaPorCorregir.getCvas(), mensajes);
    validarActasPorCorregirObservaciones(actaPorCorregir.getObservaciones(), mensajes);
    return mensajes;

  }

  @Override
  @Transactional
  public String registrarActasPorCorregir(ActaPorCorregir actaPorCorregir, TokenInfo tokenInfo) {

    Optional<Acta> optionalActa = this.cabActaRepository.findById(actaPorCorregir.getActa().getActaId());
    if (optionalActa.isEmpty()) return "0";

    List<DetActa> detActaListToErrores = new ArrayList<>();
    List<DetActaPreferencial> detActaPreferencialListToErrores = new ArrayList<>();
    List<DetActaOpcion> detActaOpcionListToErrores = new ArrayList<>();
    Acta acta = optionalActa.get();
    acta.setEstadoActaResolucion(null);
    acta.setEstadoErrorMaterial(null);

    String codigoEleccion = acta.getUbigeoEleccion().getEleccion().getCodigo();

    registrarActaPorCorregirCvas(actaPorCorregir.getCvas(), acta);

    long totalVotosCalculados;

    if(!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)){
      totalVotosCalculados = getTotalVotosAgrupacionesPoliticas(actaPorCorregir.getAgrupacionesPoliticas());
    } else {
      totalVotosCalculados = ProcessActaUtil.getTotalVotosCalculadosRevocatoria(
          actaPorCorregir.getAgrupacionesPoliticas(),
          acta.getCvas(),
          AgrupolPorCorregir::getVotosOpciones,
          VotoOpcionPorCorregir::getTerceraDigitacion);
    }

    acta.setTotalVotos(totalVotosCalculados);

    ErroresActaWrapper erroresWrapper = new ErroresActaWrapper(detActaListToErrores, detActaPreferencialListToErrores, detActaOpcionListToErrores);

    registrarActaPorCorregirAgrupacionespoliticas(codigoEleccion, acta, actaPorCorregir.getAgrupacionesPoliticas(), erroresWrapper, tokenInfo.getNombreUsuario());

    registrarActaPorCorregirObservaciones(actaPorCorregir.getObservaciones(), acta);


    if(!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      acta.setEstadoErrorMaterial(ConsultaErroresMateriales.getErrMatANivelDeActa(acta, totalVotosCalculados));
      if(acta.getEstadoErrorMaterial()!=null){
        SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
      }
      ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(acta, detActaListToErrores);
      if(ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion))
        ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(acta, detActaPreferencialListToErrores);
    } else {
      acta.setEstadoErrorMaterial(ConsultaErroresMateriales.getErrMatANivelDeActaRevocatoria(acta, totalVotosCalculados));
      registrarActaPorCorregirEstadoResolucionRevocatoria(acta, erroresWrapper.getDetActaListToErrores(), erroresWrapper.getDetActaOpcionListToErrores());
    }

    registrarActaPorCorregirEstadoActaCompu(acta, tokenInfo);

    guardarDetAccion(acta.getId(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), acta.getFechaModificacion(), ConstantesComunes.DET_ACTA_ACCION_TIEMPO_FIN,
    ConstantesComunes.DET_ACTA_ACCION_PROCESO_POR_CORREGIR, 2);


    this.logService.registrarLog(
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            String.format("El acta %s, se registró correctamente en actas por corregir.", SceUtils.getNumMesaAndCopia(acta)),
            tokenInfo.getCodigoCentroComputo(),
            ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,1);

    if(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO)) {
      return  "2";
    } else if(acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA)){
      return "1";
    }

    return "3";
  }

  @Override
  public GenericResponse<List<ActaReprocesadaListIItem>> listReprocesar(String nombreUsuario) {

    GenericResponse<List<ActaReprocesadaListIItem>> genericResponse = new GenericResponse<>();

    List<ActaReprocesadaListIItem> listActas = new ArrayList<>();

    List<TabAutorizacion> tabAutorizacionList = this.tabAutorizacionService.findByAutorizacionAndTipoAutorizacionAndActivo(ConstantesCatalogo.DET_CAT_EST_COD_AUTH_PCCC,
            ConstantesComunes.TIPO_AUTORIZACION_REPROCESAR_ACTA, ConstantesComunes.ACTIVO);

    List<TabAutorizacion> resolucionesAprobadas = tabAutorizacionList.stream().filter(tabRes -> tabRes.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_APROBADO)).toList();

    List<TabAutorizacion> resolucionesPendientes = tabAutorizacionList.stream().filter(tabRes -> tabRes.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_PENDIENTE)).toList();

    if (!resolucionesPendientes.isEmpty() || !resolucionesAprobadas.isEmpty()) {

      TabAutorizacion auto = resolucionesPendientes.isEmpty() ? resolucionesAprobadas.get(0) : resolucionesPendientes.get(0);
      List<Acta> listActasProcesar = this.cabActaRepository.findByAutorizacionIdAndReprocesar(auto.getId(), "SI");

      listActas = listActasProcesar.stream().map(ac -> {
        Eleccion maeEleccion = ac.getUbigeoEleccion().getEleccion();
        ActaReprocesadaListIItem item = new ActaReprocesadaListIItem();
        item.setActaId(ac.getId());
        item.setCopia(ac.getNumeroCopia());
        item.setEleccion(maeEleccion.getNombre());
        item.setMesa(ac.getMesa().getCodigo());
        item.setEstadoActa(ac.getEstadoActa());
        item.setAutorizacionId(ac.getAutorizacionId());
        return item;
      }).toList();

    }

    genericResponse.setSuccess(true);
    genericResponse.setData(listActas);

    return genericResponse;

  }

  @Override
  public GenericResponse<Boolean> reprocesarListActas(List<ActaReprocesadaListIItem> actasReprocesarList, String nombreUsuario) {

    GenericResponse<Boolean> genericResponse = new GenericResponse<>();
    StringBuilder mensajeResponse = new StringBuilder();
    for (ActaReprocesadaListIItem item : actasReprocesarList) {
      Optional<Acta> optionalCabActa = this.cabActaRepository.findById(item.getActaId());
      if (optionalCabActa.isPresent()) {
        Acta cabActa = optionalCabActa.get();
        cabActa.setAutorizacionId(item.getAutorizacionId());
        cabActa.setReprocesar("SI");
        this.cabActaRepository.save(cabActa);
      } else {
        mensajeResponse.append("El acta ").append(item.getMesa()).append(item.getCopia()).append(item.getDigitoChequeo()).append(", no está registrado en BD.");
      }
    }

    genericResponse.setSuccess(true);
    genericResponse.setMessage(mensajeResponse.isEmpty() ? "Lista reprocesar guardado con éxito" : mensajeResponse.toString());
    genericResponse.setData(true);

    return genericResponse;

  }

  @Override
  @Transactional
  public GenericResponse<Boolean> rechazarActaEnVerificacion(TokenInfo tokenInfo, String mesa, String codigoEleccion) {
    try {
      Acta acta = obtenerActaPorMesaYEleccion(mesa, codigoEleccion);
      if (acta == null) {
        return new GenericResponse<>(false, "No se encontró el acta para la mesa " + mesa + " y elección " + codigoEleccion);
      }

      boolean esExtranjero = acta.getMesa().getLocalVotacion().getUbigeo().getCodigo().startsWith("9")
              && this.detActaAccionService.existsByActaAccionTiempoAndUsuarioPrefix(
                      acta.getId(),
                      ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA,
                      ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI,
                      ConstantesComunes.USUARIO_CONSULADO);

      if (esExtranjero) {
        rechazarActaExtranjero(acta, tokenInfo.getNombreUsuario());
      } else {
        rechazarActaNacional(acta, tokenInfo.getNombreUsuario());
      }

      this.logService.registrarLog(
              tokenInfo.getNombreUsuario(),
              Thread.currentThread().getStackTrace()[1].getMethodName(),
              "Acta " + SceUtils.getNumMesaAndCopia(acta) + " rechazado correctamente.",
              tokenInfo.getCodigoCentroComputo(),
              ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

      return new GenericResponse<>(true,
              "Se rechazó el acta " + mesa + " - " + acta.getUbigeoEleccion().getEleccion().getCodigo() + ".",
              null, List.of(acta.getId()));

    } catch (Exception e) {
      logger.error("Error al rechazar acta mesa={}, eleccion={}", mesa, codigoEleccion, e);
      return new GenericResponse<>(false, "Ocurrió un error: " + e.getMessage());
    }
  }

  private Acta obtenerActaPorMesaYEleccion(String mesa, String codigoEleccion) {
    Mesa tabMesa = this.mesaRepository.findByCodigo(mesa);
    if (tabMesa == null) return null;

    return this.cabActaRepository.findByMesa(tabMesa).stream()
            .filter(a -> a.getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))
            .findFirst()
            .orElse(null);
  }

  private void rechazarActaExtranjero(Acta acta, String usuario) {
    acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA);
    acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE);
    acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
    acta.setVerificador(null);
    acta.setVerificador2(null);
    acta.setDigitalizacionEscrutinio(3L);
    acta.setDigitalizacionInstalacionSufragio(2L);
    acta.setUsuarioModificacion(usuario);
    acta.setFechaModificacion(new Date());

    this.detActaAccionService.inactivarPorActaYAccion(acta.getId(), ConstantesComunes.DET_ACTA_ACCION_PROCESO_MODELO_PROCESAR);
    this.cabActaRepository.save(acta);
  }

  private void rechazarActaNacional(Acta acta, String usuario) {
    acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE);
    acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE);
    acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);
    acta.setEstadoErrorMaterial(null);
    acta.setEstadoActaResolucion(null);
    acta.setUsuarioAsignado(null);
    acta.setAsignado(null);
    acta.setVerificador(null);
    acta.setVerificador2(null);
    acta.setDigitalizacionEscrutinio(0L);
    acta.setObservDigEscrutinio(null);
    acta.setObservDigInstalacionSufragio(null);
    acta.setDigitalizacionInstalacionSufragio(0L);
    acta.setArchivoEscrutinio(null);
    acta.setArchivoInstalacion(null);
    acta.setArchivoSufragio(null);
    acta.setArchivoInstalacionSufragio(null);
    acta.setArchivoEscrutinioPdf(null);
    acta.setArchivoInstalacionPdf(null);
    acta.setArchivoSufragioPdf(null);
    acta.setArchivoInstalacionSufragioPdf(null);
    acta.setArchivoEscrutinioFirmado(null);
    acta.setArchivoInstalacionFirmado(null);
    acta.setArchivoSufragioFirmado(null);
    acta.setArchivoInstalacionSufragioFirmado(null);
    acta.setTipoTransmision(null);
    acta.setUsuarioModificacion(usuario);
    acta.setFechaModificacion(new Date());

    this.cabActaRepository.save(acta);
    this.detActaAccionService.inactivarPorActaYAccion(acta.getId(), ConstantesComunes.DET_ACTA_ACCION_PROCESO_MODELO_PROCESAR);
    this.detActaRectangleService.deleteDetActaRectangleByActaId(acta.getId());
  }

  @Override
  @Transactional
  public GenericResponse<Boolean> puestaCeroPorActa(String mesa, String codigoEleccion, String usuario) {
    Mesa tabMesa = this.mesaRepository.findByCodigo(mesa);
    List<Acta> cabActaList = this.cabActaRepository.findByMesa(tabMesa);
    if (cabActaList.isEmpty())
      return new GenericResponse<>(false, "No existen registros de actas para la mesa " + mesa);

    Optional<Acta> optionalcabActa = cabActaList.stream().filter((cabActa1 -> cabActa1.getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))).findAny();
    if (optionalcabActa.isEmpty())
      return new GenericResponse<>(false, "La mesa " + mesa + " no existe para la elección " + codigoEleccion);

    Acta cabActa = optionalcabActa.get();
    //VERIFICAR LA RESOLUCION
    List<DetActaResolucion> detActaResolucionListEliminar = new ArrayList<>();

    List<TabResolucion> resolucionListEliminar = new ArrayList<>();

    List<DetActaResolucion> detActaResolucionList = this.detActaResolucionRepository.findByActa_Id(cabActa.getId());

    for( DetActaResolucion detActaResolucion: detActaResolucionList ) {
      List<DetActaResolucion> detActaResolucionListPorResolucion = this.detActaResolucionRepository.findByResolucion(detActaResolucion.getResolucion());
      if(detActaResolucionListPorResolucion.size()==1){
        detActaResolucionListEliminar.add(detActaResolucion);
        resolucionListEliminar.add(detActaResolucion.getResolucion());
      }
    }

    if(!detActaResolucionListEliminar.isEmpty()) {
      this.detActaResolucionRepository.deleteAll(detActaResolucionListEliminar);
    }

    if(!resolucionListEliminar.isEmpty()) {
      this.tabResolucionRepository.deleteAll(resolucionListEliminar);
    }

    //ELIMINAR DEL DET_ACTA_FORMATO
    this.detActaFormatoRepository.deleteDetActaFormatoByActa(cabActa);

    this.cabActaRepository.reseteaValoresPorActa(
            ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE,
            ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE,
            ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
            usuario,
            new Date(),
            cabActa.getId());

    this.detActaRectangleService.deleteDetActaRectangleByActaId(cabActa.getId());

    return new GenericResponse<>(true, "Se reealizó la puesta a cero de la mesa " + mesa + " - " + cabActa.getUbigeoEleccion().getEleccion().getCodigo() + ".");
  }

  @Override
  public List<ActaConArchivosNull> actasConArchivosNull() {
    return new ArrayList<>();
  }

  @Override
  public List<ActaDTO> findActasNative() {
    return this.cabActaRepository.findActasNative();
  }

  @Override
  public ActaRepository getCabActaRepository() {
    return cabActaRepository;
  }

  @Override
  public UsuarioService getUsuarioService() {
    return usuarioService;
  }

  @Override
  public DetActaAccionService getDetActaAccionService() {
    return detActaAccionService;
  }

  @Override
  public List<Long> findByEstadoActaAndVerificadorAndCodigoEleccion(String estadoC, String estadoW, String usuarioVerificador, String codigoEleccion) {
    return this.cabActaRepository.findByEstadoActaAndVerificadorAndCodigoEleccion(estadoC, estadoW, usuarioVerificador, codigoEleccion);
  }

  @Override
  public List<Long> findByEstadoActaAndVerificadorAndCodigoEleccionAndDigitalizacion(String estadoC, String estadoW, String usuarioVerificador, String codigoEleccion, Long digitalizacion) {
    return this.cabActaRepository.findByEstadoActaAndVerificadorAndCodigoEleccionAndDigitalizacion(estadoC, estadoW, usuarioVerificador, codigoEleccion, digitalizacion);
  }


  @Transactional(readOnly = true)
  public ReturnMonitoreoActas listActasMonitoreo(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda) {
    ReturnMonitoreoActas response = new ReturnMonitoreoActas();
    
    Long idProceso = monitoreoNacionBusqueda.getIdProceso();
    Long idEleccion = monitoreoNacionBusqueda.getIdEleccion();
    Long idUbigeo = monitoreoNacionBusqueda.getIdUbigeo();
    Long idLocalVotacion = monitoreoNacionBusqueda.getIdLocal();
    Long idDepartamento = monitoreoNacionBusqueda.getIdDepartamento();
    Long idProvincia = monitoreoNacionBusqueda.getIdProvincia();
    String mesa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getMesa());
    String grupoActa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getGrupoActa(),ConstantesMonitoreo.TODOS);
    
    Long cantidadNormales = 0L;
    Long cantidadObservadas = 0L;
    Long cantidadEnviadasJne = 0L;
    Long cantidadDevueltasJne = 0L;

    if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.CONTABILIZADAS)){
    	cantidadNormales = this.cabActaRepository.getTotalNormales(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
    }
    
    if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.OBSERVADAS)){
    	cantidadObservadas = this.cabActaRepository.getTotalObservadas(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
    }
    
    if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.ENVIADAS_JNE)){
    	cantidadEnviadasJne = this.cabActaRepository.getTotalEnviadasJne(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
    }
    
    if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.DEVUELTAS_JNE)){
    	cantidadDevueltasJne = this.cabActaRepository.getTotalDevueltasJne(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
    }
    
    Long total = cantidadNormales + cantidadObservadas + cantidadEnviadasJne + cantidadDevueltasJne;

    List<VwActaMonitoreo> vwActas = vwActaMonitoreoRepository.buscarMonitoreoNacion(
    		idProceso,
    		idEleccion, 
    		idDepartamento, 
    		idProvincia, 
    		idUbigeo, 
    		idLocalVotacion, 
    		mesa, 
    		grupoActa);
    

	List<MonitoreoListActaItem> actaItemList = vwActas.stream().map(acta -> {

		String imagenEscrutinio = "";
		String imagenInstalacion = "";
		String imagenSufragio = "";
		String imagenInstalacionSufragio = "";

		if (acta.getIdArchivoEscrutinio() != null) {
			imagenEscrutinio = acta.getIdArchivoEscrutinio().toString();
		} else if (acta.getIdArchivoEscrutinioFirmado() != null) {
			imagenEscrutinio = acta.getIdArchivoEscrutinioFirmado().toString();
		}

		if (acta.getIdArchivoSufragioFirmado() != null) {
			imagenSufragio = acta.getIdArchivoSufragioFirmado().toString();
		}

		if (acta.getIdArchivoInstalacionFirmado() != null) {
			imagenInstalacion = acta.getIdArchivoInstalacionFirmado().toString();
		}

		if (acta.getIdArchivoInstalacionSufragio() != null) {
			imagenInstalacionSufragio = acta.getIdArchivoInstalacionSufragio().toString();
		} else if(acta.getIdArchivoInstalacionSufragioFirmado()!=null){
    		imagenInstalacionSufragio = acta.getIdArchivoInstalacionSufragioFirmado().toString();
    	}
		
		
		return MonitoreoListActaItem.builder()
				.actaId(acta.getId())
				.cantidadTransmitidasFaltantes(acta.getCantidadTransmitidasFaltantes())
				.verActa(acta.getVerActa())
				.grupoActa(acta.getGrupoActa())
				.acta(acta.getMesaCodigo() 
						+ (acta.getNumeroCopia() != null ? acta.getNumeroCopia() : "")
						+ (acta.getDigitoChequeoEscrutinio() != null ? acta.getDigitoChequeoEscrutinio() : ""))
				.estado(acta.getEstadoActa())
				.mesa(acta.getMesaCodigo())
				.fecha(DateUtil.getDateString(acta.getFechaModificacion(),
						SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH))
				.imagenEscrutinio(imagenEscrutinio)				
				.imagenInstalacion(imagenInstalacion)
                .imagenSufragio(imagenSufragio)
                .imagenInstalacionSufragio(imagenInstalacionSufragio)
                .build();
		
		
	}).toList();

    response.setListActaItems(actaItemList);
    response.setTotal(total.toString());
    response.setTotalNormales(cantidadNormales.toString());
    response.setTotalObservadas(cantidadObservadas.toString());
    response.setTotalEnviadasJne(cantidadEnviadasJne.toString());
    response.setTotalDevueltasJne(cantidadDevueltasJne.toString());

    return response;

  }


  private void registrarActaPorCorregirEstadoResolucionRevocatoria(Acta acta, List<DetActa> detActaList, List<DetActaOpcion> detActaOpcions) {

    if (acta.getEstadoErrorMaterial() != null) {
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
    }

    String hashSetErroresMaterialesDetActaTotal = detActaList.stream()
        .map(DetActa::getEstadoErrorMaterial)
        .filter(estado -> estado != null && !estado.isEmpty())
        .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
        .filter(s -> !s.isEmpty())
        .distinct()
        .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

    if(!hashSetErroresMaterialesDetActaTotal.isEmpty()){
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
    }

    String hashSetErroresMaterialesDetActaOpcionTotal = detActaOpcions.stream()
        .map(DetActaOpcion::getEstadoErrorMaterial)
        .filter(estado -> estado != null && !estado.isEmpty())
        .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
        .filter(s -> !s.isEmpty())
        .distinct()
        .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));


    if(hashSetErroresMaterialesDetActaOpcionTotal.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I)){
      SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
    }

  }


  private void registrarActaPorCorregirEstadoActaCompu(Acta acta, TokenInfo tokenInfo) {

    if (acta.getEstadoActaResolucion() == null || acta.getEstadoActaResolucion().isEmpty()) {
      acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA);
      acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
    } else {
      acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
      acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
    }

    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    acta.setFechaModificacion(new Date());

    this.cabActaRepository.save(acta);

  }


  private void registrarActaPorCorregirObservaciones(List<ItemPorCorregir> itemPorCorregirList, Acta acta) {
    for (ItemPorCorregir itemPorCorregir : itemPorCorregirList) {
      String detalle = itemPorCorregir.getDetalle();

      switch (detalle) {
        case ConstantesComunes.TEXTO_ACTA_SIN_FIRMAS:
          manejarEstadoActaResolucion(itemPorCorregir, acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
          break;
        case ConstantesComunes.TEXTO_ACTA_INCOMPLETA:
          manejarEstadoActaResolucion(itemPorCorregir, acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
          break;
        case ConstantesComunes.TEXTO_ACTA_SIN_DATOS:
          manejarEstadoActaResolucion(itemPorCorregir, acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
          break;
        case ConstantesComunes.TEXTO_SOLICITUD_DE_NULIDAD:
          manejarEstadoActaResolucion(itemPorCorregir, acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
          break;
        default:
          break;
      }
    }
  }

  private void manejarEstadoActaResolucion(ItemPorCorregir itemPorCorregir, Acta acta, String estado) {
    if (ConstantesComunes.VALUE_SI.equals(itemPorCorregir.getTerceraDigitacion()))
      SceUtils.agregarEstadoResolucion(acta, estado);
    else
      SceUtils.removerEstadoResolucion(acta, estado);
  }

  private void registrarActaPorCorregirCvas(ItemPorCorregir itemPorCorregirCvas, Acta acta) {

    if (itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      acta.setCvas(null);
      acta.setIlegibleCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS);
    } else if (itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA) || itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.VACIO)) {
      acta.setCvas(null);
      acta.setIlegibleCvas(null);
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
    } else {
      acta.setCvas(Long.valueOf(itemPorCorregirCvas.getTerceraDigitacion()));
      acta.setIlegibleCvas(null);
    }

  }

  private void registrarActaPorCorregirAgrupacionespoliticas(String codigoEleccion, Acta acta, List<AgrupolPorCorregir> agrupacionesPoliticas,
                                                             ErroresActaWrapper erroresWrapper, String usuario) {

    for (AgrupolPorCorregir agrupolPorCorregir : agrupacionesPoliticas) {
      agrupolPorCorregir.setTerceraDigitacion(SceUtils.removeZerosLeft(agrupolPorCorregir.getTerceraDigitacion()));
      if (agrupolPorCorregir.getTerceraDigitacion() == null)
        agrupolPorCorregir.setTerceraDigitacion(0 + "");

      if (!agrupolPorCorregir.getTerceraDigitacion().equals(ConstantesComunes.TEXTO_NULL)) { // No considera achurados
        procesarTerceraDigitacionAgrupacion(codigoEleccion, agrupolPorCorregir, acta, erroresWrapper, usuario);
      }
    }

  }

  private void verificarVotosImpugnados(DetActa detActa, Acta acta) {
    if (Objects.equals(detActa.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS) &&
        detActa.getVotos() != null && detActa.getVotos() > 0) {
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
    }
  }

  private void procesarTerceraDigitacionAgrupacion(String codigoEleccion, AgrupolPorCorregir agrupolPorCorregir, Acta acta,
                                                   ErroresActaWrapper erroresWrapper, String usuario) {

    if (agrupolPorCorregir.getTerceraDigitacion().equals(ConstantesComunes.VACIO))
      agrupolPorCorregir.setTerceraDigitacion(ConstantesComunes.NVALUE_ZERO + ConstantesComunes.VACIO);

    Optional<DetActa> optionalDetActa = this.detActaRepository.findById(agrupolPorCorregir.getIdDetActa());
    if(optionalDetActa.isEmpty()) return;

    DetActa detActa = optionalDetActa.get();

    if(!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {

      manejarIlegibleOAsignarVotos(acta, agrupolPorCorregir, detActa);
      verificarVotosImpugnados(detActa, acta);
      long totalVotosPreferenciales = procesarVotosPreferenciales(codigoEleccion, acta, detActa, agrupolPorCorregir, erroresWrapper.getDetActaPreferencialListToErrores());
      detActa.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialAgrupol(acta, detActa, totalVotosPreferenciales));

      if(detActa.getEstadoErrorMaterial()!=null && !detActa.getEstadoErrorMaterial().isEmpty()){
        SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
      }

    } else {

      long totalVotosPrefeOpcionPorAgrupacionoAutoridad;
      Long totalVotosBNI;
      Long[] totalVotos;
      totalVotos = procesarVotosOpciones(acta, agrupolPorCorregir, erroresWrapper.getDetActaOpcionListToErrores(), usuario);
      totalVotosPrefeOpcionPorAgrupacionoAutoridad = totalVotos[0];
      totalVotosBNI = totalVotos[1];
      detActa.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialDetOpcion(acta ,totalVotosPrefeOpcionPorAgrupacionoAutoridad,totalVotosBNI));
      detActa.setVotos(totalVotosPrefeOpcionPorAgrupacionoAutoridad);
    }

    detActa.setFechaModificacion(new Date());
    detActa.setUsuarioModificacion(usuario);
    this.detActaRepository.save(detActa);
    erroresWrapper.getDetActaListToErrores().add(detActa);

  }


  private long procesarVotosPreferenciales(String codigoEleccion, Acta acta, DetActa detActa, AgrupolPorCorregir agrupolPorCorregir,
      List<DetActaPreferencial> detActaPreferencialListToErrores) {
    long totalVotosPreferencialesPorAgrupacion = 0;
    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
      for (VotoPreferencialPorCorregir votoPreferencial : agrupolPorCorregir.getVotosPreferenciales()) {
        procesarVotoPreferencial(votoPreferencial, acta, detActa, detActaPreferencialListToErrores);
        totalVotosPreferencialesPorAgrupacion += obtenerTotalVotosPreferenciales(votoPreferencial);
      }
    }
    return totalVotosPreferencialesPorAgrupacion;
  }


  private Long[] procesarVotosOpciones(Acta acta, AgrupolPorCorregir agrupolPorCorregir,
                                           List<DetActaOpcion> detActaOpcionesListToErrores, String usuario) {

    long totalVotosAutoridad = 0L;
    long totalVotosBNI = 0L;
    Long[] resultado = new Long[2];

    for (VotoOpcionPorCorregir votoOpcion : agrupolPorCorregir.getVotosOpciones()) {
      procesarVotoOpcion(votoOpcion, acta, detActaOpcionesListToErrores, usuario);
      totalVotosAutoridad += obtenerTotalVotosOpciones(votoOpcion);
      if(Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS) ||
          Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS)||
          Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)){
        totalVotosBNI += (votoOpcion.getTerceraDigitacion() == null || votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0 : Long.parseLong(votoOpcion.getTerceraDigitacion()));
      }

      //Guardando votos impugnados
      if (Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS) &&
          votoOpcion.getTerceraDigitacion() != null && (votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE) || Long.parseLong(votoOpcion.getTerceraDigitacion()) > 0)) {
        SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
      }

    }

    resultado[0] = totalVotosAutoridad;
    resultado[1] = totalVotosBNI;

    return resultado;

  }

  private void procesarVotoOpcion(VotoOpcionPorCorregir votoOpcion, Acta acta, List<DetActaOpcion> detActaOpcionlListToErrores, String usuario) {

    votoOpcion.setTerceraDigitacion(SceUtils.removeZerosLeft(votoOpcion.getTerceraDigitacion()));

    if (votoOpcion.getTerceraDigitacion() != null && votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.VACIO))
      votoOpcion.setTerceraDigitacion(ConstantesComunes.CVALUE_ZERO);

    Optional<DetActaOpcion> optionalDetActaOpcion = this.detActaOpcionService.findById(votoOpcion.getIdDetActaOpcion());

    this.utilSceService.procesarYGuardarDetActaOpcion(acta, votoOpcion, optionalDetActaOpcion, detActaOpcionlListToErrores, usuario,SceUtils::manejarIlegibleVotoOpcion);

  }


  private long obtenerTotalVotosOpciones(VotoOpcionPorCorregir votoOpcion) {
    return votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0 : Long.parseLong(votoOpcion.getTerceraDigitacion());
  }

  private long obtenerTotalVotosPreferenciales(VotoPreferencialPorCorregir votoPreferencial) {
    return votoPreferencial.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0
        : Long.parseLong(votoPreferencial.getTerceraDigitacion());
  }




  private void procesarVotoPreferencial(VotoPreferencialPorCorregir votoPreferencial, Acta acta, DetActa detActa,
      List<DetActaPreferencial> detActaPreferencialListToErrores) {
    votoPreferencial.setTerceraDigitacion(SceUtils.removeZerosLeft(votoPreferencial.getTerceraDigitacion()));
    if (votoPreferencial.getTerceraDigitacion() == null || votoPreferencial.getTerceraDigitacion().equals(ConstantesComunes.VACIO)) {
      votoPreferencial.setTerceraDigitacion(ConstantesComunes.CVALUE_ZERO);
    }

    Optional<DetActaPreferencial> optionalDetActaPreferencial = this.detActaPreferencialRepository.findById(votoPreferencial.getIdDetActaPreferencial());

    if (optionalDetActaPreferencial.isPresent()) {
      DetActaPreferencial detActaPreferencial = optionalDetActaPreferencial.get();
      manejarIlegibleOAsignarVotosPreferenciales(acta, votoPreferencial, detActaPreferencial);
      detActaPreferencial.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialPreferencial(acta, detActa, detActaPreferencial));
      this.detActaPreferencialRepository.save(detActaPreferencial);
      detActaPreferencialListToErrores.add(detActaPreferencial);

    }

  }




  private void manejarIlegibleOAsignarVotosPreferenciales(Acta acta, VotoPreferencialPorCorregir votoPreferencial,
      DetActaPreferencial detActaPreferencial) {
    if (votoPreferencial.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActaPreferencial.setVotos(ConstantesComunes.NVALUE_NULL);
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
    } else {
      detActaPreferencial.setIlegible(null);
      detActaPreferencial.setVotos(Long.parseLong(votoPreferencial.getTerceraDigitacion()));
    }
  }






  private void manejarIlegibleOAsignarVotos(Acta acta, AgrupolPorCorregir agrupolPorCorregir, DetActa detActa) {
    if (agrupolPorCorregir.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
      detActa.setVotos(ConstantesComunes.NVALUE_NULL);
      SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
    } else {
      detActa.setIlegible(null);
      detActa.setVotos(Long.parseLong(agrupolPorCorregir.getTerceraDigitacion()));
    }
  }

  private long getTotalVotosAgrupacionesPoliticas(List<AgrupolPorCorregir> agrupacionesPoliticas) {
    return agrupacionesPoliticas.stream()
        .filter(agrupol -> !agrupol.getTerceraDigitacion().equals(ConstantesComunes.TEXTO_NULL))
        .mapToLong(agrupol -> {
          String terceraDigitacion = agrupol.getTerceraDigitacion();
          if (!terceraDigitacion.equals(ConstantesComunes.VACIO) && !terceraDigitacion.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
            return Long.parseLong(terceraDigitacion);
          }
          return 0L;
        })
        .sum();
  }

  private void validarActasPorCorregirObservaciones(List<ItemPorCorregir> itemPorCorregirObservacionesList, List<String> mensajes) {
    for (ItemPorCorregir itemPorCorregir : itemPorCorregirObservacionesList) {
      if (itemPorCorregir.getTerceraDigitacion().equals(ConstantesComunes.VALUE_SI)) {
        mensajes.add("Marcó el acta como " + itemPorCorregir.getDetalle() + ".");
      }
    }
  }

  private void validarActasPorCorregirCVAS(ItemPorCorregir itemPorCorregirCvas, List<String> mensajes) {
    if (itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      mensajes.add("El total de ciudadanos que votaron tiene un valor ilegible (#).");
    } else if (itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA)
        || itemPorCorregirCvas.getTerceraDigitacion().equals(ConstantesComunes.VACIO)) {
      mensajes.add("El total de ciudadanos que votaron tiene un valor N o vacío, que corresponde a un acta incompleta.");
    }
  }

  private void validarActasPorCorregirAgrupacionesPoliticas(List<AgrupolPorCorregir> agrupolPorCorregirList, List<String> mensajes) {

    for (AgrupolPorCorregir agrupolPorCorregir : agrupolPorCorregirList) {

      if (agrupolPorCorregir.getTerceraDigitacion() != null && agrupolPorCorregir.getTerceraDigitacion()
          .equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
          mensajes.add(String.format("%s, presenta ilegibilidad (#).", agrupolPorCorregir.getOrganizacionPolitica()));
      }

      validarActasPorCorregirVotosPreferenciales(agrupolPorCorregir, mensajes);

      if (agrupolPorCorregir.getOrganizacionPolitica().equals(ConstantesComunes.DESC_AGRUPOL_VOTOS_IMPUGNADOS) &&
          agrupolPorCorregir.getTerceraDigitacion() != null &&
          !agrupolPorCorregir.getTerceraDigitacion().isEmpty() &&
          !agrupolPorCorregir.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
        double voto = Double.parseDouble(agrupolPorCorregir.getTerceraDigitacion());
        if (voto > 0) {
          mensajes.add("El acta presenta votos impugnados.");
        }
      }
    }
  }


  private void validarActasPorCorregirAutoridades(List<AgrupolPorCorregir> agrupolPorCorregirList, List<String> mensajes) {
    for (AgrupolPorCorregir agrupolPorCorregir : agrupolPorCorregirList) {
      if (agrupolPorCorregir.getVotosOpciones() != null) {
        for (VotoOpcionPorCorregir votoOpcion : agrupolPorCorregir.getVotosOpciones()) {
          normalizarVotoOpcionPorCorregir(votoOpcion);
          validarIlegibilidadPorCorregir(agrupolPorCorregir, votoOpcion, mensajes);
          validarVotosImpugnadosPorCorregir(agrupolPorCorregir, votoOpcion, mensajes);
        }
      }
    }
  }

  private void normalizarVotoOpcionPorCorregir(VotoOpcionPorCorregir votoOpcion) {
    if (votoOpcion.getTerceraDigitacion() != null && votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.VACIO)) {
      votoOpcion.setTerceraDigitacion(ConstantesComunes.CVALUE_ZERO);
    }
  }

  private void validarIlegibilidadPorCorregir(AgrupolPorCorregir agrupolPorCorregir, VotoOpcionPorCorregir votoOpcion, List<String> mensajes) {
    if (votoOpcion.getTerceraDigitacion() != null &&
        votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
      mensajes.add(String.format("%s, Votos %s, presenta ilegibilidad (#).",
          agrupolPorCorregir.getOrganizacionPolitica(),
          ConstantesComunes.getMapNamePosicionRevocatoria().get(votoOpcion.getPosicion())));
    }
  }

  private void validarVotosImpugnadosPorCorregir(AgrupolPorCorregir agrupolPorCorregir, VotoOpcionPorCorregir votoOpcion, List<String> mensajes) {
    if (votoOpcion.getPosicion().equals(ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS) &&
        votoOpcion.getTerceraDigitacion() != null &&
        !votoOpcion.getTerceraDigitacion().equals(ConstantesComunes.C_VALUE_ILEGIBLE) &&
        Long.parseLong(votoOpcion.getTerceraDigitacion()) > 0) {
      mensajes.add(String.format("%s, tiene Votos Impugnados.", agrupolPorCorregir.getOrganizacionPolitica()));
    }
  }



  private void validarActasPorCorregirVotosPreferenciales(AgrupolPorCorregir agrupolPorCorregir, List<String> mensajes) {

    if (agrupolPorCorregir.getVotosPreferenciales() != null) {
      for (VotoPreferencialPorCorregir votoPreferencialPorCorregir : agrupolPorCorregir.getVotosPreferenciales()) {
        if (votoPreferencialPorCorregir.getTerceraDigitacion() != null && votoPreferencialPorCorregir.getTerceraDigitacion()
            .equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
          mensajes.add(String.format("%s, voto preferencial %s, presenta ilegibilidad (#).", agrupolPorCorregir.getOrganizacionPolitica(),
              votoPreferencialPorCorregir.getLista()));
        }
      }
    }
  }

  private List<ItemPorCorregir> getItemPorCorregirListObs(Acta acta, ItemPorCorregir itemPorCorregirCVAS) {
    List<ItemPorCorregir> itemPorCorregirListObs = new ArrayList<>();
    itemPorCorregirListObs.add(createItemPorCorregir(
        ConstantesComunes.TEXTO_ACTA_INCOMPLETA,
        itemPorCorregirCVAS.getPrimeraDigitacion().equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        itemPorCorregirCVAS.getPrimeraDigitacion().equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        ConstantesComunes.VACIO));

    itemPorCorregirListObs.add(createItemPorCorregir(
        ConstantesComunes.TEXTO_ACTA_SIN_FIRMAS,
        Objects.equals(acta.getDigitacionFirmasManualV1(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_NO
            : ConstantesComunes.VALUE_SI,
        Objects.equals(acta.getDigitacionFirmasManualV2(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_NO
            : ConstantesComunes.VALUE_SI,
        ConstantesComunes.VACIO
    ));
    itemPorCorregirListObs.add(createItemPorCorregir(
        ConstantesComunes.TEXTO_ACTA_SIN_DATOS,
        Objects.equals(acta.getDigitacionSinDatosManualV1(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        Objects.equals(acta.getDigitacionSinDatosManualV2(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        ConstantesComunes.VACIO
    ));
    itemPorCorregirListObs.add(createItemPorCorregir(
        ConstantesComunes.TEXTO_SOLICITUD_DE_NULIDAD,
        Objects.equals(acta.getDigitacionSolicitudNulidadManualV1(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        Objects.equals(acta.getDigitacionSolicitudNulidadManualV2(), ConstantesComunes.NVALUE_UNO) ? ConstantesComunes.VALUE_SI
            : ConstantesComunes.VALUE_NO,
        ConstantesComunes.VACIO
    ));

    return itemPorCorregirListObs;
  }

  private ItemPorCorregir createItemPorCorregir(String detalle, String primeraDigitacion, String segundaDigitacion,
      String terceraDigitacion) {
    ItemPorCorregir item = new ItemPorCorregir();
    item.setDetalle(detalle);
    item.setPrimeraDigitacion(primeraDigitacion);
    item.setSegundaDigitacion(segundaDigitacion);
    item.setTerceraDigitacion(terceraDigitacion);
    return item;
  }

  private ItemPorCorregir getItemPorCorregirCvas(Acta acta) {
    ItemPorCorregir itemPorCorregirCvas = new ItemPorCorregir();
    itemPorCorregirCvas.setDetalle("TOTAL DE CIUDADANOS QUE VOTARON");
    String primeraVeriCvas = "";
    if (acta.getCvasV1() == null && acta.getIlegibleCvasV1() == null) {
      primeraVeriCvas = ConstantesComunes.VALUE_CVAS_INCOMPLETA;
    } else if (acta.getCvasV1() == null) {
      primeraVeriCvas = ConstantesComunes.C_VALUE_ILEGIBLE;
    } else if (acta.getIlegibleCvasV1() == null) {
      primeraVeriCvas = acta.getCvasV1() + ConstantesComunes.VACIO;
    }
    itemPorCorregirCvas.setPrimeraDigitacion(primeraVeriCvas);
    String segundaVeriCvas = "";
    if (acta.getCvasV2() == null && acta.getIlegibleCvasV2() == null) {
      segundaVeriCvas = ConstantesComunes.VALUE_CVAS_INCOMPLETA;
    } else if (acta.getCvasV2() == null) {
      segundaVeriCvas = ConstantesComunes.C_VALUE_ILEGIBLE;
    } else if (acta.getIlegibleCvasV2() == null) {
      segundaVeriCvas = acta.getCvasV2() + ConstantesComunes.VACIO;
    }
    itemPorCorregirCvas.setSegundaDigitacion(segundaVeriCvas);
    itemPorCorregirCvas.setTerceraDigitacion("");

    return itemPorCorregirCvas;
  }

  private List<AgrupolPorCorregir> getAgrupolPorCorregirList(Acta acta, String codigoEleccion ,List<DetActa> detActaList) {
    List<DetActa> detActaListOrdenadosPorPosicion = detActaList.stream().sorted(Comparator.comparing(DetActa::getPosicion)).toList();
    List<AgrupolPorCorregir> agrupolPorCorregirList = new ArrayList<>();
    int nro = 1;
    for (DetActa detActa : detActaListOrdenadosPorPosicion) {
      AgrupolPorCorregir agrupolPorCorregir = new AgrupolPorCorregir();
      AgrupacionPolitica agrupacionPolitica = detActa.getAgrupacionPolitica();
      agrupolPorCorregir.setIdDetActa(detActa.getId());
      DetUbigeoEleccionAgrupacionPolitica detUbigeoEleccionAgrupacionPolitica = this.detUbigeoEleccionAgrupacionPoliticaRepository.
          findByUbigeoEleccionAndAgrupacionPolitica(acta.getUbigeoEleccion(), agrupacionPolitica);

      if (detUbigeoEleccionAgrupacionPolitica == null) continue;

      if (Objects.equals(detUbigeoEleccionAgrupacionPolitica.getEstado(), ConstantesComunes.N_ACHURADO)) {
        agrupolPorCorregir.setNro(nro);
        agrupolPorCorregir.setOrganizacionPolitica(ConstantesComunes.VACIO);
        agrupolPorCorregir.setPrimeraDigitacion(ConstantesComunes.TEXTO_NULL);
        agrupolPorCorregir.setSegundaDigitacion(ConstantesComunes.TEXTO_NULL);
        agrupolPorCorregir.setTerceraDigitacion(ConstantesComunes.TEXTO_NULL);
        //Agregar Preferenciales achurados
        setPreferencialPorCorregirListToAgrupolPorCorregirAchurados(codigoEleccion, detActa, agrupolPorCorregir);
        setOpcionesPorCorregirListToAgrupolPorCorregirAchurados(codigoEleccion, detActa, agrupolPorCorregir);
      } else {
        //Sino no son achurados
        agrupolPorCorregir.setNro(nro);
        agrupolPorCorregir.setOrganizacionPolitica(agrupacionPolitica.getDescripcion());
        agrupolPorCorregir.setPrimeraDigitacion(detActa.getIlegiblev1() == null ? detActa.getVotosManual1() + ConstantesComunes.VACIO : detActa.getIlegiblev1());
        agrupolPorCorregir.setSegundaDigitacion(detActa.getIlegiblev2() == null ? detActa.getVotosManual2() + ConstantesComunes.VACIO : detActa.getIlegiblev2());
        agrupolPorCorregir.setTerceraDigitacion(ConstantesComunes.VACIO);
        setPreferencialPorCorregirListToAgrupolPorCorregir(codigoEleccion, detActa, agrupolPorCorregir);
        setOpcionesPorCorregirListToAgrupolPorCorregir(codigoEleccion, detActa, agrupolPorCorregir);
      }
      agrupolPorCorregirList.add(agrupolPorCorregir);

      nro = nro + 1;
    }

    return agrupolPorCorregirList;
  }

  private void setPreferencialPorCorregirListToAgrupolPorCorregir(String codigoEleccion , DetActa detActa, AgrupolPorCorregir agrupolPorCorregir) {
    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
      List<DetActaPreferencial> detActaPreferencialList = this.detActaPreferencialRepository.findByDetActa(detActa);
      List<DetActaPreferencial> detActaPreferencialListOrdenadaLista = detActaPreferencialList.stream().sorted(Comparator.comparing(DetActaPreferencial::getLista)).toList();
      List<VotoPreferencialPorCorregir> preferencialPorCorregirList = detActaPreferencialListOrdenadaLista.stream().map(e -> {
        VotoPreferencialPorCorregir votoPreferencialPorCorregir = new VotoPreferencialPorCorregir();
        votoPreferencialPorCorregir.setLista(e.getLista());
        votoPreferencialPorCorregir.setIdDetActaPreferencial(e.getId());
        votoPreferencialPorCorregir.setPrimeraDigitacion(e.getIlegiblev1() == null ? e.getVotosManual1() + ConstantesComunes.VACIO : e.getIlegiblev1());
        votoPreferencialPorCorregir.setSegundaDigitacion(e.getIlegiblev2() == null ? e.getVotosManual2() + ConstantesComunes.VACIO : e.getIlegiblev2());
        votoPreferencialPorCorregir.setTerceraDigitacion(ConstantesComunes.VACIO);
        return votoPreferencialPorCorregir;
      }).toList();
      agrupolPorCorregir.setVotosPreferenciales(preferencialPorCorregirList);
    }
  }

  private void setOpcionesPorCorregirListToAgrupolPorCorregir(String codigoEleccion, DetActa detActa, AgrupolPorCorregir agrupolPorCorregir) {

    if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      List<DetActaOpcion> detActaPreferencialList = this.detActaOpcionService.findByDetActa(detActa);
      List<DetActaOpcion> detActaPreferencialListOrdenadaLista = detActaPreferencialList.stream().sorted(Comparator.comparing(DetActaOpcion::getPosicion)).toList();
      List<VotoOpcionPorCorregir> opcionPorCorregirList = detActaPreferencialListOrdenadaLista.stream().map(opcion -> {
        VotoOpcionPorCorregir votoOpcionPorCorregir = new VotoOpcionPorCorregir();
        votoOpcionPorCorregir.setPosicion(opcion.getPosicion());
        votoOpcionPorCorregir.setIdDetActaOpcion(opcion.getId());
        votoOpcionPorCorregir.setPrimeraDigitacion(opcion.getIlegiblev1() == null ? opcion.getVotosManual1() + ConstantesComunes.VACIO : opcion.getIlegiblev1());
        votoOpcionPorCorregir.setSegundaDigitacion(opcion.getIlegiblev2() == null ? opcion.getVotosManual2() + ConstantesComunes.VACIO : opcion.getIlegiblev2());
        votoOpcionPorCorregir.setTerceraDigitacion(ConstantesComunes.VACIO);
        return votoOpcionPorCorregir;
      }).toList();
      agrupolPorCorregir.setVotosOpciones(opcionPorCorregirList);
    }
  }



  private void setPreferencialPorCorregirListToAgrupolPorCorregirAchurados(String codigoEleccion, DetActa detActa,
      AgrupolPorCorregir agrupolPorCorregir) {
    if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {

      List<DetActaPreferencial> detActaPreferencialList = this.detActaPreferencialRepository.findByDetActa(detActa);
      List<DetActaPreferencial> detActaPreferencialListOrdenadaLista = detActaPreferencialList.stream().sorted(Comparator.comparing(DetActaPreferencial::getLista)).toList();
      List<VotoPreferencialPorCorregir> preferencialPorCorregirList = detActaPreferencialListOrdenadaLista.stream().map(e -> {
        VotoPreferencialPorCorregir votoPreferencialPorCorregir = new VotoPreferencialPorCorregir();
        votoPreferencialPorCorregir.setIdDetActaPreferencial(e.getId());
        votoPreferencialPorCorregir.setLista(e.getLista());
        votoPreferencialPorCorregir.setPrimeraDigitacion(ConstantesComunes.TEXTO_NULL);
        votoPreferencialPorCorregir.setSegundaDigitacion(ConstantesComunes.TEXTO_NULL);
        votoPreferencialPorCorregir.setTerceraDigitacion(ConstantesComunes.TEXTO_NULL);
        return votoPreferencialPorCorregir;
      }).toList();
      agrupolPorCorregir.setVotosPreferenciales(preferencialPorCorregirList);
    }
  }

  private void setOpcionesPorCorregirListToAgrupolPorCorregirAchurados(String codigoEleccion, DetActa detActa,
                                                                           AgrupolPorCorregir agrupolPorCorregir) {


    if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
      List<DetActaOpcion> detActaPreferencialList = this.detActaOpcionService.findByDetActa(detActa);
      List<DetActaOpcion> detActaPreferencialListOrdenadaLista = detActaPreferencialList.stream().sorted(Comparator.comparing(DetActaOpcion::getPosicion)).toList();
      List<VotoOpcionPorCorregir> opcionPorCorregirList = detActaPreferencialListOrdenadaLista.stream().map(opcion -> {
        VotoOpcionPorCorregir votoOpcionPorCorregir = new VotoOpcionPorCorregir();
        votoOpcionPorCorregir.setPosicion(opcion.getPosicion());
        votoOpcionPorCorregir.setIdDetActaOpcion(opcion.getId());
        votoOpcionPorCorregir.setPrimeraDigitacion(ConstantesComunes.TEXTO_NULL);
        votoOpcionPorCorregir.setSegundaDigitacion(ConstantesComunes.TEXTO_NULL);
        votoOpcionPorCorregir.setTerceraDigitacion(ConstantesComunes.TEXTO_NULL);
        return votoOpcionPorCorregir;
      }).toList();
      agrupolPorCorregir.setVotosOpciones(opcionPorCorregirList);
    }
  }






  private ActaPorCorregirListItem getActaPorCorregirListItem(Acta acta) {
    ActaPorCorregirListItem actaPorCorregirListItem = new ActaPorCorregirListItem();
    actaPorCorregirListItem.setActaId(acta.getId());
    actaPorCorregirListItem.setMesa(acta.getMesa().getCodigo());
    actaPorCorregirListItem.setCopia(acta.getNumeroCopia());
    actaPorCorregirListItem.setDigitoChequeo(acta.getDigitoChequeoEscrutinio());

    UbigeoEleccion ubigeoEleccion = acta.getUbigeoEleccion();
    Eleccion eleccion = ubigeoEleccion.getEleccion();
    Ubigeo ubigeo = ubigeoEleccion.getUbigeo();

    actaPorCorregirListItem.setEleccion(eleccion.getNombre());
    actaPorCorregirListItem.setCodigoEleccion(eleccion.getCodigo());
    actaPorCorregirListItem.setCantidadColumnas(this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId()));

    actaPorCorregirListItem.setElectoresHabiles(acta.getElectoresHabiles());
    actaPorCorregirListItem.setUbigeo(ubigeo.getDepartamento() + " / " + ubigeo.getProvincia() + " / " + ubigeo.getNombre());
    actaPorCorregirListItem.setActa1FileId(acta.getArchivoEscrutinio().getId());
    actaPorCorregirListItem.setActa2FileId(acta.getArchivoInstalacionSufragio().getId());
    return actaPorCorregirListItem;
  }

  private void getItemHistoriesTrazabilidad(Acta cabActa, List<ItemHistory> itemHistories) {
    List<ActaTransmisionNacion> traza = actaTrazabilidadService.trazaActaExcluidos(cabActa.getId());

    itemHistories.add(createItemHistoryPendiente(cabActa));

    int iteracionActaRecibida = 1;
    int iteracionActaAprobada = 1;
    int iteracionPrimeraVerificacion = 1;
    int iteracionSegundaVerificacion = 0;
    int iteracionPorCorregir = 1;

    for (ActaTransmisionNacion actaTransmision : traza) {
      TransmisionDto transmisionDto = actaTransmision.getRequestActaTransmision();
      ActaPorTransmitirDto acta = (transmisionDto != null) ? transmisionDto.getActaTransmitida() : null;
      if (transmisionDto == null || acta == null) continue;

      String estadoEvaluar = acta.getEstadoActa() + acta.getEstadoCc() + acta.getEstadoDigitalizacion();
      ItemHistory itemHistory = this.trazabilidadService.switchItemHistoryByEstado(estadoEvaluar, actaTransmision, acta,
          iteracionActaRecibida, iteracionActaAprobada, iteracionPrimeraVerificacion, iteracionSegundaVerificacion, iteracionPorCorregir);

      if(itemHistory != null){
        itemHistory.setActivo(actaTransmision.getActivo());
        // se actualizan contadores si es necesario
        iteracionActaRecibida = updateContadorSiAplica(estadoEvaluar,iteracionActaRecibida, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTA_RECIBIDA);
        iteracionActaAprobada = updateContadorSiAplica(estadoEvaluar, iteracionActaAprobada, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTA_APROBADA, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTA_RECHAZADA);
        iteracionPrimeraVerificacion = updateContadorSiAplica(estadoEvaluar, iteracionPrimeraVerificacion, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTA_1ERA_VERIFICACION);
        iteracionSegundaVerificacion = updateContadorSiAplica(estadoEvaluar, iteracionSegundaVerificacion, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTA_2DA_VERIFICACION);
        iteracionPorCorregir = updateContadorSiAplica(estadoEvaluar, iteracionPorCorregir, ConstantesEstadosTrazabilidad.ESTADOS_COMBINADOS_ACTAXCORREGIR);

        itemHistories.add(itemHistory);
      }
    }
  }

  private int updateContadorSiAplica(String estadoEvaluar, int contador, String... valorEsperados) {
    for (String valor : valorEsperados) {
      if (estadoEvaluar.contains(valor)) {
        return contador + 1;
      }
    }
    return contador;
  }

  private ItemHistory createItemHistoryPendiente(Acta acta) {

    String fechaItem;
    if(acta.getFechaModificacion()==null){
      fechaItem = DateUtil.getDateString(null, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH);
    }else{
      fechaItem = DateUtil.getDateString(acta.getFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH);
    }

    return ItemHistory.builder()
        .id(0L)
        .codEstadoActa("ANP")
        .descripcionEstado(ConstantesComunes.MENSAJE_ACTA_PENDIENTE_DIGITALIZACION)
        .detalle("El acta se encuentra en un estado pendiente")
        .fecha(fechaItem)
        .activo(ConstantesComunes.ACTIVO)
        .build();
  }

  private InfoActa getInfoActaTrazabilidad(Acta acta,
                                           String nroMesa,
                                           @Nullable DetTipoEleccionDocumentoElectoral admDetTipoEleccion) {
    InfoActa infoActa = new InfoActa();

    infoActa.setActaId(acta.getId());
    infoActa.setEleccion(
        admDetTipoEleccion == null
            ? acta.getUbigeoEleccion().getEleccion().getNombre()
            : admDetTipoEleccion.getEleccion().getNombre()
    );
    infoActa.setMesa(nroMesa);
    infoActa.setCopia(acta.getNumeroCopia());
    infoActa.setDigitoChequeo(acta.getDigitoChequeoEscrutinio());
    infoActa.setElectoresHabiles(acta.getElectoresHabiles());
    infoActa.setTotalVotantes(acta.getCvas() == null ? 0L : acta.getCvas());
    Long participacion = (infoActa.getTotalVotantes() / infoActa.getElectoresHabiles()) * 100;
    infoActa.setParticipacionCiudadana(participacion + "%");

    UbigeoEleccion detUbigeoEleccion = acta.getUbigeoEleccion();
    Ubigeo maeUbigeo = detUbigeoEleccion.getUbigeo();
    infoActa.setUbigeoEleccion(detUbigeoEleccion.getId().toString());
    infoActa.setDepartamento(maeUbigeo.getDepartamento());
    infoActa.setProvincia(maeUbigeo.getProvincia());
    infoActa.setDistrito(maeUbigeo.getNombre());
    infoActa.setLocalVotacion(acta.getMesa().getLocalVotacion().getNombre());
    infoActa.setDescripcionEstadoActual("");

    return infoActa;
  }



  ProcesoResult<Acta, DetTipoEleccionDocumentoElectoral> validarCabActaTrazabilidad(String nroMesa, String copia, String digitoChequeo) {
      Mesa mesa = this.mesaRepository.findByCodigo(nroMesa);
    if (mesa == null) {
      return new ProcesoResult<>(true, String.format(ConstantesMensajes.MSJ_FORMAT_NUMERO_MESA_NO_EXISTE, nroMesa));
    }

    DetTipoEleccionDocumentoElectoral documentoElectoralHistorial =  this.detTipoEleccionDocumentoElectoralService.findByCopia(copia);

    if(documentoElectoralHistorial==null){
      return new ProcesoResult<>(true, String.format("No está configurado el Acta de Escrutinio para la copia %s.",copia));
    }

    Eleccion eleccion = documentoElectoralHistorial.getEleccion();

    String codigoEleccion = eleccion.getCodigo();
    String nombreEleccion = eleccion.getNombre();

    List<Acta> actaList = this.cabActaRepository.findByMesa(mesa);
    if (actaList.isEmpty()) {
      return new ProcesoResult<>(true, String.format("No existen actas para la mesa %s.", nroMesa));
    }

    Optional<Acta> optionalActa = actaList.stream().filter((cabActa1 -> cabActa1.getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))).findAny();

    if(optionalActa.isEmpty())
      return new ProcesoResult<>(true, String.format("No existe la elección %s, para la mesa %s.",nombreEleccion, nroMesa));

    Acta acta = optionalActa.get();

    if(acta.getNumeroCopia() == null || acta.getDigitoChequeoEscrutinio()==null){
      return new ProcesoResult<>(true, String.format("El número de copia %s no se encuentra registrada.",copia.concat(digitoChequeo)));
    }

    String copiaRegistrada  = acta.getNumeroCopia().concat(acta.getDigitoChequeoEscrutinio());
    String copiaParametro = copia.concat(digitoChequeo);

    if(!copiaRegistrada.equals(copiaParametro))
      return new ProcesoResult<>(true, String.format("La copia %s, no coincide con lo registrado en BD.",copia.concat(digitoChequeo)));


    return optionalActa.map(cabActa -> new ProcesoResult<>(false, "", cabActa, documentoElectoralHistorial))
        .orElseGet(() -> new ProcesoResult<>(true, String.format("La mesa %s no existe para la elección ", codigoEleccion)));

  }

  ProcesoResult<List<Acta>, DetTipoEleccionDocumentoElectoral> validarListCabActaTrazabilidad(String nroMesa) {
    Mesa mesa = this.mesaRepository.findByCodigo(nroMesa);
    if (mesa == null) {
      return new ProcesoResult<>(true, String.format(ConstantesMensajes.MSJ_FORMAT_NUMERO_MESA_NO_EXISTE, nroMesa));
    }

    List<Acta> actaList = this.cabActaRepository.findByMesaOrderById(mesa);
    if (actaList.isEmpty()) {
      return new ProcesoResult<>(true, String.format("No existen actas para la mesa %s.", nroMesa));
    }


      return new ProcesoResult<>(false, "", actaList, null);
  }

  @Override
  public void save(Acta acta) {
    this.cabActaRepository.save(acta);
  }

  @Override
  public void saveAll(List<Acta> k) {
    this.cabActaRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.cabActaRepository.deleteAll();
  }

  @Override
  public List<Acta> findAll() {
    return this.cabActaRepository.findAll();
  }


  private List<Acta> getActasDigitalizadasAAsignar(String usuario, String codigoEleccion) {

    List<Acta> listActasTotalesAsignadas =
        this.cabActaRepository.findByEstadoDigitalizacionAndUsuarioAsignado(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA, usuario);
    List<Acta> listActasTotalesAsignadasPorEleccion =
        listActasTotalesAsignadas.stream().filter(acta -> acta.getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion)).toList();

    // Si no tiene actas asignadas o son insuficientes, completar hasta el límite
    if (listActasTotalesAsignadasPorEleccion.isEmpty() || listActasTotalesAsignadasPorEleccion.size() < ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION) {
      int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION - listActasTotalesAsignadasPorEleccion.size();

      List<Acta> listActasDigitalizadasLibres =
          this.cabActaRepository.findByEstadoDigitalizacionAndUsuarioAsignadoIsNull(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);

      List<Acta> listActasDigitalizadasLibresPorEleccion =
          new ArrayList<>(listActasDigitalizadasLibres.stream().filter(acta -> acta.getUbigeoEleccion().getEleccion().getCodigo().equals(codigoEleccion))
              .toList());

      Collections.shuffle(listActasDigitalizadasLibresPorEleccion);

      List<Acta> actasAdicionales = listActasDigitalizadasLibresPorEleccion.stream()
          .limit(nuevaDistribucion)
          .toList();

      actualizarActasAsignadasListActas(usuario, actasAdicionales);

      // Crear nueva lista combinada
      List<Acta> listaCombinada = new ArrayList<>(listActasTotalesAsignadasPorEleccion);
      listaCombinada.addAll(actasAdicionales);
      
      return listaCombinada;
    }

    return listActasTotalesAsignadasPorEleccion;
  }

  private void actualizarActasAsignadasListActas(String usuario, List<Acta> listActasDigitalizadasAsignadasNuevas) {
    Usuario tabUsuario = this.usuarioService.findByUsername(usuario);
    int asignadasActual = tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas();
    tabUsuario.setActasAsignadas(asignadasActual + listActasDigitalizadasAsignadasNuevas.size());
    this.usuarioService.save(tabUsuario);
  }

  private void guardarAsignacionActaListActas(Acta cabActa, String usuario, Date fecha) {
    cabActa.setAsignado(1);
    cabActa.setUsuarioAsignado(usuario);
    cabActa.setUsuarioModificacion(usuario);
    cabActa.setFechaModificacion(fecha);
    this.cabActaRepository.save(cabActa);
  }

  private void validaRequestApproveMesa(Acta acta, DigitizationApproveMesaRequest request) {
    if (request.getEstado() != null) {
      if (acta.getArchivoEscrutinio() == null || acta.getArchivoInstalacionSufragio() == null) {
        throw new BadRequestException("Acta incompleta");
      }

      if (!acta.getArchivoEscrutinio().getId().equals(request.getFileId1()) || !acta.getArchivoInstalacionSufragio().getId()
          .equals(request.getFileId2())) {
        throw new BadRequestException("El estado de la acta ha cambiado, por favor recargue la página");
      }

      if (!acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
        throw new BadRequestException("Acta no está pendiente de aprobacion");
      }
    } else {
      if (!acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
        throw new BadRequestException("Acta no está pendiente de aprobacion");
      }
    }
  }

  private void actualizarActaApproveMesa(Acta acta, String usuario, Date fecha) {
    acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
    acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA);
    acta.setUsuarioModificacion(usuario);
    acta.setFechaModificacion(fecha);
    this.cabActaRepository.save(acta);

  }

  private void actualizarActasUsuario(String usuario) {
    Usuario tabUsuario = this.usuarioService.findByUsername(usuario);
    if(tabUsuario!=null) {
      tabUsuario.setActasAsignadas((tabUsuario.getActasAsignadas() == null ? 0 : tabUsuario.getActasAsignadas()) - 1); //un acta atendida
      tabUsuario.setActasAtendidas((tabUsuario.getActasAtendidas() == null ? 0 : tabUsuario.getActasAtendidas()) + 1); //un acta atendida
      this.usuarioService.save(tabUsuario);
    }
  }

  
  @Override
  public Object[] summaryControlCalidad(String codigoEleccion, String estadoDigitalizacion, String estadoComputoPen,
                                        String estadoDigitalizadaVal, String estadoComputoVal, String estadoDigPendiente, String estadoActaProcesada) {
	return this.cabActaRepository.getControlCalidadSummary(codigoEleccion, estadoDigitalizacion, estadoComputoPen,
			estadoDigitalizadaVal, estadoComputoVal, estadosExluidosControlCalidad(), estadoDigPendiente, estadoActaProcesada);
  }
  
  @Override
  public List<Acta> actasPendientesControlCalidadAsignados(String usuarioControlCalidad, String codigoEleccion) {
	  return this.cabActaRepository.listarActasPendientesCcAsignados(
			  ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA,
			  ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA,
			  usuarioControlCalidad,
			  codigoEleccion,
			  estadosExluidosControlCalidad(),
              ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
              ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION
			  );
  }
  
  @Override
  @Transactional
  public void observarActaControlCalidad(Long idActa, TokenInfo tokenInfo) {
    
	Optional<Acta> optionalCabActa = this.cabActaRepository.findById(idActa);
    if (optionalCabActa.isEmpty()) {
      logger.error( ConstantesComunes.MENSAJE_LOG_ERROR_ACTA_NO_EXISTE_PARA_ELECION, idActa);
      return;
    }
    Acta acta = optionalCabActa.get();

    acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
    acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);    
    SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO);    
    acta.setFechaModificacion(new Date());
    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    this.cabActaRepository.save(acta);
    
    this.logService.registrarLog(
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            String.format("Se observó el acta %s, en control de calidad de actas por el usuario %s.", SceUtils.getNumMesaAndCopia(acta), tokenInfo.getNombreUsuario()),
            tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1
    );

  }
  
  @Override
  @Transactional
  public void aceptarActaControlCalidad(Long idActa, TokenInfo tokenInfo) {
    
	Optional<Acta> optionalCabActa = this.cabActaRepository.findById(idActa);
    if (optionalCabActa.isEmpty()) {
      logger.error("El actaID {}, no se encuentra registrado.", idActa);
      return;
    }
    Acta acta = optionalCabActa.get();

    acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);
    acta.setFechaModificacion(new Date());
    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    this.cabActaRepository.save(acta);
    
    this.logService.registrarLog(
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
        String.format("Se aceptó el acta %s, en control de calidad de actas por el usuario %s.", SceUtils.getNumMesaAndCopia(acta), tokenInfo.getNombreUsuario()),
        tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1
    );

  }
  
  @Override
  @Transactional
  public void asignarUsuarioActaControlCalidad(String codigoEleccion, TokenInfo tokenInfo, int cantidad) {
	List<Acta> actas = this.cabActaRepository.listarActasPendientesCcNoAsignados(
							ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA,
							ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA,							
							codigoEleccion,
							estadosExluidosControlCalidad(),
                            ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
                            ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION);
	
	actas.stream()
		.limit(cantidad)
		.forEach( acta -> {
			acta.setFechaModificacion(new Date());
    	    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
    	    acta.setUsuarioControlCalidad(tokenInfo.getNombreUsuario());
    	    this.cabActaRepository.save(acta);
		});
	
    this.logService.registrarLog (
            tokenInfo.getNombreUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            String.format("Se asignaron actas, en control de calidad de actas para el usuario %s.", tokenInfo.getNombreUsuario()),
            tokenInfo.getCodigoCentroComputo(),
            ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1
    );

  }
  
  @Override
  @Transactional
  public void desasignarUsuarioActaControlCalidad(List<Long> idsActas, TokenInfo tokenInfo) {
	  List<Acta> actasList = this.cabActaRepository.findAllById(idsActas);
	  
	  actasList.stream().forEach( acta -> {
			acta.setFechaModificacion(new Date());
		    acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
		    acta.setUsuarioControlCalidad(null);
            acta.setFechaControlCalidad(null);
		    this.cabActaRepository.save(acta);
	  });
	    
	    this.logService.registrarLog(tokenInfo.getNombreUsuario(),
	        Thread.currentThread().getStackTrace()[1].getMethodName(),
	        String.format("Se desasignaron las actas pendientes de control de calidad al usuario %s.", tokenInfo.getNombreUsuario()),
            tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1
	    );

  }

  @Override
  public ArchivosActaDTO listarArchivosPorSolucion(Long actaId) {

    Acta acta = cabActaRepository.findById(actaId)
            .orElseThrow(() -> new BadRequestException(
                    "No se encontró el acta con identificador: " + actaId));

    List<DetCatalogoEstructuraDTO> mapSolucionTecnologica =
            detalleCatalogoEstructuraService.findByMaestroAndColumna(
                    ConstantesCatalogo.MAE_SOLUCION_TECNOLOGICA,
                    ConstantesCatalogo.DET_SOLUCION_TECNOLOGICA
            );

    List<DetCatalogoEstructuraDTO> mapTipoTransmision =
            detalleCatalogoEstructuraService.findByMaestroAndColumna(
                    ConstantesCatalogo.MAE_TIPO_TRANSMISION,
                    ConstantesCatalogo.DET_TIPO_TRANSMISION
            );

    String descSolucion = Optional.ofNullable(acta.getSolucionTecnologica())
            .flatMap(sol -> mapSolucionTecnologica.stream()
                    .filter(tp -> tp.getCodigoI().equals(sol.intValue()))
                    .map(DetCatalogoEstructuraDTO::getNombre)
                    .findFirst())
            .orElse(ConstantesComunes.VACIO);

    String descTipoTransmision = Optional.ofNullable(acta.getTipoTransmision())
            .flatMap(tipo -> mapTipoTransmision.stream()
                    .filter(tp -> tp.getCodigoI().equals(tipo))
                    .map(DetCatalogoEstructuraDTO::getNombre)
                    .findFirst())
            .orElse(ConstantesComunes.VACIO);


    ArchivosActaDTO dto = new ArchivosActaDTO();
    dto.setActaId(actaId);

    Optional.ofNullable(acta.getArchivoEscrutinio())
            .map(Archivo::getId)
            .ifPresent(dto::setIdArchivoEscrutinio);

    Optional.ofNullable(acta.getArchivoInstalacion())
            .map(Archivo::getId)
            .ifPresent(dto::setIdArchivoInstalacion);

    Optional.ofNullable(acta.getArchivoSufragio())
            .map(Archivo::getId)
            .ifPresent(dto::setIdArchivoSufragio);

    Optional.ofNullable(acta.getArchivoInstalacionSufragio())
            .map(Archivo::getId)
            .ifPresent(dto::setIdArchivoInstalacionSufragio);

    dto.setCodigoSolucionTecnologica(
            Optional.ofNullable(acta.getSolucionTecnologica())
                    .map(Long::intValue)
                    .orElse(null)
    );
    dto.setDescSolucionTecnologica(descSolucion);
    dto.setCodigoTipoTransmision(acta.getTipoTransmision());
    dto.setDescripcionTipoTransmision(descTipoTransmision);

    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Long> listarArchivosPorActa(Long actaId, String mesa, String codigoEleccion) {

    actaId = getIdActaPorMesaEleccion(actaId, mesa, codigoEleccion);

    Long finalActaId = actaId;
    Acta acta = cabActaRepository.findById(actaId)
            .orElseThrow(() -> new BadRequestException(String.format("El acta %s no existe.", finalActaId)));

    List<Long> archivos = new ArrayList<>();

    if (Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)
            || Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_VOTO_DIGITAL)) {

      if (ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA.equals(acta.getTipoTransmision())) {
        archivos.add(resolveArchivoId(acta.getArchivoEscrutinioFirmado(),
                acta.getArchivoEscrutinioPdf(),
                acta.getArchivoEscrutinio()));

        archivos.add(resolveArchivoId(acta.getArchivoInstalacionSufragioFirmado(),
                acta.getArchivoInstalacionSufragioPdf(),
                acta.getArchivoInstalacionSufragio()));

      } else if (ConstantesComunes.TIPO_HOJA_STAE_TRANSMITIDA.equals(acta.getTipoTransmision())) {
        archivos.add(resolveArchivoId(acta.getArchivoEscrutinioFirmado(),
                acta.getArchivoEscrutinioPdf(),
                acta.getArchivoEscrutinio()));

        archivos.add(resolveArchivoId(acta.getArchivoInstalacionFirmado(),
                acta.getArchivoInstalacionPdf(),
                acta.getArchivoInstalacion()));

        archivos.add(resolveArchivoId(acta.getArchivoSufragioFirmado(),
                acta.getArchivoSufragioPdf(),
                acta.getArchivoSufragio()));

      } else {
        throw new BadRequestException("El acta STAE tiene un tipo de transmisión inválido.");
      }

    } else if (Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL)) {

      if (ConstantesComunes.TIPO_HOJA_CONVENCIOANL.equals(acta.getTipoTransmision())) {
        archivos.add(resolveArchivoId(acta.getArchivoEscrutinioFirmado(),
                acta.getArchivoEscrutinioPdf(),
                acta.getArchivoEscrutinio()));

        archivos.add(resolveArchivoId(acta.getArchivoInstalacionSufragioFirmado(),
                acta.getArchivoInstalacionSufragioPdf(),
                acta.getArchivoInstalacionSufragio()));

      } else {
        throw new BadRequestException("El acta convencional no tiene registrado su tipo de transmisión.");
      }

    } else {
      throw new BadRequestException("Solución tecnológica no soportada.");
    }

    return archivos;
  }


  /**
   * Parametro estadoDigitalizacion sea
   * D -> c_estado_digitalizacion = 'D'
   * O -> c_estado_digitalizacion in ('O','X')
   * C -> c_estado_digitalizacion in ('K','C','B')
   * P -> c_estado_digitalizacion = 'P'
   * */
  @Override
  public List<ActaScanProjection> listActasSceScanner(String codigoEleccion, String estadoDigitalizacion) {
    logger.info("Iniciando listActasSceScanner - codigoEleccion: {}, estadoDigitalizacion: {}", codigoEleccion, estadoDigitalizacion);

    List<String> estados = new ArrayList<>();

    if (estadoDigitalizacion == null || estadoDigitalizacion.isEmpty()) {
      logger.info("Estado digitalizacion vacío, agregando todos los estados");
      // Si está vacío, agregar todos los estados
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);
      estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);
    } else {
      switch (estadoDigitalizacion) {
        case ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA:
          logger.info("Filtrando por estado DIGITALIZADA");
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);
          break;
        case ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA:
          logger.info("Filtrando por estado 1ER_CONTROL_RECHAZADA y 1ERA_DIGITACION_RECHAZADA");
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA);
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ERA_DIGITACION_RECHAZADA);
          break;
        case ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA:
          logger.info("Filtrando por estado 1ER_CONTROL_ACEPTADA, REVISADA_1ER_CC y 2DO_CONTROL_ACEPTADA");
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_REVISADA_1ER_CC_VISOR_ABIERTO);
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA);
          break;
        case ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
             ConstantesEstadoActa.ESTADO_DIGTAL_TEMP_SCESCANNER_NO_INSTA_EXTRA_SINIE:
          logger.info("Filtrando por estado PENDIENTE_DIGITALIZACION y Z");
          estados.add(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
          break;
        default:
          logger.info("Estado no reconocido, usando valor directo: {}", estadoDigitalizacion);
          estados.add(estadoDigitalizacion);
      }
    }

    logger.info("Estados a filtrar: {}", estados);

    List<ActaScanProjection> resultado = this.cabActaRepository.findActasSceScanenr(codigoEleccion, estados);

    List<ActaScanProjection> resultadoFiltrado;

    if (estadoDigitalizacion!=null && estadoDigitalizacion.equals(ConstantesEstadoActa.ESTADO_DIGTAL_TEMP_SCESCANNER_NO_INSTA_EXTRA_SINIE)) {
      resultadoFiltrado = resultado.stream()
              .filter(this::isNoinstalalda)
              .toList();
    } else if (estadoDigitalizacion != null && !estadoDigitalizacion.isEmpty()) {
      resultadoFiltrado = resultado.stream()
              .filter(acta -> !isNoinstalalda(acta))
              .toList();
    } else {
      resultadoFiltrado = resultado;
    }

    logger.info("Total de actas encontradas: {}", resultadoFiltrado.size());

    return resultadoFiltrado;

  }
  
  private List<String> estadosExluidosControlCalidad() {
	  return List.of(ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA,
				ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA,
				ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA);
  }

  private boolean isNoinstalalda(ActaScanProjection acta) {
    String estadoActa = acta.getEstadoActa();
    String estadoComputo = acta.getEstadoComputo();

    //  si coincide con alguna de las combinaciones
    return (ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(estadoActa)) ||
            (ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(estadoActa)) ||
            (ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA.equals(estadoActa) && ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA.equals(estadoComputo));
  }

  private Long getIdActaPorMesaEleccion(Long actaId, String mesa, String codigoEleccion) {
    if (actaId == null) {
      if (mesa == null || mesa.isBlank() || codigoEleccion == null || codigoEleccion.isBlank()) {
        throw new BadRequestException(
                "Debe enviar actaId o, en su defecto, mesa y codigoEleccion (no vacíos)."
        );
      }
      List<Acta> listActas = cabActaRepository.listarActasPorMesaAndCodigoEleccion(mesa, codigoEleccion);
      if(listActas.isEmpty()){ throw new BadRequestException(
              String.format("No existe acta para la mesa %s y codigo de elección %s.", mesa, codigoEleccion)
      );}
      Acta acta = listActas.getFirst();
      actaId = acta.getId();
    }
    return actaId;
  }

  /**
   * Resuelve el ID del archivo tomando la prioridad:
   * Firmado > PDF > Normal > null
   */
  private Long resolveArchivoId(Archivo firmado, Archivo pdf, Archivo normal) {
    if (firmado != null) return firmado.getId();
    if (pdf != null) return pdf.getId();
    if (normal != null) return normal.getId();
    return null;
  }


}
