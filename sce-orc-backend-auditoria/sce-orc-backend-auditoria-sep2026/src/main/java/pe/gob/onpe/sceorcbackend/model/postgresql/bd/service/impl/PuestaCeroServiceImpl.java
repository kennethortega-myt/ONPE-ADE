package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.exception.BusinessValidationException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.puestacero.PuestaCeroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AmbitoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Mesa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.PuestaCero;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabConfirmPc;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Ubigeo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.PuestaCeroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UbigeoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaServiceGroup;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.EleccionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaDocumentoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MiembroMesaSorteadoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OmisosServiceGroup;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.TabAutorizacionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.TabConfirmPcService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.AutorizacionDto;
import pe.gob.onpe.sceorcbackend.model.service.RespaldoService;
import pe.gob.onpe.sceorcbackend.utils.ActaDTO;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoMesa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PuestaCeroServiceImpl implements PuestaCeroService {

    Logger logger = LoggerFactory.getLogger(PuestaCeroServiceImpl.class);

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String esquema;


    private final EleccionService eleccionService;
    private final PuestaCeroRepository tabPuestaCeroRepository;
    private final ActaServiceGroup actaServiceGroup;
    private final MaeProcesoElectoralService maeProcesoElectoralService;
    private final MesaService mesaService;
    private final ResolucionService resolucionService;
    private final UsuarioService usuarioService;
    private final TabAutorizacionService tabAutorizacionService;
    private final TabConfirmPcService tabConfirmPcService;
    private final MesaDocumentoService mesaDocumentoService;
    private final MaeProcesoElectoralService procesoElectoralService;
    private final StorageService storageService;
    private final OmisosServiceGroup omisosServiceGroup;
    private final CentroComputoRepository centroComputoRepository;
    private final UbigeoRepository ubigeoRepository;
    private final ITabLogService logService;
    private final UtilSceService utilSceService;
    private final MiembroMesaSorteadoService miembroMesaSorteadoService;
    private final ParametroService parametroService;
    private final RabbitMqSender rabbitMqSender;
    private final RespaldoService  respaldoService;
    
    public PuestaCeroServiceImpl(
    		MaeProcesoElectoralService procesoElectoralService,
            PuestaCeroRepository tabPuestaCeroRepository,
            EleccionService eleccionService,
            ActaServiceGroup actaServiceGroup,
            MaeProcesoElectoralService maeProcesoElectoralService,
            MesaService mesaService,
            ResolucionService resolucionService,
            UsuarioService usuarioService,
            TabAutorizacionService tabAutorizacionService,
            TabConfirmPcService tabConfirmPcService,
            MesaDocumentoService mesaDocumentoService,
            OmisosServiceGroup omisosServiceGroup,
            StorageService storageService,
            CentroComputoRepository centroComputoRepository,
            UbigeoRepository ubigeoRepository,
            ITabLogService logService,
            UtilSceService utilSceService,
            MiembroMesaSorteadoService miembroMesaSorteadoService,
            ParametroService parametroService,
            RabbitMqSender rabbitMqSender,
            RespaldoService respaldoService) {
    	this.procesoElectoralService = procesoElectoralService;
        this.eleccionService = eleccionService;
        this.actaServiceGroup = actaServiceGroup;
        this.tabPuestaCeroRepository = tabPuestaCeroRepository;
        this.maeProcesoElectoralService = maeProcesoElectoralService;
        this.mesaService = mesaService;
        this.resolucionService = resolucionService;
        this.usuarioService = usuarioService;
        this.tabAutorizacionService = tabAutorizacionService;
        this.tabConfirmPcService = tabConfirmPcService;
        this.mesaDocumentoService = mesaDocumentoService;
        this.omisosServiceGroup = omisosServiceGroup;
        this.storageService  =storageService;
        this.centroComputoRepository = centroComputoRepository;
        this.ubigeoRepository = ubigeoRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
        this.miembroMesaSorteadoService = miembroMesaSorteadoService;
        this.parametroService = parametroService;
        this.rabbitMqSender = rabbitMqSender;
        this.respaldoService = respaldoService;
    }


    @Override
    @Transactional
    public byte[] reportePuestaCeroCentroComputo(String apr, String ccc, String ncc, String usr) {
        try {

            List<PuestaCeroDTO> puestaCeroDTOS = new ArrayList<>();

            String abreviaturaProceso = apr.substring(0, 3);//ERM   EMC

            List<Eleccion> maeEleccionList = this.eleccionService.findAll();

            maeEleccionList.sort(Comparator.comparing(Eleccion::getCodigo));

            List<ActaDTO> cabActaListProjection = this.actaServiceGroup.getCabActaService().findActasNative();

            String odpe = ccc;

            Optional<CentroComputo> optionalCentroComputo = this.centroComputoRepository.findByCodigo(ccc);
            if(optionalCentroComputo.isPresent()){
                CentroComputo centroComputo = optionalCentroComputo.get();
                Optional<Ubigeo> optionalUbigeo = this.ubigeoRepository.findFirstByCentroComputo(centroComputo);
                if(optionalUbigeo.isPresent()){
                    Ubigeo ubigeo = optionalUbigeo.get();
                    AmbitoElectoral ambitoElectoral = ubigeo.getAmbitoElectoral();
                    odpe = ambitoElectoral.getCodigo() + "-" + ambitoElectoral.getNombre();
                }
            }


            //DIGITALIZACION INI
            List<Eleccion> maeEleccionxProceso = new ArrayList<>();

            if (abreviaturaProceso.equals(ConstantesComunes.PROCESO_ERM_ABREV)) {
                maeEleccionxProceso = maeEleccionList.stream().filter(e -> e.getCodigo().equals(ConstantesComunes.COD_ELEC_REG) || e.getCodigo().equals(ConstantesComunes.COD_ELEC_PROV)).toList();
            } else if (abreviaturaProceso.equals(ConstantesComunes.PROCESO_EMC_ABREV)) {
                maeEleccionxProceso = maeEleccionList.stream().filter(e -> e.getCodigo().equals(ConstantesComunes.COD_ELEC_DIST)).toList();
            }

            if(maeEleccionxProceso.isEmpty()) {
                maeEleccionxProceso = maeEleccionList;
            }

            for (Eleccion maeEleccion : maeEleccionxProceso) {

                PuestaCeroDTO puestaCeroDTODigitalizacion = new PuestaCeroDTO();

                String eleccion2 = maeEleccion.getCodigo(); //campo1
                String nombreEleccion = maeEleccion.getNombre();//campo2

                List<ActaDTO> cabActaListByEleccion = cabActaListProjection.stream().filter(cabActa -> cabActa.getCodigo().equals(maeEleccion.getCodigo())).toList();

                int numeroactas = cabActaListByEleccion.size(); //campo3 NUMERO DE ACTAS

                List<ActaDTO> actasADigitalizar = cabActaListByEleccion.stream().filter(cabActa -> cabActa.getEstadoDigitalizacion().equals("P")).toList();

                int nActasAdigitalizar = actasADigitalizar.size(); //campo 4; --NUMERO DE ACTAS A DIGITALIZAR

                List<ActaDTO> actasDigitalizadas = cabActaListByEleccion.stream().filter(cabActa -> !cabActa.getEstadoDigitalizacion().equals("P")).toList();

                int nActasDigitalizadas = actasDigitalizadas.size(); //campo5; --NUMERO DE ACTAS DIGITALIZADAS


                List<TabResolucion> tabResolucionList = this.resolucionService.findAll();
                int numeroResoluciones = tabResolucionList.size();//campo 6 --NUMERO DE RESOLUCIONES


                List<TabResolucion> resolucionesDigitalizadas = tabResolucionList.stream().filter(tabRes -> !tabRes.getEstadoDigitalizacion().equals("P")).toList();

                int resolDigtal = resolucionesDigitalizadas.size(); // campo 7 --NUMERO DE RESOLUCIONES DIGITALIZADAS


                puestaCeroDTODigitalizacion.setOrden(2);
                if (eleccion2.equals(ConstantesComunes.COD_ELEC_REG)) {
                    eleccion2 = ConstantesComunes.COD_PROCESO_REGIONAL;//codigo de proceso
                    nombreEleccion = ConstantesComunes.NOMBL_ELEC_REG;
                } else if (eleccion2.equals(ConstantesComunes.COD_ELEC_PROV) || eleccion2.equals(ConstantesComunes.COD_ELEC_DIST)) {
                    eleccion2 = ConstantesComunes.COD_PROCESO_MUNICIPAL;//codigo de proceso
                    nombreEleccion = ConstantesComunes.NOMBL_ELEC_PROV;
                }
                puestaCeroDTODigitalizacion.setCampo1(eleccion2);
                puestaCeroDTODigitalizacion.setCampo2(nombreEleccion);
                puestaCeroDTODigitalizacion.setCampo3(numeroactas);
                puestaCeroDTODigitalizacion.setCampo4(nActasAdigitalizar);
                puestaCeroDTODigitalizacion.setCampo5(nActasDigitalizadas);
                puestaCeroDTODigitalizacion.setCampo6(numeroResoluciones);
                puestaCeroDTODigitalizacion.setCampo7(resolDigtal);
                puestaCeroDTODigitalizacion.setSuma7(numeroResoluciones);

                puestaCeroDTOS.add(puestaCeroDTODigitalizacion);
            }
            //FIN DIGITALIZACION




            //iniicio DIGITACION
            for (Eleccion maeEleccion : maeEleccionList) {

                PuestaCeroDTO puestaCeroDtoDigitacion = new PuestaCeroDTO();

                String codigoEleccion = maeEleccion.getCodigo();
                String nombreEleccion = maeEleccion.getNombre();
                List<ActaDTO> cabActaListByEleccion = cabActaListProjection.stream().filter(cabActa -> cabActa.getCodigo().equals(codigoEleccion)).toList();

                int aProcesar = cabActaListByEleccion.size();

                List<String> estadosActaPendientesDeProcesar = Arrays.asList("A", "B", "C", "E", "F", "G");
                List<ActaDTO> actasPendientesProcesar = cabActaListByEleccion.stream().filter(cabActa -> estadosActaPendientesDeProcesar.stream().anyMatch(p -> p.equals(cabActa.getEstadoActa()))).toList();


                int pendientesPorProcesar = actasPendientesProcesar.size();
                int aProcesarMenosPendientesAProcesar = aProcesar - pendientesPorProcesar;


                List<String> estadosCompuEnvjee = Arrays.asList(
                        ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA,
                        ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_OBSERVADA_IMPUGNADA);

                List<ActaDTO> envJeeList = cabActaListByEleccion.stream().filter(cabActa -> estadosCompuEnvjee.stream().anyMatch(p -> p.equals(cabActa.getEstadoCc()))).toList();

                int envJee = envJeeList.size();

                List<String> estadosActaResueltas = Arrays.asList("L", "M", "N", "O", "S");
                List<ActaDTO> actasComputadas = cabActaListByEleccion.stream().filter(cabActa -> estadosActaResueltas.stream().anyMatch(p -> p.equals(cabActa.getEstadoActa()))
                        && cabActa.getEstadoCc().equals(ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_CONTABILIZADA)).toList();

                int resueltas = actasComputadas.size();

                int envJeeMasResueltas = envJee + resueltas;

                puestaCeroDtoDigitacion.setOrden(4);
                puestaCeroDtoDigitacion.setCampo1(codigoEleccion);
                puestaCeroDtoDigitacion.setCampo2(nombreEleccion);
                puestaCeroDtoDigitacion.setCampo3(aProcesar);
                puestaCeroDtoDigitacion.setCampo4(pendientesPorProcesar);
                puestaCeroDtoDigitacion.setCampo5(aProcesarMenosPendientesAProcesar);
                puestaCeroDtoDigitacion.setCampo6(envJee);
                puestaCeroDtoDigitacion.setCampo7(resueltas);
                puestaCeroDtoDigitacion.setSuma7(envJeeMasResueltas);
                puestaCeroDTOS.add(puestaCeroDtoDigitacion);
            }
            // fin digitacion

            List<Mesa> tabMesaList = this.mesaService.findAll();


            //inicio puestaCeroDtoMiembrosMesa
            PuestaCeroDTO puestaCeroDtoMiembrosMesa = new PuestaCeroDTO();
            puestaCeroDtoMiembrosMesa.setOrden(6);
            puestaCeroDtoMiembrosMesa.setCampo1("1");
            puestaCeroDtoMiembrosMesa.setCampo2("MIEMBROS DE MESA");
            puestaCeroDtoMiembrosMesa.setCampo3(tabMesaList.size());
            puestaCeroDtoMiembrosMesa.setCampo4(this.mesaService.contarMePorActivo(ConstantesEstadoMesa.PROCESADA, ConstantesComunes.ACTIVO).intValue()); //mesas registrada de acta de escrutinio
            puestaCeroDtoMiembrosMesa.setCampo5(this.omisosServiceGroup.getMiembroMesaEscrutinioRepository().contarPorActivo(ConstantesComunes.ACTIVO).intValue()); //miembros mesas escrutnio registrados
            puestaCeroDTOS.add(puestaCeroDtoMiembrosMesa);
            //fin puestaCeroDtoMiembrosMesa


            //inicio OMISOS
            PuestaCeroDTO puestaCeroDTOOmisosVotantes = new PuestaCeroDTO();

            puestaCeroDTOOmisosVotantes.setOrden(7);
            puestaCeroDTOOmisosVotantes.setCampo1("2");
            puestaCeroDTOOmisosVotantes.setCampo2("VOTANTES");
            puestaCeroDTOOmisosVotantes.setCampo3(tabMesaList.size());
            puestaCeroDTOOmisosVotantes.setCampo4(this.mesaService.contarLePorActivo(ConstantesEstadoMesa.PROCESADA, ConstantesComunes.ACTIVO).intValue());
            puestaCeroDTOOmisosVotantes.setCampo5(tabMesaList.stream().mapToInt(Mesa::getCantidadElectoresHabiles).sum());
            puestaCeroDTOOmisosVotantes.setCampo6(this.omisosServiceGroup.getOmisoVotanteService().contarPorActivo(ConstantesComunes.ACTIVO).intValue());
            puestaCeroDTOS.add(puestaCeroDTOOmisosVotantes);

            PuestaCeroDTO puestaCeroDTOOmisosMM = new PuestaCeroDTO();

            puestaCeroDTOOmisosMM.setOrden(7);
            puestaCeroDTOOmisosMM.setCampo1("3");
            puestaCeroDTOOmisosMM.setCampo2("MIEMBROS DE MESA");
            puestaCeroDTOOmisosMM.setCampo3(tabMesaList.size());
            puestaCeroDTOOmisosMM.setCampo4(this.mesaService.contarMmPorActivo(ConstantesEstadoMesa.PROCESADA, ConstantesComunes.ACTIVO).intValue());
            puestaCeroDTOOmisosMM.setCampo5(this.miembroMesaSorteadoService.count().intValue());
            puestaCeroDTOOmisosMM.setCampo6(this.omisosServiceGroup.getOmisoMiembroMesaService().contarPorActivo(ConstantesComunes.ACTIVO).intValue());
            puestaCeroDTOS.add(puestaCeroDTOOmisosMM);
            //fin OMISOS






            //inicio puestaCeroDtoPersoneres
            PuestaCeroDTO puestaCeroDtoPersoneros = new PuestaCeroDTO();
            puestaCeroDtoPersoneros.setOrden(8);
            puestaCeroDtoPersoneros.setCampo1("4");
            puestaCeroDtoPersoneros.setCampo2("PERSONEROS");
            puestaCeroDtoPersoneros.setCampo3(tabMesaList.size());
            puestaCeroDtoPersoneros.setCampo4(this.mesaService.contarPrPorActivo(ConstantesEstadoMesa.PROCESADA, ConstantesComunes.ACTIVO).intValue()); //mesas registradas de personeros
            puestaCeroDtoPersoneros.setCampo5(this.omisosServiceGroup.getPersoneroRepository().contarPorActivo(ConstantesComunes.ACTIVO).intValue()); //personeros registrados
            puestaCeroDTOS.add(puestaCeroDtoPersoneros);
            //fin puestaCeroDtoPersoneros


            Map<String, Object> parametros = new HashMap<>();
            ProcesoElectoral maeProcesoElectoral = this.maeProcesoElectoralService.findByActivo();
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_TITULO, maeProcesoElectoral.getNombre());
            parametros.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, ConstantesComunes.PUESTA_CERO_TITULO_REPORTE.toUpperCase());
            parametros.put(ConstantesComunes.REPORT_PARAM_DESC_CC, ccc + "-" + ncc);
            parametros.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, odpe);
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
            parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, usr);
            parametros.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, ConstantesComunes.PUESTA_CERO_REPORT_JRXML);
            this.logService.registrarLog(usr, Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), "Se imprimió el reporte de puesta cero.", ccc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
            return Funciones.generarReporte(this.getClass(),puestaCeroDTOS, ConstantesComunes.PUESTA_CERO_REPORT_JRXML + ConstantesComunes.EXTENSION_REPORTES_JASPER,parametros);
        } catch (Exception e) {
            logger.error("Error:", e);
            return new byte[0];
        }
    }

    @Override
    @Transactional
    public GenericResponse<TabAutorizacion> registrarAutorizacion(String codCentroComputo, String usuarioPC, String tipoAutorizacion, String acronimoProceso) {

        GenericResponse<TabAutorizacion> genericResponse = new GenericResponse<>();

        long cantidadMesas = this.mesaService.count();
        if(cantidadMesas == 0)
            return new GenericResponse<>(false, "Antes de realizar la puesta cero debe realizar carga inicial.");

        boolean autorizado = procesoElectoralService.verificarHabilitacionDiaEleccion(acronimoProceso, SceConstantes.PATTERN_DD_MM_YYYY_DASH);
        logger.info("Autorizado puesta cero cc {}" , autorizado);
        if(autorizado){
        	List<TabAutorizacion> tabAutorizacionList = this.tabAutorizacionService.findByAutorizacionAndTipoAutorizacionAndActivo
                    (ConstantesCatalogo.DET_CAT_EST_COD_AUTH_PCCC, tipoAutorizacion, ConstantesComunes.ACTIVO);//Buscar por usuario y por tipo de autorización

            List<TabAutorizacion> autorizacionesPendientes = tabAutorizacionList.stream().filter(tabRes -> tabRes.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_PENDIENTE)).toList();

            List<TabAutorizacion> autorizacionesAprobadas = tabAutorizacionList.stream().filter(tabRes -> tabRes.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_APROBADO)).toList();

            if (autorizacionesAprobadas.size() == 1 && ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO.equals(tipoAutorizacion)) {
                genericResponse.setSuccess(Boolean.TRUE);
                TabAutorizacion autorizacion = autorizacionesAprobadas.getFirst();
                autorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_EJECUTADA);
                autorizacion.setUsuarioModificacion(usuarioPC);
                autorizacion.setFechaModificacion(new Date());
                this.tabAutorizacionService.save(autorizacion);
                genericResponse.setMessage("Se ejecutará la puesta a cero.");
            }

            if (!autorizacionesPendientes.isEmpty()) {
                genericResponse.setSuccess(Boolean.FALSE);
                String mensaje = "";
                if (tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_REPROCESAR_ACTA)) {
                    mensaje = "reprocesar acta";
                } else if (tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO)) {
                    mensaje = "realizar puesta a cero";
                }

                genericResponse.setMessage("Existe una solicitud para " + mensaje + ", pendiente de aprobación.");
            }

            if (autorizacionesAprobadas.isEmpty() && autorizacionesPendientes.isEmpty()) {
                genericResponse.setSuccess(Boolean.FALSE);
                TabAutorizacion tabAutorizacion = new TabAutorizacion();
                tabAutorizacion.setNumeroAutorizacion(this.tabAutorizacionService.findMaxNumeroAutorizacion() + 1);
                tabAutorizacion.setActivo(ConstantesComunes.ACTIVO);
                tabAutorizacion.setUsuarioCreacion(usuarioPC);
                tabAutorizacion.setFechaCreacion(new Date());
                tabAutorizacion.setTipoAutorizacion(tipoAutorizacion);
                String detalle = "";
                StringBuilder usuariosConectados = new StringBuilder();
                if (tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_REPROCESAR_ACTA)) {
                    detalle = "Reprocesar acta por el usuario " + tabAutorizacion.getUsuarioCreacion();
                } else if (tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO)) {
                    detalle = "Puesta a Cero de Centro de Cómputo por el usuario " + tabAutorizacion.getUsuarioCreacion() + ".";

                    //VALIDAR SESION ACTIVAS
                    List<Usuario> tabUsuarioList = this.usuarioService.usuarioActivos();
                    for (Usuario tabUsuario : tabUsuarioList) {
                        if (
                                tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
                                        tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
                                        tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION) ||
                                    (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC) &&
                                        !tabUsuario.getUsuario().equals(usuarioPC))
                        ) {
                            usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
                        }
                    }
                    if (!usuariosConectados.isEmpty()) {
                        usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
                        usuariosConectados.append(".");
                        detalle = "Para realizar la puesta a cero, los siguientes usuarios deben cerrar sesión: ";
                        detalle = detalle + usuariosConectados;
                    }
                }

                if (!usuariosConectados.isEmpty() && tipoAutorizacion.equals(ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO)) {
                    genericResponse.setData(null);
                    genericResponse.setMessage(detalle);
                } else {
                    tabAutorizacion.setDetalle(detalle);
                    tabAutorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_PENDIENTE);
                    tabAutorizacion.setAutorizacion(ConstantesCatalogo.DET_CAT_EST_COD_AUTH_PCCC);
                    this.tabAutorizacionService.save(tabAutorizacion);
                    genericResponse.setData(tabAutorizacion);
                    genericResponse.setMessage("Se registró su solicitud como pendiente, para realizar " + detalle);
                }

            }
        } else {
              String detalle = "";
              StringBuilder usuariosConectados = new StringBuilder();
              List<Usuario> tabUsuarioList = this.usuarioService.usuarioActivos();
              for (Usuario tabUsuario : tabUsuarioList) {
                if (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR)
                    || tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER)
                    || tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION)
                    || (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC)
                        && !tabUsuario.getUsuario().equals(usuarioPC))) {
                  usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
                }
              }
              if (!usuariosConectados.isEmpty()) {
                  usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
                  usuariosConectados.append(".");
                  detalle = "Para realizar la puesta a cero, los siguientes usuarios deben cerrar sesión: ";
                  detalle = detalle + usuariosConectados;

                  genericResponse.setSuccess(Boolean.FALSE);
                  genericResponse.setData(null);
                  genericResponse.setMessage(detalle);
              }
        }

        return genericResponse;
    }

    @Override
    @Transactional
    public GenericResponse<TabAutorizacion> aprobarAutorizacion(Long idAutorizacion, String usuarioPC) {
        GenericResponse<TabAutorizacion> genericResponse = new GenericResponse<>();
        Optional<TabAutorizacion> optionalTabAutorizacion = this.tabAutorizacionService.findById(idAutorizacion);
        if (optionalTabAutorizacion.isPresent()) {
            genericResponse.setSuccess(Boolean.TRUE);
            TabAutorizacion tabAutorizacion = optionalTabAutorizacion.get();
            tabAutorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_APROBADO);
            tabAutorizacion.setUsuarioModificacion(usuarioPC);
            tabAutorizacion.setFechaModificacion(new Date());
            genericResponse.setData(tabAutorizacion);
            genericResponse.setMessage("La autorización fue aprobada correctamente.");
            this.tabAutorizacionService.save(tabAutorizacion);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
        }

        return genericResponse;
    }

    @Override
    @Transactional
    public GenericResponse<TabAutorizacion> rechazarAutorizacion(Long idAutorizacion, String usuarioPC) {
        GenericResponse<TabAutorizacion> genericResponse = new GenericResponse<>();
        Optional<TabAutorizacion> optionalTabAutorizacion = this.tabAutorizacionService.findById(idAutorizacion);
        if (optionalTabAutorizacion.isPresent()) {
            TabAutorizacion tabAutorizacion = optionalTabAutorizacion.get();
            if (ConstantesComunes.ESTADO_PENDIENTE.equals(tabAutorizacion.getEstadoAprobacion())) {
                genericResponse.setSuccess(Boolean.TRUE);
                tabAutorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_RECHAZADO);
                tabAutorizacion.setUsuarioModificacion(usuarioPC);
                tabAutorizacion.setFechaModificacion(new Date());
                genericResponse.setData(tabAutorizacion);
                genericResponse.setMessage("La solicitud del usuario " + tabAutorizacion.getUsuarioCreacion() + " fue rechazada.");
                this.tabAutorizacionService.save(tabAutorizacion);
            }else{
                logger.warn("No se puede rechazar la autorización con ID: {} porque no esta en estado pendiente. Estado actual: {}",
                        idAutorizacion,tabAutorizacion.getEstadoAprobacion());
                genericResponse.setSuccess(Boolean.FALSE);
            }
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
        }

        return genericResponse;
    }

    @Override
    public GenericResponse<List<AutorizacionDto>> listarAutorizaciones() {

        GenericResponse<List<AutorizacionDto>> genericResponse = new GenericResponse<>();
        List<AutorizacionDto> autorizacionDtoList = new ArrayList<>();
        List<TabAutorizacion> tabAutorizacionList = this.tabAutorizacionService.findByAutorizacionOrderByFechaModificacionDesc(ConstantesCatalogo.DET_CAT_EST_COD_AUTH_PCCC);
        if (!tabAutorizacionList.isEmpty()) {
            tabAutorizacionList.sort((a1, a2) -> {
                int prioridad1 = getPrioridadEstado(a1.getEstadoAprobacion());
                int prioridad2 = getPrioridadEstado(a2.getEstadoAprobacion());

                if (prioridad1 != prioridad2) {
                    return Integer.compare(prioridad1, prioridad2);
                }

                // Mismo estado: ordenar por fecha desc (más reciente primero)
                Date fecha1 = a1.getFechaModificacion() != null ? a1.getFechaModificacion() : a1.getFechaCreacion();
                Date fecha2 = a2.getFechaModificacion() != null ? a2.getFechaModificacion() : a2.getFechaCreacion();
                return fecha2.compareTo(fecha1);
            });
        }
        for (TabAutorizacion tabAutorizacion : tabAutorizacionList) {
            AutorizacionDto autorizacionDto = new AutorizacionDto();
            autorizacionDto.setNumero(tabAutorizacion.getNumeroAutorizacion());
            autorizacionDto.setId(tabAutorizacion.getId());
            autorizacionDto.setDetalle(tabAutorizacion.getDetalle());
            autorizacionDto.setEstado(tabAutorizacion.getEstadoAprobacion());
            autorizacionDto.setDescripcionEstado(ConstantesComunes.getMapEstadosComunes().get(tabAutorizacion.getEstadoAprobacion()));
            if(tabAutorizacion.getFechaModificacion()==null){
                autorizacionDto.setFechaHora(DateUtil.getDateString(tabAutorizacion.getFechaCreacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
            }else{
                autorizacionDto.setFechaHora(DateUtil.getDateString(tabAutorizacion.getFechaModificacion(), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
            }
            autorizacionDtoList.add(autorizacionDto);
        }
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setMessage("Se listó las autorizaciones de puesta a cero a centro de cómputo correctamente.");
        genericResponse.setData(autorizacionDtoList);

        return genericResponse;
    }

    private int getPrioridadEstado(String estado) {
        return switch (estado) {
            case ConstantesComunes.ESTADO_PENDIENTE  -> 1;
            case ConstantesComunes.ESTADO_APROBADO   -> 2;
            case ConstantesComunes.ESTADO_RECHAZADO  -> 3;
            case ConstantesComunes.ESTADO_EJECUTADA  -> 4;
            default -> 5;
        };
    }

    @Override
    @Transactional
    public int realizarPuestaCeroCentroComputo() {

        try{
            Long totalActasNoPendientes = this.actaServiceGroup.getCabActaService().countByEstadoDigitalizacionNot(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);

            if (totalActasNoPendientes != 0)
                return 0;

            if (this.resolucionService.count() != 0)
                return 0;

            if (this.mesaDocumentoService.count() != 0)
                return 0;

            List<TabConfirmPc> tabConfirmPcServiceList = this.tabConfirmPcService.findAll();
            if (!tabConfirmPcServiceList.isEmpty())
                return 0;


            return 1;

        }catch (Exception e){
            return 0;
        }

    }

    @Override
    @Transactional
    public GenericResponse<Boolean> confirmarPuestaCeroDesdeCC(TokenInfo tokenInfo) {
        try{

            List<TabConfirmPc> tabConfirmPcServiceList = this.tabConfirmPcService.findAll();
            if (tabConfirmPcServiceList.isEmpty()) {
                TabConfirmPc tabConfirmPc = new TabConfirmPc();
                tabConfirmPc.setFechaCreacion(new Date());
                tabConfirmPc.setUsuarioCreacion(tokenInfo.getNombreUsuario());
                tabConfirmPc.setProcesado(1);
                this.tabConfirmPcService.save(tabConfirmPc);

                this.logService.registrarLog(
                        tokenInfo.getNombreUsuario(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        "Se registró la confirmación de la puesta a cero en la pc de digitalización.",
                         tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

                return new GenericResponse<>(Boolean.TRUE, "Se registró la confirmación de la puesta a cero en la pc de digitalización.", null);
            } else {

                return new GenericResponse<>(Boolean.FALSE, "Su confirmación ya fue registrada.", null);
            }
        }catch (Exception e) {
            return new GenericResponse<>(Boolean.FALSE, "Ocurrió un error "+e.getMessage(), null);
        }
    }

    @Override
    public int desactivarPuestaCeroMasivo() {
        return this.tabPuestaCeroRepository.desactivarPuestaCeroMasivo();
    }

    @Override
    @Transactional
    public void puestaCeroGeneral(TokenInfo tokenInfo, String idAutorizacion) {

        validarUsuariosConectados(tokenInfo);

        if (esquema == null) {
            throw new BusinessValidationException("Variable esquema no está configurada");
        }

        CentroComputo centroComputo = centroComputoRepository
                .findByCodigo(tokenInfo.getCodigoCentroComputo())
                .orElseThrow(() -> new BusinessValidationException(
                        "Centro de cómputo no registrado: " + tokenInfo.getCodigoCentroComputo()
                ));
        
        // Pausar jobs programados durante la puesta a cero
        JobServiceImpl.pausarJobs();
        long inicio = System.currentTimeMillis();

        try {
            // Limpiar todas las colas de RabbitMQ
            logger.info("Limpiando colas de RabbitMQ antes de puesta a cero");
            rabbitMqSender.purgeAllQueues();

            //validar parametro
            ParametroDto parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_COLA_PROCESAMIENTO);
            if(parametroDto !=null && parametroDto.getValor()!=null && parametroDto.getValor().toString().equals(ConstantesComunes.TEXT_TRUE)) {
                throw new BusinessValidationException("El modelo se encuentra en procesamiento, intentar nuevamente en unos segundos.");
            }
            respaldoService.eliminarPuestaCero();

            //limpiar encolados de todas las colas

            logger.info("Iniciando puesta a cero en esquema: {}", esquema);

            String fechaFormateada = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Map<String, Object> resultado = centroComputoRepository.registrarPuestaCeroCc(
                    esquema.toLowerCase(),
                    centroComputo.getId().intValue(),
                    fechaFormateada,
                    fechaFormateada,
                    tokenInfo.getNombreUsuario(),
                    0,
                    ""
            );

            validarResultadoStoredProcedure(resultado);

            eliminarArchivosRepositorio();

            long duracion = System.currentTimeMillis() - inicio;
            logger.info("Puesta a cero finalizada correctamente en {} ms ({} seg)", duracion, duracion / 1000);
        } finally {
            // Reanudar jobs siempre, incluso si hay error
            JobServiceImpl.reanudarJobs();
        }
    }


    private void validarResultadoStoredProcedure(Map<String, Object> resultado) {

        if (resultado == null)
            throw new InternalServerErrorException("El procedimiento no retornó resultado.");

        String codigoResultado = String.valueOf(resultado.get("po_resultado"));
        String mensaje = String.valueOf(resultado.get("po_mensaje"));

        logger.info("Resultado SP: {}", codigoResultado);
        logger.info("Mensaje SP: {}", mensaje);

        if (!"1".equals(codigoResultado)) {
            logger.error("Resultado SP: {}", codigoResultado);
            logger.error("Mensaje SP: {}", mensaje);

            throw new InternalServerErrorException("Error en el procedimiento de la puesta cero");
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





    private void validarUsuariosConectados(TokenInfo tokenInfo) {
        StringBuilder usuariosConectados = new StringBuilder();
        List<Usuario> tabUsuarioList = this.usuarioService.usuarioActivos();
        for (Usuario tabUsuario : tabUsuarioList) {
            if (
                    tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
                            tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
                            tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION) ||
                            (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC) &&
                                    !tabUsuario.getUsuario().equals(tokenInfo.getNombreUsuario()))
            ) {
                usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
            }
        }

        if (!usuariosConectados.isEmpty()) {
            usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
            usuariosConectados.append(".");
            throw new InternalServerErrorException("Para realizar la puesta a cero, los siguientes usuarios deben cerrar sesión:" + usuariosConectados);
        }
    }


    @Override
    @Transactional
    public void save(PuestaCero k) {
        this.tabPuestaCeroRepository.save(k);
    }

    @Override
    @Transactional
    public void saveAll(List<PuestaCero> k) {
        this.tabPuestaCeroRepository.saveAll(k);
    }

    @Override
    @Transactional
    public void deleteAll() {
        this.tabPuestaCeroRepository.deleteAll();
    }

    @Override
    public List<PuestaCero> findAll() {
        return this.tabPuestaCeroRepository.findAll();
    }


}
