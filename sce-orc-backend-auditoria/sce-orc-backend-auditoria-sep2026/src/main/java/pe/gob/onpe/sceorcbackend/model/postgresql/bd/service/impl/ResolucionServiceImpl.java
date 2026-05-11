package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.NotFoundException;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.mapper.UtilMapper;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionDevueltasRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.actas.ActaPorCorregirListItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBeanRevocatoria;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaJeeBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.AgrupolBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.AplicarActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.DigitizationListResolucionItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ResumenResolucionesDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.VotoOpcionBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.VotoPreferencialBean;
import pe.gob.onpe.sceorcbackend.model.dto.verification.ActaResumenDto;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.dto.verification.ResolucionDigtalDto;
import pe.gob.onpe.sceorcbackend.model.mapper.IResolucionMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CabActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaFormato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetUbigeoEleccionAgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DistritoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Formato;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OrcDetalleCatalogoEstructura;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UbigeoEleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaCelesteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ArchivoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CabActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaPreferencialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetUbigeoEleccionAgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetalleCatalogoEstructuraRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.FormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AgrupacionPoliticaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AmbitoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabCcResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetActaOpcionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.TipoErrorDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.MesaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar.AuxEstadoActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar.RegistroActaErrores;
import pe.gob.onpe.sceorcbackend.utils.ActaDTO;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoResolucion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;
import pe.gob.onpe.sceorcbackend.utils.ConstantesMensajes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesModulos;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.ConsultaErroresMateriales;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.OficioUtils;
import pe.gob.onpe.sceorcbackend.utils.PathUtils;
import pe.gob.onpe.sceorcbackend.utils.ProcessActaUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;
import pe.gob.onpe.sceorcbackend.utils.reimpresion.ReimpresionCargoDto;

@Service
public class ResolucionServiceImpl implements ResolucionService {

    Logger logger = LoggerFactory.getLogger(ResolucionServiceImpl.class);

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ActaRepository actaRepository;

    private final TabResolucionRepository tabResolucionRepository;

    private final DetActaResolucionRepository detActaResolucionRepository;

    private final MesaRepository mesaRepository;

    private final MaeProcesoElectoralService procesoElectoralService;

    private final OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService;

    private final DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository;

    private final DetActaRepository detActaRepository;

    private final DetActaPreferencialRepository detActaPreferencialRepository;

    private final DetActaOpcionService detActaOpcionService;

    private final ArchivoRepository archivoRepository;

    private final FormatoRepository formatoRepository;

    private final CabActaFormatoRepository cabActaFormatoRepository;

    private final DetActaFormatoRepository detActaFormatoRepository;

    private final DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository;

    private final IResolucionMapper resolucionMapper;

    private final StorageService storageService;

    private final AgrupacionPoliticaService agrupacionPoliticaService;

    private final UtilSceService utilSceService;

    private final ITabLogService logService;

    private final AmbitoElectoralService ambitoElectoralService;

    private final CabCcResolucionService cabCcResolucionService;

    private final ActaCelesteRepository actaCelesteRepository;

    private final ParametroService parametroService;

    public ResolucionServiceImpl(ActaRepository cabActaRepository, TabResolucionRepository tabResolucionRepository,
                                 DetActaResolucionRepository detActaResolucionRepository, MesaRepository mesaRepository,
                                 MaeProcesoElectoralService procesoElectoralService,
                                 OrcDetalleCatalogoEstructuraService orcDetalleCatalogoEstructuraService,
                                 DetUbigeoEleccionAgrupacionPoliticaRepository detUbigeoEleccionAgrupacionPoliticaRepository,
                                 DetActaRepository detActaRepository, DetActaPreferencialRepository detActaPreferencialRepository,
                                 ArchivoRepository archivoRepository, FormatoRepository formatoRepository,
                                 DetActaFormatoRepository detActaFormatoRepository,
                                 DetalleCatalogoEstructuraRepository detalleCatalogoEstructuraRepository, IResolucionMapper resolucionMapper,
                                 StorageService storageService, AgrupacionPoliticaService agrupacionPoliticaService,
                                 CabActaFormatoRepository cabActaFormatoRepository, UtilSceService utilSceService,
                                 DetActaOpcionService detActaOpcionService, ITabLogService logService,
                                 AmbitoElectoralService ambitoElectoralService, CabCcResolucionService cabCcResolucionService,
                                 ActaCelesteRepository actaCelesteRepository, ParametroService parametroService) {
        this.actaRepository = cabActaRepository;
        this.tabResolucionRepository = tabResolucionRepository;
        this.detActaResolucionRepository = detActaResolucionRepository;
        this.mesaRepository = mesaRepository;
        this.procesoElectoralService = procesoElectoralService;
        this.detalleCatalogoEstructuraService = orcDetalleCatalogoEstructuraService;
        this.detUbigeoEleccionAgrupacionPoliticaRepository = detUbigeoEleccionAgrupacionPoliticaRepository;
        this.detActaRepository = detActaRepository;
        this.detActaPreferencialRepository = detActaPreferencialRepository;
        this.archivoRepository = archivoRepository;
        this.formatoRepository = formatoRepository;
        this.detActaFormatoRepository = detActaFormatoRepository;
        this.detalleCatalogoEstructuraRepository = detalleCatalogoEstructuraRepository;
        this.resolucionMapper = resolucionMapper;
        this.storageService = storageService;
        this.agrupacionPoliticaService = agrupacionPoliticaService;
        this.cabActaFormatoRepository = cabActaFormatoRepository;
        this.utilSceService = utilSceService;
        this.detActaOpcionService = detActaOpcionService;
        this.logService = logService;
        this.ambitoElectoralService = ambitoElectoralService;
        this.cabCcResolucionService = cabCcResolucionService;
        this.actaCelesteRepository = actaCelesteRepository;
        this.parametroService = parametroService;
    }

    @Override
    public void save(TabResolucion tabResolucion) {
        this.tabResolucionRepository.save(tabResolucion);

    }

    @Override
    public void saveAll(List<TabResolucion> tabResolucionList) {
        this.tabResolucionRepository.saveAll(tabResolucionList);
    }

    @Override
    public void deleteAll() {
        this.tabResolucionRepository.deleteAll();
    }

    @Override
    public List<TabResolucion> findAll() {
        return this.tabResolucionRepository.findAll();
    }

    @Override
    @Transactional
    public ResolucionAsociadosRequest getRandomResolucion(String usuario) {

        List<String> estadosPermitidos = List.of(ConstantesEstadoResolucion.EN_PROCESO);

        List<Integer> tipoPermitidos = List.of(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE,
                ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE, ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE);

        // 1ER PASO BUSCAR SI TIENE UNA RESOLUCION ASIGNADA
        List<TabResolucion> tabResolucionListAsignados = this.tabResolucionRepository
                .findByEstadoResolucionInAndTipoResolucionInAndAudUsuarioAsignadoAndActivo(estadosPermitidos,
                        tipoPermitidos, usuario, ConstantesComunes.ACTIVO);

        if (!tabResolucionListAsignados.isEmpty()) {
            Long idResol = tabResolucionListAsignados.getFirst().getId();
            return getResolucion(idResol);
        } else {
            // No tiene asignado nada - obtener una resolución random con bloqueo pesimista
            TabResolucion tabResolucion = this.tabResolucionRepository
                    .findRandomResolucionParaVerificacion(estadosPermitidos, tipoPermitidos, ConstantesComunes.ACTIVO);

            if (tabResolucion == null) {
                return null;
            }

            // Asignar dentro de la misma transacción (el bloqueo pesimista se mantiene
            // activo)
            tabResolucion.setAsignado(ConstantesComunes.ACTIVO);
            tabResolucion.setAudUsuarioAsignado(usuario);
            tabResolucion.setAudFechaAsignado(new Date());
            tabResolucion.setAudUsuarioModificacion(usuario);
            tabResolucion.setAudFechaModificacion(new Date());
            this.tabResolucionRepository.save(tabResolucion);

            return getResolucion(tabResolucion.getId());
        }
    }

    @Override
    @Transactional
    public ResumenResolucionesDto resumenResoluciones(String numeroResolucion) {
        ResumenResolucionesDto dto = new ResumenResolucionesDto();

        // lista estados digitalización
        List<String> estadosDigitalizacion = List.of(ConstantesEstadoResolucion.DIGTAL_APROBADO,
                ConstantesEstadoResolucion.SEGUNDO_CC_ACEPTADA, ConstantesEstadoResolucion.RECHAZADA_2DO_CC);

        List<Object[]> result = tabResolucionRepository.resumenResoluciones(ConstantesEstadoResolucion.PROCESADO,
                ConstantesEstadoResolucion.SIN_PROCESAR, ConstantesEstadoResolucion.EN_PROCESO, estadosDigitalizacion,
                ConstantesEstadoResolucion.ANULADO, ConstantesComunes.ACTIVO);

        if (result == null || result.isEmpty()) {
            dto.setNumTotalResoluciones(0);
            dto.setNumResolucionesAnuladas(0);
            dto.setNumResolucionesSinAplicar(0);
            dto.setNumResolucionesSinAplicarAsociadas(0);
            dto.setNumResolucionesAplicadas(0);
        } else {
            Object[] row = result.getFirst();

            // Validar 4 elementos
            if (row == null || row.length < 4) {
                throw new IllegalStateException("La consulta no devolvió los 4 valores esperados");
            }

            dto.setNumTotalResoluciones(((Number) Optional.ofNullable(row[0]).orElse(0)).intValue());
            dto.setNumResolucionesSinAplicar(((Number) Optional.ofNullable(row[1]).orElse(0)).intValue());
            dto.setNumResolucionesSinAplicarAsociadas(((Number) Optional.ofNullable(row[2]).orElse(0)).intValue());
            dto.setNumResolucionesAplicadas(((Number) Optional.ofNullable(row[3]).orElse(0)).intValue());
            dto.setNumResolucionesAnuladas(((Number) Optional.ofNullable(row[4]).orElse(0)).intValue());
        }

        List<DetCatalogoEstructuraDTO> catalogoEstadosResolucion = this.detalleCatalogoEstructuraService
                .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_RESOLUCION,
                        ConstantesCatalogo.DET_ESTADO_RESOLUCION);

