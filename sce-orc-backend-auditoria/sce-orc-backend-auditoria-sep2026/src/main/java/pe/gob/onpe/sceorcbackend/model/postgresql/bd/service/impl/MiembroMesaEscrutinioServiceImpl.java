package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.MiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.MiembroMesaEscrutinioSeccionesDto;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroMiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.UbigeoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IMiembroMesaEscrutinioMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.SeccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MiembroMesaEscrutinio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MiembroMesaEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaServiceGroup;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaRectangleService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.LogUtil;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MiembroMesaEscrutinioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PadronElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.miembroMesaEscrutinio.MiembroMesaEscrutinioFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.miembroMesaEscrutinio.MiembroMesaEscrutinioSpec;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.ArchivosRectanguloDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ConstantesSecciones;
import pe.gob.onpe.sceorcbackend.utils.ActaUtils;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.TipoFiltro;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MiembroMesaEscrutinioServiceImpl implements MiembroMesaEscrutinioService {

  private final IMiembroMesaEscrutinioMapper miembroMesaEscrutinioMapper;

  private final MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository;

  private final MiembroMesaEscrutinioSpec spec;

  private final MesaService tabMesaService;

  private final PadronElectoralService maePadronService;

  private final DetActaRectangleService detActaRectangleService;

  private final PadronElectoralService padronElectoralService;

  private final CabActaService cabActaService;

  private final UbigeoService ubigeoService;

  private final EleccionService eleccionService;

  private final SeccionService adminSeccionService;

  private final ActaServiceGroup actaServiceGroup;

  private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;

  private final ITabLogService logService;

  private final LogUtil logUtil;

  public MiembroMesaEscrutinioServiceImpl(IMiembroMesaEscrutinioMapper miembroMesaEscrutinioMapper,
                                          MiembroMesaEscrutinioRepository miembroMesaEscrutinioRepository, MiembroMesaEscrutinioSpec spec, MesaService tabMesaService,
                                          PadronElectoralService maePadronService, DetActaRectangleService detActaRectangleService,
                                          PadronElectoralService padronElectoralService, CabActaService cabActaService, UbigeoService ubigeoService,
                                          EleccionService eleccionService, SeccionService adminSeccionService, ActaServiceGroup actaServiceGroup,
                                          ActaTransmisionExecuteService actaTransmisionNacionStrategyService, ITabLogService logService,
          LogUtil logUtil) {
    this.miembroMesaEscrutinioMapper = miembroMesaEscrutinioMapper;
    this.miembroMesaEscrutinioRepository = miembroMesaEscrutinioRepository;
    this.spec = spec;
    this.tabMesaService = tabMesaService;
    this.maePadronService = maePadronService;
    this.detActaRectangleService = detActaRectangleService;
    this.padronElectoralService = padronElectoralService;
    this.cabActaService = cabActaService;
    this.ubigeoService = ubigeoService;
    this.eleccionService = eleccionService;
    this.adminSeccionService = adminSeccionService;
    this.actaServiceGroup = actaServiceGroup;
    this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
    this.logService = logService;
      this.logUtil = logUtil;
  }

  @Transactional
  @Override
  public void save(MiembroMesaEscrutinioDTO miembroMesaEscrutinioDTO, TokenInfo tokenInfo) {
    try {
     final String mesa = miembroMesaEscrutinioDTO.getMesa().getMesa();
     String mensaje = "Miembros de mesa de escrutinio " +  mesa + " se ha guardado con éxito.";
      if(!miembroMesaEscrutinioDTO.getTipoFiltro().equals(TipoFiltro.NOINSTALADA.getValue())){
        if(miembroMesaEscrutinioDTO.getId() != 0L){
          MiembroMesaEscrutinio miembroMesaEscrutinio = this.miembroMesaEscrutinioRepository.getReferenceById(miembroMesaEscrutinioDTO.getId());
          miembroMesaEscrutinioDTO.setActivo(miembroMesaEscrutinio.getActivo());
          miembroMesaEscrutinioDTO.setFechaCreacion(miembroMesaEscrutinio.getFechaCreacion());
          miembroMesaEscrutinioDTO.setUsuarioCreacion(miembroMesaEscrutinio.getUsuarioCreacion());
          miembroMesaEscrutinioDTO.setFechaModificacion(new Date());
          miembroMesaEscrutinioDTO.setUsuarioModificacion(miembroMesaEscrutinioDTO.getUsuarioCreacion());
          mensaje = "Se realizó el reprocesamiento de Miembros de mesa de escrutinio de la mesa " + mesa;
        }
        this.miembroMesaEscrutinioRepository.save(this.miembroMesaEscrutinioMapper.dtoToEntity(miembroMesaEscrutinioDTO));
      }

      this.tabMesaService.actualizarEstadoDigitalizaionME(miembroMesaEscrutinioDTO.getMesa().getId(), miembroMesaEscrutinioDTO.getUsuarioCreacion(), ConstantesEstadoMesa.INSTALADA);


      this.logService.registrarLog(
              tokenInfo.getNombreUsuario(),
              Thread.currentThread().getStackTrace()[1].getMethodName(),  mensaje,
              tokenInfo.getCodigoCentroComputo(),
              ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
  }

  @Override
  public void save(MiembroMesaEscrutinioDTO miembroMesaEscrutinioDTO) {
    throw new UnsupportedOperationException("Método no implementado");
  }

  @Override
  public void saveAll(List<MiembroMesaEscrutinioDTO> k) {
    throw new UnsupportedOperationException("Método no implementado");
  }

  @Override
  public void deleteAll() {
    throw new UnsupportedOperationException("Método no implementado");
  }

  @Override
  public List<MiembroMesaEscrutinioDTO> findAll() {
    return null;
  }

  @Override
  public SearchFilterResponse<MiembroMesaEscrutinioDTO> listPaginted(String numeroDocumento, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, "fechaCreacion"));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);
    MiembroMesaEscrutinioFilter miembroMesaEscrutinioFilter =
        MiembroMesaEscrutinioFilter.builder().numeroDocumento(numeroDocumento).build();
    Specification<MiembroMesaEscrutinio> specMiembroMesaEscrutinio = this.spec.filter(miembroMesaEscrutinioFilter);
    Page<MiembroMesaEscrutinio> miembroMesaEscrutinioPage =
        this.miembroMesaEscrutinioRepository.findAll(specMiembroMesaEscrutinio, pageable);
    List<MiembroMesaEscrutinioDTO> miembroMesaEscrutinioResponse =
        miembroMesaEscrutinioPage.getContent().stream().map(this.miembroMesaEscrutinioMapper::entityToDTO).collect(Collectors.toList());
    return new SearchFilterResponse(miembroMesaEscrutinioResponse,
        page <= 1 ? miembroMesaEscrutinioResponse.size() : (size * (page - 1)) + miembroMesaEscrutinioResponse.size(), page,
        miembroMesaEscrutinioPage.getTotalElements(), miembroMesaEscrutinioPage.getTotalPages());
  }

  @Override
  public GenericResponse<RegistroMiembroMesaEscrutinioDTO> getRandomMesa(String usuario, Long idproceso, Integer tipoFiltro, boolean reprocesar) {

    Eleccion eleccion = this.eleccionService.obtenerEleccionPrincipalPorProceso(idproceso);
    if (Objects.isNull(eleccion)) {
      return new GenericResponse<>(false, "No existe una elección principal para el proceso seleccionado", null);
    }

    List<String> estados;
    if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue())) {
      estados = Arrays.asList("C", "B");
    } else if (tipoFiltro.equals(TipoFiltro.NOINSTALADA.getValue())) {
      estados = Arrays.asList("N");
    } else if (tipoFiltro.equals(TipoFiltro.EXTRAVIADASI.getValue())) {
      estados = Arrays.asList("O", "S");
    } else {
      estados = new ArrayList<>();
    }

    MesaActaDto mesaActaDto = getMesaRandom(eleccion.getCodigo(), estados, tipoFiltro, reprocesar);
    if (mesaActaDto == null) {
      return new GenericResponse<>(false, "No existen mesas disponibles", null);
    }

    final Acta acta = mesaActaDto.getActa();

    final Mesa mesa = mesaActaDto.getMesa();
      this.logUtil.iniciarPropiedadesLog(mesaActaDto.getMesa().getCodigo(), acta.getId());
      log.info("[START] random MME mesa={} acta={}", mesaActaDto.getMesa().getCodigo(), acta.getId());
    try {
        if (Objects.isNull(acta)) {
            return new GenericResponse<>(false, "No existen actas disponibles para el número de mesa", null);
        }

        List<PadronElectoral> maePadronList = this.maePadronService.findPadronElectoralByCodigoMesaOrderByOrden(mesa.getCodigo());
        if (maePadronList.isEmpty()) {
            return new GenericResponse<>(false, String.format("La mesa %s no tiene registrado su padrón electoral ", mesa.getCodigo()), null);
        }

        MiembroMesaEscrutinioSeccionesDto archivosRectanguloDtos = new MiembroMesaEscrutinioSeccionesDto();
        archivosRectanguloDtos.setArchivoPresidente(new ArchivosRectanguloDto());
        archivosRectanguloDtos.setArchivoSecretario(new ArchivosRectanguloDto());
        archivosRectanguloDtos.setArchivoTercerMiembro(new ArchivosRectanguloDto());

        if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue()) && !ActaUtils.isActaReconteo(acta)) {
            List<DetActaRectangleDTO> detRectangulos = this.actaServiceGroup.getDetActaRectangleService().findByActaId(acta.getId());
            if (detRectangulos.isEmpty()) {
                return new GenericResponse(false, "No existen registro de cortes para la mesa " + mesa.getCodigo(), null);
            }
            archivosRectanguloDtos = getSectionMiembroMesa(acta, detRectangulos);
        }

        Ubigeo ubigeo = mesa.getLocalVotacion().getUbigeo();
        UbigeoDTO ubigeoDTO = this.ubigeoService.obtenerJerarquiaUbigeo(ubigeo.getCodigo());
        this.tabMesaService.actualizarEstadoDigitalizaionME(mesaActaDto.getMesa().getId(), usuario, ConstantesEstadoMesa.IS_EDIT);
        List<MiembroMesaEscrutinioDTO> dataMe = null;
        if (reprocesar) {
            dataMe = this.miembroMesaEscrutinioRepository.findByMesaId(mesa.getId()).stream()
                    .map(this.miembroMesaEscrutinioMapper::entityToDTO).toList();
        }
        log.info("[END] random MME");
        return new GenericResponse<>(true,
                String.format("Se obtuvo la información de la mesa %s.", mesa.getCodigo()),
                RegistroMiembroMesaEscrutinioDTO
                        .builder()
                        .actaId(acta.getId())
                        .mesaId(mesa.getId())
                        .type(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)
                        .mesa(mesa.getCodigo())
                        .electoresAusentes(calcularAusentes(mesaActaDto))
                        .electoresHabiles(mesa.getCantidadElectoresHabiles())
                        .localVotacion(mesa.getLocalVotacion().getNombre())
                        .ubigeo(ubigeo.getCodigo())
                        .departamento(ubigeoDTO.getDepartamento())
                        .provincia(ubigeoDTO.getProvincia())
                        .distrito(ubigeo.getNombre())
                        .secciones(archivosRectanguloDtos)
                        .data(dataMe)
                        .archivoEscrutinioId(Objects.nonNull(acta.getArchivoEscrutinio()) ? acta.getArchivoEscrutinio().getId() : null)
                        .archivoInstalacionId(
                                Objects.nonNull(acta.getArchivoInstalacionSufragio()) ? acta.getArchivoInstalacionSufragio().getId() : null)
                        .build());
    }finally {
        this.logUtil.limpiarPropiedadesLog();
    }
  }

  @Override
  public GenericResponse<PadronDto> consultaPadronPorDni(String dni, TokenInfo tokenInfo, Integer mesaId, boolean primeraConsultaR) {
    if (dni.isEmpty()) {
      return new GenericResponse<>(false, "El DNI se encuentra vacío.", null);
    }

    if (dni.length() != 8) {
      return new GenericResponse<>(false, "El DNI debe tener 8 dígitos", null);
    }
    if(miembroMesaEscrutinioRepository.existeDniRegistrado(dni) && !primeraConsultaR){
      return new GenericResponse<>(false, "El DNI ya se encuentra registrado", null);
    }

    Optional<PadronElectoral> optionalMaePadron = this.padronElectoralService.findByDocumentoIdentidad(dni);
    if (optionalMaePadron.isEmpty()) {
      return new GenericResponse<>(false, "El DNI no se encuentra registrado en el padrón electoral de este centro de cómputo.",
              null);
    }
    if(!mesaId.equals(optionalMaePadron.get().getMesaId())){
        return new GenericResponse<>(false, "El DNI no pertenece a la mesa.",
                null);
    }

    PadronElectoral maePadron = optionalMaePadron.get();

    PadronDto padronDto = new PadronDto();
    padronDto.setIdPadron(maePadron.getId());
    padronDto.setNombres(maePadron.getNombres());
    padronDto.setApellidoMaterno(maePadron.getApellidoMaterno());
    padronDto.setApellidoPaterno(maePadron.getApellidoPaterno());

    return new GenericResponse<>(true, "Se obtuvo correctamente los datos del DNI " + dni, padronDto);
  }

  MesaActaDto getMesaRandom(String codigoEleccion, List<String> estados, Integer tipoFiltro, boolean reprocesar) {
    List<MesaActaDto> mesaRam = this.tabMesaService.findMesaRamdomME(codigoEleccion, estados, tipoFiltro, reprocesar ?
            ConstantesEstadoMesa.REPROCESAR : ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE);
    return mesaRam.isEmpty() ? null : mesaRam.getFirst();
  }

  private MiembroMesaEscrutinioSeccionesDto getSectionMiembroMesa(Acta acta, List<DetActaRectangleDTO> detActaRectangleDTOS) throws
      GenericException {
    try {

      MiembroMesaEscrutinioSeccionesDto sectionResponse = new MiembroMesaEscrutinioSeccionesDto();

      DetActaRectangleDTO archivoRectPre = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_PRESIDENT);

      if (archivoRectPre == null) {
        throw new GenericException(
            String.format("No se obtuvo la sección de firma presidente del acta de escrutinio del acta %d.", acta.getId()));
      }

      DetActaRectangleDTO archivoRectSecre = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_SECRETARY);

      if (archivoRectSecre == null) {
        throw new GenericException(
            String.format("No se obtuvo la sección de firma de secretario del acta de escrutinio del acta %d.", acta.getId()));
      }

      DetActaRectangleDTO archivoRectTerMi = findByAbreviatura(detActaRectangleDTOS, ConstantesSecciones.SECTION_ABREV_SIGN_COUNT_THIRD_MEMBER);

      if (archivoRectTerMi == null) {
        throw new GenericException(
            String.format("No se obtuvo la sección de firma de tercer miembro del acta de escrutinio del acta %d.", acta.getId()));
      }

      ArchivosRectanguloDto signPre = new ArchivosRectanguloDto();
      signPre.setFileId(archivoRectPre.getArchivo());
      signPre.setSystemValue(null);

      ArchivosRectanguloDto signSecre = new ArchivosRectanguloDto();
      signSecre.setFileId(archivoRectSecre.getArchivo());
      signSecre.setSystemValue(null);

      ArchivosRectanguloDto signTerMi = new ArchivosRectanguloDto();
      signTerMi.setFileId(archivoRectTerMi.getArchivo());
      signTerMi.setSystemValue(null);

      sectionResponse.setArchivoPresidente(signPre);
      sectionResponse.setArchivoSecretario(signSecre);
      sectionResponse.setArchivoTercerMiembro(signTerMi);
        log.info("Presidente archivoId={}", archivoRectPre.getArchivo());
        log.info("Secretario archivoId={}", archivoRectSecre.getArchivo());
        log.info("Tercer Miembro archivoId={}", archivoRectTerMi.getArchivo());
      return sectionResponse;
    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }

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

  private Integer calcularAusentes(MesaActaDto mesaActaDto) {
    int habiles = 0;
    int cvas = 0;
    if (Objects.nonNull(mesaActaDto.getMesa().getCantidadElectoresHabiles())) {
      habiles = mesaActaDto.getMesa().getCantidadElectoresHabiles();
    }
    if (Objects.nonNull(mesaActaDto.getActa().getCvas())) {
      cvas = Math.toIntExact(mesaActaDto.getActa().getCvas());
    }
    final int total = habiles - cvas;
    return Math.max(total, 0);
  }
}
