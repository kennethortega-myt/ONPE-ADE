package pe.gob.onpe.scebackend.model.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pe.gob.onpe.scebackend.model.dto.AutoridadReporteDto;
import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.scebackend.model.orc.repository.CandidatoRepository;
import pe.gob.onpe.scebackend.model.service.AutoridadReporteService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class AutoridadReporteServiceImpl implements AutoridadReporteService {

    Logger logger = LoggerFactory.getLogger(AutoridadReporteServiceImpl.class);

    private static final String IMG_ONPE_JPG = "onpe.jpg";

    private final CandidatoRepository candidatoRepository;
    private final ITabLogTransaccionalService logService;
    private final UtilSceService utilSceService;

    public AutoridadReporteServiceImpl(CandidatoRepository candidatoRepository, ITabLogTransaccionalService logService,
            UtilSceService utilSceService) {
        this.candidatoRepository = candidatoRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
    }

    @Override
    public byte[] reporteAutoridadesEnConsulta(FiltroOrganizacionesPoliticasDto filtro) {
        try {
            List<AutoridadReporteDto> autoridades = obtenerAutoridades(filtro);
            if (autoridades.isEmpty()) {
                return new byte[0];
            }

            Map<String, Object> parametros = construirParametros(filtro);

            registrarLogReporte(filtro);

            return generarPdfAutoridades(parametros, autoridades);
        } catch (Exception e) {
            logger.error("Error al generar el reporte de autoridades en consulta", e);
            return new byte[0];
        }
    }

    private List<AutoridadReporteDto> obtenerAutoridades(FiltroOrganizacionesPoliticasDto filtro) {
        TypedParameterValue<Integer> idEleccion = new TypedParameterValue<>(StandardBasicTypes.INTEGER,
                filtro.getIdEleccion());

        TypedParameterValue<String> centroComputo = new TypedParameterValue<>(StandardBasicTypes.STRING,
                filtro.getCentroComputo());

        List<Map<String, Object>> autoridadesMap = candidatoRepository.autoridadesEnConsulta(filtro.getSchema(),
                idEleccion, centroComputo);

        if (autoridadesMap == null || autoridadesMap.isEmpty()) {
            return List.of();
        }

        return autoridadesMap.parallelStream().map(reporte -> AutoridadReporteDto.builder()
                .codigoEleccion(Integer.parseInt("" + reporte.get("c_tipo_eleccion")))
                .eleccion((String) reporte.get("c_nombre_eleccion"))
                .departamento((String) reporte.get("c_departamento")).provincia((String) reporte.get("c_provincia"))
                .distrito((String) reporte.get("c_distrito")).ubigeo((String) reporte.get("c_codigo_ubigeo"))
                .cargo((String) reporte.get("c_cargo")).apellidoPaterno((String) reporte.get("c_apellido_paterno"))
                .apellidoMaterno((String) reporte.get("c_apellido_materno"))
                .nombreCandidato((String) reporte.get("c_nombres")).build()).toList();
    }

    private Map<String, Object> construirParametros(FiltroOrganizacionesPoliticasDto filtro) {
        Map<String, Object> parametros = new java.util.HashMap<>();
        parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
        parametros.put("version", utilSceService.getVersionSistema());
        parametros.put("servidor", obtenerHostNameSeguro());
        parametros.put("usuario", filtro.getUsuario());
        parametros.put("tituloGeneral", filtro.getProceso());
        parametros.put("centroComputo", filtro.getCcDescripcion());
        return parametros;
    }

    private void registrarLogReporte(FiltroOrganizacionesPoliticasDto filtro) {
        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Autoridades en Consulta",
                ConstantesComunes.CC_NACION_DESCRIPCION, "C56000", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION);
    }

    private byte[] generarPdfAutoridades(Map<String, Object> parametros, List<AutoridadReporteDto> autoridades)
            throws JRException {
        String rutaImagen = ConstantesComunes.PATH_IMAGE_COMMON_NAC + IMG_ONPE_JPG;
        String rutaJrxml = ConstantesComunes.PATH_REPORT_JRXML + File.separator
                + ConstantesComunes.AUTORIDADES_EN_CONSULTA;

        try (InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(rutaImagen);
                InputStream file = this.getClass().getClassLoader().getResourceAsStream(rutaJrxml);
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (imagen == null) {
                throw new IllegalStateException("No se encontró recurso: " + rutaImagen);
            }
            if (file == null) {
                throw new IllegalStateException("No se encontró recurso: " + rutaJrxml);
            }

            parametros.put("logo_onpe", imagen);

            JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, autoridades, file);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            exporter.exportReport();

            return out.toByteArray();
        } catch (Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }

    private static String obtenerHostNameSeguro() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "N/A";
        }
    }
}
