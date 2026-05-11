package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.OrganizacionPoliticaReporteDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.OrganizacionPoliticaService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.JasperReportUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

@Service
public class OrganizacionPoliticaServiceImpl implements OrganizacionPoliticaService {

	Logger logger = LoggerFactory.getLogger(OrganizacionPoliticaServiceImpl.class);
	
	private final AgrupacionPoliticaRepository agrupacionPoliticaRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public OrganizacionPoliticaServiceImpl(AgrupacionPoliticaRepository agrupacionPoliticaRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.agrupacionPoliticaRepository = agrupacionPoliticaRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] reporteOrganizacionesPoliticas(FiltroOrganizacionesPoliticasDto filtro) {

		byte[] pdf = null;

		try {
			TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
			TypedParameterValue idcentroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());

			List<Map<String, Object>> organizacionesMap = agrupacionPoliticaRepository
					.organizacionesPoliticas(filtro.getSchema(), idEleccion, idcentroComputo);

			if (organizacionesMap.isEmpty() ) {
				logger.info("No se encontraron organizaciones políticas para el filtro proporcionado: {}", filtro);
				return pdf; // Retorna null o un PDF vacío si no hay datos
			}

			List<OrganizacionPoliticaReporteDto> organizaciones = organizacionesMap
					.parallelStream()
					.map( reporte -> {
						return OrganizacionPoliticaReporteDto
								.builder()
								.eleccion((String) reporte.get("c_tipo_eleccion"))
								.nombreEleccion((String) reporte.get("c_nombre_eleccion"))
								.departamento((String) reporte.get("c_departamento"))
								.provincia((String) reporte.get("c_provincia"))
								.distrito((String) reporte.get("c_distrito"))
								.agrupacionPolitica((String) reporte.get("c_organicacion_politica"))
								.codigoAgrupol((String) reporte.get("c_codigo_agrupacion_politica"))
								.codigoUbigeo((String) reporte.get("c_codigo_ubigeo"))
								.proceso(filtro.getProceso().toUpperCase())
								.build();
					}).toList();

			Map<String, Object> parametros = new java.util.HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
			parametros.put("logo_onpe", imagen);
			parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
			parametros.put("version", utilSceService.getVersionSistema());
			parametros.put("servidor", InetAddress.getLocalHost().getHostName());
			parametros.put("usuario", filtro.getUsuario());

			if(filtro.getIdCentroComputo() == 0) {
				parametros.put("centroComputo", ConstantesComunes.CC_NACION_DESCRIPCION);
			} else {
				parametros.put("centroComputo", organizacionesMap.getFirst().get("c_codigo_centro_computo") + " - " + organizacionesMap.getFirst().get("c_descripcion_centro_computo") );
			}

			InputStream file = this.getClass().getClassLoader().getResourceAsStream(
					ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.ORGANIZACIONES_POLITICAS);

				this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(),
						"Se consultó el Reporte Organizaciones Políticas",
						organizacionesMap.getFirst().get("c_codigo_centro_computo").toString(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
						ConstantesComunes.LOG_TRANSACCIONES_ACCION);

			if (organizaciones != null && !organizaciones.isEmpty()) {
				JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, organizaciones, file);
				JRPdfExporter jrPdfExporter = new JRPdfExporter();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
				jrPdfExporter.exportReport();
				pdf = byteArrayOutputStream.toByteArray();
				return pdf;
			} else {
				return pdf;
			}

		} catch (Exception e) {
			logger.error("Error al generar reporte de organizaciones politicas", e);
			return pdf;
		}

	}

}

