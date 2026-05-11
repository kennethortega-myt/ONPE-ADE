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

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.scebackend.model.dto.OrganizacionPoliticaReporteDto;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.OrganizacionPoliticaService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;

@Service
public class OrganizacionPoliticaServiceImpl implements OrganizacionPoliticaService{

	Logger logger = LoggerFactory.getLogger(OrganizacionPoliticaServiceImpl.class);
	
	private final AgrupacionPoliticaRepository agrupacionPoliticaRepository;
	
	private final ITabLogTransaccionalService logService;
	
	private final UtilSceService utilSceService;
	
	public OrganizacionPoliticaServiceImpl(AgrupacionPoliticaRepository agrupacionPoliticaRepository, ITabLogTransaccionalService logService, UtilSceService utilSceService) {
		this.agrupacionPoliticaRepository = agrupacionPoliticaRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
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
            	parametros.put("centroComputo", organizacionesMap.get(0).get("c_codigo_centro_computo") + " - " + organizacionesMap.get(0).get("c_descripcion_centro_computo") );
            }

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.ORGANIZACIONES_POLITICAS);
            
        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        	"Se consultó el Reporte Organizaciones Políticas",
        			ConstantesComunes.CC_NACION_DESCRIPCION, "C56000", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
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

