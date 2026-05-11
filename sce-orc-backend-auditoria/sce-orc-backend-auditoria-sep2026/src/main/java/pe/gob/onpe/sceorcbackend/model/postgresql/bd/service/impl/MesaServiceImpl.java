package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.MesaActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.ReprocesarMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TipoDocumentoReprocesarMesaDto;
import pe.gob.onpe.sceorcbackend.model.dto.queue.ApprovedLeMm;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.mesas.DigitizationListMesasItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.MesaDocumento;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaDocumentoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetLeRectanguloService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetMmRectanguloService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OmisoMiembroMesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OmisoVotanteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.MesaProjection;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;
import pe.gob.onpe.sceorcbackend.utils.TipoDocumento;
import pe.gob.onpe.sceorcbackend.utils.TipoFiltro;

@Service
public class MesaServiceImpl implements MesaService {

    Logger logger = LoggerFactory.getLogger(MesaServiceImpl.class);

    private final MesaRepository mesaRepository;

    private final MesaDocumentoRepository mesaDocumentoRepository;

    private final DocumentoElectoralService adminDocumentoElectoralService;

    private final CabActaService cabActaService;

    private final RabbitMqSender rabbitMqSender;

    private final DetMmRectanguloService detMmRectanguloService;

    private final DetLeRectanguloService detLeRectanguloService;

    private final EleccionService eleccionService;

    private final MaeProcesoElectoralService maeProcesoElectoralService;

    private final OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService;

    private final OmisoVotanteService omisoVotanteService;

    private final OmisoMiembroMesaService omisoMiembroMesaService;

    private final ITabLogService logService;

    private final UsuarioService usuarioService;

