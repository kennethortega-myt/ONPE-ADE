package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.PersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.PersoneroRequestDTO;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroPersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.UbigeoDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IPersoneroMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.Seccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.SeccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Personero;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PersoneroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaRectangleService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.LogUtil;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PadronElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PersoneroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UbigeoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.personero.PersoneroFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.personero.PersoneroSpec;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.DetActaRectangleDTO;
import pe.gob.onpe.sceorcbackend.utils.ActaUtils;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
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
public class PersoneroServiceImpl implements PersoneroService {

    private final IPersoneroMapper personeroMapper;

    private final PersoneroRepository personeroRepository;

    private final PersoneroSpec spec;

    private final MesaService tabMesaService;

    private final PadronElectoralService maePadronService;

    private final DetActaRectangleService detActaRectangleService;

    private final PadronElectoralService padronElectoralService;

    private final CabActaService cabActaService;

    private final UbigeoService ubigeoService;

    private final EleccionService eleccionService;

    private final SeccionService adminSeccionService;

    private final ITabLogService logService;

    private  final LogUtil logUtil;

    public PersoneroServiceImpl(IPersoneroMapper personeroMapper, PersoneroRepository personeroRepository, PersoneroSpec spec,
                                MesaService tabMesaService, PadronElectoralService maePadronService, DetActaRectangleService detActaRectangleService,
                                PadronElectoralService padronElectoralService, CabActaService cabActaService, UbigeoService ubigeoService,
                                EleccionService eleccionService, SeccionService adminSeccionService, ITabLogService logService,
            LogUtil logUtil) {
        this.personeroMapper = personeroMapper;
        this.personeroRepository = personeroRepository;
        this.spec = spec;
        this.tabMesaService = tabMesaService;
        this.maePadronService = maePadronService;
        this.detActaRectangleService = detActaRectangleService;
        this.padronElectoralService = padronElectoralService;
        this.cabActaService = cabActaService;
        this.ubigeoService = ubigeoService;
        this.eleccionService = eleccionService;
        this.adminSeccionService = adminSeccionService;
        this.logService = logService;
        this.logUtil = logUtil;
    }

    @Override
    public SearchFilterResponse<PersoneroDTO> listPaginted(String numeroDocumento, Integer page, Integer size) {
        List<Sort.Order> orderList = new ArrayList<>();
        orderList.add(new Sort.Order(Direction.DESC, "fechaCreacion"));
        Sort sort = Sort.by(orderList);
        final Pageable pageable = PageRequest.of(page, size, sort);
        PersoneroFilter personeroFilter =
                PersoneroFilter.builder().numeroDocumento(numeroDocumento).build();
        Specification<Personero> specPersonero = this.spec.filter(personeroFilter);
        Page<Personero> personeroPage = this.personeroRepository.findAll(specPersonero, pageable);
        List<PersoneroDTO> personeroResponse =
                personeroPage.getContent().stream().map(this.personeroMapper::entityToDTO).collect(Collectors.toList());
        return new SearchFilterResponse(personeroResponse,
                page <= 1 ? personeroResponse.size() : (size * (page - 1)) + personeroResponse.size(), page,
                personeroPage.getTotalElements(), personeroPage.getTotalPages());
    }

