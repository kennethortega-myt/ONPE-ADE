package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.utils.JasperReportUtil;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ActasDigitalizadasReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroActasDigitalizadasDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ReporteActasDigitalizadasService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReporteActasDigitalizadasServiceImpl implements ReporteActasDigitalizadasService {

	Logger logger = LoggerFactory.getLogger(ReporteActasDigitalizadasServiceImpl.class);
	
	private final ActaRepository actasRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public ReporteActasDigitalizadasServiceImpl(ActaRepository actasRepository, 
			UtilSceService utilSceService, ITabLogService logService) {
		this.actasRepository = actasRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] reporteActasDigitalizadas(FiltroActasDigitalizadasDto filtro) {
		byte[] pdf = null;
		
		try {
			List<ActasDigitalizadasReporteDto> actasLista = getListaActasDigitalizadas(filtro);
			
			Map<String, Object> parametros = new java.util.HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("nombreCortoEleccion", filtro.getEleccion());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("odpe", filtro.getOdpe());

            String nombreReporte = ConstantesComunes.REPORTE_ACTAS_DIGITALIZADAS;

			this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					this.getClass().getSimpleName(), "Se consultó el Reporte de Actas digitalizadas.",
					filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), actasLista, nombreReporte, parametros);

		} catch (Exception e) {
			logger.error(e.getMessage());
            return pdf;
        }
	}
	
	@Override
	public byte[] reporteActasDigitalizadasExcel(FiltroActasDigitalizadasDto filtro) {
		byte[] excel = null;
		
		try {
			List<ActasDigitalizadasReporteDto> actasLista = getListaActasDigitalizadas(filtro);
			
			Map<String, Object> parametros = new java.util.HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("nombreCortoEleccion", filtro.getEleccion());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("odpe", filtro.getOdpe());

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.REPORTE_ACTAS_DIGITALIZADAS_EXCEL);
            
            if (actasLista != null && !actasLista.isEmpty()) {
                JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, actasLista, file);
                JRXlsxExporter exporter = new JRXlsxExporter();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                
                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                configuration.setWhitePageBackground(false);
                configuration.setShowGridLines(true);
                
                exporter.setConfiguration(configuration);
                
                exporter.exportReport();
                excel = byteArrayOutputStream.toByteArray();
                
            }

            return excel;
            
		} catch (Exception e) {
			logger.error(e.getMessage());
            return excel;
        }
	}
	
	private List<ActasDigitalizadasReporteDto> getListaActasDigitalizadas(FiltroActasDigitalizadasDto filtro) {
		TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
		TypedParameterValue idCC = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());
		TypedParameterValue idOdpe = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdOdpe());
		TypedParameterValue fechaInicial = new TypedParameterValue(StandardBasicTypes.TIMESTAMP, filtro.getFechaInicial());
		TypedParameterValue fechaFin = new TypedParameterValue(StandardBasicTypes.TIMESTAMP, filtro.getFechaFin());
		
		List<Map<String, Object>> actasMap = actasRepository.actasDigitalizadas(filtro.getEsquema(), 
				idEleccion, idOdpe, idCC, fechaInicial, fechaFin);
		
		return actasMap
				.parallelStream()
				.map( reporte -> {
					String ubigeo = (String)reporte.get("c_codigo_ubigeo");
					
					return ActasDigitalizadasReporteDto
							.builder()
							.numActa((String) reporte.get("c_mesa"))
							.copiaActa((String) reporte.get("c_numero_copia"))
							.digitoChequeo((String) reporte.get("c_digito_chequeo_escrutinio"))
							.codDepartamento(ubigeo.substring(0, 2))
							.descDepartamento((String) reporte.get("c_departamento"))
							.codProvincia(ubigeo.substring(2, 4))
							.descProvincia((String) reporte.get("c_provincia"))
							.codDistrito(ubigeo.substring(4, 6))
							.descDistrito((String) reporte.get("c_distrito"))
							.localVotacion((String) reporte.get("c_codigo_local_votacion") + " " + reporte.get("c_nombre_local_votacion"))
							.fechaDigtal( (Date) reporte.get("d_fecha_accion_digitacion"))
							.codCentroComputo((String)reporte.get("c_codigo_centro_computo"))
							.centroComputo((String)reporte.get("c_nombre_centro_computo"))
							.ubigeo(ubigeo)
							.build();
				}).toList();
	}
	
}