    public MesaServiceImpl(MesaRepository mesaRepository,
            MesaDocumentoRepository mesaDocumentoRepository,
            DocumentoElectoralService adminDocumentoElectoralService,
            CabActaService cabActaService,
            RabbitMqSender rabbitMqSender,
            DetMmRectanguloService detMmRectanguloService,
            DetLeRectanguloService detLeRectanguloService,
            EleccionService eleccionService,
            MaeProcesoElectoralService maeProcesoElectoralService,
            OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService,
            OmisoVotanteService omisoVotanteService,
            OmisoMiembroMesaService omisoMiembroMesaService,
            ITabLogService logService, UsuarioService usuarioService
    ) {
        this.mesaRepository = mesaRepository;
        this.mesaDocumentoRepository = mesaDocumentoRepository;
        this.adminDocumentoElectoralService = adminDocumentoElectoralService;
        this.cabActaService = cabActaService;
        this.rabbitMqSender = rabbitMqSender;
        this.detMmRectanguloService = detMmRectanguloService;
        this.detLeRectanguloService = detLeRectanguloService;
        this.eleccionService = eleccionService;
        this.maeProcesoElectoralService = maeProcesoElectoralService;
        this.detalleCatalogoEstructuraService = detalleCatalogoEstructuraService;
        this.omisoVotanteService = omisoVotanteService;
        this.omisoMiembroMesaService = omisoMiembroMesaService;
        this.logService = logService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void save(Mesa mesa) {
        this.mesaRepository.save(mesa);
    }

    @Override
    public void saveAll(List<Mesa> k) {
        this.mesaRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.mesaRepository.deleteAll();
    }

    @Override
    public List<Mesa> findAll() {
        return this.mesaRepository.findAll();
    }

    @Override
    public Optional<Mesa> findById(Long id) {
        return this.mesaRepository.findById(id);
    }

    @Override
    public Mesa findByCodigo(String mesa) {
        return this.mesaRepository.findByCodigo(mesa);
    }

    @Override
    public List<Mesa> findByCodigoIn(List<String> codigos) {
        return this.mesaRepository.findByCodigoIn(codigos);
    }

    @Override
    public List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMm(String estadoDigtalle, String estadoMesa,
            String usuario) {
        return this.mesaRepository.findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMm(estadoDigtalle, estadoMesa, usuario);
    }

    @Override
    public List<Mesa> findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMmIsNull(String estadoDigtalle, String estadoMesa) {
        return this.mesaRepository.findByEstadoDigitalizacionMmAndEstadoMesaAndUsuarioAsignadoMmIsNull(estadoDigtalle, estadoMesa);
    }

    @Override
    public List<Mesa> findByEstadoDigitalizacionMmAndUsuarioAsignadoMm(String estadoDigitalizacion, String usuario) {
        return this.mesaRepository.findByEstadoDigitalizacionMmAndUsuarioAsignadoMm(estadoDigitalizacion, usuario);
    }

    @Override
    public List<Mesa> findMesasByEstadoMmAndWithRectangulos(String estadoDigitalizacion, String estadoDigtalPerdidaTotal) {
        return this.mesaRepository.findMesasByEstadoMmAndWithRectangulos(estadoDigitalizacion, estadoDigtalPerdidaTotal);
    }

    @Override
    @Transactional
    public List<DigitizationListMesasItem> listListaElectoresDigtal(String usuario) {

        // catálogo
        var estadoDigitalizacionMap = this.detalleCatalogoEstructuraService
                .findByMaestroAndColumna(
                        ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_LE,
                        ConstantesCatalogo.DET_ESTADO_DIGITALIZACION_LE
                )
                .stream()
                .collect(Collectors.toMap(
                        DetCatalogoEstructuraDTO::getCodigoS,
                        DetCatalogoEstructuraDTO::getNombre
                ));

        // mesas asignadas al usuario
        List<Mesa> mesas = this.mesaRepository.findByEstadoDigitalizacionLeInAndUsuarioControlLe(
                Arrays.asList(
                        ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA,
                        ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE,
                        ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL
                ),
                usuario
        );

        // Si no tiene mesas asignadas o son insuficientes, completar hasta el límite
        if (mesas.isEmpty() || mesas.size() < ConstantesComunes.N_DISTRIBUCION_LE_VERIFICACION) {
            int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_LE_VERIFICACION - mesas.size();

            var mesasLibres = this.mesaRepository.findByEstadoDigitalizacionLeInAndUsuarioControlLeIsNull(
                    Arrays.asList(
                            ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA,
                            ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE,
                            ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL
                    )
            );

            if (!mesasLibres.isEmpty()) {
                Collections.shuffle(mesasLibres);
                var mesasAdicionales = mesasLibres.stream()
                        .limit(nuevaDistribucion)
                        .toList();

                // Crear nueva lista combinada
                var listaCombinada = new ArrayList<>(mesas);
                listaCombinada.addAll(mesasAdicionales);
                mesas = listaCombinada;
            }
        }

        // ordenar por código
        List<Mesa> mesasOrdenadas = mesas.stream()
                .sorted(Comparator.comparing(Mesa::getCodigo))
                .toList();

        // transformar en DTO
        List<DigitizationListMesasItem> items = mesasOrdenadas.stream()
                .map(tabMesa -> {
                    DigitizationListMesasItem item = new DigitizationListMesasItem();
                    item.setMesaId(tabMesa.getId());
                    item.setMesa(tabMesa.getCodigo());
                    item.setEstado(tabMesa.getEstadoDigitalizacionLe());
                    item.setDescEstado(
                            estadoDigitalizacionMap.getOrDefault(
                                    tabMesa.getEstadoDigitalizacionLe(),
                                    ConstantesComunes.VACIO
                            )
                    );
                    String[] datos = getIdPdfLe(tabMesa, ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
                    item.setFilePdfId(datos[0]);
                    item.setPaginas(datos[1]);
                    item.setFilePdfStatus(ConstantesComunes.TEXTO_VALIDADO_AUTOMATICO);
                    item.setListaPaginas(getListaPaginasLe(tabMesa.getId()));

                    // asignar usuario si aún no está asignado
                    if (tabMesa.getUsuarioControlLe() == null) {
                        tabMesa.setUsuarioControlLe(usuario);
                        tabMesa.setFechaUsuarioControlLe(new Date());
                    }
                    return item;
                })
                .toList();

        // actualizar en lote solo las mesas nuevas asignadas
        this.mesaRepository.saveAll(
                mesasOrdenadas.stream()
                        .filter(m -> m.getUsuarioControlLe() != null) // ya tienen asignación nueva
                        .toList()
        );

        return items;
    }

    private List<String> getListaPaginasLe(Long idMesa) {

        return this.mesaDocumentoRepository.findPaginasConEtiquetaByMesaAndAbreviatura(idMesa,
                ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
    }

    @Override
    @Transactional
    public List<DigitizationListMesasItem> listMiembrosMesaDigtal(String usuario) {
        List<DigitizationListMesasItem> digitizationListMesasItems = new ArrayList<>();

        List<Mesa> tabMesaListCompletas =
                this.mesaRepository.findByEstadoDigitalizacionMmAndUsuarioControlMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA,
                        usuario);

        // Si no tiene mesas asignadas o son insuficientes, completar hasta el límite
        if (tabMesaListCompletas.isEmpty() || tabMesaListCompletas.size() < ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION) {

            int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION - tabMesaListCompletas.size();

            List<Mesa> tabMesaListLibres = this.mesaRepository.findByEstadoDigitalizacionMmAndUsuarioControlMmIsNull(
                    ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA);

            if (!tabMesaListLibres.isEmpty()) {
                Collections.shuffle(tabMesaListLibres);
                // Agregar solo las que se necesitan según nuevaDistribucion
                List<Mesa> mesasParaAgregar = tabMesaListLibres.stream()
                        .limit(nuevaDistribucion)
                        .toList();
                tabMesaListCompletas.addAll(mesasParaAgregar);
            }

        }

        for (Mesa tabMesa : tabMesaListCompletas) {
            DigitizationListMesasItem item = new DigitizationListMesasItem();
            item.setMesaId(tabMesa.getId());
            item.setMesa(tabMesa.getCodigo());
            item.setEstado(ConstantesComunes.TEXTO_COMPLETA);
            String[] datos = getIdPdfMm(tabMesa, ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);
            item.setFilePdfId(datos[0]);
            item.setPaginas(datos[1]);
            item.setFilePdfStatus(ConstantesComunes.TEXTO_VALIDADO_AUTOMATICO);
            digitizationListMesasItems.add(item);

            if (tabMesa.getUsuarioControlMm() == null) {
                tabMesa.setUsuarioControlMm(usuario);
                tabMesa.setFechaUsuarioControlMm(new Date());
                this.mesaRepository.save(tabMesa);
            }

        }

        return digitizationListMesasItems;

    }

    @Override
    @Transactional
    public void approveMesa(Long mesaId, String tipoDocumento, String nombreUsuario, String codigoCentroComputo) {
        Optional<Mesa> optionalTabMesa = this.mesaRepository.findById(mesaId);
        if (optionalTabMesa.isPresent()) {

            Mesa tabMesa = optionalTabMesa.get();
            if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)) {
                if (ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA.equals(tabMesa.getEstadoDigitalizacionLe())) {
                    tabMesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA);
                } else if (ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL.equals(
                        tabMesa.getEstadoDigitalizacionLe())) {
                    tabMesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_CON_PERDIDA_PARCIAL);
                }
            }

