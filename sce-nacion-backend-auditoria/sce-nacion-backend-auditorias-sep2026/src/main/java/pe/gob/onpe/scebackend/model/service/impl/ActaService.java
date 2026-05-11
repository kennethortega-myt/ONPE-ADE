package pe.gob.onpe.scebackend.model.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.*;
import pe.gob.onpe.scebackend.model.dto.request.FiltrosActaNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.*;
import pe.gob.onpe.scebackend.model.orc.repository.*;
import pe.gob.onpe.scebackend.model.service.IActaService;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.SceUtils;
import pe.gob.onpe.scebackend.utils.StringCustomUtils;
import pe.gob.onpe.scebackend.utils.constantes.ConstanteTransmision;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesMonitoreo;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ActaService implements IActaService {

    public static final String ESTADO = "estado";
    public static final String CANTIDAD = "cantidad";
    Logger logger = LoggerFactory.getLogger(ActaService.class);

    @Value("${fileserver.files}")
    private String ubicacionFile;

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private ActaHistorialRepository actaHistorialRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private DetActaRepository detActaRepository;

    @Autowired
    private UbigeoEleccionRepository detUbigeoRepository;
    
    @Autowired
    private TransmisionRecepcionRepository transmisionRecepcionRepository;
    
    @Autowired
    private VwActaMonitoreoRepository vwActaMonitoreoRepository;

    @Override
    @Transactional("locationTransactionManager")
    public void save(Acta acta) {
        actaRepository.save(acta);

    }

    @Override
    @Transactional("locationTransactionManager")
    public CabActaDto save(CabActaDto actaDto) throws FileNotFoundException {
        Acta acta = null;
        Mesa mesa = null;
        UbigeoEleccion eubigeo = null;
        if (actaDto.getId() != null) {
            acta = this.actaRepository.getOne(actaDto.getId());
            if (acta == null) {
                logger.info("El ID de la Acta no se encuentra previamente registrado");
            } else {
                acta.setFechaModificacion(new Date());
                acta.setActivo(SceConstantes.ACTIVO);
                acta.setUsuarioModificacion(actaDto.getAudUsuarioModificacion());
            }
        } else {
            acta = new Acta();
            acta.setFechaCreacion(new Date());
            acta.setActivo(SceConstantes.ACTIVO);
            acta.setUsuarioCreacion(actaDto.getAudUsuarioCreacion());
        }

        if (actaDto.getIdMesa() != null) {
            mesa = this.mesaRepository.getOne(actaDto.getIdMesa());
        } else {
            logger.info("Es obligatorio ingresar el ID de la mesa");
        }

        if (actaDto.getIdDetUbigeoEleccion() != null) {
            eubigeo = this.detUbigeoRepository.getOne(actaDto.getIdDetUbigeoEleccion());
        } else {
            logger.info("Es obligatorio ingresar el ID de la mesa");
        }

        acta.setMesa(mesa);
        acta.setUbigeoEleccion(eubigeo);
        acta.setNumeroCopia(actaDto.getNumeroCopia());
        acta.setNumeroLote(actaDto.getNumeroLote());
        acta.setTipoLote(actaDto.getTipoLote());
        acta.setElectoresHabiles(actaDto.getElectoresHabiles());
        acta.setCvas(actaDto.getCvas());
        acta.setVotosCalculados(actaDto.getVotosCalculados());
        acta.setTotalVotos(actaDto.getTotalVotos());
        acta.setEstadoActa(actaDto.getEstadoActa());
        acta.setEstadoCc(actaDto.getEstadoCc());
        acta.setEstadoActaResolucion(actaDto.getEstadoActaResolucion());
        acta.setEstadoDigitalizacion(actaDto.getEstadoDigitalizacion());
        acta.setEstadoErrorMaterial(actaDto.getEstadoErrorMaterial());
        acta.setDigitalizacionEscrutinio(actaDto.getDigitalizacionEscrutinio());
        acta.setDigitalizacionInstalacionSufragio(actaDto.getDigitalizacionInstalacionSufragio());
        acta.setControlDigEscrutinio(actaDto.getControlDigEscrutinio());
        acta.setControlDigInstalacionSufragio(actaDto.getControlDigInstalacionSufragio());
        acta.setObservDigEscrutinio(actaDto.getObservDigEscrutinio());
        acta.setObservDigInstalacionSufragio(actaDto.getObservDigInstalacionSufragio());
        acta.setDigitacionHoras(actaDto.getDigitacionHoras());
        acta.setDigitacionVotos(actaDto.getDigitacionVotos());
        acta.setDigitacionObserv(actaDto.getDigitacionObserv());
        acta.setDigitacionFirmasAutomatico(actaDto.getDigitacionFirmasAutomatico());
        acta.setDigitacionFirmasManual(actaDto.getDigitacionFirmasManual());
        acta.setControlDigitacion(actaDto.getControlDigitacion());
        acta.setHoraEscrutinioAutomatico(actaDto.getHoraEscrutinioAutomatico());
        acta.setHoraEscrutinioManual(actaDto.getHoraEscrutinioManual());
        acta.setHoraInstalacionAutomatico(actaDto.getHoraInstalacionAutomatico());
        acta.setHoraInstalacionManual(actaDto.getHoraInstalacionManual());
        acta.setDescripcionObservAutomatico(actaDto.getDescripcionObservAutomatico());
        acta.setDescripcionObservManual(actaDto.getDescripcionObservManual());
        acta.setEscrutinioFirmaMm1Automatico(actaDto.getEscrutinioFirmaMm1Automatico());

        acta.setEscrutinioFirmaMm2Automatico(actaDto.getEscrutinioFirmaMm2Automatico());

        acta.setEscrutinioFirmaMm3Automatico(actaDto.getEscrutinioFirmaMm3Automatico());

        acta.setInstalacionFirmaMm1Automatico(actaDto.getInstalacionFirmaMm1Automatico());

        acta.setInstalacionFirmaMm2Automatico(actaDto.getInstalacionFirmaMm2Automatico());

        acta.setInstalacionFirmaMm3Automatico(actaDto.getInstalacionFirmaMm3Automatico());

        acta.setSufragioFirmaMm1Automatico(actaDto.getSufragioFirmaMm1Automatico());

        acta.setSufragioFirmaMm2Automatico(actaDto.getSufragioFirmaMm2Automatico());

        acta.setSufragioFirmaMm3Automatico(actaDto.getSufragioFirmaMm3Automatico());


        Archivo archivoEscrutinio = null;
        Archivo archivoSufragio = null;

        if (actaDto.getContentArchivoEscrutinio() != null) {
            archivoEscrutinio = this.getArchivo(actaDto.getContentArchivoEscrutinio(), actaDto.getFormatoArchivoEscrutinio());
            archivoEscrutinio.setActivo(SceConstantes.ACTIVO);
            archivoEscrutinio.setFechaCreacion(new Date());
            archivoEscrutinio.setUsuarioCreacion(actaDto.getAudUsuarioCreacion());
            acta.setArchivoEscrutinio(archivoEscrutinio);
        } // end-if


        if (actaDto.getContentArchivoInstalacionSufragio() != null) {
            archivoSufragio = this.getArchivo(actaDto.getContentArchivoInstalacionSufragio(), actaDto.getFormatoArchivoInstalacionSufragio());
            archivoSufragio.setActivo(SceConstantes.ACTIVO);
            archivoSufragio.setFechaCreacion(new Date());
            archivoSufragio.setUsuarioCreacion(actaDto.getAudUsuarioCreacion());
            acta.setArchivoInstalacionSufragio(archivoSufragio);
        } // end-if


        Set<DetActa> detalles = null;
        if (actaDto.getDetalleActas() != null) {
            detalles = new HashSet<>();
            DetActa detalle = null;
            for (DetActaDto detDto : actaDto.getDetalleActas()) {
                if (detDto.getId() != null) {
                    detalle = detActaRepository.getOne(detDto.getId());
                    detalle.setActivo(SceConstantes.ACTIVO);
                    detalle.setFechaModificacion(new Date());
                    detalle.setUsuarioModificacion(detDto.getAudUsuarioModificacion());
                } else {
                    detalle = new DetActa();
                    detalle.setActivo(SceConstantes.ACTIVO);
                    detalle.setFechaCreacion(new Date());
                    detalle.setUsuarioCreacion(detDto.getAudUsuarioCreacion());
                }
                detalle.setActa(acta);
                detalle.setIdAgrupacionPolitica(detDto.getIdAgrupacionPolitica());
                detalle.setPosicion(detDto.getPosicion());
                detalle.setVotos(detDto.getVotos());
                detalle.setEstadoErrorMaterial(detDto.getEstadoErrorMaterial());
                detalle.setIlegible(detDto.getIlegible());
                detalles.add(detalle);
            } // end-for
        } // end-if */
        acta.setDetalles(detalles);
        actaRepository.save(acta);
        actaDto.setId(acta.getId());

        // add historial

        ActaHistorial actaHistorial = new ActaHistorial();
        actaHistorial.setActa(acta);
        actaHistorial.setMesa(mesa);
        actaHistorial.setUbigeoEleccion(eubigeo);
        actaHistorial.setNumeroCopia(actaDto.getNumeroCopia());
        actaHistorial.setNumeroLote(actaDto.getNumeroLote());
        actaHistorial.setTipoLote(actaDto.getTipoLote());
        actaHistorial.setElectoresHabiles(actaDto.getElectoresHabiles());
        actaHistorial.setCvas(actaDto.getCvas());
        actaHistorial.setVotosCalculados(actaDto.getVotosCalculados());
        actaHistorial.setTotalVotos(actaDto.getTotalVotos());
        actaHistorial.setEstadoActa(actaDto.getEstadoActa());
        actaHistorial.setEstadoCc(actaDto.getEstadoCc());
        actaHistorial.setEstadoActaResolucion(actaDto.getEstadoActaResolucion());
        actaHistorial.setEstadoDigitalizacion(actaDto.getEstadoDigitalizacion());
        actaHistorial.setEstadoErrorMaterial(actaDto.getEstadoErrorMaterial());
        actaHistorial.setDigitalizacionEscrutinio(actaDto.getDigitalizacionEscrutinio());
        actaHistorial.setDigitalizacionInstalacionSufragio(actaDto.getDigitalizacionInstalacionSufragio());
        actaHistorial.setControlDigEscrutinio(actaDto.getControlDigEscrutinio());
        actaHistorial.setControlDigInstalacionSufragio(actaDto.getControlDigInstalacionSufragio());
        actaHistorial.setObservDigEscrutinio(actaDto.getObservDigEscrutinio());
        actaHistorial.setObservDigInstalacionSufragio(actaDto.getObservDigInstalacionSufragio());
        actaHistorial.setDigitacionHoras(actaDto.getDigitacionHoras());
        actaHistorial.setDigitacionVotos(actaDto.getDigitacionVotos());
        actaHistorial.setDigitacionObserv(actaDto.getDigitacionObserv());
        actaHistorial.setDigitacionFirmasAutomatico(actaDto.getDigitacionFirmasAutomatico());
        actaHistorial.setDigitacionFirmasManual(actaDto.getDigitacionFirmasManual());
        actaHistorial.setControlDigitacion(actaDto.getControlDigitacion());
        actaHistorial.setHoraEscrutinioAutomatico(actaDto.getHoraEscrutinioAutomatico());
        actaHistorial.setHoraEscrutinioManual(actaDto.getHoraEscrutinioManual());
        actaHistorial.setHoraInstalacionAutomatico(actaDto.getHoraInstalacionAutomatico());
        actaHistorial.setHoraInstalacionManual(actaDto.getHoraInstalacionManual());
        actaHistorial.setDescripcionObservAutomatico(actaDto.getDescripcionObservAutomatico());
        actaHistorial.setDescripcionObservManual(actaDto.getDescripcionObservManual());
        actaHistorial.setEscrutinioFirmaMm1Automatico(actaDto.getEscrutinioFirmaMm1Automatico());

        actaHistorial.setEscrutinioFirmaMm2Automatico(actaDto.getEscrutinioFirmaMm2Automatico());

        actaHistorial.setEscrutinioFirmaMm3Automatico(actaDto.getEscrutinioFirmaMm3Automatico());

        actaHistorial.setInstalacionFirmaMm1Automatico(actaDto.getInstalacionFirmaMm1Automatico());

        actaHistorial.setInstalacionFirmaMm2Automatico(actaDto.getInstalacionFirmaMm2Automatico());

        actaHistorial.setInstalacionFirmaMm3Automatico(actaDto.getInstalacionFirmaMm3Automatico());

        actaHistorial.setSufragioFirmaMm1Automatico(actaDto.getSufragioFirmaMm1Automatico());

        actaHistorial.setSufragioFirmaMm2Automatico(actaDto.getSufragioFirmaMm2Automatico());

        actaHistorial.setSufragioFirmaMm3Automatico(actaDto.getSufragioFirmaMm3Automatico());


        if (archivoEscrutinio != null) {
            actaHistorial.setArchivoEscrutinio(archivoEscrutinio);
        } // end-if

        if (archivoSufragio != null) {
            actaHistorial.setArchivoInstalacionSufragio(archivoSufragio);
        }

        Set<DetActaHistorial> detallesHist = null;
        if (actaDto.getDetalleActas() != null) {
            detallesHist = new HashSet<>();
            DetActaHistorial detalleHist = null;
            for (DetActaDto detDto : actaDto.getDetalleActas()) {
                detalleHist = new DetActaHistorial();
                detalleHist.setActivo(SceConstantes.ACTIVO);
                detalleHist.setFechaCreacion(new Date());
                detalleHist.setUsuarioCreacion(detDto.getAudUsuarioCreacion());
                detalleHist.setActaHistorial(actaHistorial);
                detalleHist.setIdAgrupacionPolitica(detDto.getIdAgrupacionPolitica());
                detalleHist.setPosicion(detDto.getPosicion());
                detalleHist.setVotos(detDto.getVotos());
                detalleHist.setEstadoErrorMaterial(detDto.getEstadoErrorMaterial());
                detalleHist.setIlegible(detDto.getIlegible());
                detallesHist.add(detalleHist);
            } // end-for
        } // end-if */
        actaHistorial.setDetalles(detallesHist);
        actaHistorialRepository.save(actaHistorial);

        return actaDto;
    }

    public Archivo getArchivo(String base64String, String formato) throws FileNotFoundException {
        Archivo archivo = new Archivo();
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        UUID uuid = UUID.randomUUID();
        String filename = uuid.toString();
        String filenameExt = filename + "." + formato;
        String ruta = this.ubicacionFile + filenameExt;
        archivo.setPeso(SceUtils.formatBytes(decodedBytes.length));
        archivo.setNombre(formato);
        archivo.setNombre(filenameExt);
        archivo.setNombreOriginal(filenameExt);
        archivo.setRuta(ruta);
        archivo.setFormato(formato);
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.ubicacionFile + filename + "." + formato)) {
            fileOutputStream.write(decodedBytes);
        } catch (IOException e) {
            logger.info("Se inicio el registro de la transmision! {}",e.getMessage());
        }
        return archivo;
    }

    @Override
    public List<AvanceMesaReporteDto> getAvanceMesa(FiltroAvanceMesaDto filtro) {
        String schema = filtro.getSchema();
        Integer idEleccion = filtro.getIdEleccion();
        Integer idAmbito = Math.toIntExact(filtro.getIdAmbito());
        Integer idCentroComputo = Math.toIntExact(filtro.getIdCentroComputo());
        String ubigeo = obtenerUbigeo(filtro.getDepartamento(),filtro.getProvincia(),filtro.getDistrito());
        String mesa = StringUtils.isBlank(filtro.getMesa()) ? "" : filtro.getMesa();

        return actaRepository.avanceMesaMesa(schema, idEleccion, idAmbito, idCentroComputo, ubigeo, mesa, "prueba")
                .stream().map(reporte -> {
                    AvanceMesaReporteDto mesaReporte = new AvanceMesaReporteDto();
                    BigInteger idActa = (BigInteger) reporte.get("n_acta_pk");
                    mesaReporte.setIdActa(Long.valueOf(String.valueOf(idActa)));
                    mesaReporte.setProceso("sce_nacion_eg2025");
                    mesaReporte.setEleccion("sce_nacion_eg2025");

                    mesaReporte.setAmbito((String) reporte.get("c_nombre_ambito_electoral"));
                    mesaReporte.setCentroComputo((String) reporte.get("c_nombre_centro_computo"));
                    mesaReporte.setDepartamento((String) reporte.get("c_nombre_ubigeo_nivel_01"));
                    mesaReporte.setProvincia((String) reporte.get("c_nombre_ubigeo_nivel_02"));
                    mesaReporte.setDistrito((String) reporte.get("c_nombre_ubigeo_nivel_03"));
                    mesaReporte.setLocalVotacion((String) reporte.get("c_nombre_local_votacion"));
                    mesaReporte.setNumeroMesa((String) reporte.get("c_mesa"));

                    mesaReporte.setFechaHora(DateUtil.getDateString((Date) reporte.get("d_aud_fecha_verificador"), SceConstantes.FORMATO_FECHA));
                    mesaReporte.setCopia((String) reporte.get("c_numero_copia"));
                    String cantidad = (String) reporte.get("c_votos");
                    mesaReporte.setVerificador((String) reporte.get("c_aud_usuario_verificador"));

                    mesaReporte.setCvas((String) reporte.get("n_cvas"));
                    mesaReporte.setCantidadElectoresHabiles((Long) reporte.get("n_electores_habiles"));
                    mesaReporte.setEstado((String) reporte.get("c_descripcion_estado_acta"));

                    mesaReporte.setAgrupacionPolitica((String) reporte.get("c_descripcion_agrupacion_politica"));
                    mesaReporte.setIdAgrupacionPolitica((Integer) reporte.get("n_agrupacion_politica_pk"));
                    Short posi = (Short) reporte.get("n_posicion");
                    mesaReporte.setPosicion(Long.valueOf(posi));
                    mesaReporte.setVotos(cantidad);
                    mesaReporte.setTotalVotos(cantidad);
                    return mesaReporte;
                }).sorted(Comparator
                        .comparing(AvanceMesaReporteDto::getIdActa)
                        .thenComparing(AvanceMesaReporteDto::getPosicion))
                .collect(Collectors.toList());

    }
    
    private String obtenerUbigeo(String ubigeoNivelUno,String ubigeoNivelDos,String ubigeoNivelTres) {
        String ubigeRetorno = ConstantesReportes.CODIGO_UBIGEO_NACION;
        if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelTres)) {
            ubigeRetorno = ubigeoNivelTres;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelDos)) {
            ubigeRetorno = ubigeoNivelDos;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelUno)) {
            ubigeRetorno = ubigeoNivelUno;
        }
        return ubigeRetorno;
    }

    // para monitoreo
    @Override
    @Transactional(value = "locationTransactionManager", readOnly = true)
    public List<Acta> listActa(FiltrosActaNacionRequestDto filtros) {
        return this.actaRepository.findAll();
    }

    @Override
    public String transmisionRecepcionDto(Long id) {

        List<TransmisionRecepcion> transmisionRecepcionList = this.transmisionRecepcionRepository.findByRecepcionStae(id);

        if(!transmisionRecepcionList.isEmpty()){

            TransmisionRecepcion transmisionRecepcion = transmisionRecepcionList.get(0);
            TransmisionRecepcionDto transmisionRecepcionDto = new TransmisionRecepcionDto();
            transmisionRecepcionDto.setTramaDato(transmisionRecepcion.getTramaDato());
            transmisionRecepcionDto.setEstado(transmisionRecepcion.getEstado());
            transmisionRecepcionDto.setIdActa(transmisionRecepcion.getActa());

            return transmisionRecepcionDto.getTramaDato();


        }else {
            return null;
        }
    }

    @Override
    @Transactional(value = "locationTransactionManager", readOnly = true)
    public ReturnMonitoreoActas listActasMonitoreo(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda) {

        Long idProceso = monitoreoNacionBusqueda.getIdProceso();
        Long idEleccion = monitoreoNacionBusqueda.getIdEleccion();
        Long idUbigeo = monitoreoNacionBusqueda.getIdUbigeo();
        Long idLocalVotacion = monitoreoNacionBusqueda.getIdLocal();
        Long idDepartamento = monitoreoNacionBusqueda.getIdDepartamento();
        Long idProvincia = monitoreoNacionBusqueda.getIdProvincia();
        String mesa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getMesa());
        String grupoActa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getGrupoActa(),ConstantesMonitoreo.TODOS);

        ReturnMonitoreoActas response = new ReturnMonitoreoActas();

        Pageable paging = PageRequest.of(monitoreoNacionBusqueda.getPageIndex(), monitoreoNacionBusqueda.getCantidadPorpagina());
        List<VwActaMonitoreo> actas = vwActaMonitoreoRepository.buscarMonitoreoNacion(idProceso, 
        		idEleccion, 
        		idDepartamento, 
        		idProvincia, 
        		idUbigeo, 
        		idLocalVotacion, 
        		mesa, 
        		grupoActa,
        		paging);

        logger.info("se inicia el resumen de estadistica");
        logger.info("idProceso: {}", idProceso);
        logger.info("idEleccion: {}", idEleccion);
        logger.info("idDepartamento: {}", idDepartamento);
        logger.info("idProvincia: {}", idProvincia);
        logger.info("idUbigeo: {}", idUbigeo);
        logger.info("idLocalVotacion: {}", idLocalVotacion);
        logger.info("mesa: {}", mesa);
        
        Long cantidadNormales = 0L;
        Long cantidadObservadas = 0L;
        Long cantidadEnviadasJne = 0L;
        Long cantidadDevueltasJne = 0L;

        if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.CONTABILIZADAS)){
        	cantidadNormales = this.actaRepository.getTotalNormales(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
        }
        
        if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.OBSERVADAS)){
        	cantidadObservadas = this.actaRepository.getTotalObservadas(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
        }
        
        if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.ENVIADAS_JNE)){
        	cantidadEnviadasJne = this.actaRepository.getTotalEnviadasJne(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
        }
        
        if(grupoActa==null || grupoActa.equalsIgnoreCase(ConstantesMonitoreo.DEVUELTAS_JNE)){
        	cantidadDevueltasJne = this.actaRepository.getTotalDevueltasJne(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa);
        }

        Long total = cantidadNormales + cantidadObservadas + cantidadEnviadasJne + cantidadDevueltasJne;
       
        response.setTotalNormales(cantidadNormales.toString());
        response.setTotalObservadas(cantidadObservadas.toString());
        response.setTotalEnviadasJne(cantidadEnviadasJne.toString());
        response.setTotalDevueltasJne(cantidadDevueltasJne.toString());
        response.setTotal(total.toString());
        
        logger.info("total normales: {}", response.getTotalNormales());
        logger.info("total observadas: {}", response.getTotalObservadas());
        logger.info("total enviadas jne: {}", response.getTotalEnviadasJne());
        logger.info("total devueltas jne: {}", response.getTotalDevueltasJne());
        logger.info("total cantidad: {}", response.getTotal());

        Long indiceInicio = (long) (monitoreoNacionBusqueda.getPageIndex() * monitoreoNacionBusqueda.getCantidadPorpagina());
        
        List<MonitoreoListActaItem> actaItemList = actas.stream()
                .map(acta -> {
                	
                	String imagenEscrutinio = "";
                    String imagenInstalacion = "";
                    String imagenSufragio = "";
                    String imagenInstalacionSufragio = "";
                	
                	if(acta.getIdArchivoEscrutinioPdf()!=null){
                		imagenEscrutinio = acta.getIdArchivoEscrutinioPdf().toString();
                	} else if(acta.getIdArchivoEscrutinioFirmado()!=null) {
                		imagenEscrutinio = acta.getIdArchivoEscrutinioFirmado().toString();
                	}
                	
                	if(acta.getIdArchivoSufragioFirmado()!=null){
                		imagenSufragio = acta.getIdArchivoSufragioFirmado().toString();
                	}
                	
                	if(acta.getIdArchivoInstalacionFirmado()!=null){
                		imagenInstalacion = acta.getIdArchivoInstalacionFirmado().toString();
                	}
                	
                	if(acta.getIdArchivoInstalacionSufragioPdf()!=null){
                		imagenInstalacionSufragio = acta.getIdArchivoInstalacionSufragioPdf().toString();
                	}else if(acta.getIdArchivoInstalacionSufragioFirmado()!=null){
                		imagenInstalacionSufragio = acta.getIdArchivoInstalacionSufragioFirmado().toString();
                	}
                	
                    return MonitoreoListActaItem.builder()
                            .actaId(acta.getId())
                            .verActa(acta.getVerActa())
                            .grupoActa(acta.getGrupoActa())
                            .nroActa(acta.getCodigoCompleto())
                            .estado(acta.getEstadoActa())
                            .estadoDescripcion(ConstantesComunes.ESTADOS_ACTAS.get(acta.getEstadoActa()))
                            .mesa(Objects.isNull(acta.getMesaCodigo()) ? "" : acta.getMesaCodigo())
                            .fecha(acta.getFechaModificacionCc()!=null ? DateUtil.getDateString(acta.getFechaModificacionCc(),ConstanteTransmision.FORMATO_FECHA_MONITEREO) : "")
                            .imagenEscrutinio(imagenEscrutinio)
                            .imagenInstalacion(imagenInstalacion)
                            .imagenSufragio(imagenSufragio)
                            .imagenInstalacionSufragio(imagenInstalacionSufragio)
                            .build();
                })
                .collect(Collectors.toList());

        long x = indiceInicio;

        for (int i = 0; i < actaItemList.size(); i++){
            actaItemList.get(i).setNro(x+1);
            x++;
        }

        response.setListActaItems(actaItemList);



        return response;

    }

    @Override
    @Transactional(value = "locationTransactionManager", readOnly = true)
    public PaginacionDetalleDto informacionPaginacion(MonitoreoNacionBusquedaDto monitoreoNacionBusqueda, Integer resultadoPorPagina) {
        Long idProceso = monitoreoNacionBusqueda.getIdProceso();
        Long idEleccion = monitoreoNacionBusqueda.getIdEleccion();
        Long idUbigeo = monitoreoNacionBusqueda.getIdUbigeo();
        Long idLocalVotacion = monitoreoNacionBusqueda.getIdLocal();
        Long idDepartamento = monitoreoNacionBusqueda.getIdDepartamento();
        Long idProvincia = monitoreoNacionBusqueda.getIdProvincia();
        String mesa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getMesa());
        String grupoActa = StringCustomUtils.emptyToNull(monitoreoNacionBusqueda.getGrupoActa(),ConstantesMonitoreo.TODOS);

        PaginacionDetalleDto response = new PaginacionDetalleDto();

        Integer total = vwActaMonitoreoRepository.getCantidadRegistros(idProceso, idEleccion, idDepartamento, idProvincia, idUbigeo, idLocalVotacion, mesa, grupoActa);
        response.setTotalRegistro(total);
        response.setCantidadPagina(resultadoPorPagina);
        int numeroDePaginas = total / resultadoPorPagina;

        if (total % resultadoPorPagina != 0) {
            numeroDePaginas++;
        }
        response.setPaginas(numeroDePaginas);
        return response;
    }

    @Override
    public GenericResponse trazabilidadActa(String mesaCopiaDigito) {

        /*
        String nroMesa = mesaCopiaDigito.substring(0, 6);
        String copia = mesaCopiaDigito.substring(6, 8);
        String digito = mesaCopiaDigito.substring(8,9);*/

        return null;
    }



}