    @Override
    public GenericResponse<RegistroPersoneroDTO> getRandomMesa(String usuario, Long idProceso, Integer tipoFiltro, boolean reprocesar) {

        Eleccion eleccion = this.eleccionService.obtenerEleccionPrincipalPorProceso(idProceso);
        if (Objects.isNull(eleccion)) {
            return new GenericResponse<>(false, "No existe una elección principal para el proceso seleccionado", null);
        }

        List<String> estados;
        if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue())) {
            estados = List.of("C", "B");
        } else if (tipoFiltro.equals(TipoFiltro.NOINSTALADA.getValue())) {
            estados = List.of("N");
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
     
        if (Objects.isNull(acta)) {
            return new GenericResponse<>(false, "No existen actas disponibles para el número de mesa", null);
        }
        this.logUtil.iniciarPropiedadesLog(mesaActaDto.getMesa().getCodigo(), acta.getId());
        log.info("[START] random personeros mesa={} acta={}", mesaActaDto.getMesa().getCodigo(), acta.getId());
         List<PadronElectoral> maePadronList =
                this.maePadronService.findPadronElectoralByCodigoMesaOrderByOrden(mesaActaDto.getMesa().getCodigo());
try {
    Optional<Seccion> seccion = this.adminSeccionService.findByAbreviatura(SceConstantes.SECCION_ABREVIATURA_PERSONERO);
    if (!seccion.isPresent()) {
        return new GenericResponse<>(false, "No existen seccion con la abreviatura " + SceConstantes.SECCION_ABREVIATURA_PERSONERO, null);
    }
    if (maePadronList.isEmpty()) {
        return new GenericResponse<>(false,
                String.format("La mesa %s no tiene registrado su padrón electoral ", mesaActaDto.getMesa().getCodigo()), null);
    }
    DetActaRectangleDTO detRectangulo = new DetActaRectangleDTO();

    if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue()) && !ActaUtils.isActaReconteo(acta)) {
        List<DetActaRectangleDTO> detRectangulos =
                this.detActaRectangleService.findByActaIdAndSeccion(acta.getId(), seccion.get().getId());

        if (detRectangulos.isEmpty()) {
            return new GenericResponse<>(false,
                    String.format("EL modelo no ha generado las secciones para la mesa %s ", mesaActaDto.getMesa().getCodigo()), null);
        }
        detRectangulo = detRectangulos.getFirst();
        log.info("archivoId {}", detRectangulo.getArchivo());
    }
    List<PersoneroDTO> listaPersonero = null;
    if (reprocesar) {
        listaPersonero =
                this.personeroRepository.findByIdMesa(mesaActaDto.getMesa().getId()).stream().map(personeroMapper::entityToDTO).toList();
    }

    Ubigeo ubigeo = mesaActaDto.getMesa().getLocalVotacion().getUbigeo();
    UbigeoDTO ubigeoDTO = this.ubigeoService.obtenerJerarquiaUbigeo(ubigeo.getCodigo());
    this.tabMesaService.actualizarEstadoDigitalizaionPR(mesaActaDto.getMesa().getId(), usuario, ConstantesEstadoMesa.IS_EDIT);
    log.info("[END] random personeros");
    return new GenericResponse<>(true,
            String.format("Se obtuvo la información de la mesa %s.", mesaActaDto.getMesa().getCodigo()),
            RegistroPersoneroDTO
                    .builder()
                    .actaId(acta.getId())
                    .mesaId(mesaActaDto.getMesa().getId())
                    .type(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)
                    .mesa(mesaActaDto.getMesa().getCodigo())
                    .electoresAusentes(calcularAusentes(mesaActaDto))
                    .electoresHabiles(mesaActaDto.getMesa().getCantidadElectoresHabiles())
                    .localVotacion(mesaActaDto.getMesa().getLocalVotacion().getNombre())
                    .ubigeo(ubigeo.getCodigo())
                    .departamento(ubigeoDTO.getDepartamento())
                    .provincia(ubigeoDTO.getProvincia())
                    .distrito(ubigeo.getNombre())
                    .fileId(detRectangulo.getArchivo())
                    .data(listaPersonero)
                    .archivoEscrutinioId(Objects.nonNull(acta.getArchivoEscrutinio()) ? acta.getArchivoEscrutinio().getId() : null)
                    .archivoInstalacionId(
                            Objects.nonNull(acta.getArchivoInstalacionSufragio()) ? acta.getArchivoInstalacionSufragio().getId() : null)
                    .build());
}finally {
    this.logUtil.limpiarPropiedadesLog();
}
    }

    @Override
    public GenericResponse<PadronDto> consultaPadronPorDni(String dni, TokenInfo tokenInfo, Integer mesaId) {
        if (dni.isEmpty()) {
            return new GenericResponse<>(false, "El DNI se encuentra vacío.", null);
        }

        if (dni.length() != 8) {
            return new GenericResponse<>(false, "El DNI debe tener 8 dígitos", null);
        }


        Optional<PadronElectoral> optionalMaePadron = this.padronElectoralService.findByDocumentoIdentidad(dni);
        if (optionalMaePadron.isEmpty()) {
            return new GenericResponse<>(false, "El DNI ingresado no se encuentra en el padrón del centro de cómputo. Aún así se permitirá el registro.",
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

    @Transactional
    @Override
    public void save(PersoneroDTO personeroDTO, PersoneroRequestDTO request, TokenInfo tokenInfo) {
        try {
            final String mesa = request.getMesa().getMesa();
            if (!request.getTipoFiltro().equals(TipoFiltro.NOINSTALADA.getValue())) {
                if(Objects.nonNull(personeroDTO.getAgrupacionPolitica()) && personeroDTO.getAgrupacionPolitica().getId() == 0){
                    personeroDTO.setAgrupacionPolitica(null);
                }
                if(!request.getPersoneros().isEmpty()){
                    if(Objects.isNull(personeroDTO.getId())){
                        this.personeroRepository.save(this.personeroMapper.dtoToEntity(personeroDTO));
                    }else{
                        this.personeroRepository.actualizarPersonero(personeroDTO.getActivo(), personeroDTO.getUsuarioCreacion(), personeroDTO.getId());
                    }

                }

            }
            this.tabMesaService.actualizarEstadoDigitalizaionPR(personeroDTO.getMesa().getId(), personeroDTO.getUsuarioCreacion(),
                    ConstantesEstadoMesa.INSTALADA);


            this.logService.registrarLog(
                    tokenInfo.getNombreUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("Los personeros de la mesa %s se guardaron de forma correcta", mesa),
                    tokenInfo.getCodigoCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    MesaActaDto getMesaRandom(String codigoEleccion, List<String> estados, Integer tipoFiltro, boolean reprocesar) {
        List<MesaActaDto> mesaRam = this.tabMesaService.findMesaRamdomPR(codigoEleccion, estados, tipoFiltro, reprocesar ?
                ConstantesEstadoMesa.REPROCESAR : ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE);
        return mesaRam.isEmpty() ? null : mesaRam.getFirst();
    }

    @Override
    public void save(PersoneroDTO personeroDTO) {
        try {
            this.personeroRepository.save(this.personeroMapper.dtoToEntity(personeroDTO));
            this.tabMesaService.actualizarEstadoDigitalizaionPR(personeroDTO.getMesa().getId(), personeroDTO.getUsuarioCreacion(),
                    ConstantesEstadoMesa.INSTALADA);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public void saveAll(List<PersoneroDTO> k) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<PersoneroDTO> findAll() {
        return null;
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