            if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)) {
                tabMesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_APROBADA_COMPLETA);
            }

            tabMesa.setUsuarioModificacion(nombreUsuario);
            tabMesa.setFechaModificacion(new Date());
            this.mesaRepository.save(tabMesa);

            logger.info("enviando a la cola mesaId{}, tipoDocumento{},  nombreUsuario{} ", mesaId, tipoDocumento, nombreUsuario);

            //Registrar acción que se ejecuta SOLO después del commit exitoso
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    ApprovedLeMm queueMessage = new ApprovedLeMm();
                    queueMessage.setMesaId(mesaId);
                    queueMessage.setAbrevDocumento(tipoDocumento);
                    queueMessage.setCodUsuario(nombreUsuario);
                    queueMessage.setCodCentroComputo(codigoCentroComputo);

                    logger.info("Transacción comiteada, enviando mensaje a RabbitMQ: {}", queueMessage);
                    rabbitMqSender.sendProcessLeMm(queueMessage);
                }
            });

        } else {
            throw new BadRequestException("La mesa no existe en la BD.");
        }
    }

    @Override
    @Transactional
    public void rejectMesa(Long mesaId, String tipoDocumento, String nombreUsuario) {
        try {
            Optional<Mesa> optionalTabMesa = this.mesaRepository.findById(mesaId);
            if (optionalTabMesa.isPresent()) {
                Mesa tabMesa = optionalTabMesa.get();
                if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)) {
                    tabMesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA);
                    tabMesa.setUsuarioAsignadoLe(null);
                    tabMesa.setFechaAsignadoLe(null);
                    tabMesa.setUsuarioControlLe(null);
                    tabMesa.setFechaUsuarioControlLe(null);
                }

                if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)) {
                    tabMesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_RECHAZADA);
                    tabMesa.setUsuarioAsignadoMm(null);
                    tabMesa.setFechaAsignadoMm(null);
                    tabMesa.setUsuarioControlMm(null);
                    tabMesa.setFechaUsuarioControlMm(null);
                }

                tabMesa.setUsuarioModificacion(nombreUsuario);
                tabMesa.setFechaModificacion(new Date());
                this.mesaRepository.save(tabMesa);

                DocumentoElectoral admTabDocumentoElectoral = adminDocumentoElectoralService.findByAbreviatura(tipoDocumento);

                if (admTabDocumentoElectoral == null) {
                    throw new BadRequestException("Tipo de documento electoral " + tipoDocumento + " no esta registrado en BD.");
                }

                this.mesaDocumentoRepository.deleteByAdmDocumentoElectoral_IdAndMesa_Id(admTabDocumentoElectoral.getId(), tabMesa.getId());

                if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)) {
                    this.detLeRectanguloService.deleteByMesaIdAndType(tabMesa.getId(), ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
                }

                if (tipoDocumento.equals(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)) {
                    this.detMmRectanguloService.deleteByMesaId(tabMesa.getId());
                }

            } else {
                throw new BadRequestException("La mesa no existe en la BD.");
            }

        } catch (Exception e) {

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ActaBean> validaHabilitarContingenciaStae(String mesa) {

        if (mesa.length() != ConstantesComunes.LONGITUD_MESA) {
            throw new BadRequestException(String.format("La mesa %s debe contener 6 caracteres.", mesa));
        }

        Mesa tabMesa = this.mesaRepository.findByCodigo(mesa);
        if (tabMesa == null) {
            throw new BadRequestException(String.format(ConstantesComunes.MENSAJE_FORMAT_MESA_NO_REGISTRADA_EN_BD, mesa));
        }

        if (!Objects.equals(tabMesa.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)) {
            throw new BadRequestException(String.format("La mesa %s no está registrada como solución tecnológica STAE.", mesa));
        }

        List<Acta> cabActaList = this.cabActaService.findByMesaId(tabMesa.getId());
        if (cabActaList.isEmpty()) {
            throw new BadRequestException("No existen registros de actas para la mesa " + mesa + ".");
        }

        List<Acta> cabActaListPendientes = cabActaList.stream()
                .filter(e -> e.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE))
                .toList();

        if (cabActaListPendientes.isEmpty()) {
            throw new BadRequestException(
                    "No existen actas en estado pendiente para la mesa " + mesa + ". Solo las pendientes pueden habilitarse.");
        }

        List<Acta> cabActasNoHabilitadas = cabActaList.stream()
                .filter(e -> e.getTipoTransmision() == null)
                .toList();

        if (cabActasNoHabilitadas.isEmpty()) {
            throw new BadRequestException("Las actas de la " + mesa + ", ya fueron habilitadas.");
        }

        List<ActaBean> actaBeanList = new ArrayList<>();
        for (Acta cabActa : cabActasNoHabilitadas) {
            Eleccion eleccion = cabActa.getUbigeoEleccion().getEleccion();

            ActaBean actaBean = new ActaBean();
            actaBean.setActaId(cabActa.getId());
            actaBean.setCopia("");
            actaBean.setMesaId(tabMesa.getId());
            actaBean.setMesa(tabMesa.getCodigo());
            actaBean.setEleccion(eleccion.getNombre());
            actaBean.setCodigoEleccion(eleccion.getCodigo());
            actaBean.setEstadoActa(cabActa.getEstadoActa());
            actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(cabActa.getEstadoActa()));
            actaBean.setEstadoMesa(tabMesa.getEstadoMesa());
            actaBean.setDescripcionEstadoMesa(ConstantesEstadoMesa.getMapEstadoMesa().get(actaBean.getEstadoMesa()));
            actaBeanList.add(actaBean);
        }

        // Ordenar por código de elección
        actaBeanList.sort(Comparator.comparing(ActaBean::getCodigoEleccion));

        return actaBeanList;
    }

    @Override
    @Transactional
    public void habilitarContingenciaStae(List<ActaBean> actaBeanList, String nombreUsuario) {
        if (actaBeanList.isEmpty()) {
            throw new BadRequestException("La lista de actas se encuentra vacía.");
        }

        List<Acta> actasActualizar = new ArrayList<>();

        for (ActaBean actaBean : actaBeanList) {
            Mesa tabMesa = mesaRepository.findByCodigo(actaBean.getMesa());
            if (tabMesa == null) {
                throw new BadRequestException(
                        String.format("La mesa %s no se encuentra registrada.", actaBean.getMesa()));
            }

            if (!Objects.equals(tabMesa.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)) {
                throw new BadRequestException(
                        String.format("La mesa %s no está registrada como Solución tecnológica STAE.", actaBean.getMesa()));
            }

            Acta cabActa = cabActaService.findById(actaBean.getActaId()).orElse(null);
            if (cabActa == null) {
                throw new BadRequestException(
                        String.format("La elección %s no está registrada para la mesa %s.", actaBean.getEleccion(), actaBean.getMesa()));
            }

            if (!ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE.equals(cabActa.getEstadoActa())) {
                throw new BadRequestException(
                        String.format("La elección %s de la mesa %s no se encuentra en estado PENDIENTE.", actaBean.getEleccion(),
                                actaBean.getMesa()));
            }

            if (actaBean.getTipoTransmision() == null) {
                throw new BadRequestException(
                        String.format("Debe seleccionar un tipo de transmisión para la elección %s de la mesa %s.", actaBean.getEleccion(),
                                actaBean.getMesa()));
            }

            if (!List.of(ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA,
                            ConstantesComunes.TIPO_HOJA_STAE_NO_TRANSMITIDA)
                    .contains(actaBean.getTipoTransmision())) {
                throw new BadRequestException(
                        String.format(
                                "El tipo de transmisión seleccionado no es válido. Solo está permitido seleccionar STAE Contingencia o "
                                        + "STAE No transmitida (Elección %s, Mesa %s).",
                                actaBean.getEleccion(), actaBean.getMesa()));
            }

            // Actualización en memoria
            cabActa.setTipoTransmision(actaBean.getTipoTransmision());
            cabActa.setUsuarioModificacion(nombreUsuario);
            cabActa.setFechaModificacion(new Date());
            actasActualizar.add(cabActa);
        }

        cabActaService.saveAll(actasActualizar);
    }

    @Override
    public long count() {
        return this.mesaRepository.count();
    }

    @Override
    public int reseteaValores(String estadoDigitalizacion, String estadoMesa, String usuario, Date fechaModificacion) {
        return mesaRepository.reseteaValores(estadoDigitalizacion, estadoMesa, usuario, fechaModificacion);
    }

    @Override
    public void actualizarEstadoDigitalizaionPR(Long idMesa, String usuario, String estado) {
        this.mesaRepository.actualizarEstadoPR(estado, new Date(), usuario, idMesa);
    }

    @Override
    public void actualizarEstadoDigitalizaionPRisEdit(Long idMesa, String estado) {
        this.mesaRepository.actualizarEstadoPR(estado, null, null, idMesa);
    }

    @Override
    public List<MesaActaDto> findMesaRamdomPR(String codigoEleccion, List<String> estados, Integer tipoFiltro, String estadoMesa) {
        if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue())) {
            return this.mesaRepository.findRandomMesa(codigoEleccion, estados, estadoMesa);
        } else if(tipoFiltro.equals(TipoFiltro.NOINSTALADA.getValue())){
            return this.mesaRepository.findRandomMesaNoInstaladas(codigoEleccion, estados, estadoMesa);
        }else {
            return this.mesaRepository.findRandomMesaExSi(codigoEleccion, estados, estadoMesa);
        }
    }

    @Override
    public void actualizarEstadoDigitalizaionME(Long idMesa, String usuario, String estado) {
        this.mesaRepository.actualizarEstadoME(estado, new Date(), usuario, idMesa);
    }

    @Override
    public void actualizarEstadoDigitalizaionMEisEdit(Long idMesa, String estado) {
        this.mesaRepository.actualizarEstadoME(estado, null, null, idMesa);
    }

    @Override
    public List<MesaActaDto> findMesaRamdomME(String codigoEleccion, List<String> estadosActa, Integer tipoFiltro, String estadoMesa) {
        if (tipoFiltro.equals(TipoFiltro.NORMAL.getValue())) {
            return this.mesaRepository.findRandomMesaME(codigoEleccion, estadosActa, estadoMesa);
        } else if (tipoFiltro.equals(TipoFiltro.NOINSTALADA.getValue())) {
            return this.mesaRepository.findRandomMesaMeNoInstalada(codigoEleccion, estadosActa, estadoMesa);
        } else {
            return this.mesaRepository.findRandomMesaMeExSi(codigoEleccion, estadosActa, estadoMesa);
        }
    }

    @Override
    public List<Mesa> listLiberarMesasME() {
        return this.mesaRepository.findMesasAsignadasMEHaceMasDe15Min();
    }

    @Override
    public List<Mesa> listLiberarMesasPR() {
        return this.mesaRepository.findMesasAsignadasPRHaceMasDe15Min();
    }

    @Override
    @Transactional
    public Integer validarActaPrincipalProcesada(Mesa mesa) {

        ProcesoElectoral procesoElectoral = this.maeProcesoElectoralService.findByActivo();
        if (procesoElectoral == null) {
            throw new InternalServerErrorException("El proceso electoral no esta registrado.");
        }

        Eleccion eleccionPrincipal = this.eleccionService.obtenerEleccionPrincipalPorProceso(procesoElectoral.getId());
        if (eleccionPrincipal == null) {
            throw new InternalServerErrorException("No existe una elección marcada como principal en la BD.");
        }

        List<Acta> actaList = this.cabActaService.findByMesaId(mesa.getId());

        Optional<Acta> actaPrincipalOpt = actaList.stream()
                .filter(acta -> eleccionPrincipal.getCodigo().equals(acta.getUbigeoEleccion().getEleccion().getCodigo()))
                .findFirst();

        if (actaPrincipalOpt.isEmpty()) {
            throw new InternalServerErrorException(String.format("El acta principal asociada a la mesa %s, no existe.", mesa.getCodigo()));
        }

        Acta actaPrincipal = actaPrincipalOpt.get();

        if (actaPrincipal.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITALIZADA) ||
                actaPrincipal.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE) ||
                actaPrincipal.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION) ||
                actaPrincipal.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_DIGITADA)) {
            throw new InternalServerErrorException(
                    String.format("El acta principal de la mesa %s, no se encuentra en un estado PROCESADO.", mesa.getCodigo()));
        }

        int cantidadAusentes = actaPrincipal.getElectoresHabiles() != null ? actaPrincipal.getElectoresHabiles().intValue() : 0;
        if (actaPrincipal.getCvas() != null) {
            cantidadAusentes -= actaPrincipal.getCvas().intValue();
        }

        if (cantidadAusentes < 0) {
            cantidadAusentes = 0;
        }

        return cantidadAusentes;
    }

    @Override
    public GenericResponse<ReprocesarMesaResponseDto> buscarMesaReprocesar(String codMesa) {
        GenericResponse<ReprocesarMesaResponseDto> response = new GenericResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setMessage("Mesa ingresado no existe para reprocesamiento");
        try {
            Mesa mesa = this.mesaRepository.findByCodigo(codMesa);
            if (Objects.nonNull(mesa)) {
                final Mesa mesaEntity = mesa;
                if (verificarExistenciaReprocesar(mesaEntity)) {
                    return new GenericResponse<>(false, "La mesa " + codMesa + " ya se encuentra habilitada para reprocesamiento");
                }
                ReprocesarMesaResponseDto reprocesarMesa = new ReprocesarMesaResponseDto();
                reprocesarMesa.setMesa(new MesaDTO(mesa.getId(), mesaEntity.getCodigo()));
                List<TipoDocumentoReprocesarMesaDto> tipos = new ArrayList<>();
                if (mesaEntity.getEstadoDigitalizacionMe().equals(ConstantesEstadoMesa.PROCESADA)) {
                    agregarTiposDisponibles(tipos, TipoDocumento.MESA_ESCRUTINIO);
                }
                if (mesaEntity.getEstadoDigitalizacionPr().equals(ConstantesEstadoMesa.PROCESADA)) {
                    agregarTiposDisponibles(tipos, TipoDocumento.PERSONEROS);
                }
                if (mesaEntity.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.PROCESADA)) {
                    agregarTiposDisponibles(tipos, TipoDocumento.LISTA_ELECTORES);
                }
                if (mesaEntity.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.PROCESADA)) {
                    agregarTiposDisponibles(tipos, TipoDocumento.HOJA_ASISTENCIA);
                }
                if (tipos.isEmpty()) {
                    return new GenericResponse<>(false, "No se ha procesado ningun tipo de documento para esta mesa " + codMesa);
                }
                reprocesarMesa.setTipoDocumentos(tipos);
                response.setData(reprocesarMesa);
                response.setSuccess(Boolean.TRUE);
                response.setMessage("");
            }
            return response;
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public void procesarReprocesarMesa(List<ReprocesarMesaResponseDto> data, String usuario, String codigoCC) {
        final String estadoReprocesar = ConstantesEstadoMesa.REPROCESAR;
        final Date fechaReprocesar = new Date();
        try {
            data.forEach(reprocesarMesaDto -> {
                final MesaDTO mesaDTO = reprocesarMesaDto.getMesa();
                reprocesarMesaDto.getTipoDocumentos().forEach(tipoDocumento -> {
                    if (tipoDocumento.getCodigo().equals(TipoDocumento.MESA_ESCRUTINIO.getId()) && tipoDocumento.isReprocesar()) {
                        this.mesaRepository.actualizarEstadoME(estadoReprocesar, fechaReprocesar, null, mesaDTO.getId());
                    }
                    if (tipoDocumento.getCodigo().equals(TipoDocumento.PERSONEROS.getId()) && tipoDocumento.isReprocesar()) {
                        this.mesaRepository.actualizarEstadoPR(estadoReprocesar, fechaReprocesar, null, mesaDTO.getId());
                    }
                    if (tipoDocumento.getCodigo().equals(TipoDocumento.LISTA_ELECTORES.getId()) && tipoDocumento.isReprocesar()) {
                        this.mesaRepository.actualizarEstadoLE(estadoReprocesar, fechaReprocesar, null, mesaDTO.getId());
                    }
                    if (tipoDocumento.getCodigo().equals(TipoDocumento.HOJA_ASISTENCIA.getId()) && tipoDocumento.isReprocesar()) {
                        this.mesaRepository.actualizarEstadoMM(estadoReprocesar, fechaReprocesar, null, mesaDTO.getId());
                    }
                });

            });

            this.logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getSimpleName(),
                    "Se reprocesó la mesa con autorización nación.",
                    codigoCC,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );

        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public List<MesaProjection> buscarMesaLeControlDigtalTomadas(List<String> list) {
        return this.mesaRepository.buscarMesaLeControlDigtalTomadas(list);
    }

    @Override
    public void liberarMesaLeControlDigtalTomadas(Long id) {

        this.mesaRepository.liberarMesaLeControlDigtalTomadas(id);

    }

    @Override
    public List<MesaProjection> buscarMesamMmControlDigtalTomadas(List<String> cEstadoDigtalDigitalizada) {
        return this.mesaRepository.buscarMesaMmControlDigtalTomadas(cEstadoDigtalDigitalizada);
    }

    @Override
    public void liberarMesaMmControlDigtalTomadas(Long id) {
        this.mesaRepository.liberarMesaMmControlDigtalTomadas(id);

    }

    @Override
    public List<MesaProjection> buscarMesaLeVerificacionTomadas(List<String> estadosDigitalizacion, String cEstadoDigtalPendiente,
            String estadoDigtalPerdidaTotal, String noInstalada) {
        return this.mesaRepository.buscarMesaLeVerificacionTomadas(estadosDigitalizacion, cEstadoDigtalPendiente, estadoDigtalPerdidaTotal,
                noInstalada);
    }

    @Override
    public void liberarMesaLeVerificacionTomadas(Long id) {
        this.mesaRepository.liberarMesaLeVerificacionTomadas(id);

    }

    @Override
    public List<MesaProjection> buscarMesaMmVerificacionTomadas(List<String> estadosDigitalizacion, String cEstadoDigtalPendiente,
            String estadoDigtalPerdidaTotal, String noInstalada) {
        return mesaRepository.buscarMesaMmVerificacionTomadas(estadosDigitalizacion, cEstadoDigtalPendiente, estadoDigtalPerdidaTotal,
                noInstalada);
    }

    @Override
    public void liberarMesaMmVerificacionTomadas(Long id) {
        mesaRepository.liberarMesaMmVerificacionTomadas(id);
    }

    @Override
    public Long contarMmPorActivo(String estadoDigitalizacionMm, Integer activo) {
        return this.mesaRepository.contarMmPorActivo(estadoDigitalizacionMm, activo);
    }

    @Override
    public Long contarLePorActivo(String estadoDigitalizacionLe, Integer activo) {
        return this.mesaRepository.contarLePorActivo(estadoDigitalizacionLe, activo);
    }

    @Override
    public Long contarMePorActivo(String estadoDigitalizacionMe, Integer activo) {
        return this.mesaRepository.contarMePorActivo(estadoDigitalizacionMe, activo);
    }

    @Override
    public Long contarPrPorActivo(String estadoDigitalizacionPr, Integer activo) {
        return this.mesaRepository.contarPrPorActivo(estadoDigitalizacionPr, activo);
    }

    @Override
    public GenericResponse<MesaDTO> buscarMesaEliminarOmiso(String codMesa) {
        GenericResponse<MesaDTO> response = new GenericResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setMessage("La mesa ingresado no existe para eliminar omisos");
        try {
            final Mesa mesa = this.mesaRepository.findByCodigo(codMesa);
            if (Objects.nonNull(mesa)) {

                if (!mesa.getEstadoMesa().equals(ConstantesEstadoMesa.NO_INSTALADA)) {
                    return new GenericResponse<>(false, "La mesa " + codMesa + " no es no instalada");
                }

                if (verificarExistenciaEliminarOmiso(mesa)) {
                    return new GenericResponse<>(false, "No se ha realizado aún el registro de omisos para la mesa " + codMesa);
                }
                MesaDTO mesaDTO = new MesaDTO();
                mesaDTO.setId(mesa.getId());
                mesaDTO.setMesa(mesa.getCodigo());
                response.setData(mesaDTO);
                response.setSuccess(Boolean.TRUE);
                response.setMessage("");
            }
            return response;
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @Override
    public void procesarEliminarOmisosMesa(List<MesaDTO> data, String usuario, String codigoCC) {
        if (data == null || data.isEmpty()) {
            return;
        }

        for (MesaDTO mesa : data) {
            if (mesa == null || mesa.getId() == null) {
                continue;
            }
            try {
                omisoVotanteService.inhabilitarOmisosVotantes(mesa.getId(), usuario);
                omisoMiembroMesaService.inhabilitarOmisoMiembroMesa(mesa.getId(), usuario);
            } catch (Exception e) {
                throw new GenericException("Error procesando mesa ID " + mesa.getId(), e);
            }
        }

        this.logService.registrarLog(
                usuario,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getSimpleName(),
                "Se eliminaron los omisos con autorización nación.",
                codigoCC,
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION
        );

    }

    @Override
    public List<Long> findMesaIdsLeRandom(List<String> estadosFiltro, String estadoP,
            String estadoMesaNoInstalada, List<String> estadosSC,
            List<String> estadosActaExcluidos) {
        return this.mesaRepository.findMesaIdsLeRandomLibres(estadosFiltro, estadoP, estadoMesaNoInstalada, estadosSC,
                estadosActaExcluidos);
    }

    @Override
    public List<Long> findMesasAsignadasConFiltro(String usuario, List<String> estadosFiltro, String estadoDigitalPendiente,
            String estadoMesaNoInstalada) {
        return this.mesaRepository.findMesasAsignadasConFiltro(usuario, estadosFiltro, estadoDigitalPendiente, estadoMesaNoInstalada);
    }

    @Override
    public List<OtroDocumentoDto> listarListaElectoresScanner(String nombreUsuario) {
        List<OtroDocumentoDto> otroDocumentoDtoList = new ArrayList<>();
        List<Mesa> mesaList = this.mesaRepository.findAllByOrderByCodigoAsc();
        for (Mesa mesa : mesaList) {
            OtroDocumentoDto dto = OtroDocumentoDto.builder()
                    .idOtroDocumento(mesa.getId())
                    .numeroDocumento(mesa.getCodigo())
                    .abrevTipoDocumento(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)
                    .estadoDigitalizacion(mesa.getEstadoDigitalizacionLe())
                    .estadoDocumento(mesa.getEstadoMesa())
                    .fechaSceScanner(mesa.getFechaModificacion() == null ? mesa.getFechaCreacion() : mesa.getFechaModificacion())
                    .activo(mesa.getActivo())
                    .build();
            otroDocumentoDtoList.add(dto);
        }
        return otroDocumentoDtoList;
    }

    @Override
    public List<OtroDocumentoDto> listarMiembrosMesaScanner(String nombreUsuario) {
        List<OtroDocumentoDto> otroDocumentoDtoList = new ArrayList<>();
        List<Mesa> mesaList = this.mesaRepository.findAllByOrderByCodigoAsc();
        for (Mesa mesa : mesaList) {

            OtroDocumentoDto dto = OtroDocumentoDto.builder()
                    .idOtroDocumento(mesa.getId())
                    .numeroDocumento(mesa.getCodigo())
                    .abrevTipoDocumento(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)
                    .estadoDigitalizacion(mesa.getEstadoDigitalizacionMm())
                    .estadoDocumento(mesa.getEstadoMesa())
                    .fechaSceScanner(mesa.getFechaModificacion() == null ? mesa.getFechaCreacion() : mesa.getFechaModificacion())
                    .activo(mesa.getActivo())
                    .build();
            otroDocumentoDtoList.add(dto);
        }
        return otroDocumentoDtoList;
    }

    @Override
    public Long obtenerMesaForUpdate(String estado) {
        try{
            return this.mesaRepository.obtenerMesaForUpdate(estado);
        }catch(Exception e){
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    public void actualizarUsuarioAsignadoMM(String usuario, Long idMesa) {
        try{
            this.mesaRepository.actualizarUsuarioAsignadoMM(new Date(), usuario, idMesa);
        }catch(Exception e){
            throw new GenericException(e.getMessage());
        }
    }

    private void agregarTiposDisponibles(List<TipoDocumentoReprocesarMesaDto> tipos, TipoDocumento tipoDocumento) {
        tipos.add(new TipoDocumentoReprocesarMesaDto(tipoDocumento.getId(), tipoDocumento.getDescripcion(), false));
    }

    private boolean verificarExistenciaReprocesar(Mesa mesaEntity) {
        return mesaEntity.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.REPROCESAR) && mesaEntity.getEstadoDigitalizacionPr()
                .equals(ConstantesEstadoMesa.REPROCESAR)
                && mesaEntity.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.REPROCESAR) && mesaEntity.getEstadoDigitalizacionMe()
                .equals(ConstantesEstadoMesa.REPROCESAR);
    }

    private boolean verificarExistenciaEliminarOmiso(Mesa mesaEntity) {
        return mesaEntity.getEstadoDigitalizacionMm().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE)
                && mesaEntity.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_PENDIENTE);
    }

    public String[] getIdPdfLe(Mesa tabMesa, String tipoDocumentoAbreviatura) {

        String[] retorno = new String[]{"", ""};

        DocumentoElectoral admTabDocumentoElectoral = adminDocumentoElectoralService.findByAbreviatura(tipoDocumentoAbreviatura);
        if (admTabDocumentoElectoral == null) {
            return retorno;
        }
        List<MesaDocumento> tabMesaDocumentoList =
                this.mesaDocumentoRepository.findByMesaAndAdmDocumentoElectoral(tabMesa, admTabDocumentoElectoral);
        if (tabMesaDocumentoList.isEmpty()) {
            return retorno;
        }

        List<MesaDocumento> tabMesaDocumentoList1 =
                tabMesaDocumentoList.stream().filter(e -> e.getArchivo().getFormato().equals(ConstantesFormatos.PDF_VALUE)).toList();
        List<MesaDocumento> tabMesaDocumentoList2 =
                tabMesaDocumentoList.stream().filter(e -> e.getArchivo().getFormato().equals(ConstantesFormatos.IMAGE_TIF_VALUE)).toList();

        int resto = tabMesa.getCantidadElectoresHabiles() % 10;
        int cantidadPaginas = tabMesa.getCantidadElectoresHabiles() / 10;
        if (resto > 0) {
            cantidadPaginas += 1;
        }

        if (!tabMesaDocumentoList1.isEmpty()) {
            retorno[0] =
                    tabMesaDocumentoList1.get(0).getArchivo() != null ? String.valueOf(tabMesaDocumentoList1.get(0).getArchivo().getId())
                            : "";
        }

        retorno[1] = tabMesaDocumentoList2.size() + " de " + cantidadPaginas;

        return retorno;
    }

    public String[] getIdPdfMm(Mesa tabMesa, String tipoDocumentoAbreviatura) {

        String[] retorno = new String[]{"", ""};

        DocumentoElectoral admTabDocumentoElectoral = adminDocumentoElectoralService.findByAbreviatura(tipoDocumentoAbreviatura);
        if (admTabDocumentoElectoral == null) {
            return retorno;
        }

        List<MesaDocumento> tabMesaDocumentoList =
                this.mesaDocumentoRepository.findByMesaAndAdmDocumentoElectoral(tabMesa, admTabDocumentoElectoral);
        if (tabMesaDocumentoList.isEmpty()) {
            return retorno;
        }

        List<MesaDocumento> tabMesaDocumentoList1 =
                tabMesaDocumentoList.stream().filter(e -> e.getArchivo().getFormato().equals(ConstantesFormatos.PDF_VALUE)).toList();

        int cantidadPaginas = 2;

        if (!tabMesaDocumentoList1.isEmpty()) {
            retorno[0] =
                    tabMesaDocumentoList1.get(0).getArchivo() != null ? String.valueOf(tabMesaDocumentoList1.get(0).getArchivo().getId())
                            : "";
        }

        retorno[1] = "2 de " + cantidadPaginas;

        return retorno;
    }

}
