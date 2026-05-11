package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.FiltroPuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroResponseDto;
import pe.gob.onpe.scebackend.model.dto.ResponseAutorizacionDTO;
import pe.gob.onpe.scebackend.model.dto.reportes.BaseReporteParams;
import pe.gob.onpe.scebackend.model.orc.entities.*;
import pe.gob.onpe.scebackend.model.orc.repository.*;
import pe.gob.onpe.scebackend.model.repository.Procedures;
import pe.gob.onpe.scebackend.model.service.*;
import pe.gob.onpe.scebackend.model.service.comun.IAmbitoElectoralService;
import pe.gob.onpe.scebackend.model.stae.dto.pc.DataReportePcDto;
import pe.gob.onpe.scebackend.model.stae.dto.pc.PuestaCeroResponse;
import pe.gob.onpe.scebackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.scebackend.utils.JsonUtils;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.LoggingUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesParametros;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PuestaCeroServiceImpl implements IPuestaCeroService {


    public static final String CAMPO_1 = "campo1";
    public static final String CAMPO_2 = "campo2";
    public static final String CAMPO_3 = "campo3";
    public static final String CAMPO_4 = "campo4";
    public static final String CAMPO_5 = "campo5";
    public static final String CAMPO_6 = "campo6";
    public static final String CAMPO_7 = "campo7";
    Logger logger = LogManager.getLogger(PuestaCeroServiceImpl.class);

    private static final String CODIGO_ELECTORAL = "c_codigo_eleccion";
    private static final String COLUMN_N_TOTAL_MESAS = "n_total_mesas";
    private static final String NOMBRE_ELECCION = "c_nombre_eleccion";
    private static final String NOMBRE_SERVIDOR = "c_nombre_servidor";
    private static final String PUESTA_CERO = "reportePuestaCero";

    private static final String PUESTA_CERO_TITULO_REPORTE = "Reporte de Puesta a Cero";

    private static final String PUESTA_STAE_CERO_TITULO_REPORTE = "Reporte de Puesta a Cero STAE";

    private static final String PUESTA_VD_CERO_TITULO_REPORTE = "Reporte de Puesta a Cero Voto Digital";

    private final ProcesoElectoralRepository procesoElectoralRepository;

    private final ICentroComputoService centroComputoService;

    private final IAmbitoElectoralService ambitoElectoralService;

    private final Procedures procedures;

    private final ITabLogTransaccionalService logTransaccionalService;

    private final UtilSceService utilSceService;

    private final PuestaCeroPrService puestaCeroPr;

    private final ITabAutorizacionService autorizacionService;

    private final PuestaCeroRepository puestaCeroRepository;

    private final IFileStorageService fileStorageService;
    
	private final PuestaCeroPrRepository puestaCeroPrRepository;

    private final IDetParametroService  detParametroService;


  @Override
  @Transactional(transactionManager = "locationTransactionManager")
  public PuestaCeroResponseDto puestaCero(String esquema, Integer idCentroComputo, String usuario, Integer resultado, String acronimo) {
      PuestaCeroResponseDto responseDto = new PuestaCeroResponseDto();
      int autorizacion = 0;
      String mensaje = "Se realizó la puesta a cero en nación.";
      try {
          Optional<ProcesoElectoral> optional = this.procesoElectoralRepository.findByAcronimo(acronimo);
          if (optional.isPresent()) {
              ResponseAutorizacionDTO responseAutorizacionDTO = this.autorizacionService.verificarAutorizacionv2(optional.get().getFechaConvocatoria(), usuario, ConstantesAutorizacion.TIPO_AUTORIZACION_PUESTO_CERO,
                      "la Puesta a cero", String.format("Solicitud para la Puesta a cero por el usuario %s", usuario));
              if (!responseAutorizacionDTO.isContinue()) {
                  responseDto.setSuccess(Boolean.FALSE);
                  responseDto.setMessage(responseAutorizacionDTO.getMessage());
                  return responseDto;
              }
              if (responseAutorizacionDTO.isAutorizado() && responseAutorizacionDTO.getIdAutorizacion() != 0L) {
                  autorizacion = 1;
                  mensaje = String.format("Se realizó la puesta a cero con autorización Nación nro. %s.", responseAutorizacionDTO.getIdAutorizacion());
              }

          }
          boolean rpta = false;
          boolean rptaNacion = this.procedures.executeProcedurePuestaCero(esquema, usuario);
          boolean rptaPr = this.puestaCeroPr.puestoCeroPr(usuario, null);
          Long idPcStae = null;
          Long idPcVd = null;
          
          if (rptaNacion || (rptaPr && idPcStae!=null)) {
        	  rpta = true;
              eliminarArchivos(acronimo);
          }
          logger.info("rpta puesta cero nacion = {}", rpta);
          responseDto.setSuccess(rpta);
          responseDto.setIdPuestaCeroStae(idPcStae);
          responseDto.setPuestaCeroStae(idPcStae!=null);
          responseDto.setPuestaCeroPr(rptaPr);
          responseDto.setPuestaCeroVd(idPcVd!=null);
          responseDto.setPuestaCeroNacion(rptaNacion);
          CentroComputo centroComputoNacion = centroComputoService.getPadreNacion();
          AmbitoElectoral ambitoNacion = ambitoElectoralService.getPadreNacion();
          registrolog(usuario, mensaje, ambitoNacion.getCodigo(),centroComputoNacion.getCodigo(),autorizacion, "puestaCero");
          return responseDto;
      } catch (Exception e) {
          this.detParametroService.actualizarValor(ConstantesParametros.PUESTA_CERO_INACTIVA, usuario, ConstantesParametros.DET_PARAM_EJECUCION_PUESTA_CERO_NACION );
          throw new GenericException(e.getMessage());
      }
  }

  private void eliminarArchivos(final String acronimo){
      try {
          logger.info("Se eliminará todos los archivos de la subcarpeta {}", acronimo);
          this.fileStorageService.deleteAllByFolder(acronimo);
      } catch (Exception e) {
          logger.info("Error al limpiar los archivos de la subcarpeta {}", acronimo);
      }
  }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public byte[] reportePuestaCero(FiltroPuestaCeroDTO filtro, String centroComputo, String ncc, String usuario) {
        final String acronimoProceso = filtro.getAcronimo();
        final String esquema = filtro.getEsquema();

        try {
            log.debug("[START] Reporte puesta cero");
            this.detParametroService.actualizarValor(ConstantesParametros.PUESTA_CERO_INACTIVA, usuario, ConstantesParametros.DET_PARAM_EJECUCION_PUESTA_CERO_NACION );
            ProcesoElectoral proceso = obtenerProcesoElectoral(acronimoProceso);
            CentroComputo centroComputoNacion = centroComputoService.getPadreNacion();
            AmbitoElectoral ambitoNacion = ambitoElectoralService.getPadreNacion();
            List<PuestaCeroDTO> puestaCeroDTOS = obtenerDatosPuestaCero(esquema, usuario);
            List<Map.Entry<String, List<PuestaCeroDTO>>> gruposOrdenados = agruparYOrdenarPorServidor(puestaCeroDTOS);
            List<JasperPrint> prints = generarReportesNacion(gruposOrdenados, proceso, usuario);

            agregarReporteStaeSiAplica(filtro.getIdPuestaCeroStae(), proceso,
                    centroComputoNacion.getCodigo().concat(ConstantesComunes.GUION_MEDIO).concat(centroComputoNacion.getNombre()),
                    ambitoNacion.getCodigo().concat(ConstantesComunes.GUION_MEDIO).concat(ambitoNacion.getNombre()),
                    usuario, prints);
            agregarReporteVdSiAplica(filtro.getIdPuestaCeroVd(), proceso,
                    centroComputoNacion.getCodigo().concat(ConstantesComunes.GUION_MEDIO).concat(centroComputoNacion.getNombre()),
                    ambitoNacion.getCodigo().concat(ConstantesComunes.GUION_MEDIO).concat(ambitoNacion.getNombre()),
                    usuario, prints);

            byte[] pdf = exportarPdf(prints);
            registrolog(usuario, "Se generó el reporte de puesta a cero.", ambitoNacion.getCodigo(),centroComputoNacion.getCodigo(),0, PUESTA_CERO);

            log.debug("[FIN] Reporte puesta cero");
            return pdf;

        } catch (Exception e) {
            LoggingUtil.logTrace(PUESTA_CERO, "PuestaCeroServiceImpl.java",
                    Arrays.asList(centroComputo, ncc, usuario, acronimoProceso), null, true, e);
            return new byte[0];
        }
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public void activarBloqueo(String usuario) {
        try{
            log.debug("[START] bloqueo puesta cero nacion");
            this.detParametroService.actualizarValor(ConstantesParametros.PUESTA_CERO_ACTIVA, usuario, ConstantesParametros.DET_PARAM_EJECUCION_PUESTA_CERO_NACION );
            log.debug("[END] bloqueo puesta cero nacion");
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    private ProcesoElectoral obtenerProcesoElectoral(String acronimo) {
        return procesoElectoralRepository.findByAcronimo(acronimo)
                .orElseThrow(() -> new GenericException("No se encuentra registrado un proceso electoral"));
    }

    private List<PuestaCeroDTO> obtenerDatosPuestaCero(String esquema, String usuario) {
        List<PuestaCeroDTO> puestaCeroDTOS = new ArrayList<>();

        mapperDigitalizacion(puestaCeroRepository.reportePuestaCeroDigitalizacion(esquema, usuario), puestaCeroDTOS);
        mapperDigitacion(puestaCeroRepository.reportePuestaCeroDigitacion(esquema, usuario), puestaCeroDTOS);

        List<Map<String, Object>> listaOmisos = puestaCeroRepository.reportePuestaCeroOmisos(esquema, usuario);
        mapperRegistroMiembrosMesaEscrutinio(listaOmisos, puestaCeroDTOS);
        mapperOmisoVotante(listaOmisos, puestaCeroDTOS);
        mapperOmisoMiembrosMesa(listaOmisos, puestaCeroDTOS);
        mapperRegistroPersonero(listaOmisos, puestaCeroDTOS);

        return puestaCeroDTOS;
    }

    private List<Map.Entry<String, List<PuestaCeroDTO>>> agruparYOrdenarPorServidor(List<PuestaCeroDTO> lista) {
        Map<String, List<PuestaCeroDTO>> grupos = lista.stream()
                .collect(Collectors.groupingBy(PuestaCeroDTO::getServidor));

        grupos.values().forEach(l ->
                l.sort(Comparator
                        .comparing(PuestaCeroDTO::getOrden, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(PuestaCeroDTO::getCampo1, Comparator.nullsLast(String::compareTo)))
        );

        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
    }

    private List<JasperPrint> generarReportesNacion(List<Map.Entry<String, List<PuestaCeroDTO>>> grupos,
                                                    ProcesoElectoral proceso, String usuario) throws JRException {

        CentroComputo ccNacion = centroComputoService.getPadreNacion();
        AmbitoElectoral ambito = ambitoElectoralService.getPadreNacion();
        JasperReport reporte = generarReporteFile(ConstantesComunes.PUESTA_CERO_REPORT_JRXML);
        List<JasperPrint> prints = new ArrayList<>();

        for (var entry : grupos) {
            String servidor = entry.getKey();
            List<PuestaCeroDTO> datos = entry.getValue();

            Map<String, Object> params = crearParametrosReporte(proceso, usuario, servidor, ccNacion, ambito);

            prints.add(getJasperPrint(datos, reporte, params));
        }

        return prints;
    }

    private Map<String, Object> crearParametrosReporte(ProcesoElectoral proceso, String usuario, String servidor,
                                                       CentroComputo cc, AmbitoElectoral ambito) {
        Map<String, Object> params = new HashMap<>();
        params.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE,
                getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE));
        params.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE,
                getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE));
        params.put(ConstantesComunes.REPORT_PARAM_TITULO, proceso.getNombre().toUpperCase());
        params.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, PUESTA_CERO_TITULO_REPORTE.toUpperCase() + "\n\n" + servidor);
        params.put(ConstantesComunes.REPORT_PARAM_DESC_CC, cc.getCodigo() + ConstantesComunes.GUION_MEDIO + ConstantesComunes.CC_NACION_DESCRIPCION);
        params.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, ambito.getCodigo() + ConstantesComunes.GUION_MEDIO + ConstantesComunes.CC_NACION_DESCRIPCION);
        params.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(proceso.getId()));
        params.put(ConstantesComunes.REPORT_PARAM_USUARIO, usuario);
        params.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
        params.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, ConstantesComunes.PUESTA_CERO_REPORT_JRXML.replace(ConstantesComunes.EXTENSION_REPORTES_JASPER, ""));
        return params;
    }


    private void agregarReporteStaeSiAplica(Long id, ProcesoElectoral proceso, String codigoCcDescripcion,
                                            String codAmbitoDescripcion,String usuario, List<JasperPrint> prints) throws JRException {
        agregarReportePcSiAplica(
                new BaseReporteParams(
                        id, proceso, codigoCcDescripcion, codAmbitoDescripcion,
                        usuario, prints
                ), PUESTA_STAE_CERO_TITULO_REPORTE, "PuestaCeroSTAE");
    }

    private void agregarReporteVdSiAplica(Long id, ProcesoElectoral proceso, String codigoCcDescripcion,
                                          String codAmbitoDescripcion,String usuario, List<JasperPrint> prints) throws JRException {
        agregarReportePcSiAplica(new BaseReporteParams(
                id, proceso, codigoCcDescripcion, codAmbitoDescripcion,
                usuario, prints
        ), PUESTA_VD_CERO_TITULO_REPORTE, "PuestaCeroVD");
    }

    private void agregarReportePcSiAplica(
            BaseReporteParams base,
            String tituloReporte,
            String nombreReporte
    ) throws JRException {

        if (base.idPuestaCero() == null) return;

        Optional<PuestaCeroPr> pcOpt = puestaCeroPrRepository.findById(base.idPuestaCero());
        if (pcOpt.isEmpty()) return;

        PuestaCeroPr pc = pcOpt.get();
        if (pc.getRespuestaPcStae() == null || pc.getRespuestaPcStae().isEmpty()) return;

        PuestaCeroResponse response = JsonUtils.getPuestaCeroStaeResponse(pc.getRespuestaPcStae());
        if (response == null || response.getData() == null || response.getData().getData() == null) return;

        List<DataReportePcDto> data = response.getData().getData();
        if (data.isEmpty()) return;

        List<PuestaCeroDTO> datos = data.stream().map(puesta -> {
            PuestaCeroDTO dto = new PuestaCeroDTO();
            dto.setCampo1(puesta.getIdEleccion());
            dto.setCampo2(puesta.getNombreEleccion());
            dto.setCampo3(puesta.getActaProcesar());
            dto.setCampo4(puesta.getActaPendiente());
            dto.setCampo5(puesta.getActaProcesada());
            dto.setCampo6(puesta.getActaJEEPendiente());
            dto.setCampo7(puesta.getActaJEEResuelta());
            dto.setSuma7(puesta.getActaJEEPendienteResuelta());
            dto.setOrden(2);
            return dto;
        }).toList();

        Map<String, Object> parametros = new HashMap<>();
        InputStream imagen = getClass().getClassLoader()
                .getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE);
        InputStream pixelTransparente = getClass().getClassLoader()
                .getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE);

        parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
        parametros.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO, base.proceso().getNombre().toUpperCase());
        parametros.put(ConstantesComunes.REPORT_PARAM_TITULO_REPORTE, tituloReporte.toUpperCase());
        parametros.put(ConstantesComunes.REPORT_PARAM_DESC_CC, base.codigoCcDescripcion() );
        parametros.put(ConstantesComunes.REPORT_PARAM_DESC_ODPE, base.codAmbitoDescripcion());
        parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(base.proceso().getId()));
        parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, base.usuario());
        parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
        parametros.put(ConstantesComunes.REPORT_PARAM_NOMBRE_REPORTE, nombreReporte);

        JasperReport reporteFile = generarReporteFile(ConstantesComunes.PUESTA_CERO_STAE_REPORT_JRXML);
        JasperPrint print = getJasperPrint(datos, reporteFile, parametros);
        base.prints().add(print);
    }



    private byte[] exportarPdf(List<JasperPrint> prints) throws JRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        return baos.toByteArray();
    }



    private static JasperPrint getJasperPrint(List<PuestaCeroDTO> datos, JasperReport reporteCompilado, Map<String, Object> parametros) throws JRException {
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datos);
        return JasperFillManager.fillReport(reporteCompilado, parametros, dataSource);
    }

    private JasperReport generarReporteFile(final String nombreReporte) throws JRException {
        InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                ConstantesComunes.PATH_REPORT_PUESTA_CERO_JRXML + File.separator + nombreReporte);
        return JasperCompileManager.compileReport(file);
    }

    private void mapperDigitalizacion(List<Map<String, Object>> listaDigitalizacion, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero(
                listaDigitalizacion,
                puestaCeroDTOS,
                2,
                Map.of(CAMPO_1, CODIGO_ELECTORAL,
                        CAMPO_2, NOMBRE_ELECCION,
                        CAMPO_3, "n_total_actas",
                        CAMPO_4, "n_actas_a_digitalizar",
                        CAMPO_5, "n_actas_digitalizadas",
                        CAMPO_6, "n_total_resoluciones",
                        CAMPO_7, "n_resoluciones_digitalizadas"

                ),
                ConstantesComunes.VACIO,
                null
        );
    }

    private void mapperDigitacion(List<Map<String, Object>> listaDigitacion, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero(
                listaDigitacion,
                puestaCeroDTOS,
                4,
                Map.of(CAMPO_1, CODIGO_ELECTORAL,
                        CAMPO_2, NOMBRE_ELECCION,
                        CAMPO_3, "n_actas_a_procesar",
                        CAMPO_4, "n_actas_pendientes",
                        CAMPO_5, "n_actas_procesadas",
                        CAMPO_6, "n_actas_enviadas_por_resolver",
                        CAMPO_7, "n_actas_enviadas_resueltas"
                ),
                ConstantesComunes.VACIO,
                "n_actas_por_resolver_y_resueltas"
        );
    }

    private void mapperRegistroMiembrosMesaEscrutinio(List<Map<String, Object>> listaOmiso, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero (
                listaOmiso,
                puestaCeroDTOS,
                6,
                Map.of(
                        CAMPO_3, COLUMN_N_TOTAL_MESAS,
                        CAMPO_4, "n_total_mesas_registradas_me",
                        CAMPO_5, "n_total_me_registrados"
                ),
                ConstantesComunes.TXT_MIEMBRO_MESA_ESCRUTINIO,
                null
        );
    }

    private void mapperOmisoVotante(List<Map<String, Object>> listaOmiso, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero(
                listaOmiso,
                puestaCeroDTOS,
                7,
                Map.of(
                        CAMPO_3, COLUMN_N_TOTAL_MESAS,
                        CAMPO_4, "n_total_mesas_registradas_votantes",
                        CAMPO_5, "n_total_electores_habiles",
                        CAMPO_6, "n_total_omisos_votantes"
                ),
                ConstantesComunes.TXT_OMISOS_VOTANTES,
                null
        );
    }

    private void mapperOmisoMiembrosMesa(List<Map<String, Object>> listaOmiso, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero(
                listaOmiso,
                puestaCeroDTOS,
                7,
                Map.of(
                        CAMPO_3, COLUMN_N_TOTAL_MESAS,
                        CAMPO_4, "n_total_mesas_registradas_miembros_mesa",
                        CAMPO_5, "n_total_miembros_mesa",
                        CAMPO_6, "n_total_omisos_miembros_mesa"
                ),
                ConstantesComunes.TXT_OMISOS_MIEMBROS_MESA,
                null
        );
    }


    private void mapperRegistroPersonero(List<Map<String, Object>> listaOmiso, List<PuestaCeroDTO> puestaCeroDTOS) {
        mapearPuestaCero (
                listaOmiso,
                puestaCeroDTOS,
                8,
                Map.of(
                        CAMPO_3, COLUMN_N_TOTAL_MESAS,
                        CAMPO_4, "n_total_mesas_registradas_personeros",
                        CAMPO_5, "n_total_personeros_registrados"
                ),
                ConstantesComunes.TXT_PERSONEROS,
                null
        );
    }

    private void registrolog(String usuario, String mensaje,String ambito, String cc , int autorizacion, String metodo) {
        try {



            this.logTransaccionalService.registrarLog(usuario, metodo, "PuestaCeroServiceImpl", mensaje,
                    ambito, cc, autorizacion, 1);
        } catch (Exception e) {
            logger.info("Error al guardar el log");
        }
    }

    private void mapearPuestaCero(
            List<Map<String, Object>> origen,
            List<PuestaCeroDTO> destino,
            int orden,
            Map<String, String> campos,
            String tipoOmiso,
            String claveSuma7
    ) {
        for (Map<String, Object> fila : origen) {
            PuestaCeroDTO dto = new PuestaCeroDTO();
            dto.setOrden(orden);

            if (tipoOmiso.equals(ConstantesComunes.TXT_MIEMBRO_MESA_ESCRUTINIO)) {
                dto.setCampo1("1");
                dto.setCampo2(ConstantesComunes.TXT_OMISOS_MIEMBROS_MESA);
            }else if (tipoOmiso.equals(ConstantesComunes.TXT_OMISOS_VOTANTES)) {
                dto.setCampo1("2");
                dto.setCampo2(tipoOmiso);
            }else if (tipoOmiso.equals(ConstantesComunes.TXT_OMISOS_MIEMBROS_MESA)) {
                dto.setCampo1("3");
                dto.setCampo2(tipoOmiso);
            }else if (tipoOmiso.equals(ConstantesComunes.TXT_PERSONEROS)) {
                dto.setCampo1("4");
                dto.setCampo2(tipoOmiso);
            } else {
                dto.setCampo1((String) fila.get(CODIGO_ELECTORAL));
                dto.setCampo2((String) fila.get(NOMBRE_ELECCION));
            }

            // Campos numéricos
            dto.setCampo3(getIntCampo(fila, campos, CAMPO_3));
            dto.setCampo4(getIntCampo(fila, campos, CAMPO_4));
            dto.setCampo5(getIntCampo(fila, campos, CAMPO_5));
            dto.setCampo6(getIntCampo(fila, campos, CAMPO_6));
            dto.setCampo7(getIntCampo(fila, campos, CAMPO_7));

            // Suma7
            if (claveSuma7 != null) {
                dto.setSuma7(toInt(fila.get(claveSuma7)));
            }

            dto.setServidor((String) fila.get(NOMBRE_SERVIDOR));
            destino.add(dto);
        }
    }


    private Integer getIntCampo(Map<String, Object> fila, Map<String, String> campos, String claveCampo) {
        Object clave = campos.get(claveCampo);
        if (clave == null) return 0; // Evita NullPointerException si CAMPO_x no existe
        Object valor = fila.get(clave);
        return toInt(valor);
    }


    private int toInt(Object val) {
        return val != null ? ((Long) val).intValue() : 0;
    }


}
