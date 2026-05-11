package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleEstadoActasOdpe;
import pe.gob.onpe.scebackend.model.dto.reportes.EstadoActasOdpeReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroEstadoActasOdpeDto;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ActasRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ReporteEstadoActasOdpeService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class ReporteEstadoActasOdpeServiceImpl implements ReporteEstadoActasOdpeService{

	Logger logger = LoggerFactory.getLogger(ReporteEstadoActasOdpeServiceImpl.class);
	
	private final ActasRepository actasRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;
	
	public ReporteEstadoActasOdpeServiceImpl(ActasRepository actasRepository, ITabLogTransaccionalService logService,
			UtilSceService utilSceService) {
		this.actasRepository = actasRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
	}
	
	@Override
	public byte[] reporteEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro) {
		byte[] pdf = null;
		
		try {
			List<DetalleEstadoActasOdpe> actasLista = getListaEstadoActas(filtro);
			
			Map<String, Object> parametros = new java.util.HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("nombreCortoEleccion", filtro.getEleccion());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("odpe", filtro.getOdpe());

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.REPORTE_ESTADO_ACTAS_ODPE);
            
        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        			"Se consultó el Reporte de Estados de acta por ODPE",
        			ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            if (actasLista != null && !actasLista.isEmpty()) {
                JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, actasLista, file);
                JRPdfExporter jrPdfExporter = new JRPdfExporter();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                jrPdfExporter.exportReport();
                pdf = byteArrayOutputStream.toByteArray();
            } 

            return pdf;
            
		} catch (Exception e) {
			logger.error(e.getMessage());
            return pdf;
        }
	}
	
	public EstadoActasOdpeReporteDto getListaEstadoActasOdpe(FiltroEstadoActasOdpeDto filtro) {
		
		List<DetalleEstadoActasOdpe> detalle = getListaEstadoActas(filtro);
        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Estado de Actas por ODPE.", "",
                filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
		return EstadoActasOdpeReporteDto
				.builder()
				.detalleEstadoActasOdpe(detalle)
				.totalEstadoActasOdpe(getTotalEstadoActas(detalle))
				.build();
		
	}
	
	private List<DetalleEstadoActasOdpe> getListaEstadoActas(FiltroEstadoActasOdpeDto filtro) {
		TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
		TypedParameterValue idOdpe = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdOdpe());
		TypedParameterValue idCentroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());
		
		List<Map<String, Object>> actasMap = actasRepository.estadoActasOdpe(filtro.getEsquema(), 
				idEleccion, idOdpe, idCentroComputo);
		AtomicInteger index = new AtomicInteger();
		return actasMap
				.stream()
				.map( reporte -> {
					Integer aProcesar = Integer.parseInt( reporte.get("n_actas_a_procesar").toString() );
					Integer procesadas = Integer.parseInt( reporte.get("n_actas_procesadas").toString() );
					
					return DetalleEstadoActasOdpe
							.builder()
							.num(index.incrementAndGet())
							.descOdpe( reporte.get("c_nombre_ambito_electoral").toString() )
							.descCentroCompu( reporte.get("c_nombre_centro_computo").toString() )
							.ahProcesar(aProcesar)
							.porProcesar(aProcesar - procesadas)
							.procesadas(procesadas)
							.observadas(Integer.parseInt( reporte.get("n_actas_observadas").toString() ))
							.resueltas(Integer.parseInt( reporte.get("n_actas_resueltas").toString() ))
							.pendienteResol(Integer.parseInt( reporte.get("n_actas_pendiente_resolucion").toString() ))
							.build();
				}).toList();
	}
	
	private List<DetalleEstadoActasOdpe> getTotalEstadoActas(List<DetalleEstadoActasOdpe> detalle) {
		DetalleEstadoActasOdpe totalEstadoActas = DetalleEstadoActasOdpe
							.builder()
							.descCentroCompu( "TOTAL" )
							.ahProcesar(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getAhProcesar).sum())
							.porProcesar(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getPorProcesar).sum())
							.procesadas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getProcesadas).sum())
							.observadas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getObservadas).sum())
							.resueltas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getResueltas).sum())
							.pendienteResol(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getPendienteResol).sum())
							.build();
		
		List<DetalleEstadoActasOdpe> listaTotal = new ArrayList<>();
		listaTotal.add(totalEstadoActas);
		
		return listaTotal;
	}
}