        List<DetCatalogoEstructuraDTO> catalogoEstadosDigtalResolucion = this.detalleCatalogoEstructuraService
                .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_RESOLUCION,
                        ConstantesCatalogo.DET_ESTADO_DIGITALIZACION_RESOLUCION);

        dto.setResoluciones(listaResoluciones(numeroResolucion, estadosDigitalizacion, catalogoEstadosResolucion,
                catalogoEstadosDigtalResolucion));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse<Object> validarActaDevueltaJee(String codigoCentroComputo, String nroActa,
                                                          String nroCopiaAndDigCheck) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActa.concat(nroCopiaAndDigCheck), codigoCentroComputo,
                Boolean.FALSE);

        List<String> estadosEnvioJeePermitidos = List.of(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
        if (estadosEnvioJeePermitidos.stream().noneMatch(p -> p.equals(actaInfo.getActa().getEstadoActa()))) {
            return new GenericResponse<>(false, "El acta " + nroActa + "-" + nroCopiaAndDigCheck
                    + " no se encuentra en un estado Enviado al Jurado.");
        }

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(actaInfo.getActa().getId());
        actaBean.setMesa(actaInfo.getActa().getMesa().getCodigo());
        actaBean.setEleccion(actaInfo.getNombreEleccion());
        actaBean.setCopia(nroCopiaAndDigCheck);
        actaBean.setEstadoActa(actaInfo.getActa().getEstadoActa());
        actaBean.setDescripcionEstadoActa(
                ConstantesEstadoActa.getMapEstadoActas().get(actaInfo.getActa().getEstadoActa()));
        return new GenericResponse<>(true,
                "El acta " + nroActa + "-" + nroCopiaAndDigCheck + " ha sido validada correctamente.", actaBean);
    }

    private GenericResponse<Object> obtenerInfoDeNoInstaladaParaAsociacion(String nroActaCopiaDig) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroActaCopiaDig);
        Mesa mesa = mesaInfo.getMesa();

        if (!ConstantesEstadoMesa.POR_INFORMAR.equals(mesa.getEstadoMesa())) {
            return new GenericResponse<>(false,
                    String.format("La mesa %s debe estar en estado Por Informar.", nroActaCopiaDig));
        }

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(null);
        actaBean.setMesaId(mesa.getId());
        actaBean.setMesa(mesa.getCodigo());
        actaBean.setEleccion(null);
        actaBean.setCopia(null);
        actaBean.setEstadoMesa(mesa.getEstadoMesa());
        actaBean.setDescripcionEstadoMesa(ConstantesEstadoMesa.getMapEstadoMesa().get(actaBean.getEstadoMesa()));

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroActaCopiaDig), actaBean);
    }

    private GenericResponse<Object> obtenerInfoDeActaExtraSiniestradaParaAsociacion(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();
        List<Acta> cabActaList = mesaInfo.getActaList();

        this.utilSceService.validarMesaNoInstalada(mesa);

        List<Acta> cabActaListPendientes = cabActaList.stream()
                .filter(e -> ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE.equals(e.getEstadoActa())).toList();

        if (cabActaListPendientes.isEmpty()) {
            List<Acta> actasExtraviadas = cabActaList.stream()
                    .filter(e -> ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(e.getEstadoActa())
                            && ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA
                            .equals(e.getEstadoActaResolucion()))
                    .toList();

            if (!actasExtraviadas.isEmpty()) {
                return new GenericResponse<>(false,
                        "Existen actas declaradas EXTRAVIADAS, para resolverlas, debe seleccionar como Tipo de Resolución: "
                                + ConstantesCatalogo.getMapTiposResoluciones()
                                .get(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE));
            }

            List<Acta> actasSiniestradas = cabActaList.stream()
                    .filter(e -> ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(e.getEstadoActa())
                            && ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA
                            .equals(e.getEstadoActaResolucion()))
                    .toList();

            if (!actasSiniestradas.isEmpty()) {
                return new GenericResponse<>(false,
                        "Existen actas declaradas SINIESTRADAS, para resolverlas, debe seleccionar como Tipo de Resolución: "
                                + ConstantesCatalogo.getMapTiposResoluciones()
                                .get(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE));
            }

            return new GenericResponse<>(false,
                    "Para declarar actas como EXTRAVIADA/SINIESTRADA, deben existir actas en estado PENDIENTE para la mesa "
                            + nroMesa + ".");
        }

        List<ActaBean> actaBeanList = new ArrayList<>();
        for (Acta acta : cabActaListPendientes) {
            ActaBean actaBean = construirActaBean(acta, mesa);
            actaBeanList.add(actaBean);
        }

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroMesa), actaBeanList);
    }

    private ActaBean construirActaBean(Acta acta, Mesa mesa) {
        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(acta.getId());
        actaBean.setCopia("");
        actaBean.setMesaId(mesa.getId());
        actaBean.setMesa(mesa.getCodigo());
        Eleccion ele = acta.getUbigeoEleccion().getEleccion();
        actaBean.setCodigoEleccion(ele.getCodigo());
        actaBean.setEleccion(ele.getNombre());
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
        actaBean.setEstadoMesa(mesa.getEstadoMesa());
        actaBean.setDescripcionEstadoMesa(ConstantesEstadoMesa.getMapEstadoMesa().get(mesa.getEstadoMesa()));
        return actaBean;
    }

    private GenericResponse<Object> validarActasEnviadasAJeexMesaParaAsociacion(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();
        List<Acta> cabActaList = mesaInfo.getActaList();

        this.utilSceService.validarMesaNoInstalada(mesa);

        List<Acta> actasFiltradas = cabActaList.stream()
                .filter(e -> ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA.equals(e.getEstadoCc())
                        && (ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(e.getEstadoActa())
                        || ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(e.getEstadoActa())))
                .toList();

        if (actasFiltradas.isEmpty()) {
            return new GenericResponse<>(false,
                    String.format("La mesa %s no cuenta con actas declaradas extraviadas o siniestradas. "
                                    + "Si cuenta con actas asociadas a este tipo de resolución, debe generar su cargo de entrega para aplicar la resolución/memo.",
                            nroMesa));
        }

        List<ActaBean> actaBeanList = actasFiltradas.stream().map(acta -> {
            ActaBean bean = new ActaBean();
            bean.setActaId(acta.getId());
            bean.setCopia("");
            bean.setMesaId(mesa.getId());
            bean.setMesa(mesa.getCodigo());
            bean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());
            bean.setEstadoActa(acta.getEstadoActa());
            bean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
            bean.setEstadoMesa(mesa.getEstadoMesa());
            bean.setDescripcionEstadoMesa(ConstantesEstadoMesa.getMapEstadoMesa().get(bean.getEstadoMesa()));
            return bean;
        }).toList();

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroMesa), actaBeanList);
    }

    private GenericResponse<Object> validarActasEnviadasAJeexCopiaParaAsociacion(String nroActaCopiaDig) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActaCopiaDig, ConstantesComunes.VACIO, Boolean.FALSE);
        Acta acta = actaInfo.getActa();
        BarCodeInfo barCodeInfo = actaInfo.getBarCodeInfo();

        if (ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(acta.getEstadoActa())
                || ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(acta.getEstadoActa())) {
            return new GenericResponse<>(false,
                    String.format("El acta %s se encuentra en estado %s, debe ingresar solo la mesa para validar.",
                            nroActaCopiaDig, acta.getEstadoActa()));
        }

        if (!ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA.equals(acta.getEstadoActa())) {
            return new GenericResponse<>(false, String.format(ConstantesComunes.MENSAJE_FORMATO_ACTA_ESTADO_NO_DEVUELTA,
                    barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
        }

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(acta.getId());
        actaBean.setMesa(acta.getMesa().getCodigo());
        actaBean.setIdArchivoEscrutinio(
                acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId().toString() : "");
        actaBean.setIdArchivoInstalacionSufragio(
                acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId().toString()
                        : "");
        actaBean.setEleccion(actaInfo.getNombreEleccion());
        actaBean.setCopia(barCodeInfo.getNroCopiaAndDigito());
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroActaCopiaDig), actaBean);
    }

    public GenericResponse<Object> obtenerInfoActa999VerificacionResolucion(String nroActaCopiaDig,
                                                                            Integer codTipoResolucion) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActaCopiaDig, ConstantesComunes.VACIO, Boolean.FALSE);
        Acta acta = actaInfo.getActa();
        BarCodeInfo barCodeInfo = actaInfo.getBarCodeInfo();

        if (codTipoResolucion == 999) {
            if (!ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(acta.getEstadoActa())) {
                return new GenericResponse<>(false,
                        String.format("El acta %s-%s no se encuentra en estado asociada a la resolución %s.",
                                barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito(),
                                ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION));
            }
        } else {
            if (!ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA.equals(acta.getEstadoActa())) {
                return new GenericResponse<>(false,
                        String.format(ConstantesComunes.MENSAJE_FORMATO_ACTA_ESTADO_NO_DEVUELTA,
                                barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
            }
        }

        ActaBean actaBean = getActaBeanVerificacionResolucion(acta, actaInfo.getNombreEleccion(),
                barCodeInfo.getNroCopiaAndDigito());

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroActaCopiaDig), actaBean);
    }

    private ActaBean getActaBeanVerificacionResolucion(Acta acta, String eleccionSeleccionada, String nroCopiaDig) {
        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(acta.getId());
        actaBean.setCodigoEleccion(acta.getUbigeoEleccion().getEleccion().getCodigo());
        actaBean.setMesa(acta.getMesa().getCodigo());
        actaBean.setIdArchivoEscrutinio(
                acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId().toString() : "");
        actaBean.setIdArchivoInstalacionSufragio(
                acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId().toString()
                        : "");
        actaBean.setEleccion(eleccionSeleccionada);
        actaBean.setCopia(nroCopiaDig);
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
        actaBean.setElectoresHabiles(acta.getElectoresHabiles() == null ? 0 : acta.getElectoresHabiles());
        actaBean.setEstadoDigitacion("PRIMERA DIGITACIÓN");

        UbigeoEleccion detUbigeoEleccion = acta.getUbigeoEleccion();
        actaBean.setUbigeo(detUbigeoEleccion.getUbigeo().getCodigo() + "-" + detUbigeoEleccion.getUbigeo().getNombre());
        actaBean.setLocalVotacion(acta.getMesa().getLocalVotacion().getNombre());
        actaBean.setHoraEscrutinio(acta.getHoraEscrutinioManual() == null ? ConstantesComunes.TEXT_HORA_CERO
                : acta.getHoraEscrutinioManual());
        actaBean.setHoraInstalacion(acta.getHoraInstalacionManual() == null ? ConstantesComunes.TEXT_HORA_CERO
                : acta.getHoraInstalacionManual());

        if (acta.getIlegibleCvas() == null) {
            actaBean.setCvas(
                    acta.getCvas() == null ? ConstantesComunes.VALUE_CVAS_INCOMPLETA : acta.getCvas().toString());
        } else {
            actaBean.setCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
        }

        actaBean.setSolNulidad(ConstantesComunes.VALUE_NO);
        actaBean.setActaSinFirma(ConstantesComunes.VALUE_NO);
        actaBean.setActasIncompletas(ConstantesComunes.VALUE_NO);
        actaBean.setActaSinDatos(ConstantesComunes.VALUE_NO);
        actaBean.setCantidadColumnas(0);

        Integer cantidadColumnasPreferencial = this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId());
        actaBean.setCantidadColumnas(cantidadColumnasPreferencial);

        setearObservacionesInfoActaVerificacionResoluciones(acta, actaBean);

        List<AgrupolBean> agrupolBeans = getAgrupolBeansByActa(acta, actaBean.getCodigoEleccion());
        actaBean.setAgrupacionesPoliticas(agrupolBeans);
        return actaBean;
    }

    private static void setearObservacionesInfoActaVerificacionResoluciones(Acta acta, ActaBean actaBean) {
        String estadoResolucion = acta.getEstadoActaResolucion();
        if (estadoResolucion != null) {
            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA)) {
                actaBean.setActaSinFirma(ConstantesComunes.VALUE_SI);
            }
            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS)) {
                actaBean.setActaSinDatos(ConstantesComunes.VALUE_SI);
            }
            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD)) {
                actaBean.setSolNulidad(ConstantesComunes.VALUE_SI);
            }
            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA)) {
                actaBean.setActasIncompletas(ConstantesComunes.VALUE_SI);
            }
        }
    }

    // obtiene informacion del acta, para la ASOCIACION
    @Override
    public GenericResponse<Object> obtenerInfoActa(Integer codTipoResolucion, String nroActaCopiaDig, Long idProceso) {

        if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)) {

            return obtenerInfoDeNoInstaladaParaAsociacion(nroActaCopiaDig);

        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                || Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)) {

            return obtenerInfoDeActaExtraSiniestradaParaAsociacion(nroActaCopiaDig);

        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE)) {

            if (nroActaCopiaDig.isEmpty()) {
                return new GenericResponse<>(false, ConstantesMensajes.MSJ_FORMAT_COPIA_Y_MESA_VACIAS);
            }

            if (nroActaCopiaDig.length() == 6) {
                return validarActasEnviadasAJeexMesaParaAsociacion(nroActaCopiaDig);
            } else if (nroActaCopiaDig.length() == 9) {
                return validarActasEnviadasAJeexCopiaParaAsociacion(nroActaCopiaDig);
            } else {
                return new GenericResponse<>(false,
                        "La cantidad de caracteres ingresada deber ser 6 (mesa) ó 9 (mesa+copia+digichequep).");
            }

        } else {// son de TIPO 999 para las RESOLUCIONES

            return obtenerInfoActa999VerificacionResolucion(nroActaCopiaDig, codTipoResolucion);
        }
    }

    @Override
    public GenericResponse<ActaBean> obtenerInfoActaById(Long idActa) {
        Optional<Acta> optionalActa = actaRepository.findById(idActa);
        if (optionalActa.isEmpty()) {
            return new GenericResponse<>(false, "El acta no esta registrada en la BD");
        }
        Acta acta = optionalActa.get();

        if (estaExtraviadaOSiniestrada(acta)) {
            logger.info("el acta esta extraviada o siniestrada.");
        } else if (!ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(acta.getEstadoActa())) {
            return new GenericResponse<>(false,
                    String.format("El acta %s-%s no se encuentra en estado asociada a la resolución %s.",
                            acta.getMesa().getCodigo(), acta.getUbigeoEleccion().getEleccion().getNombre(),
                            ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION));
        }

        ActaBean actaBean = construirActaBean(acta);
        List<AgrupolBean> agrupolBeans = getAgrupolBeansByActa(acta, actaBean.getCodigoEleccion());
        actaBean.setAgrupacionesPoliticas(agrupolBeans);

        return new GenericResponse<>(true, String.format("El acta %s-%s ha sido retornada correctamente.",
                acta.getMesa().getCodigo(), acta.getUbigeoEleccion().getEleccion().getNombre()), actaBean);
    }

    private boolean estaExtraviadaOSiniestrada(Acta acta) {
        String estado = acta.getEstadoActaResolucion();
        return estado != null && (estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)
                || estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA));
    }

    private ActaBean construirActaBean(Acta acta) {
        ActaBean bean = new ActaBean();
        bean.setActaId(acta.getId());
        bean.setCodigoEleccion(acta.getUbigeoEleccion().getEleccion().getCodigo());
        bean.setMesa(acta.getMesa().getCodigo());
        bean.setIdArchivoEscrutinio(getArchivoId(acta.getArchivoEscrutinio()));
        bean.setIdArchivoInstalacionSufragio(getArchivoId(acta.getArchivoInstalacionSufragio()));
        bean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());

        if (estaExtraviadaOSiniestrada(acta)) {
            bean.setCopia(
                    acta.getNumeroCopia() == null ? "" : acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio());
        } else {
            bean.setCopia(acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio());
        }

        bean.setEstadoActa(acta.getEstadoActa());
        bean.setEstadoComputo(acta.getEstadoCc());
        bean.setEstadoResolucion(acta.getEstadoActaResolucion());
        bean.setEstadoDigitalizacion(acta.getEstadoDigitalizacion());
        bean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
        bean.setElectoresHabiles(acta.getElectoresHabiles() == null ? 0 : acta.getElectoresHabiles());
        bean.setEstadoDigitacion("PRIMERA DIGITACIÓN");
        bean.setUbigeo(acta.getUbigeoEleccion().getUbigeo().getCodigo() + "-"
                + acta.getUbigeoEleccion().getUbigeo().getNombre());
        bean.setLocalVotacion(acta.getMesa().getLocalVotacion().getNombre());
        bean.setHoraEscrutinio(acta.getHoraEscrutinioManual() == null ? ConstantesComunes.TEXT_HORA_CERO
                : acta.getHoraEscrutinioManual());
        bean.setHoraInstalacion(acta.getHoraInstalacionManual() == null ? ConstantesComunes.TEXT_HORA_CERO
                : acta.getHoraInstalacionManual());

        if (acta.getIlegibleCvas() == null) {
            bean.setCvas(acta.getCvas() == null ? ConstantesComunes.VALUE_CVAS_INCOMPLETA : acta.getCvas() + "");
        } else {
            bean.setCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
        }

        bean.setSolNulidad(ConstantesComunes.VALUE_NO);
        bean.setActaSinFirma(ConstantesComunes.VALUE_NO);
        bean.setActasIncompletas(ConstantesComunes.VALUE_NO);
        bean.setActaSinDatos(ConstantesComunes.VALUE_NO);

        bean.setCantidadColumnas(this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId()));

        marcarEstadosResolucion(acta.getEstadoActaResolucion(), bean);

        return bean;
    }

    private String getArchivoId(Archivo archivo) {
        return archivo != null ? archivo.getId().toString() : "";
    }

    private void marcarEstadosResolucion(String estado, ActaBean bean) {
        if (estado == null)
            return;
        if (estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA)) {
            bean.setActaSinFirma(ConstantesComunes.VALUE_SI);
        }
        if (estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS)) {
            bean.setActaSinDatos(ConstantesComunes.VALUE_SI);
        }
        if (estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD)) {
            bean.setSolNulidad(ConstantesComunes.VALUE_SI);
        }
        if (estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA)) {
            bean.setActasIncompletas(ConstantesComunes.VALUE_SI);
        }
    }

    // Refactorización para reducir la complejidad de 138 a ~15
    private List<AgrupolBean> getAgrupolBeansByActa(Acta acta, String codigoEleccion) {
        return estaExtraviadaOSiniestrada(acta) ? construirAgrupolDesdeUbigeo(acta, codigoEleccion)
                : construirAgrupolDesdeDetActa(acta, codigoEleccion);
    }

    private List<AgrupolBean> construirAgrupolDesdeUbigeo(Acta acta, String codigoEleccion) {
        List<DetUbigeoEleccionAgrupacionPolitica> politicas = detUbigeoEleccionAgrupacionPoliticaRepository
                .findByUbigeoEleccion(acta.getUbigeoEleccion()).stream()
                .sorted(Comparator.comparing(DetUbigeoEleccionAgrupacionPolitica::getPosicion)).toList();

        long posicionActa = 1L;
        List<AgrupolBean> agrupolBeans = new ArrayList<>();

        for (DetUbigeoEleccionAgrupacionPolitica d : politicas) {
            AgrupolBean agrupolBean = construirAgrupolBase(d.getAgrupacionPolitica(), null, d.getEstado());

            if (esAgrupacionValida(d.getPosicion().longValue())) {
                agrupolBean.setPosicion(posicionActa);
                agregarVotosPorTipoEleccionExtraviada(agrupolBean, d.getEstado(), codigoEleccion, acta);
            } else {
                agrupolBean.setPosicion(d.getPosicion().longValue());
            }

            agrupolBeans.add(agrupolBean);
            posicionActa++;
        }
        return agrupolBeans;
    }

    private List<AgrupolBean> construirAgrupolDesdeDetActa(Acta acta, String codigoEleccion) {
        List<DetActa> detActas = detActaRepository.findByActa(acta).stream()
                .sorted(Comparator.comparing(DetActa::getPosicion)).toList();

        long posicionActa = 1L;
        List<AgrupolBean> agrupolBeans = new ArrayList<>();

        for (DetActa d : detActas) {
            DetUbigeoEleccionAgrupacionPolitica elecagrupl = detUbigeoEleccionAgrupacionPoliticaRepository
                    .findByUbigeoEleccionAndAgrupacionPolitica(acta.getUbigeoEleccion(), d.getAgrupacionPolitica());
            if (elecagrupl == null)
                continue;

            AgrupolBean agrupolBean = construirAgrupolBase(d.getAgrupacionPolitica(), d, elecagrupl.getEstado());
            agrupolBean.setIdDetActa(d.getId().toString());
            agrupolBean.setPosicion(esAgrupacionValida(d.getPosicion()) ? posicionActa : d.getPosicion());

            agregarVotosPorTipoEleccion(agrupolBean, d, elecagrupl.getEstado(), codigoEleccion);

            agrupolBeans.add(agrupolBean);
            posicionActa++;
        }
        return agrupolBeans;
    }

    private AgrupolBean construirAgrupolBase(AgrupacionPolitica agrupacion, DetActa detActa, Integer estado) {
        AgrupolBean bean = new AgrupolBean();
        bean.setIdAgrupol(agrupacion.getId());
        bean.setCodiAgrupol(agrupacion.getCodigo());
        bean.setEstado(estado);
        bean.setNombreAgrupacionPolitica(
                estado.equals(ConstantesComunes.N_ACHURADO) ? ConstantesComunes.VACIO : agrupacion.getDescripcion());
        if (detActa == null) {
            bean.setVotos(ConstantesComunes.CVALUE_ZERO);
        } else {
            if (Objects.equals(detActa.getIlegible(), ConstantesComunes.C_VALUE_ILEGIBLE)) {
                bean.setVotos(ConstantesComunes.C_VALUE_ILEGIBLE);
            } else if (detActa.getVotos() != null) {
                bean.setVotos(detActa.getVotos().toString());
            } else {
                bean.setVotos(ConstantesComunes.CVALUE_ZERO);
            }
        }
        return bean;
    }

    private void agregarVotosPorTipoEleccionExtraviada(AgrupolBean agrupolBean, Integer estado, String codigoEleccion,
                                                       Acta acta) {
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            List<VotoOpcionBean> opciones = crearVotoOpcionBeans(estado);
            agrupolBean.setVotosOpciones(opciones);
        } else if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
            int columnas = this.utilSceService.obtenerCantidadCandidatos(schema, acta.getId());
            List<VotoPreferencialBean> preferenciales = crearVotoPreferencialBeans(columnas, estado);
            agrupolBean.setVotosPreferenciales(preferenciales);
        }
    }

    private void agregarVotosPorTipoEleccion(AgrupolBean agrupolBean, DetActa d, Integer estado,
                                             String codigoEleccion) {
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            List<VotoOpcionBean> beans = construirVotosOpcionBeans(d, estado);
            agrupolBean.setVotosOpciones(beans);
        } else if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
            List<VotoPreferencialBean> beans = construirVotosPreferencialesBeans(d, estado);
            agrupolBean.setVotosPreferenciales(beans);
        }
    }

    private List<VotoOpcionBean> construirVotosOpcionBeans(DetActa d, Integer estado) {
        return detActaOpcionService.findByDetActa(d).stream().sorted(Comparator.comparing(DetActaOpcion::getPosicion))
                .map(o -> {
                    VotoOpcionBean bean = new VotoOpcionBean();
                    bean.setIdDetActaOpcion(o.getId());
                    bean.setPosicion(o.getPosicion());
                    bean.setEstado(estado);

                    if (Objects.equals(o.getIlegible(), ConstantesComunes.C_VALUE_ILEGIBLE)) {
                        bean.setVotos(ConstantesComunes.C_VALUE_ILEGIBLE);
                    } else if (o.getVotos() != null) {
                        bean.setVotos(o.getVotos().toString());
                    } else {
                        bean.setVotos(ConstantesComunes.CVALUE_ZERO);
                    }
                    return bean;
                }).toList();
    }

    private List<VotoPreferencialBean> construirVotosPreferencialesBeans(DetActa d, Integer estado) {
        return detActaPreferencialRepository.findByDetActa(d).stream()
                .sorted(Comparator.comparing(DetActaPreferencial::getLista)).map(p -> {
                    VotoPreferencialBean bean = new VotoPreferencialBean();
                    bean.setIdDetActaPreferencial(p.getId().toString());
                    bean.setLista(p.getLista());
                    bean.setEstado(estado);

                    if (Objects.equals(p.getIlegible(), ConstantesComunes.C_VALUE_ILEGIBLE)) {
                        bean.setVotos(ConstantesComunes.C_VALUE_ILEGIBLE);
                    } else if (p.getVotos() != null) {
                        bean.setVotos(p.getVotos().toString());
                    } else {
                        bean.setVotos(ConstantesComunes.CVALUE_ZERO);
                    }
                    return bean;
                }).toList();
    }

    private boolean esAgrupacionValida(Long posicion) {
        return !Objects.equals(posicion, ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)
                && !Objects.equals(posicion, ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS)
                && !Objects.equals(posicion, ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS);
    }

    private List<VotoPreferencialBean> crearVotoPreferencialBeans(int cantidad, Integer estado) {
        List<VotoPreferencialBean> lista = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            VotoPreferencialBean bean = new VotoPreferencialBean();
            bean.setLista(i);
            bean.setIdDetActaPreferencial(null);
            bean.setEstado(estado);
            bean.setVotos(Objects.equals(estado, ConstantesComunes.N_ACHURADO) ? ConstantesComunes.TEXTO_NULL : "0");
            if (!Objects.equals(estado, ConstantesComunes.N_ACHURADO))
                bean.setIlegible(null);
            lista.add(bean);
        }
        return lista;
    }

    private List<VotoOpcionBean> crearVotoOpcionBeans(Integer estado) {
        List<VotoOpcionBean> lista = new ArrayList<>();
        for (int i = ConstantesComunes.N_POSICION_CPR_VOTOS_SI; i <= ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS; i++) {
            VotoOpcionBean bean = new VotoOpcionBean();
            bean.setIdDetActaOpcion(null);
            long posicion;
            if (i == ConstantesComunes.N_POSICION_CPR_VOTOS_BLANCOS) {
                posicion = ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS;
            } else if (i == ConstantesComunes.N_POSICION_CPR_VOTOS_NULOS) {
                posicion = ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS;
            } else if (i == ConstantesComunes.N_POSICION_CPR_VOTOS_IMPUGNADOS) {
                posicion = ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS;
            } else {
                posicion = i;
            }
            bean.setPosicion(posicion);
            bean.setVotos(Objects.equals(estado, ConstantesComunes.N_ACHURADO) ? ConstantesComunes.TEXTO_NULL : "0");
            if (!Objects.equals(estado, ConstantesComunes.N_ACHURADO))
                bean.setIlegible(null);
            lista.add(bean);
        }
        return lista;
    }

    public List<ResolucionAsociadosRequest> listaResoluciones(String numeroResolucion,
                                                              List<String> estadosDigitalizacion, List<DetCatalogoEstructuraDTO> catalogoEstadosResolucion,
                                                              List<DetCatalogoEstructuraDTO> catalogoEstadosDigtalResolucion) {

        List<TabResolucion> tabResolucionList = this.tabResolucionRepository
                .findByEstadosDigitalAndActivoAndNumeroResolucionLike(estadosDigitalizacion, ConstantesComunes.ACTIVO,
                        numeroResolucion);

        return tabResolucionList.stream().map(
                        tab -> mapToResolucionAsociadosRequest(tab, catalogoEstadosResolucion, catalogoEstadosDigtalResolucion))
                .toList();

    }

    private ResolucionAsociadosRequest mapToResolucionAsociadosRequest(TabResolucion tabRes,
                                                                       List<DetCatalogoEstructuraDTO> catalogoEstadosResolucion,
                                                                       List<DetCatalogoEstructuraDTO> catalogoEstadosDigtalResolucion) {
        ResolucionAsociadosRequest resReq = new ResolucionAsociadosRequest();
        resReq.setId(tabRes.getId());
        resReq.setNumeroResolucion(tabRes.getNumeroResolucion());
        resReq.setNumeroExpediente(tabRes.getNumeroExpediente());
        resReq.setTipoResolucion(tabRes.getTipoResolucion());
        resReq.setDescripcionTipoResolucion(
                ConstantesCatalogo.getMapTiposResoluciones().getOrDefault(tabRes.getTipoResolucion(), "-"));
        resReq.setProcedencia(tabRes.getProcedencia());
        resReq.setFechaResolucion(tabRes.getFechaResolucion());
        resReq.setFechaResolucion2(tabRes.getFechaResolucion());
        resReq.setFechaRegistro(tabRes.getAudFechaCreacion());
        resReq.setEstadoResolucion(tabRes.getEstadoResolucion());
        resReq.setUsuarioAsociado(tabRes.getUsuarioAsociacion());

        if (tabRes.getArchivoResolucion() != null) {
            resReq.setIdArchivo(tabRes.getArchivoResolucion().getId());
            resReq.setNombreArchivo(tabRes.getArchivoResolucion().getNombre());
        }

        resReq.setNumeroPaginas(tabRes.getNumeroPaginas());
        resReq.setEstadoDigitalizacion(tabRes.getEstadoDigitalizacion());
        resReq.setDescripcionEstadoResolucion(
                buscarDescripcionCatalogo(catalogoEstadosResolucion, resReq.getEstadoResolucion()));
        resReq.setDescripcionEstadoDigitalizacion(
                buscarDescripcionCatalogo(catalogoEstadosDigtalResolucion, resReq.getEstadoDigitalizacion()));
        List<DetActaResolucion> detActaResolucionList = detActaResolucionRepository.findByResolucion(tabRes);
        resReq.setActasAsociadas(
                detActaResolucionList.stream().map(det -> mapToActaBean(det, tabRes.getTipoResolucion())).toList());

        return resReq;
    }

    private String buscarDescripcionCatalogo(List<DetCatalogoEstructuraDTO> catalogo, String codigo) {
        return catalogo.stream().filter(dto -> dto.getCodigoS().equals(codigo)).map(DetCatalogoEstructuraDTO::getNombre)
                .findFirst().orElse("-");
    }

    private ActaBean mapToActaBean(DetActaResolucion det, Integer tipoResolucion) {
        ActaBean actaBean = new ActaBean();
        Acta acta = det.getActa();
        actaBean.setActaId(acta.getId());
        actaBean.setMesa(acta.getMesa().getCodigo());

        if (esTipoResolucionSinCopia(tipoResolucion)) {
            actaBean.setCopia("");
        } else if (esActaExtraviada(acta)) {
            actaBean.setCopia(ConstantesComunes.TEXT_EXTRAVIADA);
        } else if (esActaSiniestrada(acta)) {
            actaBean.setCopia(ConstantesComunes.TEXT_SINIESTRADA);
        } else {
            actaBean.setCopia(
                    acta.getNumeroCopia() == null ? "" : acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio());
        }

        actaBean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));

        return actaBean;
    }

    private boolean esTipoResolucionSinCopia(Integer tipoResolucion) {
        return Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS);
    }

    private boolean esActaExtraviada(Acta acta) {
        return ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(acta.getEstadoActa())
                && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA);
    }

    private boolean esActaSiniestrada(Acta acta) {
        return ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(acta.getEstadoActa())
            && acta.getEstadoActaResolucion()!=null && acta.getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA);
    }

    @Override
    public Optional<TabResolucion> findById(Long id) {
        return this.tabResolucionRepository.findById(id);
    }

    @Override
    public ActaJeeBean obtenerActasEnvioJee(Long idEleccion) {
        ActaJeeBean response = new ActaJeeBean();
        long totalActas;
        long actasObservadas;
        long actasNormales;

        if (Objects.isNull(idEleccion) || idEleccion.equals(0L)) {
            // TODAS LAS ELECCIONES
            totalActas = this.actaRepository.count();
            actasObservadas = this.actaRepository
                    .findByEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO).size();
            actasNormales = this.actaRepository.findByEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA).size();
            int procesadasPorResolucion = this.actaRepository
                    .findByEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION).size();
            actasNormales = actasNormales + procesadasPorResolucion;
        } else {
            List<ActaDTO> actas = this.actaRepository.findActasByEleccion(idEleccion.toString());
            totalActas = actas.size();
            actasObservadas = actas.stream()
                    .filter(acta -> ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(acta.getEstadoActa()))
                    .count();
            actasNormales = actas.stream()
                    .filter(acta -> ConstantesEstadoActa.ESTADO_ACTA_PROCESADA.equals(acta.getEstadoActa())
                            || ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION.equals(acta.getEstadoActa()))
                    .count();
        }

        response.setTotal(String.valueOf(totalActas));
        response.setTotalNormales(String.valueOf(actasNormales));
        response.setTotalObservadas(String.valueOf(actasObservadas));
        return response;
    }

    @Override
    public GenericResponse<Object> obtenerActaEnvioJee(String cc, String nroMesa, String nroCopiaAndDigCheck) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroMesa.concat(nroCopiaAndDigCheck), cc, Boolean.FALSE);
        Acta acta = actaInfo.getActa();

        List<String> estadosEnvioJeePermitidos = Arrays.asList(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA,
                ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION,
                ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
        if (estadosEnvioJeePermitidos.stream().noneMatch(p -> p.equals(acta.getEstadoActa()))) {
            return new GenericResponse<>(false, String.format(
                    "El acta %s-%s no se encuentra en un estado normal u observada.", nroMesa, nroCopiaAndDigCheck));
        }

        ParametroDto parametroDto = parametroService
                .obtenerParametro(ConstantesParametros.CAB_PARAM_FLUJO_AUTOMATIZADO);
        boolean flujoAutomatizadoActivo = parametroDto != null && parametroDto.getValor() != null
                && ConstantesComunes.TEXT_TRUE.equalsIgnoreCase(parametroDto.getValor().toString());

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(acta.getId());
        actaBean.setIdArchivoEscrutinio(getArchivoIdAsString(acta.getArchivoEscrutinio()));
        actaBean.setIdArchivoInstalacionSufragio(getArchivoIdAsString(acta.getArchivoInstalacionSufragio()));
        actaBean.setIdArchivoEscrutinioFirmado(getArchivoIdAsString(acta.getArchivoEscrutinioFirmado()));
        actaBean.setIdArchivoInstalacionFirmado(getArchivoIdAsString(acta.getArchivoInstalacionFirmado()));
        actaBean.setIdArchivoSufragioFirmado(getArchivoIdAsString(acta.getArchivoSufragioFirmado()));
        actaBean.setMesa(acta.getMesa().getCodigo());
        actaBean.setEleccion(actaInfo.getNombreEleccion());
        actaBean.setCopia(nroCopiaAndDigCheck);
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));

        boolean esStaeIntegrada = Long.valueOf(2).equals(acta.getSolucionTecnologica())
                && Integer.valueOf(1).equals(acta.getTipoTransmision());
        actaBean.setStaeIntegrada(esStaeIntegrada);
        actaBean.setFlujoAutomatizado(flujoAutomatizadoActivo);

        return new GenericResponse<>(true,
                String.format("El acta %s-%s ha sido validada correctamente.", nroMesa, nroCopiaAndDigCheck), actaBean);
    }

    private String getArchivoIdAsString(Archivo archivo) {
        return archivo != null ? String.valueOf(archivo.getId()) : null;
    }

    @Override
    public ResolucionAsociadosRequest getResolucion(Long id) {
        ResolucionAsociadosRequest resReq = new ResolucionAsociadosRequest();
        if (id != null) {

            List<DetCatalogoEstructuraDTO> catalogoEstadosResolucion = this.detalleCatalogoEstructuraService
                    .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_RESOLUCION,
                            ConstantesCatalogo.DET_ESTADO_RESOLUCION);

            List<DetCatalogoEstructuraDTO> catalogoEstadosDigtalResolucion = this.detalleCatalogoEstructuraService
                    .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_RESOLUCION,
                            ConstantesCatalogo.DET_ESTADO_DIGITALIZACION_RESOLUCION);

            Optional<TabResolucion> optTabRes = this.tabResolucionRepository.findById(id);
            if (optTabRes.isPresent()) {
                TabResolucion tabRes = optTabRes.get();
                resReq.setId(tabRes.getId());
                resReq.setNumeroResolucion(tabRes.getNumeroResolucion());
                resReq.setNumeroExpediente(tabRes.getNumeroExpediente());
                resReq.setTipoResolucion(tabRes.getTipoResolucion());
                resReq.setProcedencia(tabRes.getProcedencia());
                resReq.setFechaResolucion(tabRes.getFechaResolucion());
                resReq.setFechaResolucion2(tabRes.getFechaResolucion());
                resReq.setFechaRegistro(tabRes.getAudFechaCreacion());
                resReq.setEstadoResolucion(tabRes.getEstadoResolucion());
                resReq.setIdArchivo(
                        tabRes.getArchivoResolucion() == null ? null : tabRes.getArchivoResolucion().getId());
                resReq.setNombreArchivo(
                        tabRes.getArchivoResolucion() == null ? null : tabRes.getArchivoResolucion().getNombre());
                resReq.setNumeroPaginas(tabRes.getNumeroPaginas());
                resReq.setEstadoDigitalizacion(tabRes.getEstadoDigitalizacion());
                resReq.setDescripcionEstadoResolucion(
                        buscarDescripcionCatalogo(catalogoEstadosResolucion, resReq.getEstadoResolucion()));
                resReq.setDescripcionEstadoDigitalizacion(
                        buscarDescripcionCatalogo(catalogoEstadosDigtalResolucion, resReq.getEstadoDigitalizacion()));
                List<ActaBean> actaBeanList = new ArrayList<>();
                List<DetActaResolucion> detActaResolucionList = this.detActaResolucionRepository
                        .findByResolucion(tabRes);
                for (DetActaResolucion detActaResolucion : detActaResolucionList) {
                    ActaBean actaBean = new ActaBean();
                    Acta acta = detActaResolucion.getActa();
                    Eleccion eleccion = acta.getUbigeoEleccion().getEleccion();
                    actaBean.setActaId(acta.getId());
                    actaBean.setMesa(acta.getMesa().getCodigo());
                    actaBean.setCopia(obtenerDescripcionCopia(detActaResolucion, resReq.getTipoResolucion()));
                    actaBean.setEleccion(eleccion.getNombre());
                    actaBean.setCodigoEleccion(eleccion.getCodigo());
                    actaBean.setCodigoProceso(eleccion.getProcesoElectoral().getId() + "");
                    actaBean.setEstadoActa(acta.getEstadoActa());
                    actaBean.setEstadoResolucion(acta.getEstadoActaResolucion());
                    actaBean.setEstadoDigitalizacion(acta.getEstadoDigitalizacion());
                    actaBean.setEstadoComputo(acta.getEstadoCc());
                    actaBean.setDescripcionEstadoActa(
                            ConstantesEstadoActa.getMapEstadoActas().get(actaBean.getEstadoActa()));
                    actaBean.setObservacionesJNE(detActaResolucion.getObservacionJne());
                    actaBeanList.add(actaBean);
                }
                resReq.setActasAsociadas(actaBeanList);
            } else {
                return null;
            }
        }
        return resReq;
    }

    @Override
    public SearchFilterResponse<ResolucionDevueltasRequest> getResolucionesDevueltas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("resolucion.audFechaCreacion").descending());

        Page<DetActaResolucion> detActaPage = detActaResolucionRepository.findAll(pageable);

        List<DetCatalogoEstructuraDTO> catalogoEstadosResolucion = this.detalleCatalogoEstructuraService
                .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_RESOLUCION,
                        ConstantesCatalogo.DET_ESTADO_RESOLUCION);

        List<DetCatalogoEstructuraDTO> catalogoEstadosDigtalResolucion = this.detalleCatalogoEstructuraService
                .findByMaestroAndColumna(ConstantesCatalogo.MAE_ESTADO_DIGITALIZACION_RESOLUCION,
                        ConstantesCatalogo.DET_ESTADO_DIGITALIZACION_RESOLUCION);

        List<ResolucionDevueltasRequest> listaRes = new ArrayList<>();

        for (DetActaResolucion det : detActaPage.getContent()) {
            TabResolucion tabRes = det.getResolucion();
            Acta acta = det.getActa();
            Eleccion eleccion = acta.getUbigeoEleccion().getEleccion();

            ResolucionDevueltasRequest req = new ResolucionDevueltasRequest();
            req.setId(tabRes.getId());
            req.setNumeroResolucion(tabRes.getNumeroResolucion());
            req.setNumeroExpediente(tabRes.getNumeroExpediente());
            req.setTipoResolucion(tabRes.getTipoResolucion());
            req.setProcedencia(tabRes.getProcedencia());
            req.setFechaResolucion(tabRes.getFechaResolucion());
            req.setFechaResolucion2(tabRes.getFechaResolucion());
            req.setFechaRegistro(tabRes.getAudFechaCreacion());
            req.setEstadoResolucion(tabRes.getEstadoResolucion());
            req.setIdArchivo(tabRes.getArchivoResolucion() == null ? null : tabRes.getArchivoResolucion().getId());
            req.setNombreArchivo(
                    tabRes.getArchivoResolucion() == null ? null : tabRes.getArchivoResolucion().getNombre());
            req.setNumeroPaginas(tabRes.getNumeroPaginas());
            req.setEstadoDigitalizacion(tabRes.getEstadoDigitalizacion());

            req.setDescripcionEstadoResolucion(
                    buscarDescripcionCatalogo(catalogoEstadosResolucion, req.getEstadoResolucion()));
            req.setDescripcionEstadoDigitalizacion(
                    buscarDescripcionCatalogo(catalogoEstadosDigtalResolucion, req.getEstadoDigitalizacion()));

            req.setMesa(acta.getMesa().getCodigo());
            req.setNumeroActa(acta.getId().toString());
            req.setCodigoEleccion(eleccion.getCodigo());
            req.setCodigoProceso(String.valueOf(eleccion.getProcesoElectoral().getId()));

            listaRes.add(req);
        }

        return SearchFilterResponse.<ResolucionDevueltasRequest>builder().list(listaRes).page(page).size(size)
                .total(detActaPage.getTotalElements()).totalPages(detActaPage.getTotalPages()).build();
    }

    private String obtenerDescripcionCopia(DetActaResolucion det, Integer tipoResolucion) {
        Acta acta = det.getActa();

        if (Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS))
            return ConstantesComunes.VACIO;

        if (Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE))
            return descripcionActasEnviadasJEE(acta);

        if (Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE))
            return descripcionReproceso(acta);

        return acta.getNumeroCopia() == null ? ConstantesComunes.VACIO
                : acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio();
    }

    private String descripcionActasEnviadasJEE(Acta acta) {
        if (acta.getNumeroCopia() != null) {
            return acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio();
        }

        boolean procesadaObservada = acta.getEstadoCc()
                .contains(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);

        if (procesadaObservada) {
            return switch (acta.getEstadoActaResolucion()) {
                case ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA -> ConstantesComunes.TEXT_EXTRAVIADA;
                case ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA -> ConstantesComunes.TEXT_SINIESTRADA;
                default -> ConstantesComunes.VACIO;
            };
        }

        boolean procesadaContabilizada = acta.getEstadoCc()
                .contains(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
        if (procesadaContabilizada) {
            return switch (acta.getEstadoActa()) {
                case ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA -> ConstantesComunes.TEXT_EXTRAVIADA;
                case ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA -> ConstantesComunes.TEXT_SINIESTRADA;
                default -> ConstantesComunes.VACIO;
            };
        }

        return ConstantesComunes.VACIO;
    }

    private String descripcionReproceso(Acta acta) {

        if (acta == null) {
            return ConstantesComunes.VACIO;
        }

        String estadoResolucion = acta.getEstadoActaResolucion();
        String estadoCc = acta.getEstadoCc();

        boolean enProceso = estadoCc != null &&
            estadoCc.contains(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO);

        if (enProceso && estadoResolucion != null) {

            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)) {
                return ConstantesComunes.TEXT_EXTRAVIADA + "-REPROCESO";
            }

            if (estadoResolucion.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA)) {
                return ConstantesComunes.TEXT_SINIESTRADA + "-REPROCESO";
            }
        }

        String numeroCopia = acta.getNumeroCopia();
        String digito = acta.getDigitoChequeoEscrutinio();

        if (numeroCopia != null && digito != null) {
            return numeroCopia + digito;
        }

        return ConstantesComunes.VACIO;
    }

    @Override
    @Transactional
    public GenericResponse<Object> registrarAsociacionConActas(TokenInfo tokenInfo, ResolucionAsociadosRequest resReq, Long idUbigeo, Long idLocalVotacion, Long idEleccion) {

        Optional<TabResolucion> optTabResolucion = this.tabResolucionRepository.findById(resReq.getId());
        if (optTabResolucion.isEmpty()) {
            return new GenericResponse<>(false,
                    String.format("La resolución %s no se encuentra registrada.", resReq.getNumeroResolucion()));
        }

        TabResolucion tabResolucion = optTabResolucion.get();

        if (tabResolucion.getEstadoResolucion().equals(ConstantesEstadoResolucion.PROCESADO)) {
            throw new BadRequestException("La resolución se encuentra en estado Procesado. No puede editarse");
        }

        // Validar todas las actas antes de procesar
        List<String> erroresValidacion = validarActasParaAsociacion(
                resReq.getActasAsociadas(), resReq.getTipoResolucion(), idUbigeo, idLocalVotacion, idEleccion);
        if (!erroresValidacion.isEmpty()) {
            return new GenericResponse<>(false, String.join(" | ", erroresValidacion));
        }

        // actas a exluir que ya vienen con estado ASOCIADO para no volver a generar su
        // trama de asociacion
        List<Long> idActasAExcluir = resReq.getActasAsociadas().stream()
                .filter(a -> a.getEstadoActa() != null
                        && a.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION))
                .map(ActaBean::getActaId).toList();
        logger.info("Actas ya asociadas, por excluir de la generación de tramas: {}", idActasAExcluir);

        actualizarDatosResolucionAsociacion(tabResolucion, resReq, tokenInfo.getNombreUsuario());

        this.tabResolucionRepository.save(tabResolucion);

        revertirEstadosActasAntesdeAsociar(tabResolucion, tokenInfo.getNombreUsuario());

        // Precalcular actas que ya tienen det_acta_resolucion para esta resolución (antes del delete)
        List<Long> actaIdsConResolucionPrevia = this.detActaResolucionRepository.findByResolucion(tabResolucion).stream()
                .map(d -> d.getActa().getId())
                .distinct()
                .toList();

        this.detActaResolucionRepository.deleteDetActaResolucionByResolucion(tabResolucion);

        List<Long> actaIds = new ArrayList<>();
        for (ActaBean actaBean : resReq.getActasAsociadas()) {
            GenericResponse<Object> resultado = procesarActaAsociada(tabResolucion, tokenInfo.getNombreUsuario(),
                    actaBean, actaIds, resReq.getTipoPasarNulos(), actaIdsConResolucionPrevia);
            if (!resultado.isSuccess())
                return resultado;
        }

        if (!actaIds.isEmpty()) {
            actaIds.removeAll(idActasAExcluir);
        }

        this.logService.registrarLog(tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                "Se registró la asociación correctamente con la resolución " + tabResolucion.getNumeroResolucion(),
                tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return new GenericResponse<>(true, "Se registró la asociación correctamente.", null, actaIds);
    }

    private void actualizarDatosResolucionAsociacion(TabResolucion tabResolucion, ResolucionAsociadosRequest resReq,
                                                     String usuario) {
        tabResolucion.setProcedencia(resReq.getProcedencia());
        tabResolucion.setNumeroResolucion(resReq.getNumeroResolucion());
        tabResolucion.setTipoResolucion(resReq.getTipoResolucion());
        tabResolucion.setNumeroExpediente(resReq.getNumeroExpediente());

        if (resReq.getFechaResolucion() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(resReq.getFechaResolucion());
            Calendar now = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, now.get(Calendar.SECOND));
            cal.set(Calendar.MILLISECOND, now.get(Calendar.MILLISECOND));
            tabResolucion.setFechaResolucion(cal.getTime());
        }

        tabResolucion.setEstadoResolucion(ConstantesEstadoResolucion.EN_PROCESO);
        tabResolucion.setAudUsuarioModificacion(usuario);
        tabResolucion.setAudFechaModificacion(new Date());
    }

    private void revertirEstadosActasAntesdeAsociar(TabResolucion tabResolucion, String usuario) {

        List<DetActaResolucion> anteriores = this.detActaResolucionRepository.findByResolucion(tabResolucion);

        for (DetActaResolucion detalle : anteriores) {
            Acta acta = detalle.getActa();
            Integer tipoResol = tabResolucion.getTipoResolucion();

            validarRestricciones(tabResolucion, acta, tipoResol);

            if (tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE)) {
                procesarRevertirEnviadasJEE(acta, usuario);
            } else if(
                tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS) ||
                tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)) {
                procesarRevertirDeclaradasSiniestradas(acta, usuario);
            } else if (tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE)
                    || tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE)) {
                procesarRevertirReproceso(acta, usuario);
            }
        }

    }

    private void validarRestricciones(TabResolucion tabResolucion, Acta acta, Integer tipoResol) {

        if ((tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE)
                || tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE)
                || tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE))
                && tabResolucion.getAudUsuarioAsignado() != null) {
            throw new BadRequestException(String.format(
                    "No es posible actualizar la asociación, la resolución fue asignada al usuario %s para su aplicación.",
                    tabResolucion.getAudUsuarioAsignado()));
        }

        if (tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)
                && ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA.equals(acta.getEstadoActa())) {
            throw new BadRequestException(String.format(
                    "No es posible actualizar la asociación, la mesa del acta %d se encuentra en estado no instalada.",
                    acta.getId()));
        }

        if (tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                && ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(acta.getEstadoActa())) {
            throw new BadRequestException(String.format(
                    "No es posible actualizar la asociación, el acta %d se encuentra en estado extraviado.",
                    acta.getId()));
        }

        if (tipoResol.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)
                && ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(acta.getEstadoActa())) {
            throw new BadRequestException(String.format(
                    "No es posible actualizar la asociación, el acta %d se encuentra en estado siniestrado.",
                    acta.getId()));
        }
    }

    private void procesarRevertirEnviadasJEE(Acta acta, String usuario) {
        if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA);
        } else if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA);
        } else {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
        }

        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());

        this.actaRepository.save(acta);
    }

    private void procesarRevertirDeclaradasSiniestradas(Acta acta, String usuario) {
        if (acta.getEstadoActaResolucion() == null && acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE)
            && acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE);
        }

        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());

        this.actaRepository.save(acta);
    }

    private void procesarRevertirReproceso(Acta acta, String usuario) {

        if (ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(acta.getEstadoActa())
                && acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO)) {

            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
            acta.setUsuarioModificacion(usuario);
            acta.setFechaModificacion(new Date());
            this.actaRepository.save(acta);
        }

    }

    private GenericResponse<Object> procesarActaAsociada(TabResolucion tabResolucion, String usuario, ActaBean actaBean,
                                                         List<Long> actaIds, String tipoPasarNulos, List<Long> actaIdsConResolucionPrevia) {
        if (Objects.equals(tabResolucion.getTipoResolucion(),
                ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)) {
            return procesarActaNoInstaladaAsociacion(tabResolucion, usuario, actaBean, actaIds);
        } else {
            return procesarActaNormalAsociacion(tabResolucion, usuario, actaBean, actaIds, tipoPasarNulos, actaIdsConResolucionPrevia);
        }
    }

    private GenericResponse<Object> procesarActaNoInstaladaAsociacion(TabResolucion tabResolucion, String usuario,
                                                                      ActaBean actaBean, List<Long> actaIds) {
        Optional<Mesa> optMesa = this.mesaRepository.findById(actaBean.getMesaId());
        if (optMesa.isEmpty()) {
            return new GenericResponse<>(false,
                    String.format("La mesa %s no se encuentra registrada.", actaBean.getMesa()));
        }

        for (Acta acta : this.actaRepository.findByMesa(optMesa.get())) {
            actaIds.add(acta.getId());
            actualizarYGuardarActaAsociada(acta, usuario);
            guardarDetActaResolucionAsociacion(usuario, acta, tabResolucion);
        }

        return new GenericResponse<>(true, "");
    }

    private GenericResponse<Object> procesarActaNormalAsociacion(TabResolucion tabResolucion, String usuario,
                                                                 ActaBean actaBean, List<Long> actaIds, String tipoPasarNulos, List<Long> actaIdsConResolucionPrevia) {
        Optional<Acta> optional = this.actaRepository.findById(actaBean.getActaId());
        if (optional.isEmpty()) {
            return new GenericResponse<>(false, String.format("El acta %s-%s no se encuentra registrada.",
                    actaBean.getMesa(), actaBean.getCopia()));
        }

        Acta acta = optional.get();

        if (List.of(ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS,
                        ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO,
                        ConstantesCatalogo.CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO)
                .contains(tabResolucion.getTipoResolucion())) {
            // Solo agregar a actaIds si no tenía registros previos en det_acta_resolucion para esta resolución
            if (!actaIdsConResolucionPrevia.contains(acta.getId())) {
                actaIds.add(acta.getId());
            }
        } else {
            actaIds.add(acta.getId());
            actualizarYGuardarActaAsociada(acta, usuario);
        }

        if (Objects.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO,
                tabResolucion.getTipoResolucion())) {
            procesarActaAnulada(tabResolucion.getId(), acta, usuario, tipoPasarNulos);
        }

        guardarDetActaResolucionAsociacion(usuario, acta, tabResolucion);

        return new GenericResponse<>(true, "");
    }

    /**
     * Valida las actas del request contra getInfoActaParaAsociacionResoluciones.
     * Para tipos por mesa (no instaladas, extraviadas, siniestradas): agrupa por mesa,
     * valida que cada acta del request sea subconjunto del resultado.
     * Para tipos por acta: valida una a una.
     * Para tipos por ubigeo: valida con idUbigeo/idLocalVotacion/idEleccion.
     * Retorna lista de errores (vacía si todo OK).
     */
    private List<String> validarActasParaAsociacion(List<ActaBean> actasAsociadas, Integer tipoResolucion,
                                                     Long idUbigeo, Long idLocalVotacion, Long idEleccion) {
        List<String> errores = new ArrayList<>();

        boolean esTipoPorMesa = Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS);

        boolean esTipoPorUbigeo = Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO)
                || Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO);

        if (esTipoPorMesa) {
            validarActasPorMesa(actasAsociadas, tipoResolucion, errores);
        } else if (esTipoPorUbigeo) {
            validarActasPorUbigeo(actasAsociadas, tipoResolucion, idUbigeo, idLocalVotacion, idEleccion, errores);
        } else {
            validarActasPorCopia(actasAsociadas, tipoResolucion, errores);
        }

        return errores;
    }

    private void validarActasPorMesa(List<ActaBean> actasAsociadas, Integer tipoResolucion, List<String> errores) {
        boolean esNoInstalada = Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS);

        // Agrupar actas del request por mesa
        Map<String, List<ActaBean>> actasPorMesa = actasAsociadas.stream()
                .filter(a -> !ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(a.getEstadoActa()))
                .collect(Collectors.groupingBy(ActaBean::getMesa));

        for (Map.Entry<String, List<ActaBean>> entry : actasPorMesa.entrySet()) {
            String nroMesa = entry.getKey();
            List<ActaBean> actasDelRequest = entry.getValue();

            try {
                GenericResponse<List<ActaBean>> resultado = getInfoActaParaAsociacionResoluciones(
                        tipoResolucion, nroMesa, null, null, null, null);

                if (!resultado.isSuccess()) {
                    errores.add("Mesa " + nroMesa + " ya no cumple las condiciones para asociarse: " + resultado.getMessage());
                    continue;
                }

                // No instalada: solo viene la mesa, no actas individuales
                if (esNoInstalada) {
                    continue;
                }

                // Extraviadas/siniestradas: verificar que cada acta del request esté en el resultado
                List<Long> actaIdsValidos = resultado.getData().stream()
                        .map(ActaBean::getActaId)
                        .filter(Objects::nonNull)
                        .toList();

                for (ActaBean actaReq : actasDelRequest) {
                    if (actaReq.getActaId() != null && !actaIdsValidos.contains(actaReq.getActaId())) {
                        String eleccion = actaReq.getEleccion() != null ? " (" + actaReq.getEleccion() + ")" : "";
                        errores.add(nroMesa + eleccion + ": el acta ya no cumple las condiciones para asociarse.");
                    }
                }
            } catch (Exception e) {
                errores.add("Mesa " + nroMesa + ": " + e.getMessage());
            }
        }
    }


    private void validarActasPorUbigeo(List<ActaBean> actasAsociadas, Integer tipoResolucion,
                                        Long idUbigeo, Long idLocalVotacion, Long idEleccion, List<String> errores) {
        try {
            GenericResponse<List<ActaBean>> resultado = getInfoActaParaAsociacionResoluciones(
                    tipoResolucion, null, idUbigeo, idLocalVotacion, idEleccion, null);

            if (!resultado.isSuccess()) {
                errores.add(resultado.getMessage());
                return;
            }

            List<Long> actaIdsValidos = resultado.getData().stream()
                    .map(ActaBean::getActaId)
                    .filter(Objects::nonNull)
                    .toList();

            for (ActaBean actaReq : actasAsociadas) {
                if (ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(actaReq.getEstadoActa())) {
                    continue;
                }
                if (actaReq.getActaId() != null && !actaIdsValidos.contains(actaReq.getActaId())) {
                    String identificador = actaReq.getMesa()
                            + (actaReq.getCopia() != null && !actaReq.getCopia().isEmpty() ? "-" + actaReq.getCopia() : "")
                            + (actaReq.getEleccion() != null ? " (" + actaReq.getEleccion() + ")" : "");
                    errores.add("Acta " + identificador + ": ya no cumple las condiciones para asociarse.");
                }
            }
        } catch (Exception e) {
            errores.add(e.getMessage());
        }
    }

    private void validarActasPorCopia(List<ActaBean> actasAsociadas, Integer tipoResolucion, List<String> errores) {
        for (ActaBean actaBean : actasAsociadas) {
            if (ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(actaBean.getEstadoActa())) {
                continue;
            }

            String nroActaCopiaDig = actaBean.getMesa() + (actaBean.getCopia() != null ? actaBean.getCopia() : "");
            String identificador = actaBean.getMesa()
                    + (actaBean.getCopia() != null && !actaBean.getCopia().isEmpty() ? "-" + actaBean.getCopia() : "")
                    + (actaBean.getEleccion() != null ? " (" + actaBean.getEleccion() + ")" : "");

            try {
                GenericResponse<List<ActaBean>> resultado = getInfoActaParaAsociacionResoluciones(
                        tipoResolucion, nroActaCopiaDig, null, null, null, null);
                if (!resultado.isSuccess()) {
                    errores.add("Acta " + identificador + ": " + resultado.getMessage());
                }
            } catch (Exception e) {
                errores.add("Acta " + identificador + ": " + e.getMessage());
            }
        }
    }

    private void actualizarYGuardarActaAsociada(Acta acta, String usuario) {
        acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION);
        acta.setFechaModificacion(new Date());
        acta.setUsuarioModificacion(usuario);
        this.actaRepository.save(acta);
    }

    private void procesarActaAnulada(Long idResolucion, Acta acta, String usuario, String tipoPasarNulos) {

        this.cabCcResolucionService.spRegistrarCcResolucion(schema, acta.getId(),
                ConstantesComunes.ESTADO_CAMBIO_RESOL_ANTES, idResolucion, usuario);

        List<DetActa> detActas = this.detActaRepository.findByActa(acta);
        long valorParaNulos = 0L;

        if (tipoPasarNulos.equals(ConstantesComunes.TIPO_PASAR_NULOS_ELECTORES_HABILES)) {
            valorParaNulos = acta.getElectoresHabiles();

        } else if (tipoPasarNulos.equals(ConstantesComunes.TIPO_PASAR_NULOS_CVAS)) {
            valorParaNulos = acta.getCvas();

        } else if (tipoPasarNulos.equals(ConstantesComunes.TIPO_PASAR_NULOS_SUMA_VOTOS)) {
            if (acta.getUbigeoEleccion().getEleccion().getCodigo().equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
                valorParaNulos = this.detActaOpcionService.obtenerSumaVotosPrimerDetActa(acta.getId());
            } else {
                valorParaNulos = detActas.stream().map(DetActa::getVotos).filter(Objects::nonNull)
                        .mapToLong(Long::longValue).sum();
            }
        }

        this.detActaRepository.anularDetActaContabilizada(acta.getId(), valorParaNulos,
                ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS);
        this.detActaPreferencialRepository.anularDetActaPreferencialContabilizada(acta.getId());
        this.detActaOpcionService.anularDetActaOpcionContabilizada(acta.getId(), valorParaNulos,
                ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS);

        acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ANULADA);
        acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
        acta.setEstadoErrorMaterial(null);
        acta.setEstadoActaResolucion(null);
        acta.setFechaModificacion(new Date());
        acta.setUsuarioModificacion(usuario);
        this.actaRepository.save(acta);

        this.cabCcResolucionService.spRegistrarCcResolucion(schema, acta.getId(),
                ConstantesComunes.ESTADO_CAMBIO_RESOL_DESPUES, idResolucion, usuario);
    }

    private void guardarDetActaResolucionAsociacion(String usuario, Acta acta, TabResolucion resolucion) {
        DetActaResolucion det = getDetActaResolucion(usuario, acta, resolucion);
        this.detActaResolucionRepository.save(det);
    }

    @Override
    public TabResolucion saveResolucion(String numeroResolucion, int numeroPaginas, Archivo archivo, String usuario) {
        TabResolucion tabResolucion = new TabResolucion();
        tabResolucion.setArchivoResolucion(archivo);

        Optional<AmbitoElectoral> optAmbitoElectoral = this.ambitoElectoralService.findOneAmbitoElectoral();
        optAmbitoElectoral.ifPresent(ambitoElectoral -> tabResolucion.setIdAmbitoElectoral(ambitoElectoral.getId()));

        tabResolucion.setNumeroResolucion(numeroResolucion);
        tabResolucion.setEstadoResolucion(ConstantesEstadoResolucion.SIN_PROCESAR);
        tabResolucion.setEstadoDigitalizacion(ConstantesEstadoResolucion.DIGTAL_DIGITALIZADA);
        tabResolucion.setNumeroPaginas(numeroPaginas);
        tabResolucion.setActivo(ConstantesComunes.ACTIVO);
        tabResolucion.setAudFechaCreacion(new Date());
        tabResolucion.setAudUsuarioCreacion(usuario);

        tabResolucionRepository.save(tabResolucion);
        return tabResolucion;
    }

    private DetActaResolucion getDetActaResolucion(String usuario, Acta acta, TabResolucion tabResolucion) {

        List<DetActaResolucion> detActaResolucionList = this.detActaResolucionRepository.findByActa_Id(acta.getId());
        Optional<Integer> maxCorrelativo = detActaResolucionList.stream().map(DetActaResolucion::getCorrelativo)
                .max(Integer::compareTo);

        int correlativo = 1;
        if (maxCorrelativo.isPresent()) {
            correlativo = maxCorrelativo.get() + 1;// la siguiente resolucion
        }

        DetActaResolucion detActaResolucion = new DetActaResolucion();
        detActaResolucion.setActa(acta);
        detActaResolucion.setResolucion(tabResolucion);
        detActaResolucion.setActivo(ConstantesComunes.ACTIVO);
        detActaResolucion.setFechaCreacion(new Date());
        detActaResolucion.setUsuarioCreacion(usuario);
        detActaResolucion.setCorrelativo(correlativo);
        detActaResolucion.setEstadoActa(acta.getEstadoActa());
        return detActaResolucion;
    }

    @Override
    @Transactional
    public GenericResponse<AplicarActaBean> aplicarResolucion(TokenInfo tokenInfo, ActaBean actaBean) {

        Long actaId = actaBean.getActaId();
        Long resolucionId = Long.valueOf(actaBean.getResolucionId());

        // Bloquear acta al inicio para evitar procesamiento paralelo
        Acta acta = actaRepository.findByIdForUpdate(actaId)
            .orElseThrow(() -> new IllegalArgumentException("El acta no está registrada en BD."));

        GenericResponse<AplicarActaBean> x = validarActaReconteoAnuladaEncontrada(actaBean, acta);
        if (x != null) return x;

        logger.info("*** Aplicando resolución *** ActaID={}, ResolucionID={}, Usuario={}", actaId, resolucionId,
                tokenInfo.getNombreUsuario());
        logger.debug("ActaBean recibido: {}", actaBean);

        // Obtener resolución o lanzar error
        TabResolucion tabResolucion = tabResolucionRepository.findById(resolucionId)
                .orElseThrow(() -> new IllegalArgumentException("La resolución no está registrada en BD."));

        // Validar que el usuario asignado sea el mismo que está aplicando la resolución
        if (tabResolucion.getAudUsuarioAsignado() != null
                && !tabResolucion.getAudUsuarioAsignado().equals(tokenInfo.getNombreUsuario())) {
            logger.warn("Usuario {} intentó aplicar resolución {} que está asignada a {}", tokenInfo.getNombreUsuario(),
                    resolucionId, tabResolucion.getAudUsuarioAsignado());
            return new GenericResponse<>(false,
                    String.format("No puede aplicar esta resolución porque está asignada al usuario %s.",
                            tabResolucion.getAudUsuarioAsignado()));
        }

        if (ConstantesComunes.TEXT_ANULADA.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
                && actaBean.getActasIncompletas().equals(ConstantesComunes.VALUE_SI)) {
            return new GenericResponse<>(false,
                    "Ha seleccionado anular el acta, debe seleccionar la opción de pasar a nulos los electores hábiles.");
        }

        // Registrar estado previo
        this.cabCcResolucionService.spRegistrarCcResolucion(schema, actaId, ConstantesComunes.ESTADO_CAMBIO_RESOL_ANTES,
                resolucionId, tokenInfo.getNombreUsuario());

        AplicarActaBean aplicarActaBean = new AplicarActaBean();
        List<DetActa> detActaListToErrores = new ArrayList<>();
        List<DetActaPreferencial> detActaPreferencialListToErrores = new ArrayList<>();
        List<DetActaOpcion> detActaOpcionListToErrores = new ArrayList<>();

        // Validar relación entre resolución y acta
        List<DetActaResolucion> detActaResolucionList = detActaResolucionRepository
                .findByResolucionAndActa(tabResolucion, acta);

        if (detActaResolucionList.isEmpty()) {
            return new GenericResponse<>(false, "La resolución y el acta no se encuentran asociadas.");
        }

        AuxEstadoActa estadoActa = determinarEstadoActa(acta, actaBean);

        acta.setEstadoActaResolucion(null);
        acta.setEstadoErrorMaterial(null);
        acta.setFechaModificacion(new Date());
        acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());

        procesarCvas(acta, actaBean);

        String codigoEleccion = actaBean.getCodigoEleccion();
        long totalVotosCalculados = calcularTotalVotos(codigoEleccion, actaBean, acta);
        acta.setTotalVotos(totalVotosCalculados);

        RegistroActaErrores errores = new RegistroActaErrores(detActaListToErrores, detActaPreferencialListToErrores,
                detActaOpcionListToErrores);

        DistritoElectoral distritoElectoral = acta.getUbigeoEleccion().getUbigeo().getDistritoElectoral();

        procesarRegistroAgrupacionespoliticas(acta, distritoElectoral, codigoEleccion,
                actaBean.getAgrupacionesPoliticas(), errores, tokenInfo.getNombreUsuario());

        procesarFirmas(acta, actaBean);
        procesarSinDatos(acta, actaBean);
        procesarIncompleta(acta, actaBean);
        procesarSolicitudNulidad(acta, actaBean);

        procesarErroresMateriales(codigoEleccion, acta, detActaListToErrores, detActaPreferencialListToErrores,
                detActaOpcionListToErrores, totalVotosCalculados);

        procesarEstadoFinalActayCompuVersionReprocesamiento(acta, estadoActa.isAnularActaPorSiniestro(),
                estadoActa.isAnularActaPorExtravio(), estadoActa.isActaPorReproceso(), actaBean.getTipoComboNulos(),
                tokenInfo.getNombreUsuario(), ConstantesModulos.MODULO_APLICAR_RESOLUCIONES);

        actaRepository.save(acta);
        tabResolucionRepository.save(tabResolucion);
        actualizarDetActaResolucion(detActaResolucionList, acta, actaBean, tokenInfo.getNombreUsuario());

        this.cabCcResolucionService.spRegistrarCcResolucion(schema, actaBean.getActaId(),
                ConstantesComunes.ESTADO_CAMBIO_RESOL_DESPUES, Long.valueOf(actaBean.getResolucionId()),
                tokenInfo.getNombreUsuario());

        logService.registrarLog(tokenInfo.getNombreUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                "Se aplicó la resolución " + tabResolucion.getNumeroResolucion() + ", al acta "
                        + SceUtils.getNumMesaAndCopia(acta) + " correctamente.",
                tokenInfo.getCodigoCentroComputo(), 0, 1);

        return buscarSiguienteActa(tabResolucion, aplicarActaBean, actaBean);

    }

    private static GenericResponse<AplicarActaBean> validarActaReconteoAnuladaEncontrada(ActaBean actaBean, Acta acta) {
        //VALIDAR ACTA ANULADO
        if (ConstantesComunes.TEXT_RECONTEO.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
            && acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
            return new GenericResponse<>(false,
                "Ha seleccionado ACTA POR RECONTEO pero el acta se encuentra digitalizada. Recargue la página y seleccione ACTA ENCONTRADA.");
        }

        if (ConstantesComunes.TEXT_RECONTEO.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
            && acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
            return new GenericResponse<>(false,
                "Ha seleccionado ACTA POR RECONTEO, pero el acta se encuentra aprobada en digitalización. Recargue la página y seleccione ACTA ENCONTRADA.");
        }


        //VALIDAR RECONTEO
        if (ConstantesComunes.TEXT_ANULADA.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
            && acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA)) {
            return new GenericResponse<>(false,
                "Ha seleccionado ACTA ANULADA, pero el acta se encuentra digitalizada. Recargue la página y seleccione ACTA ENCONTRADA.");
        }

        if (ConstantesComunes.TEXT_ANULADA.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
            && acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
            return new GenericResponse<>(false,
                "Ha seleccionado ACTA ANULADA, pero el acta se encuentra aprobada en digitalización. Recargue la página y seleccione ACTA ENCONTRADA.");
        }
        //VALIDAR ENCONTRADA
        if (ConstantesComunes.TEXT_ENCONTRADA.equals(actaBean.getTipoResolverExtraviadaSiniestrada())
            &&  !acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)) {
            return new GenericResponse<>(false,
                "Ha seleccionado ACTA ENCONTRADA, debe digitalizar el acta y aprobarla en control de digitalización.");
        }


        return null;
    }

    private AuxEstadoActa determinarEstadoActa(Acta acta, ActaBean actaBean) {
        boolean anularExtravio = false;
        boolean anularSiniestro = false;
        boolean reproceso = false;

        String estadoResol = acta.getEstadoActaResolucion();
        String tipo = actaBean.getTipoResolverExtraviadaSiniestrada();

        if (estadoResol != null) {
            anularExtravio = procesarEstado(acta, actaBean, estadoResol, tipo,
                    ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA);
            anularSiniestro = procesarEstado(acta, actaBean, estadoResol, tipo,
                    ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA);
            if (estadoResol.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO)) {
                reproceso = true;
            }
        }

        return new AuxEstadoActa(anularExtravio, anularSiniestro, reproceso);
    }

    private boolean procesarEstado(Acta acta, ActaBean actaBean, String estadoResol, String tipo,
                                   String estadoConstante) {
        if (estadoResol.contains(estadoConstante)) {
            if (ConstantesComunes.TEXT_ANULADA.equals(tipo))
                return true;
            if (ConstantesComunes.TEXT_ENCONTRADA.equals(tipo)) {
                acta.setHoraEscrutinioManual(actaBean.getHoraEscrutinio());
            }
        }
        return false;
    }

    private long calcularTotalVotos(String codigoEleccion, ActaBean actaBean, Acta acta) {
        if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            return ProcessActaUtil.getTotalVotosAgrupacionesPoliticas(actaBean.getAgrupacionesPoliticas());
        }
        return ProcessActaUtil.getTotalVotosCalculadosRevocatoria(actaBean.getAgrupacionesPoliticas(), acta.getCvas(),
                AgrupolBean::getVotosOpciones, VotoOpcionBean::getVotos);
    }

    private void procesarErroresMateriales(String codigoEleccion, Acta acta, List<DetActa> detActaListToErrores,
                                           List<DetActaPreferencial> detActaPreferencialListToErrores, List<DetActaOpcion> detActaOpcionListToErrores,
                                           long totalVotosCalculados) {

        if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            acta.setEstadoErrorMaterial(ConsultaErroresMateriales.getErrMatANivelDeActa(acta, totalVotosCalculados));

            if (acta.getEstadoErrorMaterial() != null) {
                SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
            }

            ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialAgrupol(acta, detActaListToErrores);
            if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion))
                ProcessActaUtil.guardarVeriConvencionalEstadoResolucionErrorMaterialPreferencial(acta,
                        detActaPreferencialListToErrores);
        } else {
            acta.setEstadoErrorMaterial(
                    ConsultaErroresMateriales.getErrMatANivelDeActaRevocatoria(acta, totalVotosCalculados));
            registrarActaPorCorregirEstadoResolucionRevocatoria(acta, detActaListToErrores, detActaOpcionListToErrores);
        }
    }

    private void registrarActaPorCorregirEstadoResolucionRevocatoria(Acta acta, List<DetActa> detActaList,
                                                                     List<DetActaOpcion> detActaOpcions) {

        if (acta.getEstadoErrorMaterial() != null) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT);
        }

        String hashSetErroresMaterialesDetActaTotal = detActaList.stream().map(DetActa::getEstadoErrorMaterial)
                .filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
                .filter(s -> !s.isEmpty()).distinct().collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

        if (!hashSetErroresMaterialesDetActaTotal.isEmpty()) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
        }

        String hashSetErroresMaterialesDetActaOpcionTotal = detActaOpcions.stream()
                .map(DetActaOpcion::getEstadoErrorMaterial).filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES)))
                .filter(s -> !s.isEmpty()).distinct().collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

        if (hashSetErroresMaterialesDetActaOpcionTotal.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_I)) {
            SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
        }

    }

    private void procesarRegistroAgrupacionespoliticas(Acta acta, DistritoElectoral ambitoElectoral,
                                                       String codigoEleccion, List<AgrupolBean> agrupacionesPoliticas, RegistroActaErrores errores,
                                                       String usuario) {

        // Bulk load: precargar datos para evitar queries individuales por agrupación
        Map<Long, DetActa> detActaMap = precargarDetActas(acta);
        Map<String, DetActaPreferencial> prefMap = precargarDetActaPreferenciales(acta);
        Map<Long, AgrupacionPolitica> agrupMap = precargarAgrupacionesPoliticas(agrupacionesPoliticas);

        for (AgrupolBean agrupolBean : agrupacionesPoliticas) {

            Optional<DetActa> optionalDetActa = getDetActaBulk(agrupolBean.getIdDetActa(), acta, agrupolBean, usuario, detActaMap, agrupMap);
            if (optionalDetActa.isEmpty())
                continue;

            DetActa detActa = optionalDetActa.get();

            if (!codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
                procesarVotoseIlegibilidad(agrupolBean, detActa, acta);
                procesarVotosImpugnados(detActa, acta);

                long totalVotosPreferenciales = 0;
                if (!Objects.equals(agrupolBean.getIdAgrupol(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS)
                        && !Objects.equals(agrupolBean.getIdAgrupol(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS)
                        && !Objects.equals(agrupolBean.getIdAgrupol(),
                        ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)) {
                    totalVotosPreferenciales = procesarVotosPreferencialesBulk(acta, ambitoElectoral, codigoEleccion,
                            detActa, agrupolBean, errores.getDetActasPreferenciales(), usuario, prefMap);
                }

                detActa.setEstadoErrorMaterial(
                        ConsultaErroresMateriales.getDetErrorMaterialAgrupol(acta, detActa, totalVotosPreferenciales));

            } else {
                Long[] totalVotos = procesarVotosOpciones(acta, detActa, agrupolBean, errores.getDetActasOpcion(),
                        usuario);
                detActa.setVotos(totalVotos[0]);
                detActa.setEstadoErrorMaterial(
                        ConsultaErroresMateriales.getDetErrorMaterialDetOpcion(acta, totalVotos[0], totalVotos[1]));
            }

            detActa.setFechaModificacion(new Date());
            detActa.setUsuarioModificacion(usuario);
            this.detActaRepository.save(detActa);
            errores.getDetActas().add(detActa);
        }
    }

    private Map<Long, DetActa> precargarDetActas(Acta acta) {
        List<DetActa> detActas = this.detActaRepository.findByActa_IdOrderByPosicion(acta.getId());
        Map<Long, DetActa> map = new HashMap<>();
        for (DetActa d : detActas) {
            map.put(d.getId(), d);
        }
        return map;
    }

    private Map<String, DetActaPreferencial> precargarDetActaPreferenciales(Acta acta) {
        List<DetActaPreferencial> prefs = this.detActaPreferencialRepository.findByDetActa_Acta_Id(acta.getId());
        Map<String, DetActaPreferencial> map = new HashMap<>();
        for (DetActaPreferencial p : prefs) {
            String key = p.getDetActa().getId() + "-" + p.getLista();
            map.put(key, p);
        }
        return map;
    }

    private Map<Long, AgrupacionPolitica> precargarAgrupacionesPoliticas(List<AgrupolBean> agrupacionesPoliticas) {
        List<Long> ids = agrupacionesPoliticas.stream()
                .map(AgrupolBean::getIdAgrupol)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ids.isEmpty()) return new HashMap<>();
        List<AgrupacionPolitica> agrupaciones = this.agrupacionPoliticaService.findAllById(ids);
        Map<Long, AgrupacionPolitica> map = new HashMap<>();
        for (AgrupacionPolitica a : agrupaciones) {
            map.put(a.getId(), a);
        }
        return map;
    }

    private Optional<DetActa> getDetActaBulk(String idDetActa, Acta acta, AgrupolBean agrupolBean, String usuario,
                                              Map<Long, DetActa> detActaMap, Map<Long, AgrupacionPolitica> agrupMap) {
        if (idDetActa == null) {
            DetActa detActa = new DetActa();
            detActa.setEstado(agrupolBean.getEstado());
            AgrupacionPolitica agrup = agrupMap.get(agrupolBean.getIdAgrupol());
            if (agrup != null) detActa.setAgrupacionPolitica(agrup);
            detActa.setActa(acta);
            detActa.setActivo(ConstantesComunes.ACTIVO);
            detActa.setPosicion(agrupolBean.getPosicion());
            detActa.setUsuarioCreacion(usuario);
            detActa.setFechaCreacion(new Date());
            this.detActaRepository.save(detActa);
            return Optional.of(detActa);
        } else {
            return Optional.ofNullable(detActaMap.get(Long.valueOf(idDetActa)));
        }
    }

    private long procesarVotosPreferencialesBulk(Acta acta, DistritoElectoral distritoElectoral, String codigoEleccion,
                                                  DetActa detActa, AgrupolBean agrupolBean, List<DetActaPreferencial> detActaPreferencialListToErrores,
                                                  String usuario, Map<String, DetActaPreferencial> prefMap) {
        long totalVotosPreferencialesPorAgrupacion = 0;
        if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES.contains(codigoEleccion)) {
            for (VotoPreferencialBean votoPreferencial : agrupolBean.getVotosPreferenciales()) {
                procesarVotoPreferencialBulk(votoPreferencial, acta, distritoElectoral, detActa,
                        detActaPreferencialListToErrores, usuario, prefMap);
                totalVotosPreferencialesPorAgrupacion += obtenerVotosPreferenciales(votoPreferencial);
            }
        }
        return totalVotosPreferencialesPorAgrupacion;
    }

    private void procesarVotoPreferencialBulk(VotoPreferencialBean votoPreferencial, Acta acta,
                                               DistritoElectoral distritoElectoral, DetActa detActa,
                                               List<DetActaPreferencial> detActaPreferencialListToErrores, String usuario,
                                               Map<String, DetActaPreferencial> prefMap) {
        Optional<DetActaPreferencial> optionalDetActaPreferencial = getDetActaPreferencialBulk(votoPreferencial,
                distritoElectoral, detActa, usuario, prefMap);
        if (optionalDetActaPreferencial.isEmpty()) {
            return;
        }
        DetActaPreferencial detActaPreferencial = optionalDetActaPreferencial.get();
        procesarVotosPreferencialeseIlegibilidad(votoPreferencial, detActaPreferencial, acta);
        detActaPreferencial.setEstadoErrorMaterial(
                ConsultaErroresMateriales.getDetErrorMaterialPreferencial(acta, detActa, detActaPreferencial));
        this.detActaPreferencialRepository.save(detActaPreferencial);
        detActaPreferencialListToErrores.add(detActaPreferencial);
    }

    private Optional<DetActaPreferencial> getDetActaPreferencialBulk(VotoPreferencialBean votoPreferencialBean,
                                                                      DistritoElectoral distritoElectoral, DetActa detActa, String usuario,
                                                                      Map<String, DetActaPreferencial> prefMap) {
        if (votoPreferencialBean.getIdDetActaPreferencial() == null) {
            DetActaPreferencial detActaPreferencial = new DetActaPreferencial();
            detActaPreferencial.setEstado(detActa.getEstado());
            detActaPreferencial.setDistritoElectoral(distritoElectoral);
            detActaPreferencial.setPosicion(detActa.getPosicion().intValue());
            detActaPreferencial.setLista(votoPreferencialBean.getLista());
            detActaPreferencial.setDetActa(detActa);
            detActaPreferencial.setUsuarioCreacion(usuario);
            this.detActaPreferencialRepository.save(detActaPreferencial);
            return Optional.of(detActaPreferencial);
        } else {
            String key = detActa.getId() + "-" + votoPreferencialBean.getLista();
            return Optional.ofNullable(prefMap.get(key));
        }
    }

    private Long[] procesarVotosOpciones(Acta acta, DetActa detActa, AgrupolBean agrupolBean,
                                         List<DetActaOpcion> detActaOpcionesListToErrores, String usuario) {

        long totalVotosAutoridad = 0L;
        long totalVotosBNI = 0L;
        Long[] resultado = new Long[2];

        for (VotoOpcionBean votoOpcion : agrupolBean.getVotosOpciones()) {
            procesarVotoOpcion(votoOpcion, acta, detActa, detActaOpcionesListToErrores, usuario);
            totalVotosAutoridad += obtenerTotalVotosOpciones(votoOpcion);
            if (Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS)
                    || Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS)
                    || Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)) {
                totalVotosBNI += (votoOpcion.getVotos() == null
                        || votoOpcion.getVotos().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0
                        : Long.parseLong(votoOpcion.getVotos()));
            }

            // Guardando votos impugnados
            if (Objects.equals(votoOpcion.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)
                    && votoOpcion.getVotos() != null
                    && (votoOpcion.getVotos().equals(ConstantesComunes.C_VALUE_ILEGIBLE)
                    || Long.parseLong(votoOpcion.getVotos()) > 0)) {
                SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
            }
        }

        resultado[0] = totalVotosAutoridad;
        resultado[1] = totalVotosBNI;

        return resultado;
    }

    private void procesarVotoOpcion(VotoOpcionBean votoOpcion, Acta acta, DetActa detActa,
                                    List<DetActaOpcion> detActaOpcionlListToErrores, String usuario) {

        votoOpcion.setVotos(SceUtils.removeZerosLeft(votoOpcion.getVotos()));

        if (votoOpcion.getVotos() != null && votoOpcion.getVotos().equals(ConstantesComunes.VACIO))
            votoOpcion.setVotos(ConstantesComunes.CVALUE_ZERO);

        Optional<DetActaOpcion> optionalDetActaOpcion = getDetActaOpcion(votoOpcion, detActa, usuario);

        this.utilSceService.procesarYGuardarDetActaOpcion(acta, votoOpcion, optionalDetActaOpcion,
                detActaOpcionlListToErrores, usuario, SceUtils::manejarIlegibleVotoOpcion);

    }

    private Optional<DetActaOpcion> getDetActaOpcion(VotoOpcionBean votoOpcionBean, DetActa detActa, String usuario) {

        if (votoOpcionBean.getIdDetActaOpcion() == null) {
            DetActaOpcion detActaOpcion = new DetActaOpcion();
            detActaOpcion.setPosicion(votoOpcionBean.getPosicion());
            detActaOpcion.setDetActa(detActa);
            detActaOpcion.setUsuarioCreacion(usuario);
            detActaOpcion.setFechaCreacion(new Date());
            detActaOpcion.setActivo(ConstantesComunes.ACTIVO);
            this.detActaOpcionService.save(detActaOpcion);
            return Optional.of(detActaOpcion);
        } else {
            return this.detActaOpcionService.findById(votoOpcionBean.getIdDetActaOpcion());
        }

    }

    private long obtenerTotalVotosOpciones(VotoOpcionBean votoOpcion) {
        return votoOpcion.getVotos().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0
                : Long.parseLong(votoOpcion.getVotos());
    }



    private void procesarVotoseIlegibilidad(AgrupolBean agrupolBean, DetActa detActa, Acta acta) {
        agrupolBean.setVotos(SceUtils.removeZerosLeft(agrupolBean.getVotos()));

        Integer estado = agrupolBean.getEstado();
        String votos = agrupolBean.getVotos();

        if (ConstantesComunes.N_ACHURADO.equals(estado)) {
            // caso achurado
            detActa.setVotos(null);
            detActa.setIlegible(null);
        } else if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votos)) {
            detActa.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
            detActa.setVotos(ConstantesComunes.NVALUE_NULL);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL);
        } else if (ConstantesComunes.VACIO.equals(votos)) {
            detActa.setVotos(0L);
            detActa.setIlegible(null);
        } else {
            try {
                detActa.setVotos(votos != null ? Long.parseLong(votos) : null);
                detActa.setIlegible(null);
            } catch (NumberFormatException e) {
                detActa.setVotos(null);
                detActa.setIlegible(null);
            }
        }
    }

    private void procesarVotosImpugnados(DetActa detActa, Acta acta) {
        if (Objects.equals(detActa.getPosicion(), ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)
                && detActa.getVotos() != null && detActa.getVotos() > 0) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA);
        }
    }


    private void procesarVotosPreferencialeseIlegibilidad(VotoPreferencialBean votoPreferencialBean,
                                                          DetActaPreferencial detActaPreferencial, Acta acta) {

        String votos = votoPreferencialBean.getVotos();
        votoPreferencialBean.setVotos(SceUtils.removeZerosLeft(votos));

        if (detActaPreferencial.getEstado().equals(ConstantesComunes.N_ACHURADO) || votos == null) {
            detActaPreferencial.setVotos(null);
            detActaPreferencial.setIlegible(null);
        } else if (ConstantesComunes.VACIO.equals(votos)) {
            detActaPreferencial.setVotos(0L);
            detActaPreferencial.setIlegible(null);
        } else if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(votos)) {
            detActaPreferencial.setIlegible(ConstantesComunes.C_VALUE_ILEGIBLE);
            detActaPreferencial.setVotos(ConstantesComunes.NVALUE_NULL);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL);
        } else {
            try {
                detActaPreferencial.setVotos(Long.parseLong(votos));
                detActaPreferencial.setIlegible(null);
            } catch (NumberFormatException e) {
                detActaPreferencial.setVotos(null);
                detActaPreferencial.setIlegible(null);
            }
        }
    }


    private long obtenerVotosPreferenciales(VotoPreferencialBean votoPreferencial) {
        Long total = 0L;
        if (!votoPreferencial.getVotos().equals("null")) {
            total = votoPreferencial.getVotos().equals(ConstantesComunes.C_VALUE_ILEGIBLE) ? 0
                    : Long.parseLong(votoPreferencial.getVotos());
        }
        return total;
    }

    private void procesarCvas(Acta acta, ActaBean actaBean) {
        // LOGICA PARA EL CVAS
        String cvasBean = actaBean.getCvas();
        if (cvasBean.equals(ConstantesComunes.C_VALUE_ILEGIBLE)) {
            acta.setCvas(null);
            acta.setIlegibleCvas(ConstantesComunes.C_VALUE_ILEGIBLE);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS);
        } else if (cvasBean.equals(ConstantesComunes.VALUE_CVAS_INCOMPLETA)
                || cvasBean.equals(ConstantesComunes.VACIO)) {
            acta.setCvas(null);
            acta.setIlegibleCvas(null);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
        } else {
            acta.setCvas(Long.valueOf(cvasBean));
            acta.setIlegibleCvas(null);
        }
    }

    private void procesarFirmas(Acta acta, ActaBean actaBean) {
        // ACTAS SIN FIRMAS
        if (actaBean.getActaSinFirma().equals(ConstantesComunes.VALUE_SI)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
        } else if (actaBean.getActaSinFirma().equals(ConstantesComunes.VALUE_NO)) {
            SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA);
        }
    }

    private void procesarSinDatos(Acta acta, ActaBean actaBean) {
        // ACTAS SIN DATOS
        if (actaBean.getActaSinDatos().equals(ConstantesComunes.VALUE_SI)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
        } else if (actaBean.getActaSinDatos().equals(ConstantesComunes.VALUE_NO)) {
            SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS);
        }
    }

    private void procesarIncompleta(Acta acta, ActaBean actaBean) {
        // ACTA INCOMPLETA
        if (actaBean.getActasIncompletas().equals(ConstantesComunes.VALUE_SI)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
        } else if (actaBean.getActasIncompletas().equals(ConstantesComunes.VALUE_NO)) {
            SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA);
        }

    }

    private void procesarSolicitudNulidad(Acta acta, ActaBean actaBean) {
        // ACTA SOLICITUD DE NULIDAD
        if (actaBean.getSolNulidad().equals(ConstantesComunes.VALUE_SI)) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
        } else if (actaBean.getSolNulidad().equals(ConstantesComunes.VALUE_NO)) {
            SceUtils.removerEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD);
        }

    }

    private void procesarEstadoFinalActayCompuVersionReprocesamiento(Acta acta, boolean anularSiniestro,
                                                                     boolean anularExtravio, boolean esReproceso, String comboNulos, String usuario, String modulo) {
        if (estadoResolucionVacio(acta)) {
            procesarEstadoActaSegunCondiciones(acta, anularSiniestro, anularExtravio, esReproceso, comboNulos, modulo);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
        } else {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
        }

        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());

    }

    private boolean estadoResolucionVacio(Acta acta) {
        return acta.getEstadoActaResolucion() == null || acta.getEstadoActaResolucion().isEmpty();
    }

    private void procesarEstadoActaSegunCondiciones(Acta acta, boolean anularSiniestro, boolean anularExtravio,
                                                    boolean esReproceso, String comboNulos, String modulo) {
        if (anularSiniestro || anularExtravio) {
            procesarAnulacionPorSiniestroOExtravio(acta, anularSiniestro);
        } else if (esReproceso) {
            procesarReproceso(acta, comboNulos);
        } else {
            procesarEstadoActa(acta, comboNulos, modulo);
        }
    }

    private void procesarAnulacionPorSiniestroOExtravio(Acta acta, boolean anularSiniestro) {
        String estadoCc = acta.getEstadoCc();
        if (ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO.equals(estadoCc)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_ANULADA);
        } else if (ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA.equals(estadoCc)) {
            acta.setEstadoActa(anularSiniestro ? ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA
                    : ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA);
        }
    }

    private void procesarReproceso(Acta acta, String comboNulos) {
        acta.setEstadoActa(
                comboNulos != null && !"0".equals(comboNulos) ? ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_ANULADA
                        : ConstantesEstadoActa.ESTADO_ACTA_REPROCESADA_NORMAL);
    }

    private void procesarEstadoActa(Acta acta, String comboNulos, String modulo) {
        if (Objects.equals(acta.getDigitalizacionEscrutinio(),
                ConstantesComunes.PROC_MANUAL_DIGITALIZACION_ESCRUTINIO)) {
            if(modulo.equals(ConstantesModulos.MODULO_DIGITACION_MANUAL_ACTAS)) {
                acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA);
            }else {
                setearEstadoActaEnResoluciones(acta, comboNulos);
            }
        } else {
            setearEstadoActaEnResoluciones(acta, comboNulos);
        }
    }

    private void setearEstadoActaEnResoluciones(Acta acta, String comboNulos){
        if (ConstantesComunes.TIPO_PASAR_NULOS_ELECTORES_HABILES.equals(comboNulos)
                || ConstantesComunes.TIPO_PASAR_NULOS_CVAS.equals(comboNulos)
                || ConstantesComunes.TIPO_PASAR_NULOS_SUMA_VOTOS.equals(comboNulos)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ANULADA);
        } else {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION);
        }
    }

    private void actualizarDetActaResolucion(List<DetActaResolucion> detActaResolucionList, Acta acta,
                                             ActaBean actaBean, String usuario) {
        DetActaResolucion detActaResolucion = detActaResolucionList.getFirst();
        detActaResolucion.setEstadoActa(acta.getEstadoActa());
        detActaResolucion.setFechaModificacion(new Date());
        detActaResolucion.setObservacionJne(actaBean.getObservacionesJNE());
        detActaResolucion.setUsuarioModificacion(usuario);
        this.detActaResolucionRepository.save(detActaResolucion);
    }

    private GenericResponse<AplicarActaBean> buscarSiguienteActa(TabResolucion tabResolucion,
                                                                 AplicarActaBean aplicarActaBean, ActaBean actaBean) {
        List<DetActaResolucion> detActaResolucionListTodas = this.detActaResolucionRepository
                .findByResolucion(tabResolucion);
        List<DetActaResolucion> detActaResolucionListPendiente = detActaResolucionListTodas.stream()
                .filter(e -> Objects.equals(e.getEstadoActa(), ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION))
                .toList();

        if (detActaResolucionListPendiente.isEmpty()) {
            aplicarActaBean.setIdResolucion(actaBean.getResolucionId());
            aplicarActaBean.setMesa("");
            aplicarActaBean.setCopia("");
            aplicarActaBean.setCodigoEleccion("");
            aplicarActaBean.setCodigoProceso("");
            aplicarActaBean.setSiguiente(false);
            tabResolucion.setEstadoResolucion(ConstantesEstadoResolucion.PROCESADO);
            this.tabResolucionRepository.save(tabResolucion);
            return new GenericResponse<>(true,
                    "Se completó la aplicación de la resolución para todas las actas asociadas.", aplicarActaBean);
        } else {
            DetActaResolucion detActaResolucionSiguiente = detActaResolucionListPendiente.getFirst();
            aplicarActaBean.setIdResolucion(actaBean.getResolucionId());
            Acta acta = detActaResolucionSiguiente.getActa();
            aplicarActaBean.setActaId(acta.getId());
            aplicarActaBean.setMesa(acta.getMesa().getCodigo());
            aplicarActaBean.setCopia(acta.getNumeroCopia() + acta.getDigitoChequeoEscrutinio());
            aplicarActaBean.setCodigoEleccion(acta.getUbigeoEleccion().getEleccion().getCodigo());
            aplicarActaBean.setCodigoProceso(acta.getUbigeoEleccion().getEleccion().getProcesoElectoral().getId() + "");
            aplicarActaBean.setSiguiente(true);
            return new GenericResponse<>(true,
                    "Se aplicó la resolución correctamente. Se mostrará los datos de la siguiente acta.",
                    aplicarActaBean);
        }
    }

    @Override
    public GenericResponse<TabResolucionDTO> actualizarEstadoDigitalizacion(TokenInfo tokenInfo, Long idResolucion,
                                                                            String estadoDigitalizacion) {

        String mensaje = "";

        TabResolucionDTO tabResolucionDTO = new TabResolucionDTO();
        Optional<TabResolucion> optTabResolucion = this.tabResolucionRepository.findById(idResolucion);

        if (optTabResolucion.isPresent()) {
            TabResolucion tabResol = optTabResolucion.get();
            tabResol.setEstadoDigitalizacion(estadoDigitalizacion);
            tabResol.setAudFechaModificacion(new Date());
            tabResol.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
            if (estadoDigitalizacion.equals(ConstantesEstadoResolucion.DIGTAL_RECHAZADO)) {
                mensaje = String.format("La resolución %s, fue rechazada correctamente.",
                        tabResol.getNumeroResolucion());
                tabResol.setNumeroPaginas(0);

                Archivo archivoResolucion = tabResol.getArchivoResolucion();
                archivoResolucion.setActivo(ConstantesComunes.INACTIVO);
                this.archivoRepository.save(archivoResolucion);
                tabResol.setArchivoResolucion(null);
                this.tabResolucionRepository.save(tabResol);
                tabResolucionDTO = UtilMapper.tabResolucionToDto(tabResol);

                this.logService.registrarLog(tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(), mensaje,
                        tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

                try {
                    this.storageService.deleteFile(archivoResolucion.getGuid());
                } catch (IOException e) {
                    logger.error("Error: ", e);
                }

            } else if (estadoDigitalizacion.equals(ConstantesEstadoResolucion.DIGTAL_APROBADO)) {
                mensaje = String.format("La resolución %s, fue aprobada correctamente.",
                        tabResol.getNumeroResolucion());
                this.tabResolucionRepository.save(tabResol);
                tabResolucionDTO = this.resolucionMapper.entityToDTO(tabResol);
                this.logService.registrarLog(tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(), mensaje,
                        tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            } else if (estadoDigitalizacion.equals(ConstantesEstadoResolucion.RECHAZADA_2DO_CC)) {
                mensaje = String.format("La resolución %s, fue rechazada del control de calidad correctamente.",
                        tabResol.getNumeroResolucion());
                this.tabResolucionRepository.save(tabResol);
                tabResolucionDTO = this.resolucionMapper.entityToDTO(tabResol);
                this.logService.registrarLog(tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(), mensaje,
                        tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            }
            return new GenericResponse<>(true, mensaje, tabResolucionDTO);

        } else {
            return new GenericResponse<>(false, "La resolución no se encuentra registrada.");
        }
    }

    @Override
    @Transactional
    public void anularResolucion(TokenInfo tokenInfo, Long idResolucion) {

        TabResolucion tabResol = this.tabResolucionRepository.findById(idResolucion)
                .orElseThrow(() -> new BadRequestException("La resolución no se encuentra registrada."));

        if (!ConstantesEstadoResolucion.SIN_PROCESAR.equals(tabResol.getEstadoResolucion())) {
            throw new BadRequestException(String.format("La resolución %s debe estar en un estado sin procesar.",
                    tabResol.getNumeroResolucion()));
        }

        tabResol.setEstadoResolucion(ConstantesEstadoResolucion.ANULADO);
        tabResol.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
        tabResol.setAudFechaModificacion(new Date());
        this.tabResolucionRepository.save(tabResol);

        Archivo archivoAnterior = tabResol.getArchivoResolucion();
        this.utilSceService.inactivarArchivo(archivoAnterior, tokenInfo);

        this.logService.registrarLog(tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                String.format("Se anuló la resolución %s correctamente.", tabResol.getNumeroResolucion()),
                tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
    }

    @Override
    public GenericResponse<List<ActaBean>> getInfoActaParaAsociacionResoluciones(Integer codTipoResolucion,
                                                                                 String nroActaCopiaDig, Long idUbigeo, Long idLocalVotacion, Long idEleccion, Long idProceso) {
        if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)) {
            return validarMesaNoInstalada(nroActaCopiaDig);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                || Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)) {
            return validarActasExtraviadasOSiniestradas(nroActaCopiaDig);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_JNE)
                || Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_REPRO_ONPE)) {
            return validarActasReproceso(nroActaCopiaDig);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS)) {
            return validarActasInfundadas(nroActaCopiaDig);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO)) {
            if (nroActaCopiaDig == null || nroActaCopiaDig.isEmpty())
                return validarActasInfundadasPorUbigeo(idUbigeo, idLocalVotacion, idEleccion);
            return validarActasInfundadas(nroActaCopiaDig);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ANULACION_ACTAS_X_UBIGEO)) {
            return validarActasInfundadasPorUbigeo(idUbigeo, idLocalVotacion, idEleccion);
        } else if (Objects.equals(codTipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE)) {
            return validarActasEnviadasJee(nroActaCopiaDig);
        } else {
            return new GenericResponse<>(false, "Tipo de resolución seleccionada no tiene implementación.");
        }
    }

    private GenericResponse<List<ActaBean>> validarMesaNoInstalada(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();

        this.utilSceService.validarMesaNoInstalada(mesa);

        if (ConstantesEstadoMesa.INSTALADA.equals(mesa.getEstadoMesa())) {
            return new GenericResponse<>(false, String.format("La mesa %s está declarada como instalada.", nroMesa));
        }

        List<Acta> actaList = this.actaRepository.findByMesa(mesa);
        List<Acta> actasPendientes = actaList.stream()
                .filter(acta -> acta.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE)
                        && acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PENDIENTE)
                        && acta.getEstadoDigitalizacion()
                        .equals(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION))
                .toList();

        if (actaList.size() != actasPendientes.size()) {
            return new GenericResponse<>(false, String.format(
                    "La mesa %s no puede declararse como no instalada, todas sus actas asociadas deben estar pendientes de procesar.",
                    nroMesa));
        }

        List<Acta> listaFinal = validarActaStaeHabilitadas(actaList);

        if (listaFinal.isEmpty()) {
            return new GenericResponse<>(false, "No existen mesas disponibles para asociar.");
        }

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(null);
        actaBean.setMesaId(mesa.getId());
        actaBean.setMesa(mesa.getCodigo());
        actaBean.setEleccion(null);
        actaBean.setCopia(null);
        actaBean.setEstadoMesa(mesa.getEstadoMesa());
        actaBean.setDescripcionEstadoMesa(ConstantesEstadoMesa.getMapEstadoMesa().get(actaBean.getEstadoMesa()));
        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroMesa), List.of(actaBean));
    }

    private GenericResponse<List<ActaBean>> validarActasExtraviadasOSiniestradas(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();

        this.utilSceService.validarMesaNoInstalada(mesa);

        List<Acta> cabActaListPendientes = mesaInfo.getActaList().stream()
                .filter(e -> e.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_PENDIENTE) && e
                        .getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION))
                .toList();

        if (cabActaListPendientes.isEmpty()) {
            return new GenericResponse<>(false, "No existen actas en estado PENDIENTE para la mesa " + nroMesa + ".");
        }

        List<Acta> listaFinal = validarActaStaeHabilitadas(cabActaListPendientes);

        if (listaFinal.isEmpty()) {
            return new GenericResponse<>(false, "No existen actas disponibles para asociar.");
        }

        List<ActaBean> actaBeanList = new ArrayList<>();
        for (Acta acta : listaFinal) {
            boolean isAsociada = this.detActaResolucionRepository.findByActaOrderByFechaCreacionDesc(acta).stream()
                    .anyMatch(r -> ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(r.getEstadoActa()));

            if (!isAsociada) {
                ActaBean actaBean = construirActaBean(acta, mesa);
                actaBeanList.add(actaBean);
            }
        }

        if (actaBeanList.isEmpty()) {
            return new GenericResponse<>(false, "Las actas de la mesa " + mesa.getCodigo()
                    + ", se encuentran asociadas a otra resolución de tipo extraviada/siniestrada.");
        }

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroMesa), actaBeanList);
    }

    private List<Acta> validarActaStaeHabilitadas(List<Acta> cabActaListPendientes) {
        return cabActaListPendientes.stream().filter(
                        acta -> !Objects.equals(acta.getSolucionTecnologica(), ConstantesComunes.SOLUCION_TECNOLOGICA_STAE)
                                || Objects.equals(acta.getTipoTransmision(), ConstantesComunes.TIPO_HOJA_STAE_CONTINGENCIA))
                .toList();
    }

    private GenericResponse<List<ActaBean>> validarActasInfundadasPorUbigeo(Long idUbigeo, Long idLocalVotacion,
                                                                            Long idEleccion) {

        if (idUbigeo == null) {
            throw new BadRequestException("El ubigeo no puede ser nulo.");
        }

        if (idLocalVotacion == null) {
            throw new BadRequestException("El local de votación no puede ser nulo.");
        }

        List<String> estadosActaContabilizado = List.of(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA,
                ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION, ConstantesEstadoActa.ESTADO_ACTA_ANULADA,
                ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA);

        List<Object[]> resultados = this.actaRepository.contarCoincidenciasYTotal(idUbigeo, idLocalVotacion, idEleccion,
                estadosActaContabilizado);

        if (!resultados.isEmpty()) {
            Object[] fila = resultados.getFirst(); // Obtener la primera fila

            Long cantidadContabilizadasPorUbigeo = (fila[0] != null) ? ((Number) fila[0]).longValue() : 0L;
            Long totalActas = (fila[1] != null) ? ((Number) fila[1]).longValue() : 0L;
            boolean todosCoinciden = cantidadContabilizadasPorUbigeo.equals(totalActas);
            if (!todosCoinciden) {
                throw new IllegalStateException(
                        "Para proceder todas las actas de los parámetros seleccionados deben estar contabilizadas.");
            }
        }

        List<ActaResumenDto> actaResumenDtos = this.actaRepository.listarActasPorEstadoUbigeoLocalVotacion(
                estadosActaContabilizado, idUbigeo, idLocalVotacion, idEleccion);
        if (actaResumenDtos.isEmpty()) {
            throw new IllegalStateException("No existen actas contabilizadas para los parámetros seleccionados.");
        }

        List<ActaBean> actaBeanList = actaResumenDtos.stream().map(acta -> {
            ActaBean actaBean = new ActaBean();
            actaBean.setActaId(acta.getIdActa());
            actaBean.setCopia((acta.getNumeroCopia() != null ? acta.getNumeroCopia() : "")
                    + (acta.getDigitoChequeoEscrutinio() != null ? acta.getDigitoChequeoEscrutinio() : ""));
            actaBean.setMesa(acta.getNumeroMesa());
            actaBean.setEstadoActa(acta.getEstadoActa());
            actaBean.setEleccion(acta.getNombreEleccion());
            return actaBean;
        }).toList();

        return new GenericResponse<>(Boolean.TRUE, "Lista de actas contabilizadas obtenidas correctamente.",
                actaBeanList);
    }

    private GenericResponse<List<ActaBean>> validarActasEnviadasJee(String nroActaCopiaDig) {

        if (nroActaCopiaDig.isEmpty()) {
            return new GenericResponse<>(Boolean.FALSE, ConstantesMensajes.MSJ_FORMAT_COPIA_Y_MESA_VACIAS);
        }

        if (nroActaCopiaDig.length() == ConstantesComunes.LONGITUD_MESA) {
            return validarActasEnviadasJePorMesa(nroActaCopiaDig);
        } else {
            return validarActasEnviadasJePorMesaYCopia(nroActaCopiaDig);
        }
    }

    private GenericResponse<List<ActaBean>> validarActasEnviadasJePorMesa(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();
        List<Acta> actaList = mesaInfo.getActaList();

        this.utilSceService.validarMesaNoInstalada(mesa);

        List<Acta> actasConEstadoValido = actaList.stream().filter(e -> e.getEstadoActaResolucion() != null && (e
                        .getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)
                        || e.getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA)))
                .toList();

        if (actasConEstadoValido.isEmpty()) {
            return new GenericResponse<>(Boolean.FALSE,
                    String.format("La mesa %s no cuenta con actas declaradas extraviadas o siniestradas...", nroMesa));
        }

        List<ActaBean> actaBeanList = actasConEstadoValido.stream().filter(acta -> !estaAsociadaResolucion(acta))
                .map(acta -> {
                    ActaBean actaBean = new ActaBean();
                    actaBean.setActaId(acta.getId());
                    actaBean.setCopia((acta.getNumeroCopia() != null ? acta.getNumeroCopia() : "")
                            + (acta.getDigitoChequeoEscrutinio() != null ? acta.getDigitoChequeoEscrutinio() : ""));
                    actaBean.setMesaId(mesa.getId());
                    actaBean.setMesa(mesa.getCodigo());
                    actaBean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());
                    actaBean.setEstadoActa(acta.getEstadoActa());
                    actaBean.setDescripcionEstadoActa(
                            ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
                    actaBean.setEstadoMesa(mesa.getEstadoMesa());
                    actaBean.setDescripcionEstadoMesa(
                            ConstantesEstadoMesa.getMapEstadoMesa().get(mesa.getEstadoMesa()));
                    return actaBean;
                }).toList();

        if (actaBeanList.isEmpty()) {
            return new GenericResponse<>(false, String.format(
                    "Las actas de la mesa %s se encuentran asociadas a otra resolución de tipo actas enviadas al JEE.",
                    mesa.getCodigo()));
        }

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroMesa), actaBeanList);
    }

    private GenericResponse<List<ActaBean>> validarActasEnviadasJePorMesaYCopia(String nroActaCopiaDig) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActaCopiaDig, ConstantesComunes.VACIO, Boolean.FALSE);

        Acta acta = actaInfo.getActa();
        BarCodeInfo barCodeInfo = actaInfo.getBarCodeInfo();

        if (ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(acta.getEstadoActa())) {
            return new GenericResponse<>(false,
                    String.format(ConstantesComunes.MENSAJE_FORMATO_ACTA_ESTADO_PARA_ENVIO_JURADO,
                            barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
        }

        if (!ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA.equals(acta.getEstadoActa())) {
            return new GenericResponse<>(false, String.format(ConstantesComunes.MENSAJE_FORMATO_ACTA_ESTADO_NO_DEVUELTA,
                    barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
        }

        if (estaAsociadaResolucion(acta)) {
            return new GenericResponse<>(false,
                    String.format("No se puede agregar el acta %s-%s, se encuentra asociada a la resolución.",
                            barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
        }

        ActaBean actaBean = new ActaBean();
        actaBean.setActaId(acta.getId());
        actaBean.setMesa(acta.getMesa().getCodigo());
        actaBean.setIdArchivoEscrutinio(
                acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId().toString() : "");
        actaBean.setIdArchivoInstalacionSufragio(
                acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId().toString()
                        : "");
        actaBean.setEleccion(actaInfo.getNombreEleccion());
        actaBean.setCopia(barCodeInfo.getNroCopiaAndDigito());
        actaBean.setEstadoActa(acta.getEstadoActa());
        actaBean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));

        return new GenericResponse<>(true,
                String.format(ConstantesComunes.MENSAJE_FORMAT_VALIDADA_CORRECTAMENTE, nroActaCopiaDig),
                List.of(actaBean));
    }

    private boolean estaAsociadaResolucion(Acta acta) {
        List<DetActaResolucion> resoluciones = this.detActaResolucionRepository
                .findByActaOrderByFechaCreacionDesc(acta);
        if (resoluciones.isEmpty())
            return false;

        DetActaResolucion ultima = resoluciones.getLast();
        return ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(ultima.getEstadoActa());
    }

    private GenericResponse<List<ActaBean>> validarActasReproceso(String nroActaCopiaDig) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActaCopiaDig, ConstantesComunes.VACIO, Boolean.FALSE);
        Acta acta = actaInfo.getActa();
        BarCodeInfo barCodeInfo = actaInfo.getBarCodeInfo();

        boolean validacionEstado = acta.getEstadoActaResolucion() != null
                && acta.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_EN_PROCESO)
                && (acta.getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)
                || acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA)
                || acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_REPROCESO));

        if (!validacionEstado)
            throw new IllegalStateException(
                    String.format("El acta %s no se encuentra en un estado para ser reprocesada por resolución.",
                            barCodeInfo.getNroMesa() + barCodeInfo.getNroCopiaAndDigito()));

        if (!ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA.equals(acta.getEstadoActa())) {
            throw new IllegalStateException(String.format(ConstantesComunes.MENSAJE_FORMATO_ACTA_ESTADO_NO_DEVUELTA,
                    barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito()));
        }

        validacionComunAsociadaOtraResolucion(acta, barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito());

        ActaBean actaBean = construirActaBeanParaReprocesoOrInfundada(acta, barCodeInfo.getNroCopiaAndDigito(),
                actaInfo.getNombreEleccion());
        return new GenericResponse<>(true,
                String.format("El acta %s ha sido validada correctamente.", barCodeInfo.getNroCopiaAndDigito()),
                List.of(actaBean));
    }

    private GenericResponse<List<ActaBean>> validarActasInfundadas(String nroActaCopiaDig) {

        ActaInfo actaInfo = this.utilSceService.validarActa(nroActaCopiaDig, ConstantesComunes.VACIO, Boolean.FALSE);
        Acta acta = actaInfo.getActa();
        BarCodeInfo barCodeInfo = actaInfo.getBarCodeInfo();

        if (!ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA.equals(acta.getEstadoCc())) {
            throw new InternalServerErrorException("El acta " + nroActaCopiaDig + " debe estar Contabilizada.");
        }
        validacionComunAsociadaOtraResolucion(acta, barCodeInfo.getNroMesa(), barCodeInfo.getNroCopiaAndDigito());

        ActaBean actaBean = construirActaBeanParaReprocesoOrInfundada(acta, barCodeInfo.getNroCopiaAndDigito(),
                actaInfo.getNombreEleccion());
        return new GenericResponse<>(true, String.format("El acta %s ha sido validada correctamente.", nroActaCopiaDig),
                List.of(actaBean));

    }

    private void validacionComunAsociadaOtraResolucion(Acta acta, String nroActa, String nroCopiaDig) {

        List<DetActaResolucion> resoluciones = this.detActaResolucionRepository
                .findByActaOrderByFechaCreacionDesc(acta);
        if (!resoluciones.isEmpty()) {
            DetActaResolucion ultima = resoluciones.get(resoluciones.size() - 1);
            if (ultima.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION)) {
                throw new IllegalStateException(
                        String.format("No se puede agregar el acta %s-%s, se encuentra asociada a la resolución: %S",
                                nroActa, nroCopiaDig, ultima.getResolucion().getNumeroResolucion()));
            }
        }
    }

    private ActaBean construirActaBeanParaReprocesoOrInfundada(Acta acta, String nroCopiaDig,
                                                               String eleccionSeleccionada) {
        ActaBean bean = new ActaBean();
        bean.setActaId(acta.getId());
        bean.setMesa(acta.getMesa().getCodigo());
        bean.setIdArchivoEscrutinio(
                acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId().toString() : "");
        bean.setIdArchivoInstalacionSufragio(
                acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId().toString()
                        : "");
        bean.setEleccion(eleccionSeleccionada);
        bean.setCopia(nroCopiaDig);
        bean.setEstadoActa(acta.getEstadoActa());
        bean.setDescripcionEstadoActa(ConstantesEstadoActa.getMapEstadoActas().get(acta.getEstadoActa()));
        return bean;
    }

    @Override
    public GenericResponse<Object> generarCargoEntrega(TokenInfo tokenInfo, List<ActaBean> actaBeanLis) {
        ResolucionAsociadosRequest resolucionAsociadosRequest = new ResolucionAsociadosRequest();
        resolucionAsociadosRequest.setTipoResolucion(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE);
        resolucionAsociadosRequest.setActasAsociadas(actaBeanLis);
        return generarCargoEntregaComunes(tokenInfo, resolucionAsociadosRequest);
    }

    @Override
    public GenericResponse<Object> generarCargoEntregaOficio(TokenInfo tokenInfo, ActaBean actaBean) {
        try {
            ResolucionAsociadosRequest resolucionAsociadosRequest = new ResolucionAsociadosRequest();
            resolucionAsociadosRequest.setTipoResolucion(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE);
            resolucionAsociadosRequest.setActasAsociadas(Collections.singletonList(actaBean));
            return generarCargoEntregaOficio(tokenInfo, resolucionAsociadosRequest);
        } catch (IOException e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return new GenericResponse<>(false, e.getMessage());
        }
    }

    @Override
    public GenericResponse<Object> generarOficioActaObservada(TokenInfo tokenInfo, List<ActaBean> actaBeanLis) {
        ResolucionAsociadosRequest resolucionAsociadosRequest = new ResolucionAsociadosRequest();
        resolucionAsociadosRequest.setTipoResolucion(ConstantesCatalogo.CATALOGO_TIPO_RESOL_OFICIO_ACTA_OBSERVADA);
        resolucionAsociadosRequest.setActasAsociadas(actaBeanLis);
        return generarOficioActasObservadas(tokenInfo, resolucionAsociadosRequest);
    }

    private void generarListaDetalleParaReporteComun(List<ActaBean> actaBeanListSoloObservadas) {
        int index = 0;
        for (ActaBean actaBean : actaBeanListSoloObservadas) {
            actaBean.setIndex(++index);
            Optional<Acta> optionalActa = actaRepository.findById(actaBean.getActaId());
            if (optionalActa.isEmpty())
                continue;

            Acta acta = optionalActa.get();
            List<DetActa> detActaList = detActaRepository.findByActa_Id(acta.getId());
            String errorMaterialGeneral = construirErrorMaterial(acta);
            actaBean.setErrorMaterial(
                    errorMaterialGeneral != null ? ConstantesComunes.VALUE_SI : ConstantesComunes.VALUE_NO);
            actaBean.setTipoErrorM(
                    ConstantesComunes.VALUE_NO.equals(actaBean.getErrorMaterial()) ? ConstantesComunes.VACIO
                            : errorMaterialGeneral);
            asignarEstadosResolucionGenerales(actaBean, acta);
            actaBean.setTipoIlegible(getTipoIlegible(acta));

            StringBuilder detalleIlegible = construirDetalleIlegibleGenerales(acta, detActaList);
            actaBean.setDetalleIlegible(construirObservacionDetalleGenerales(acta, actaBean, detalleIlegible));
        }
    }

    private void asignarEstadosResolucionGenerales(ActaBean actaBean, Acta acta) {
        String estado = acta.getEstadoActaResolucion();
        if (estado == null)
            return;
        actaBean.setVotosImpugnados(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);
        actaBean.setSolNulidad(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        actaBean.setIlegibilidad(Stream.of(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS,
                ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL,
                ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL).anyMatch(estado::contains)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);

        actaBean.setActaSinDatos(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);
        actaBean.setActasIncompletas(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        actaBean.setExtraviada(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        actaBean.setSiniestrada(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        actaBean.setActaSinFirma(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        actaBean.setObsMesa(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_OBS_MESA) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);

        List<DetActaResolucion> resoluciones = detActaResolucionRepository.findByActaOrderByFechaCreacionDesc(acta);
        String observacion = resoluciones.isEmpty() ? ""
                : Optional.ofNullable(resoluciones.getLast().getObservacionJne()).orElse("");
        actaBean.setObservacion(observacion.isEmpty() ? ConstantesComunes.VALUE_NO : ConstantesComunes.VALUE_SI);
    }

    private String construirErrorMaterial(Acta acta) {
        List<String> totalErrorMateriales = new ArrayList<>();

        if (acta.getEstadoErrorMaterial() != null && !acta.getEstadoErrorMaterial().isEmpty()) {
            totalErrorMateriales.add(acta.getEstadoErrorMaterial());
        }

        List<String> errorMaterialAgrupol = detActaRepository.findAllEstadoErrorMaterialRaw(acta.getId());
        if (errorMaterialAgrupol != null && !errorMaterialAgrupol.isEmpty()) {
            totalErrorMateriales.addAll(errorMaterialAgrupol);
        }

        List<String> errorMaterialPreferencial = detActaPreferencialRepository
                .findErroresMaterialesByActaId(acta.getId());
        if (errorMaterialPreferencial != null && !errorMaterialPreferencial.isEmpty()) {
            totalErrorMateriales.addAll(errorMaterialPreferencial);
        }

        String errorMaterialesNoRepetidos = String.join(ConstantesComunes.SEPARADOR_ERRORES, totalErrorMateriales
                .stream().filter(estado -> estado != null && !estado.isEmpty())
                .flatMap(estado -> Arrays.stream(estado.split(ConstantesComunes.SEPARADOR_ERRORES))).map(String::trim)
                .filter(s -> !s.isEmpty() && !s.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_O_ILEGIBLE_CVAS)
                        && !s.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL)
                        && !s.equals(ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_P_ILEGIBLE_AGRUPOL))
                .collect(Collectors.toCollection(LinkedHashSet::new)));

        return errorMaterialesNoRepetidos.isEmpty() ? null : errorMaterialesNoRepetidos;
    }

    private StringBuilder construirDetalleIlegibleGenerales(Acta acta, List<DetActa> detActaList) {
        StringBuilder detalle = new StringBuilder();

        for (DetActa detActa : detActaList) {
            AgrupacionPolitica agrupacionPolitica = detActa.getAgrupacionPolitica();
            if (agrupacionPolitica == null)
                break;

            boolean tieneDetalle = false;

            String erroresFiltrados = filtrarErrores(detActa.getEstadoErrorMaterial(),
                    ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_P_ILEGIBLE_AGRUPOL);

            if (!erroresFiltrados.isEmpty()) {
                iniciarDetalle(detalle, detActa, agrupacionPolitica, tieneDetalle);
                detalle.append(ConstantesComunes.ESPACIO_DOBLE).append("* Error Arit. ").append(erroresFiltrados)
                        .append(ConstantesComunes.SALTO_LINEA);
                tieneDetalle = true;
            }

            if (ConstantesComunes.C_VALUE_ILEGIBLE.equals(detActa.getIlegible())) {
                iniciarDetalle(detalle, detActa, agrupacionPolitica, tieneDetalle);
                detalle.append(ConstantesComunes.ESPACIO_DOBLE).append("* Ileg en Total de Votos")
                        .append(ConstantesComunes.SALTO_LINEA);
                tieneDetalle = true;
            }

            if (ConstantesComunes.CODIGOS_ELECCIONES_PREFERENCIALES
                    .contains(acta.getUbigeoEleccion().getEleccion().getCodigo())) {

                List<DetActaPreferencial> preferencias = detActaPreferencialRepository.findByDetActa(detActa);

                String posicionesIlegibles = preferencias.stream()
                        .filter(p -> ConstantesComunes.C_VALUE_ILEGIBLE.equals(p.getIlegible()))
                        .sorted(Comparator.comparing(DetActaPreferencial::getLista))
                        .map(p -> String.valueOf(p.getLista()))
                        .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));

                if (!posicionesIlegibles.isEmpty()) {
                    iniciarDetalle(detalle, detActa, agrupacionPolitica, tieneDetalle);
                    detalle.append(ConstantesComunes.ESPACIO_DOBLE).append("* Ileg VP en candidatos ")
                            .append(posicionesIlegibles).append(ConstantesComunes.SALTO_LINEA);
                    tieneDetalle = true;
                }

                String resultado = construirErroresPreferenciales(preferencias);
                if (!resultado.isEmpty()) {
                    iniciarDetalle(detalle, detActa, agrupacionPolitica, tieneDetalle);
                    detalle.append(ConstantesComunes.ESPACIO_DOBLE).append("* Error Arit. VP candidatos ")
                            .append(resultado).append(ConstantesComunes.SALTO_LINEA);
                }
            }
        }
        return detalle;
    }

    private void iniciarDetalle(StringBuilder sb, DetActa detActa, AgrupacionPolitica agrupacion, boolean yaIniciado) {
        if (!yaIniciado) {
            sb.append("Pos. ").append(detActa.getPosicion()).append(": ").append(agrupacion.getDescripcion())
                    .append(ConstantesComunes.SALTO_LINEA);
        }
    }

    private String filtrarErrores(String errores, String excluir) {
        if (errores == null || errores.isBlank())
            return "";
        return Arrays.stream(errores.split(ConstantesComunes.SEPARADOR_ERRORES)).map(String::trim)
                .filter(err -> !err.equalsIgnoreCase(excluir))
                .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));
    }

    private String construirErroresPreferenciales(List<DetActaPreferencial> preferencias) {
        return preferencias.stream()
                .filter(p -> p.getEstadoErrorMaterial() != null && !p.getEstadoErrorMaterial().isBlank())
                .collect(Collectors.groupingBy(DetActaPreferencial::getLista,
                        Collectors.mapping(
                                p -> Arrays
                                        .stream(p.getEstadoErrorMaterial().split(ConstantesComunes.SEPARADOR_ERRORES))
                                        .map(String::trim)
                                        .filter(err -> !err.equalsIgnoreCase(
                                                ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL))
                                        .collect(Collectors.toSet()),
                                Collectors.reducing(new HashSet<String>(), (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                }))))
                .entrySet().stream().filter(e -> !e.getValue().isEmpty())
                .map(e -> e.getKey() + " (" + String.join(ConstantesComunes.SEPARADOR_ERRORES, e.getValue()) + ")")
                .collect(Collectors.joining(ConstantesComunes.SEPARADOR_ERRORES));
    }

    private String construirObservacionDetalleGenerales(Acta acta, ActaBean actaBean, StringBuilder detalleIlegible) {
        List<DetActaResolucion> resoluciones = detActaResolucionRepository.findByActaOrderByFechaCreacionDesc(acta);
        String observacion = resoluciones.isEmpty() ? ""
                : Optional.ofNullable(resoluciones.getLast().getObservacionJne()).orElse("");
        if (detalleIlegible.isEmpty())
            return observacion;
        if (ConstantesComunes.VALUE_NO.equals(actaBean.getObservacion()))
            return detalleIlegible.toString();
        return detalleIlegible + " / " + observacion;
    }

    private void generarListaDetalleParaReporteRevocatoria(String usr, List<ActaBean> actaBeanListSoloObservadas,
                                                           List<ActaBeanRevocatoria> actaBeanRevocatorias, String correlativo) {

        int index = 0;
        for (ActaBean actaBean : actaBeanListSoloObservadas) {
            index++;
            Optional<Acta> optionalCabActa = this.actaRepository.findById(actaBean.getActaId());
            if (optionalCabActa.isEmpty())
                continue;

            Acta acta = actualizarActaComoEnviada(optionalCabActa.get(), usr);

            List<DetActa> detActaList = this.detActaRepository.findByActa_IdOrderByPosicion(acta.getId());
            ActaBeanRevocatoria cabecera = construirCabeceraRevocatoria(actaBean, acta, correlativo, ++index);
            actaBeanRevocatorias.add(cabecera);

            for (DetActa detActa : detActaList) {
                ActaBeanRevocatoria detalle = construirDetalleRevocatoria(actaBean, correlativo, acta, detActa);
                if (autoridadConErrores(detalle)) {
                    detalle.setIndex(++index);
                    actaBeanRevocatorias.add(detalle);
                }
            }
        }
    }

    private Acta actualizarActaComoEnviada(Acta acta, String usr) {
        acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
        acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
        acta.setActivo(ConstantesComunes.ACTIVO);
        acta.setUsuarioModificacion(usr);
        acta.setFechaModificacion(new Date());
        return this.actaRepository.save(acta);
    }

    private ActaBeanRevocatoria construirCabeceraRevocatoria(ActaBean actaBean, Acta acta, String correlativo,
                                                             int index) {
        String estado = acta.getEstadoActaResolucion();
        ActaBeanRevocatoria bean = new ActaBeanRevocatoria();
        bean.setIndex(index);
        bean.setMesa(actaBean.getMesa());
        bean.setCopia(actaBean.getCopia());
        bean.setLote(correlativo);
        bean.setAutoridad(ConstantesComunes.VACIO);
        bean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());
        bean.setErrorMaterial(
                acta.getEstadoErrorMaterial() != null ? ConstantesComunes.VALUE_SI : ConstantesComunes.VALUE_NO);
        bean.setTipoErrorM(
                bean.getErrorMaterial().equals(ConstantesComunes.VALUE_NO) ? "" : acta.getEstadoErrorMaterial());
        bean.setVotosImpugnados(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_IMPUGNADA) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);
        bean.setIlegibilidad(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);
        bean.setActasIncompletas(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_INCOMPLETA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        bean.setSolNulidad(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SOLICITUD_NULIDAD)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        bean.setActaSinDatos(
                estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SIN_DATOS) ? ConstantesComunes.VALUE_SI
                        : ConstantesComunes.VALUE_NO);
        bean.setActaSinFirma(estado.contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_SIN_FIRMA_HUELLA)
                ? ConstantesComunes.VALUE_SI
                : ConstantesComunes.VALUE_NO);
        return bean;
    }

    private ActaBeanRevocatoria construirDetalleRevocatoria(ActaBean actaBean, String correlativo, Acta acta,
                                                            DetActa detActa) {
        ActaBeanRevocatoria bean = new ActaBeanRevocatoria();
        bean.setMesa(actaBean.getMesa());
        bean.setCopia(actaBean.getCopia());
        bean.setLote(correlativo);
        bean.setAutoridad(detActa.getAgrupacionPolitica().getDescripcion());
        bean.setEleccion(acta.getUbigeoEleccion().getEleccion().getNombre());

        bean.setErrorMaterial(
                detActa.getEstadoErrorMaterial() != null ? ConstantesComunes.VALUE_SI : ConstantesComunes.VALUE_NO);
        bean.setTipoErrorM(
                bean.getErrorMaterial().equals(ConstantesComunes.VALUE_NO) ? "" : detActa.getEstadoErrorMaterial());

        List<DetActaOpcion> opciones = this.detActaOpcionService.findByDetActaOrderByPosicion(detActa);
        String resumenErrores = opciones.stream().map(DetActaOpcion::getEstadoErrorMaterial).filter(Objects::nonNull)
                .flatMap(error -> Arrays.stream(error.split(","))).map(String::trim).filter(s -> !s.isEmpty())
                .distinct().collect(Collectors.joining(","));

        if (!resumenErrores.isEmpty()) {
            bean.setErrorMaterial(ConstantesComunes.VALUE_SI);
            bean.setTipoErrorM(
                    detActa.getEstadoErrorMaterial() != null
                            ? detActa.getEstadoErrorMaterial().concat(ConstantesComunes.SEPARADOR_ERRORES)
                            .concat(resumenErrores)
                            : resumenErrores);
        }

        boolean tieneIlegible = opciones.stream()
                .anyMatch(o -> ConstantesComunes.C_VALUE_ILEGIBLE.equals(o.getIlegible()));
        bean.setIlegibilidad(tieneIlegible ? ConstantesComunes.VALUE_SI : ConstantesComunes.VALUE_NO);

        boolean tieneImpugnados = opciones.stream()
                .anyMatch(o -> o.getPosicion().equals(ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)
                        && o.getVotos() != null && o.getVotos() > 0);
        bean.setVotosImpugnados(tieneImpugnados ? ConstantesComunes.VALUE_SI : ConstantesComunes.VALUE_NO);

        boolean ilegibleImpugnado = opciones.stream()
                .anyMatch(o -> o.getPosicion().equals(ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS)
                        && ConstantesComunes.C_VALUE_ILEGIBLE.equals(o.getIlegible()));
        if (ilegibleImpugnado) {
            bean.setVotosImpugnados(ConstantesComunes.VALUE_SI);
        }

        bean.setActasIncompletas(ConstantesComunes.VACIO);
        bean.setSolNulidad(ConstantesComunes.VACIO);
        bean.setActaSinDatos(ConstantesComunes.VACIO);
        bean.setActaSinFirma(ConstantesComunes.VACIO);
        return bean;
    }

    boolean autoridadConErrores(ActaBeanRevocatoria actaBeanRevocatoria) {
        return ConstantesComunes.VALUE_SI.equals(actaBeanRevocatoria.getVotosImpugnados())
                || ConstantesComunes.VALUE_SI.equals(actaBeanRevocatoria.getIlegibilidad())
                || ConstantesComunes.VALUE_SI.equals(actaBeanRevocatoria.getErrorMaterial());
    }

    private static String getTipoIlegible(Acta acta) {
        String tipoIlegible = "";
        if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_PREFERENCIAL)) {
            tipoIlegible += ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_N_ILEGIBLE_PREFERENCIAL
                    + ConstantesComunes.SEPARADOR_ERRORES;// ILEGIBILIDAD EN VOTO PREFERENCIAL
        }
        if (acta.getEstadoActaResolucion() != null
                && acta.getEstadoActaResolucion().contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_CVAS)) {
            tipoIlegible += ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_O_ILEGIBLE_CVAS
                    + ConstantesComunes.SEPARADOR_ERRORES;// ILEGIBILIDAD EN EL CVAS
        }
        if (acta.getEstadoActaResolucion() != null && acta.getEstadoActaResolucion()
                .contains(ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ILEGIBLE_AGRUPOL)) {
            tipoIlegible += ConstantesEstadoActa.ESTADO_ERROR_MATERIAL_P_ILEGIBLE_AGRUPOL
                    + ConstantesComunes.SEPARADOR_ERRORES;// ILEGIBLIDAD EN UNA AGRUPACION POLITICA
        }

        return SceUtils.quitarComasAlFinal(tipoIlegible);
    }

    @Override
    public GenericResponse<Object> generarCargoEntregaActaDevuelta(TokenInfo tokenInfo, List<ActaBean> actaBeanLis) {

        ResolucionAsociadosRequest resolucionAsociadosRequest = new ResolucionAsociadosRequest();
        resolucionAsociadosRequest.setTipoResolucion(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTA_DEVUELTA);
        resolucionAsociadosRequest.setActasAsociadas(actaBeanLis);
        return generarCargoEntregaComunes(tokenInfo, resolucionAsociadosRequest);
    }

    private void validarEstadosDeActasParaCargoEntrega(List<ActaBean> actaBeanList, Integer codigoTipoResolucion) {

        Integer codigoCargoEntrega = ConstantesCatalogo.getMapTipoResolucionCargoEntrega()
                .getOrDefault(codigoTipoResolucion, ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_DEFAULT);

        StringBuilder noExisten = new StringBuilder();
        StringBuilder estadoNoPermitido = new StringBuilder();

        List<String> estadosPermitidos = getEstadosPermitidosPorCodigoCargoEntrega(codigoCargoEntrega);

        for (ActaBean bean : actaBeanList) {
            Optional<Acta> opt = this.actaRepository.findById(bean.getActaId());
            if (opt.isEmpty()) {
                noExisten.append(bean.getMesa()).append(",");
                continue;
            }

            Acta acta = opt.get();
            if (!estadosPermitidos.isEmpty()
                    && estadosPermitidos.stream().noneMatch(p -> p.equals(acta.getEstadoActa()))) {
                estadoNoPermitido.append(bean.getMesa()).append(",");
            }
        }

        if (!noExisten.isEmpty() || !estadoNoPermitido.isEmpty()) {
            StringBuilder mensaje = new StringBuilder();
            if (!noExisten.isEmpty()) {
                mensaje.append("Las siguientes actas no se encuentran registradas en base de datos: ").append(noExisten)
                        .append(".\n");
            }
            if (!estadoNoPermitido.isEmpty()) {
                mensaje.append("Las siguientes actas no cuentan con un estado permitido: ").append(estadoNoPermitido)
                        .append(".");
            }

            throw new IllegalStateException(mensaje.toString());
        }

    }

    private static List<String> getEstadosPermitidosPorCodigoCargoEntrega(Integer codigoCargoEntrega) {
        List<String> estadosPermitidos = new ArrayList<>();
        if (codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_DEVUELTA)) {
            estadosPermitidos = List.of(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
        } else if (codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA)
                || codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO)) {
            estadosPermitidos = List.of(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA,
                    ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION);
        } else if (codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA)
                || codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA)
                || codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA)) {
            estadosPermitidos = List.of(ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION);
        } else if (codigoCargoEntrega.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)) {
            estadosPermitidos = List.of(ConstantesEstadoActa.ESTADO_ACTA_PROCESADA,
                    ConstantesEstadoActa.ESTADO_ACTA_PROCESADA_POR_RESOLUCION,
                    ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO);
        }
        return estadosPermitidos;

    }

    public byte[] generarByteOficioActasObservadas(TokenInfo tokenInfo,
                                                   ResolucionAsociadosRequest resolucionAsociadosRequest, String correlativo, String nombreReporte) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            ProcesoElectoral procesoElectoral = this.procesoElectoralService.findByActivo();

            parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE,
                    this.getClass().getClassLoader().getResourceAsStream(
                            ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE));
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
            parametros.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE,
                    this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON
                            + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE));
            parametros.put(ConstantesComunes.OFICIO_FECHA, OficioUtils.obtenerFechaOficio(null));
            parametros.put(ConstantesComunes.OFICIO_NUMERO, OficioUtils.generarNumeroOficio(correlativo, tokenInfo));
            parametros.put(ConstantesComunes.OFICIO_DESTINATARIO, "CUEVA CHAUCA BACILIO LUCIANO"); // PREGUNTAR EL
            // DESTINATARIO, DE
            // DONDE OBTENERLO.
            parametros.put(ConstantesComunes.OFICIO_CARGO, "JURADO ELECTORAL ESPECIAL LIMA CENTRO"); // PREGUNTAR DE
            // DONDE OBTENER EL
            // CARGO.
            parametros.put(ConstantesComunes.OFICIO_DIRECCION, "Av. Nicolás de Piérola N° 1070 Cercado de Lima."); // PREGUNTAR
            // DE
            // DONDE
            // OBTENER
            // LA
            // DIRECCIÓN.
            parametros.put(ConstantesComunes.OFICIO_ASUNTO,
                    "Remite Actas Electorales Observadas del proceso " + procesoElectoral.getAcronimo()); // PREGUNTAR
            // SI EL EN EL
            // ASUNTO SOLO
            // VARIA EL
            // NOMBRE DEL
            // PROCESO
            parametros.put(ConstantesComunes.OFICIO_NOMBRE_PROCESO, procesoElectoral.getNombre());

            int cantidad = resolucionAsociadosRequest.getActasAsociadas().size();
            parametros.put(ConstantesComunes.OFICIO_CANTIDAD_ACTAS, String.valueOf(cantidad));
            parametros.put(ConstantesComunes.OFICIO_CANTIDAD_TEXTO, OficioUtils.convertirNumeroATexto(cantidad));
            parametros.put(ConstantesComunes.OFICIO_CODIGO_VERIFICACION, "YUXFLJA"); // PREGUNTAR DE DONDE OBTENGO O
            // GENERO EL CODIGO.

            return Funciones.generarReporte(this.getClass(), resolucionAsociadosRequest.getActasAsociadas(),
                    nombreReporte + ConstantesComunes.EXTENSION_REPORTES_JASPER, parametros);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return new byte[0];
        }
    }

    public byte[] generarByteCargoDeEntrega(TokenInfo tokenInfo, ResolucionAsociadosRequest resolucionAsociadosRequest,
                                            List<ActaBeanRevocatoria> actaBeanRevocatoriaList, DetCatalogoEstructuraDTO detalleCatalogoEstructuraDTO,
                                            String correlativo, String fechaLotizacion, String nombreReporte) {
        try {
            Map<String, Object> parametros = new java.util.HashMap<>();
            ProcesoElectoral procesoElectoral = this.procesoElectoralService.findByActivo();
            parametros.put(ConstantesComunes.REPORT_PARAM_TITULO, procesoElectoral.getNombre());
            parametros.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE,
                    detalleCatalogoEstructuraDTO.getNombre().toUpperCase());
            parametros.put(ConstantesComunes.REPORT_PARAM_DESC_CC, tokenInfo.getCodigoCentroComputo()
                    + ConstantesComunes.GUION_MEDIO + tokenInfo.getNombreCentroComputo());
            parametros.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, tokenInfo.getDescOdpe());
            parametros.put(ConstantesComunes.REPORT_PARAM_SERVIDOR, ConstantesComunes.NOMBRE_SERVIDOR_BD);
            parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, tokenInfo.getNombreUsuario());
            parametros.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, nombreReporte);
            parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
            parametros.put(ConstantesComunes.REPORT_PARAM_CORRELATIVO, correlativo);
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE);
            parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_LOTI, fechaLotizacion);

            if (detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA)) {
                AtomicInteger index = new AtomicInteger(1);
                resolucionAsociadosRequest.getActasAsociadas().sort(Comparator.comparing(ActaBean::getMesa));
                resolucionAsociadosRequest.getActasAsociadas().forEach(acta -> acta.setIndex(index.getAndIncrement()));
                parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_RESOLUCION,
                        DateUtil.getDateString(resolucionAsociadosRequest.getFechaResolucion2(),
                                SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
                parametros.put(ConstantesComunes.REPORT_PARAM_CODIGO_RESOLUCION,
                        resolucionAsociadosRequest.getNumeroResolucion());
            } else if (detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)) {
                parametros.put(ConstantesComunes.REPORT_PARAM_SUBREPORT_DIR, ConstantesComunes.PATH_REPORT_JRXML);
                List<OrcDetalleCatalogoEstructura> detCatalogoEstructuraList = this.detalleCatalogoEstructuraRepository
                        .findByColumnaAndActivoOrderByCodigoS(ConstantesCatalogo.C_COLUMNA_ERROR_MATERIAL,
                                ConstantesCatalogo.DET_CAT_EST_ACTIVO);
                List<TipoErrorDTO> tipoErrorDTOList = detCatalogoEstructuraList.stream()
                        .map(entity -> new TipoErrorDTO(entity.getNombre(), entity.getCodigoS())).toList();
                parametros.put(ConstantesComunes.REPORT_PARAM_LISTA_ERROR_MATERIAL,
                        new JRBeanCollectionDataSource(tipoErrorDTOList));
            } else if (detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA)
                    || detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA)
                    || detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA)
                    || detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO)
                    || detalleCatalogoEstructuraDTO.getCodigoI()
                    .equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ANULADA_X_UBIGEO)) {
                parametros.put(ConstantesComunes.REPORT_PARAM_FECHA_RESOLUCION,
                        DateUtil.getDateString(resolucionAsociadosRequest.getFechaResolucion2(),
                                SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
                parametros.put(ConstantesComunes.REPORT_PARAM_CODIGO_RESOLUCION,
                        resolucionAsociadosRequest.getNumeroResolucion());
            }

            if (tokenInfo.getAbrevProceso().contains(ConstantesComunes.PROCESO_CPR_ABREV)) {
                return Funciones.generarReporte(this.getClass(), actaBeanRevocatoriaList,
                        nombreReporte + ConstantesComunes.EXTENSION_REPORTES_JASPER, parametros);
            } else {
                return Funciones.generarReporte(this.getClass(), resolucionAsociadosRequest.getActasAsociadas(),
                        nombreReporte + ConstantesComunes.EXTENSION_REPORTES_JASPER, parametros);
            }
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return new byte[0];
        }
    }

    private String getNombreReporteSegunCodigoCargoEntrega(Integer codigoCargoEntrega, String abrevProceso) {

        if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_DEVUELTA.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_ACTA_DEVUELTA;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_INFUNDADA;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_INFUNDADA_XUBIGEO.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_INFUNDADA_XUBIGEO;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ANULADA_X_UBIGEO.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_ANULADAS_X_UBIGEO;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA.equals(codigoCargoEntrega)
                || ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_EXT_SINIE;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA.equals(codigoCargoEntrega)) {
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_MESA_NO_INSTALADA;
        } else if (ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE.equals(codigoCargoEntrega)) {
            if (abrevProceso.contains(ConstantesComunes.PROCESO_CPR_ABREV))
                return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA_REV;
            return ConstantesComunes.RESOLUCIONES_REPORT_JRXML_CARGO_ENTREGA;
        } else
            return ConstantesComunes.VACIO;

    }

    @Override
    public GenericResponse<Object> generarCargoEntregaMesaNoInstaladas(TokenInfo tokenInfo,
                                                                       ResolucionAsociadosRequest resolucionAsociadosRequest) {
        return generarCargoEntregaComunes(tokenInfo, resolucionAsociadosRequest);
    }

    @Override
    public GenericResponse<Object> generarCargoEntregaInfundadas(TokenInfo tokenInfo,
                                                                 ResolucionAsociadosRequest resolucionAsociadosRequest) {
        return generarCargoEntregaComunes(tokenInfo, resolucionAsociadosRequest);
    }

    public GenericResponse<Object> generarCargoEntregaComunes(TokenInfo tokenInfo,
                                                              ResolucionAsociadosRequest resolucionAsociadosRequest) {

        List<ActaBean> actasIniciales = new ArrayList<>(resolucionAsociadosRequest.getActasAsociadas());

        validarEstadosDeActasParaCargoEntrega(resolucionAsociadosRequest.getActasAsociadas(),
                resolucionAsociadosRequest.getTipoResolucion());

        tokenInfo.setDescOdpe(obtenerDescOdpe(resolucionAsociadosRequest.getActasAsociadas()));

        DetCatalogoEstructuraDTO detCatalogo = obtenerDetalleCatalogoEstructura(
                resolucionAsociadosRequest.getTipoResolucion());
        if (detCatalogo == null)
            throw new IllegalStateException("No se encuentra registrado en el catálogo el tipo de formato "
                    + ConstantesCatalogo.getMapTiposResoluciones().get(resolucionAsociadosRequest.getTipoResolucion())
                    + ".");

        Formato formato = obtenerFormatoPorTipoCargoEntrega(tokenInfo.getNombreUsuario(), detCatalogo.getCodigoI());
        String numeroCorrelativo = String.format("%06d", formato.getCorrelativo());

        CabActaFormato cab = crearCabActaFormato(formato, tokenInfo.getNombreUsuario());
        procesarActasParaCargoEntrega(resolucionAsociadosRequest, tokenInfo.getNombreUsuario(), cab,
                resolucionAsociadosRequest.getTipoResolucion());

        Date fechaLote = Optional.ofNullable(formato.getFechaModificacion()).orElse(formato.getFechaCreacion());
        String nombreReporte = getNombreReporteSegunCodigoCargoEntrega(detCatalogo.getCodigoI(),
                tokenInfo.getAbrevProceso());

        List<ActaBean> unicos = resolucionAsociadosRequest.getActasAsociadas().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ActaBean::getMesa))),
                        ArrayList::new));

        List<ActaBeanRevocatoria> revList = new ArrayList<>();

        if (detCatalogo.getCodigoI().equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA)) {
            resolucionAsociadosRequest.setActasAsociadas(unicos);// solo se manda las mesas
            actualizarMesa(unicos, tokenInfo.getNombreUsuario(), ConstantesEstadoMesa.NO_INSTALADA);
        } else if (detCatalogo.getCodigoI().equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)) {

            if (tokenInfo.getAbrevProceso().contains(ConstantesComunes.PROCESO_CPR_ABREV)) {
                generarListaDetalleParaReporteRevocatoria(tokenInfo.getNombreUsuario(),
                        resolucionAsociadosRequest.getActasAsociadas(), revList, numeroCorrelativo);
            } else {
                generarListaDetalleParaReporteComun(resolucionAsociadosRequest.getActasAsociadas());
            }

        } else if (detCatalogo.getCodigoI().equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA)) {
            actualizarMesa(unicos, tokenInfo.getNombreUsuario(), ConstantesEstadoMesa.INSTALADA);
        } else if (detCatalogo.getCodigoI().equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA)) {
            actualizarMesa(unicos, tokenInfo.getNombreUsuario(), ConstantesEstadoMesa.INSTALADA);
        }

        byte[] pdf = generarByteCargoDeEntrega(tokenInfo, resolucionAsociadosRequest, revList, detCatalogo,
                numeroCorrelativo, DateUtil.getDateString(fechaLote, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH),
                nombreReporte);

        if (pdf != null && pdf.length > 0) {
            guardarCargoPdfAArchivo(cab, pdf, tokenInfo, nombreReporte);
        }

        // regresando lista inicial
        resolucionAsociadosRequest.setActasAsociadas(actasIniciales);

        return new GenericResponse<>(true, numeroCorrelativo, Base64.getEncoder().encodeToString(pdf));
    }

    public GenericResponse<Object> generarCargoEntregaOficio(TokenInfo tokenInfo,
                                                             ResolucionAsociadosRequest resolucionAsociadosRequest) throws IOException {
        ActaBean acta = resolucionAsociadosRequest.getActasAsociadas().get(0);

        boolean esStaeIntegrada = Boolean.TRUE.equals(acta.isStaeIntegrada());

        if (!esStaeIntegrada && actaCelesteRepository.findByActa_IdAndEstadoDigitalizacion(acta.getActaId(),
                ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA).isEmpty()) {
            return new GenericResponse<>(false, "No se encontró la imagen del acta del sobre celeste.");
        }

        Optional<DetActaFormato> detFormatoOpt = detActaFormatoRepository.findByActa_Id(acta.getActaId()).stream()
                .filter(daf -> daf.getCabActaFormato() != null && daf.getCabActaFormato().getFormato() != null
                        && Objects.equals(daf.getCabActaFormato().getFormato().getTipoFormato(),
                        ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)
                        && Objects.equals(daf.getActivo(), ConstantesComunes.ACTIVO))
                .findFirst();

        GenericResponse<Object> cargoExistente = obtenerCargoExistente(detFormatoOpt);
        if (cargoExistente != null) {
            return cargoExistente;
        }

        List<ActaBean> actas = Collections.singletonList(acta);

        validarEstadosDeActasParaCargoEntrega(Collections.singletonList(acta),
                resolucionAsociadosRequest.getTipoResolucion());

        tokenInfo.setDescOdpe(obtenerDescOdpe(resolucionAsociadosRequest.getActasAsociadas()));

        DetCatalogoEstructuraDTO detCatalogo = obtenerDetalleCatalogoEstructura(
                resolucionAsociadosRequest.getTipoResolucion());
        if (detCatalogo == null) {
            throw new IllegalStateException("No se encuentra registrado en el catálogo el tipo de formato "
                    + ConstantesCatalogo.getMapTiposResoluciones().get(resolucionAsociadosRequest.getTipoResolucion())
                    + ".");
        }

        Formato formato = obtenerFormatoPorTipoCargoEntrega(tokenInfo.getNombreUsuario(), detCatalogo.getCodigoI());
        String numeroCorrelativo = String.format("%06d", formato.getCorrelativo());

        CabActaFormato cab = crearCabActaFormato(formato, tokenInfo.getNombreUsuario());
        procesarActasParaCargoEntregaOficio(acta, tokenInfo.getNombreUsuario(), cab);

        Date fechaLote = Optional.ofNullable(formato.getFechaModificacion()).orElse(formato.getFechaCreacion());
        String nombreReporte = getNombreReporteSegunCodigoCargoEntrega(detCatalogo.getCodigoI(),
                tokenInfo.getAbrevProceso());

        List<ActaBeanRevocatoria> revList = new ArrayList<>();
        Integer codigoCatalogo = detCatalogo.getCodigoI();
        if (codigoCatalogo.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_MESA_NO_INSTALADA)) {
            actualizarMesa(actas, tokenInfo.getNombreUsuario(), ConstantesEstadoMesa.NO_INSTALADA);
        } else if (codigoCatalogo.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE)) {
            if (tokenInfo.getAbrevProceso().contains(ConstantesComunes.PROCESO_CPR_ABREV)) {
                generarListaDetalleParaReporteRevocatoria(tokenInfo.getNombreUsuario(), actas, revList,
                        numeroCorrelativo);
            } else {
                generarListaDetalleParaReporteComun(actas);
            }
        } else if (codigoCatalogo.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_SINIESTRADA)
                || codigoCatalogo.equals(ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ACTA_EXTRAVIADA)) {
            actualizarMesa(actas, tokenInfo.getNombreUsuario(), ConstantesEstadoMesa.INSTALADA);
        }

        byte[] pdf = generarByteCargoDeEntrega(tokenInfo, resolucionAsociadosRequest, revList, detCatalogo,
                numeroCorrelativo, DateUtil.getDateString(fechaLote, SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH),
                nombreReporte);

        if (pdf != null && pdf.length > 0) {
            guardarCargoPdfAArchivo(cab, pdf, tokenInfo, nombreReporte);
        }
        return new GenericResponse<>(true, numeroCorrelativo, Base64.getEncoder().encodeToString(pdf));
    }

    private GenericResponse<Object> obtenerCargoExistente(Optional<DetActaFormato> detFormatoOpt) throws IOException {
        if (detFormatoOpt.isPresent()) {
            Archivo archivo = archivoRepository
                    .findById(detFormatoOpt.get().getCabActaFormato().getArchivoFormatoPdf().getId()).orElse(null);

            if (archivo != null) {
                String base64 = OficioUtils.convertToBase64(PathUtils.normalizePath(this.storageService.getPathUpload(), archivo.getGuid()));
                return new GenericResponse<>(true, archivo.getNombre(), base64);
            }
            return new GenericResponse<>(false, "Ocurrio un error al mostrar el cargo de entrega.");
        }
        return null;
    }

    public GenericResponse<Object> generarOficioActasObservadas(TokenInfo tokenInfo,
                                                                ResolucionAsociadosRequest request) {
        try {
            List<ActaBean> actasIniciales = new ArrayList<>(request.getActasAsociadas());
            tokenInfo.setDescOdpe(obtenerDescOdpe(request.getActasAsociadas()));

            DetCatalogoEstructuraDTO detCatalogo = obtenerDetalleCatalogoEstructura(request.getTipoResolucion());
            if (detCatalogo == null) {
                throw new IllegalStateException("No se encuentra el tipo de formato para el oficio.");
            }

            Formato formato = obtenerFormatoPorTipoCargoEntrega(tokenInfo.getNombreUsuario(), detCatalogo.getCodigoI());
            String numeroCorrelativo = String.format("%06d", formato.getCorrelativo());

            CabActaFormato cab = crearCabActaFormato(formato, tokenInfo.getNombreUsuario());
            procesarActasParaCargoEntrega(request, tokenInfo.getNombreUsuario(), cab, request.getTipoResolucion());

            String nombreReporte = ConstantesComunes.OFICIO_ACTAS_OBSERVADAS_JRXML;

            byte[] pdf = generarByteOficioActasObservadas(tokenInfo, request, numeroCorrelativo, nombreReporte);

            if (pdf != null && pdf.length > 0) {
                guardarCargoPdfAArchivo(cab, pdf, tokenInfo, nombreReporte);
            }

            request.setActasAsociadas(actasIniciales);

            return new GenericResponse<>(true, numeroCorrelativo, Base64.getEncoder().encodeToString(pdf));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return new GenericResponse<>(false, e.getMessage());
        }
    }

    private String obtenerDescOdpe(List<ActaBean> actaBeanList) {
        return this.actaRepository.findById(actaBeanList.getFirst().getActaId())
                .map(a -> a.getUbigeoEleccion().getUbigeo().getAmbitoElectoral())
                .map(a -> a.getCodigo() + "-" + a.getNombre()).orElse("");
    }

    private DetCatalogoEstructuraDTO obtenerDetalleCatalogoEstructura(Integer codigoTipoResolucion) {

        Integer codigoCatalogoCargoEntrega = ConstantesCatalogo.getMapTipoResolucionCargoEntrega()
                .getOrDefault(codigoTipoResolucion, ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_DEFAULT);

        List<DetCatalogoEstructuraDTO> lista = this.detalleCatalogoEstructuraService.findByMaestroAndColumna(
                ConstantesComunes.CAT_ESTRUCTURA_COLUMN_MAESTRO_TIPO_FORMATO,
                ConstantesCatalogo.C_COLUMNA_TIPO_FORMATO);

        return lista.stream().filter(e -> Objects.equals(e.getCodigoI(), codigoCatalogoCargoEntrega)).findFirst()
                .orElse(null);

    }

    private Formato obtenerFormatoPorTipoCargoEntrega(String usuario, Integer codigoCargoEntrega) {
        List<Formato> lista = this.formatoRepository.findByTipoFormato(codigoCargoEntrega);
        if (lista.isEmpty()) {
            Formato nuevo = new Formato();
            nuevo.setId(codigoCargoEntrega.longValue());
            nuevo.setActivo(ConstantesComunes.ACTIVO);
            nuevo.setCorrelativo(1);
            nuevo.setUsuarioCreacion(usuario);
            nuevo.setFechaCreacion(new Date());
            nuevo.setTipoFormato(codigoCargoEntrega);
            return this.formatoRepository.save(nuevo);
        } else {
            Formato existente = lista.getFirst();
            existente.setActivo(ConstantesComunes.ACTIVO);
            existente.setCorrelativo(existente.getCorrelativo() + 1);
            existente.setFechaModificacion(new Date());
            existente.setUsuarioModificacion(usuario);
            return this.formatoRepository.save(existente);
        }
    }

    private CabActaFormato crearCabActaFormato(Formato formato, String usuario) {
        CabActaFormato cab = new CabActaFormato();
        cab.setFormato(formato);
        cab.setCorrelativo(formato.getCorrelativo());
        cab.setActivo(ConstantesComunes.ACTIVO);
        cab.setUsuarioCreacion(usuario);
        cab.setFechaCreacion(new Date());
        return this.cabActaFormatoRepository.save(cab);
    }

    private void procesarActasParaCargoEntrega(ResolucionAsociadosRequest req, String usr, CabActaFormato cab,
                                               Integer tipoResolucion) {
        int index = 1;
        TabResolucion tab = Optional.ofNullable(req.getId()).flatMap(this.tabResolucionRepository::findById)
                .orElse(null);

        if (ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE.equals(tipoResolucion)) {
            List<ActaBean> listaActaBeanSoloObservadas = req.getActasAsociadas().stream()
                    .filter(acta -> ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(acta.getEstadoActa()))
                    .toList();

            req.setActasAsociadas(listaActaBeanSoloObservadas);
        }

        for (ActaBean bean : req.getActasAsociadas()) {
            bean.setIndex(index++);
            Optional<Acta> optActa = this.actaRepository.findById(bean.getActaId());
            if (optActa.isEmpty())
                continue;
            Acta acta = optActa.get();

            if (tipoResolucion != null) {
                actualizarEstadosPorTipoResolucion(acta, tipoResolucion);
                acta.setUsuarioModificacion(usr);
                acta.setFechaModificacion(new Date());
                this.actaRepository.save(acta);
            }

            DetActaFormato det = new DetActaFormato();
            det.setCabActaFormato(cab);
            det.setActa(acta);
            det.setActivo(ConstantesComunes.ACTIVO);
            det.setUsuarioCreacion(usr);
            det.setFechaCreacion(new Date());
            this.detActaFormatoRepository.save(det);

            if (tab != null) {
                if (!List.of(ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS,
                        ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO).contains(tipoResolucion)) {
                    actualizarDetalleResolucion(tab, acta, usr);
                }
                actualizarResolucionAplicada(tab, usr);
            }
        }
    }

    private void actualizarEstadosPorTipoResolucion(Acta acta, Integer tipoResolucion) {
        if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_MESAS_NO_INSTALADAS)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_MESA_NO_INSTALADA);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA);
        } else if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_EXTRAVIADA);
        } else if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ACTA_SINIESTRADA);
        } else if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTA_DEVUELTA)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
        } else if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS) ||
            tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_INFUNDADAS_XUBIGEO)) {
            if(acta.getEstadoDigitalizacion().equals(ConstantesEstadoActa.ESTADO_DIGTAL_2DO_CONTROL_ACEPTADA)){
                acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA);
            }
            if(acta.getNumeroCopia()==null || acta.getNumeroCopia().isEmpty()){
                acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
            }
        } else if (tipoResolucion.equals(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE)) {
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
            acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
        }
    }

    private void procesarActasParaCargoEntregaOficio(ActaBean bean, String usuario, CabActaFormato cab) {
        Optional<Acta> optActa = this.actaRepository.findById(bean.getActaId());
        if (optActa.isEmpty())
            return;

        Acta acta = optActa.get();
        acta.setUsuarioModificacion(usuario);
        acta.setEstadoCc(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA);
        acta.setFechaModificacion(new Date());
        this.actaRepository.save(acta);

        DetActaFormato det = new DetActaFormato();
        det.setCabActaFormato(cab);
        det.setActa(acta);
        det.setActivo(ConstantesComunes.ACTIVO);
        det.setUsuarioCreacion(usuario);
        det.setFechaCreacion(new Date());
        this.detActaFormatoRepository.save(det);

    }

    private void actualizarDetalleResolucion(TabResolucion tab, Acta acta, String usr) {
        List<DetActaResolucion> detalles = this.detActaResolucionRepository.findByResolucionAndActa(tab, acta);
        if (!detalles.isEmpty()) {
            DetActaResolucion detalle = detalles.getFirst();
            detalle.setEstadoActa(acta.getEstadoActa());
            detalle.setUsuarioModificacion(usr);
            detalle.setFechaModificacion(new Date());
            this.detActaResolucionRepository.save(detalle);
        }
    }

    private void actualizarResolucionAplicada(TabResolucion tab, String usr) {
        tab.setEstadoResolucion(ConstantesEstadoResolucion.PROCESADO);
        tab.setAudUsuarioModificacion(usr);
        tab.setAudFechaModificacion(new Date());
        this.tabResolucionRepository.save(tab);
    }

    private void guardarCargoPdfAArchivo(CabActaFormato cab, byte[] pdf, TokenInfo tokenInfo, String nombreReporte) {
        String nombreReporteFinal = nombreReporte + "_correlativo_" + cab.getCorrelativo()
                + ConstantesFormatos.EXTENSION_FILE_PDF;
        Archivo archivo = this.utilSceService.guardarArchivoPdf(pdf, nombreReporteFinal, tokenInfo);
        if (archivo != null) {
            cab.setArchivoFormatoPdf(archivo);
            this.cabActaFormatoRepository.save(cab);
        }
    }

    public void actualizarMesa(List<ActaBean> unicos, String usuario, String estadoMesa) {
        for (ActaBean actaBean : unicos) {
            Mesa mesa = this.mesaRepository.findByCodigo(actaBean.getMesa());
            mesa.setEstadoMesa(estadoMesa);
            mesa.setUsuarioModificacion(usuario);
            mesa.setFechaModificacion(new Date());
            this.mesaRepository.save(mesa);
        }
    }

    @Override
    public Long count() {
        return this.tabResolucionRepository.count();
    }

    @Override
    public void deleteAllInBatch() {
        this.tabResolucionRepository.deleteAllInBatch();

    }

    @Override
    public List<ReimpresionCargoDto> reimpresionCargos(String nroMesa) {

        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        List<Acta> actaList = mesaInfo.getActaList();

        List<ReimpresionCargoDto> reimpresionCargoDtos = actaList.stream()
                .flatMap(acta -> generarDtosDesdeActa(nroMesa, acta).stream()).toList();

        if (reimpresionCargoDtos.isEmpty())
            throw new BadRequestException(String.format("La mesa %s, no presenta cargos asociados.", nroMesa));

        return reimpresionCargoDtos;
    }

    private List<ReimpresionCargoDto> generarDtosDesdeActa(String mesa, Acta acta) {
        if (acta == null || acta.getUbigeoEleccion() == null) {
            return Collections.emptyList();
        }

        String eleccion = Optional.ofNullable(acta.getUbigeoEleccion().getEleccion()).map(Eleccion::getNombre)
                .orElse(ConstantesComunes.VACIO);

        String copia = (acta.getNumeroCopia() != null && acta.getDigitoChequeoEscrutinio() != null)
                ? String.format("%s%s", acta.getNumeroCopia(), acta.getDigitoChequeoEscrutinio())
                : ConstantesComunes.VACIO;

        List<DetActaFormato> detActaFormatoes = detActaFormatoRepository
            .findByActa_IdOrderByFechaCreacion(acta.getId()).stream().filter(det -> det.getActivo()==1).toList();

        if (detActaFormatoes.isEmpty()) {
            return Collections.emptyList();
        }

        return detActaFormatoes.stream().map(det -> {
            ReimpresionCargoDto dto = new ReimpresionCargoDto();
            dto.setMesa(mesa);
            dto.setEleccion(eleccion);
            dto.setActa(copia.isEmpty() ? mesa : String.format("%s-%s", mesa, copia));

            CabActaFormato cab = det.getCabActaFormato();
            if (cab != null && cab.getFormato() != null) {
                Archivo archivo = cab.getArchivoFormatoPdf();
                dto.setIdArchivo(Optional.ofNullable(archivo).map(Archivo::getId).orElse(-1L));
                dto.setNombreArchivo(
                        Optional.ofNullable(archivo).map(Archivo::getNombre).orElse(ConstantesComunes.VACIO));
                dto.setCorrelativo(cab.getCorrelativo());

                Integer tipoCargo = cab.getFormato().getTipoFormato();
                dto.setTipoCargo(tipoCargo);
                dto.setDescripcionCargo(
                        ConstantesCatalogo.getMapTiposCargos().getOrDefault(tipoCargo, "Descripción no disponible"));
            }
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public List<DigitizationListResolucionItem> listaResolucionesDigtal(String usuario) {
        List<TabResolucion> tabResolucionList = this.tabResolucionRepository
                .findByEstadoDigitalizacionAndUsuarioControlAndActivo(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA,
                        usuario, ConstantesComunes.ACTIVO);

        // Si tiene resoluciones asignadas pero es menor que la distribución, agregar
        // libres
        if (tabResolucionList.isEmpty()
                || tabResolucionList.size() < ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION) {

            int nuevaDistribucion = ConstantesComunes.N_DISTRIBUCION_ACTAS_VERIFICACION - tabResolucionList.size();

            List<TabResolucion> tabResolucionListLibres = this.tabResolucionRepository
                    .findByEstadoDigitalizacionAndUsuarioControlIsNullAndActivo(
                            ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA, ConstantesComunes.ACTIVO);

            if (!tabResolucionListLibres.isEmpty()) {
                Collections.shuffle(tabResolucionListLibres);
                // Agregar solo las que se necesitan según nuevaDistribucion
                List<TabResolucion> resolucionesParaAgregar = tabResolucionListLibres.stream().limit(nuevaDistribucion)
                        .toList();
                tabResolucionList.addAll(resolucionesParaAgregar);
            }
        }

        return tabResolucionList.stream().map(tabResolucion -> {
            if (tabResolucion.getUsuarioControl() == null) {
                tabResolucion.setUsuarioControl(usuario);
                tabResolucion.setFechaUsuarioControl(new Date());
                this.tabResolucionRepository.save(tabResolucion);
            }

            DigitizationListResolucionItem item = new DigitizationListResolucionItem();
            item.setId(tabResolucion.getId());
            item.setNumeroResolucion(tabResolucion.getNumeroResolucion());
            item.setIdArchivo(tabResolucion.getArchivoResolucion().getId());
            item.setNumeroPaginas(tabResolucion.getNumeroPaginas());
            item.setFechaRegistro(tabResolucion.getAudFechaCreacion());
            item.setEstadoDigitalizacion(tabResolucion.getEstadoDigitalizacion());
            item.setListaPaginas(SceUtils.getPaginadoResolucion(
                    tabResolucion.getNumeroPaginas() == null ? 0 : tabResolucion.getNumeroPaginas()));

            return item;
        }).toList();
    }

    @Override
    public List<DigitizationListResolucionItem> listaResolucionesParaEditar(String nombreUsuario) {

        List<TabResolucion> tabResolucionList = this.tabResolucionRepository
                .findByActivoOrderByAudFechaModificacionDesc(ConstantesComunes.ACTIVO);

        return tabResolucionList.stream().filter(
                        tabResolucion -> !ConstantesEstadoResolucion.ANULADO.equals(tabResolucion.getEstadoResolucion()))
                .map(tabResolucion -> {
                    DigitizationListResolucionItem item = new DigitizationListResolucionItem();
                    item.setId(tabResolucion.getId());
                    item.setNumeroResolucion(tabResolucion.getNumeroResolucion());
                    Archivo archivo = tabResolucion.getArchivoResolucion();
                    item.setIdArchivo(archivo == null ? 0 : archivo.getId());
                    item.setNumeroPaginas(tabResolucion.getNumeroPaginas());
                    item.setFechaRegistro(
                            tabResolucion.getAudFechaModificacion() == null ? tabResolucion.getAudFechaCreacion()
                                    : tabResolucion.getAudFechaModificacion());
                    item.setNombreArchivo(archivo == null ? ConstantesComunes.VACIO : archivo.getNombre());
                    item.setEstadoDigitalizacion(tabResolucion.getEstadoDigitalizacion());
                    return item;
                }).toList();

    }

    @Override
    public GenericResponse<TabResolucionDTO> validarParaEdicion(TokenInfo tokenInfo, String numeroResolucion) {

        TabResolucion tabResolucion = tabResolucionRepository
                .findByNumeroResolucionAndActivo(numeroResolucion, ConstantesComunes.ACTIVO).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format("No se encuentra registrada la resolución con número %s.", numeroResolucion)));

        if (!tabResolucion.getEstadoDigitalizacion().equals(ConstantesEstadoResolucion.RECHAZADA_2DO_CC)) {
            throw new IllegalStateException(
                    String.format("La resolución %s no se encuentra en un estado para edición.", numeroResolucion));
        }

        return new GenericResponse<>(Boolean.TRUE, "La resolución se encuentra en un estado habilitado para edición");
    }

    @Override
    @Transactional
    public void digitalizarResolucion(TokenInfo tokenInfo, Long idResolucion, String numeroResolucion,
                                      Integer numeroPaginas, MultipartFile file) {

        this.utilSceService.validarNumeroResolucion(numeroResolucion);

        String mimeDetectado = this.utilSceService.validarArchivoEscaneado(file,
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_RESOLUCION, ConstantesComunes.VACIO);

        if (idResolucion != null) {
            TabResolucion tabResolucion = this.tabResolucionRepository.findById(idResolucion)
                    .orElseThrow(() -> new BadRequestException(
                            String.format("La resolución con ID : %d no se encuentra registrada.", idResolucion)));

            if (!List.of(ConstantesEstadoResolucion.DIGTAL_RECHAZADO, ConstantesEstadoResolucion.RECHAZADA_2DO_CC)
                    .contains(tabResolucion.getEstadoDigitalizacion())) {
                throw new BadRequestException(String.format(
                        "La resolución %s debe estar en estado rechazado en control de digitalización o calidad.",
                        tabResolucion.getNumeroResolucion()));
            }
            Archivo archivoAnterior = tabResolucion.getArchivoResolucion();
            this.utilSceService.inactivarArchivo(archivoAnterior, tokenInfo);

            procesarResolucionDigitalizada(tabResolucion,
                    crearDto(numeroResolucion, file, numeroPaginas, mimeDetectado), tokenInfo);

        } else {
            Optional<TabResolucion> tabResolucionOptional = this.tabResolucionRepository
                    .findByNumeroResolucionAndActivoAndEstadoResolucionNot(numeroResolucion, ConstantesComunes.ACTIVO,
                            ConstantesEstadoResolucion.ANULADO)
                    .stream().findFirst();
            if (tabResolucionOptional.isPresent()) {
                if (List.of(ConstantesEstadoResolucion.DIGTAL_RECHAZADO, ConstantesEstadoResolucion.RECHAZADA_2DO_CC)
                        .contains(tabResolucionOptional.get().getEstadoDigitalizacion())) {
                    throw new BadRequestException(String.format(
                            "La resolución : %s se encuentra en estado rechazado, digitalizarla desde la opción Lista de resoluciones.",
                            numeroResolucion));
                }

                throw new BadRequestException(
                        String.format("El número de resolución %s ya se encuentra registrado.", numeroResolucion));
            }

            this.utilSceService.validarGuidUnico(tokenInfo, file);
            this.utilSceService.validarNombreArchivoUnico(file);

            procesarResolucionDigitalizada(null, crearDto(numeroResolucion, file, numeroPaginas, mimeDetectado),
                    tokenInfo);
        }

        logService.registrarLog(tokenInfo.getNombreUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                String.format("La resolución %s fue procesada correctamente.", numeroResolucion),
                tokenInfo.getCodigoCentroComputo(), 0, 1);
    }

    private ActaPorCorregirListItem mapToListItem(Acta acta) {
        return new ActaPorCorregirListItem(acta.getId(), acta.getMesa().getCodigo(), acta.getNumeroCopia(),
                acta.getDigitoChequeoEscrutinio(), acta.getUbigeoEleccion().getEleccion().getNombre());
    }

    private ResolucionDigtalDto crearDto(String numeroResolucion, MultipartFile file, Integer numeroPaginas,
                                         String mimeType) {
        ResolucionDigtalDto dto = new ResolucionDigtalDto();
        dto.setNumeroResolucion(numeroResolucion);
        dto.setFile(file);
        dto.setDetectedType(mimeType);
        dto.setNumeroPaginas(numeroPaginas);
        return dto;
    }

    private void procesarResolucionDigitalizada(TabResolucion tabResolucion, ResolucionDigtalDto resolucionDigtalDto,
                                                TokenInfo tokenInfo) {

        if (tabResolucion != null) {
            Archivo archivo = guardarArchivoResolucion(resolucionDigtalDto, tokenInfo);
            actualizarTabResolucion(tabResolucion, resolucionDigtalDto, archivo, tokenInfo);
        } else {// resolucion nueva
            Archivo archivo = guardarArchivoResolucion(resolucionDigtalDto, tokenInfo);
            this.saveResolucion(resolucionDigtalDto.getNumeroResolucion(), resolucionDigtalDto.getNumeroPaginas(),
                    archivo, archivo.getUsuarioCreacion());
        }
    }

    private void actualizarTabResolucion(TabResolucion tabResolucion, ResolucionDigtalDto resolucionDigtalDto,
                                         Archivo archivo, TokenInfo tokenInfo) {
        tabResolucion.setArchivoResolucion(archivo);
        tabResolucion.setNumeroPaginas(resolucionDigtalDto.getNumeroPaginas());
        tabResolucion.setNumeroResolucion(resolucionDigtalDto.getNumeroResolucion());
        tabResolucion.setEstadoDigitalizacion(ConstantesEstadoResolucion.DIGTAL_DIGITALIZADA);
        tabResolucion.setAudFechaModificacion(new Date());
        tabResolucion.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
        this.tabResolucionRepository.save(tabResolucion);
    }

    private Archivo guardarArchivoResolucion(ResolucionDigtalDto resolucionDigtalDto, TokenInfo tokenInfo) {
        Archivo archivo = new Archivo();
        try {
            archivo.setNombre(resolucionDigtalDto.getFile().getOriginalFilename());
            archivo.setNombreOriginal(archivo.getNombre().contains(".")
                    ? archivo.getNombre().substring(0, archivo.getNombre().indexOf("."))
                    : archivo.getNombre());
            archivo.setFormato(resolucionDigtalDto.getDetectedType());
            archivo.setPeso(String.valueOf(resolucionDigtalDto.getFile().getSize()));
            String guid = String.format("%s%s%s", tokenInfo.getCodigoCentroComputo(), ConstantesComunes.GUION_MEDIO,
                    DigestUtils.sha256Hex(resolucionDigtalDto.getFile().getInputStream()));
            archivo.setGuid(guid);
            archivo.setRuta(storageService.getPathUpload());
            archivo.setFechaCreacion(new Date());
            archivo.setCodigoDocumentoElectoral(ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_RESOLUCION);
            archivo.setUsuarioCreacion(tokenInfo.getNombreUsuario());
            archivo.setActivo(ConstantesComunes.ACTIVO);
            this.archivoRepository.save(archivo);
            this.storageService.storeFile(resolucionDigtalDto.getFile(), archivo.getGuid());
        } catch (IOException e) {
            throw new InternalServerErrorException(String.format("Error al guardar el archivo: %s", e.getMessage()));
        }
        return archivo;
    }

    @Override
    @Transactional
    public GenericResponse<Boolean> bloquearYAsignarResolucion(Long idResolucion, String usuario) {
        try {
            // 1. Consultar la resolución actual
            Optional<TabResolucion> optionalResolucion = this.tabResolucionRepository.findById(idResolucion);

            if (optionalResolucion.isEmpty()) {
                logger.warn("Resolución {} no encontrada", idResolucion);
                throw new NotFoundException("Resolución no encontrada");
            }

            TabResolucion resolucion = optionalResolucion.get();
            String usuarioAsociado = resolucion.getUsuarioAsociacion();

            // CASO 1: Si c_usuario_asociacion es NULL → bloquear y asignar
            if (usuarioAsociado == null) {
                // Intentar bloquear con FOR UPDATE SKIP LOCKED
                Long resolucionBloqueada = this.tabResolucionRepository.bloquearResolucionParaAsociacion(idResolucion);

                if (resolucionBloqueada == null) {
                    // Otro usuario la tomó justo antes
                    logger.info("Resolución {} ya fue tomada por otro usuario", idResolucion);
                    throw new BadRequestException("La resolución está siendo procesada por otro usuario.");
                }

                // Asignar el usuario
                int resultado = this.tabResolucionRepository.asignarUsuarioAsociacion(idResolucion, usuario,
                        new Date());

                if (resultado > 0) {
                    logger.info("Resolución {} asignada exitosamente a usuario {}", idResolucion, usuario);
                    return new GenericResponse<>(true, "Usuario asociado asignado correctamente", true);
                }

                throw new InternalServerErrorException("No se pudo asignar la resolución");
            }

            // CASO 2: Si c_usuario_asociacion NO es NULL y es DIFERENTE del usuario
            // logueado
            if (!usuarioAsociado.equals(usuario)) {
                logger.warn("Usuario {} intentó modificar resolución {} que pertenece a {}", usuario, idResolucion,
                        usuarioAsociado);
                throw new BadRequestException(String.format(
                        "No puede continuar porque la resolución le pertenece al usuario (%s)", usuarioAsociado));
            }

            // CASO 3: Si c_usuario_asociacion NO es NULL y es IGUAL al usuario logueado
            logger.info("Usuario {} ya tiene asignada la resolución {}", usuario, idResolucion);
            return new GenericResponse<>(true, "La resolución ya está asignada a este usuario", true);

        } catch (BadRequestException | NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException("Error al bloquear la resolución: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GenericResponse<Boolean> desbloquearResolucion(Long idResolucion, String usuario) {
        try {
            // 1. Consultar la resolución actual
            Optional<TabResolucion> optionalResolucion = this.tabResolucionRepository.findById(idResolucion);

            if (optionalResolucion.isEmpty()) {
                logger.warn("Resolución {} no encontrada para desbloquear", idResolucion);
                throw new NotFoundException("Resolución no encontrada");
            }

            TabResolucion resolucion = optionalResolucion.get();
            String usuarioAsociado = resolucion.getUsuarioAsociacion();
            String estadoResolucion = resolucion.getEstadoResolucion();

            // Validar que esté en estado SIN_PROCESAR
            if (!ConstantesEstadoResolucion.SIN_PROCESAR.equals(estadoResolucion)) {
                logger.warn("Resolución {} no puede ser desbloqueada porque está en estado {}", idResolucion,
                        estadoResolucion);
                return new GenericResponse<>(false, "La resolución no puede ser desbloqueada porque está en estado %s. Solo se pueden desbloquear resoluciones sin procesar.");
            }

            // Si no está bloqueada
            if (usuarioAsociado == null) {
                logger.info("Resolución {} no está bloqueada", idResolucion);
                return new GenericResponse<>(false, "La resolución no está bloqueada");
            }

            // Desbloquear directamente sin validar usuario
            int resultado = this.tabResolucionRepository.desbloquearResolucion(idResolucion);

            if (resultado > 0) {
                logger.info("Resolución {} desbloqueada exitosamente por usuario {} (estaba bloqueada por {})",
                        idResolucion, usuario, usuarioAsociado);
                return new GenericResponse<>(true, String.format(
                        "Resolución desbloqueada correctamente (estaba bloqueada por %s)", usuarioAsociado), true);
            }

            return new GenericResponse<>(false, "No se pudo desbloquear la resolución");
        } catch (Exception e) {
            return new GenericResponse<>(false, "Error al desbloquear la resolución");
        }
    }
}
