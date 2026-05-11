package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
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
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleResumenTotal;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResumenTotalDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ActasRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ResumenTotalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service
public class ResumenTotalServiceImpl implements ResumenTotalService{

	Logger logger = LoggerFactory.getLogger(ResumenTotalServiceImpl.class);
	
	private final ActasRepository actasRepository;
	
	private final ITabLogTransaccionalService logService;
	
	private final UtilSceService utilSceService;
	
	public ResumenTotalServiceImpl(ActasRepository actasRepository, ITabLogTransaccionalService logService, UtilSceService utilSceService) {
		this.actasRepository = actasRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
	}
	
	@Override
	public List<DetalleResumenTotal> resumenTotalCentroComputo(FiltroResumenTotalDto filtro) {
		if(filtro.getTipoReporte() == 1) {
			return resumenTotalPorcentajes(filtro);
		} else {
			return resumenTotalCifras(filtro);
		}
	}
	
	@Override
    public byte[] getReporteResumenTotalPdf(FiltroResumenTotalDto filtro) {
        try{
        	List<DetalleResumenTotal> detalleResumen;
        	List<DetalleResumenTotal> detalleCifras = getDetalleResumenCifras(filtro);
        	
        	if(detalleCifras == null || detalleCifras.isEmpty()) return null;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream logoOnpe = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("logo_onpe", logoOnpe);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("proceso", filtro.getProceso());
            parametros.put("tituloEleccionSimple", filtro.getEleccion());
            
            String nombreReporte = "";
            if(filtro.getTipoReporte() == 1) {
            	detalleResumen = new ArrayList<>(getDetalleResumenPorcentajes(detalleCifras));
    			nombreReporte = ConstantesComunes.RESUMEN_TOTAL_PORCENTAJES_REPORT_JRXML;
    		} else {
    			detalleResumen = detalleCifras;
        		nombreReporte = ConstantesComunes.RESUMEN_TOTAL_CIFRAS_REPORT_JRXML;
    		}
            
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("estado", filtro.getEstado());

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_REPORT_JRXML + File.separator + nombreReporte);

            byte[] pdf = null;

            if(detalleResumen != null && !detalleResumen.isEmpty()) {
                JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(getParametrosPorcentaje(parametros, detalleCifras), detalleResumen, file );
                JRPdfExporter jrPdfExporter = new JRPdfExporter();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                jrPdfExporter.exportReport();
                pdf=byteArrayOutputStream.toByteArray();
                return pdf;
            } else {
                return pdf;
            }

        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
    }
	
	private Map<String, Object> getParametrosPorcentaje(Map<String, Object> parametros, List<DetalleResumenTotal> detalle) {
		DetalleResumenTotal totalPorcentajes =  getTotalesResumenPorcentaje(getTotalesResumenCifras(detalle));
		
		parametros.put("totalSiniestradas", totalPorcentajes.getActasSiniestradas());
		parametros.put("totalExtraviadas", totalPorcentajes.getActasExtraviadas());
		parametros.put("totalProcesadas", totalPorcentajes.getActasProcesadas());
		parametros.put("totalContabilizadas", totalPorcentajes.getActasContabilizadas());
		parametros.put("totalDigActas", totalPorcentajes.getDigActas());
		parametros.put("totalDigListaElectores", totalPorcentajes.getDigListaElectores());
		parametros.put("totalDigHojasAsist", totalPorcentajes.getDigHojasAsistencia());
		parametros.put("totalDigResolucion", totalPorcentajes.getDigResoluciones());
		parametros.put("totalTxActas", totalPorcentajes.getTxActas());
		parametros.put("totalTxResolucion", totalPorcentajes.getTxResolucion());
		parametros.put("totalOmisosMiembros", totalPorcentajes.getOmisosMm());
		parametros.put("totalOmisosVotantes", totalPorcentajes.getOmisosVotantes());
		parametros.put("totalOmisosMactaEscrut", totalPorcentajes.getOmisosMmActaEscrutinio());
		parametros.put("totalOmisosPersoneros", totalPorcentajes.getOmisosPersoneros());
		
		return parametros;
	}
	
	private List<DetalleResumenTotal> resumenTotalPorcentajes(FiltroResumenTotalDto filtro) {
		try {
			List<DetalleResumenTotal> detalleTotal = new ArrayList<>(getDetalleResumenCifras(filtro));
			
			if(detalleTotal != null && !detalleTotal.isEmpty()) {
				List<DetalleResumenTotal> detallePorcentajes = getDetalleResumenPorcentajes(detalleTotal);
				
				DetalleResumenTotal totalesPorcentaje = getTotalesResumenPorcentaje(getTotalesResumenCifras(detalleTotal));
				
				List<DetalleResumenTotal> detalleTotalPorcentajes = new ArrayList<>(detallePorcentajes);
				detalleTotalPorcentajes.add(totalesPorcentaje);
				
				this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, 
						this.getClass().getSimpleName(), "Se consultó el Reporte de Resumen Total Porcentajes",
						ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
				
				return detalleTotalPorcentajes;
			}
			
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private List<DetalleResumenTotal> resumenTotalCifras(FiltroResumenTotalDto filtro) {
		try {
			List<DetalleResumenTotal> detalleTotal = new ArrayList<>(getDetalleResumenCifras(filtro));
			
			if(detalleTotal != null && !detalleTotal.isEmpty()) {
				
				DetalleResumenTotal totales = getTotalesResumenCifras(detalleTotal);
				detalleTotal.add(totales);
				
				this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, 
						this.getClass().getSimpleName(), "Se consultó el Reporte de Resumen Total Cifras",
						ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
				
				return detalleTotal;
			}
			
		}catch(Exception e) {			
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private DetalleResumenTotal getTotalesResumenCifras(List<DetalleResumenTotal> detalle) {
		return DetalleResumenTotal
				.builder()
				.codigoCc("")
				.centroComputo("TOTAL")
				.habilitado(null)
				.mesasAInstalar(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getMesasAInstalar(), Double::sum))
				.mesasInstal(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getMesasInstal(), Double::sum))
				.mesasNoInstal(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getMesasNoInstal(), Double::sum))
				.actasSiniestradas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getActasSiniestradas(), Double::sum))
				.actasExtraviadas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getActasExtraviadas(), Double::sum))
				.actasProcesadas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getActasProcesadas(), Double::sum))
				.actasContabilizadas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getActasContabilizadas(), Double::sum))

				.digActas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getDigActas(), Double::sum))
				.digListaElectores(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getDigListaElectores(), Double::sum))
				.digHojasAsistencia(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getDigHojasAsistencia(), Double::sum))
				.digResoluciones(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getDigResoluciones(), Double::sum))

				.txActas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getTxActas(), Double::sum))
				.txResolucion(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getTxResolucion(), Double::sum))

				.omisosMm(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getOmisosMm(), Double::sum))
				.omisosVotantes(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getOmisosVotantes(), Double::sum))
				.omisosMmActaEscrutinio(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getOmisosMmActaEscrutinio(), Double::sum))
				.omisosPersoneros(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getOmisosPersoneros(), Double::sum))
				
				.digActasPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getDigActasPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.digListaElectoresPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getDigListaElectoresPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.digHojasAsistenciaPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getDigHojasAsistenciaPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.digResolucionesPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getDigResolucionesPorcentaje()*resumen.getResolucionesProcesadas()), Double::sum))

				.txActasPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getTxActasPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.txResolucionPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getTxResolucionPorcentaje()*resumen.getResolucionesProcesadas()), Double::sum))
				
				.omisosMmPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getOmisosMmPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.omisosVotantesPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getOmisosVotantesPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.omisosMmActaEscrutinioPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getOmisosMmActaEscrutinioPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				.omisosPersonerosPorcentaje(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + (resumen.getOmisosPersonerosPorcentaje()*resumen.getMesasAInstalar()), Double::sum))
				
				.totalMesas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getTotalMesas(), Double::sum))
				.resolucionesProcesadas(detalle.parallelStream().reduce(0d, (suma, resumen) -> suma + resumen.getResolucionesProcesadas(), Double::sum))
				.build();
	}
	
	private List<DetalleResumenTotal> getDetalleResumenCifras(FiltroResumenTotalDto filtro) {
		try {
			String habilitado = null;
			if(filtro.getHabilitado() == 1) {
				habilitado = "SI";
			} else if (filtro.getHabilitado() == 2) {
				habilitado = "NO";
			}
			
			TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
			TypedParameterValue centroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());
			TypedParameterValue estado = new TypedParameterValue(StandardBasicTypes.STRING, habilitado);
			
			List<Map<String, Object>> resumenMap = actasRepository
					.resumenTotalCentroComputo(filtro.getEsquema(), idEleccion, centroComputo, estado);
			
			if(resumenMap == null || resumenMap.size() == 0) return null;
			
			return resumenMap
					.parallelStream()
					.map( resumen -> {
						return DetalleResumenTotal
								.builder()
								.codigoCc(resumen.get("c_codigo_centro_computo").toString())
								.centroComputo(resumen.get("c_descripcion_centro_computo").toString())
								.habilitado(resumen.get("c_estado").toString())
								
								.mesasAInstalar(resumen.get("n_mesas_a_instalar") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_a_instalar").toString()))
								.mesasInstal(resumen.get("n_mesas_instaladas") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_instaladas").toString()))
								.mesasNoInstal(resumen.get("n_mesas_no_instaladas") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_no_instaladas").toString()))
								.actasSiniestradas(resumen.get("n_total_mesas_siniestras") == null ? 0 : Double.parseDouble(resumen.get("n_total_mesas_siniestras").toString()))
								.actasExtraviadas(resumen.get("n_total_mesas_extraviadas") == null ? 0 : Double.parseDouble(resumen.get("n_total_mesas_extraviadas").toString()))
								.actasProcesadas(resumen.get("n_actas_procesadas") == null ? 0 : Double.parseDouble(resumen.get("n_actas_procesadas").toString()))
								.actasContabilizadas(resumen.get("n_actas_computadas") == null ? 0 : Double.parseDouble(resumen.get("n_actas_computadas").toString()))

								.digActas(resumen.get("n_actas_digitalizadas") == null ? 0 : Double.parseDouble(resumen.get("n_actas_digitalizadas").toString()))
								.digListaElectores(resumen.get("n_mesas_digitalizadas_le") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_digitalizadas_le").toString()))
								.digHojasAsistencia(resumen.get("n_mesas_digitalizadas_mm") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_digitalizadas_mm").toString()))
								.digResoluciones(resumen.get("n_resolucion_digitalizada") == null ? 0 : Double.parseDouble(resumen.get("n_resolucion_digitalizada").toString()))
								
								.txActas(resumen.get("n_actas_transmitidas") == null ? 0 : Double.parseDouble(resumen.get("n_actas_transmitidas").toString()))
								.txResolucion(resumen.get("n_resoluciones_transmitidas") == null ? 0 : Double.parseDouble(resumen.get("n_resoluciones_transmitidas").toString()))								
								
								.omisosMm(resumen.get("n_mesas_registradas_mm") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_mm").toString()))
								.omisosVotantes(resumen.get("n_mesas_registradas_le") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_le").toString()))
								.omisosMmActaEscrutinio(resumen.get("n_mesas_registradas_me") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_me").toString()))
								.omisosPersoneros(resumen.get("n_mesas_registradas_pr") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_pr").toString()))
								
								.digActasPorcentaje(resumen.get("n_actas_digitalizadas_procesadas") == null ? 0 : Double.parseDouble(resumen.get("n_actas_digitalizadas_procesadas").toString()))
								.digListaElectoresPorcentaje(resumen.get("n_mesas_digitalizadas_le_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_digitalizadas_le_porcentaje").toString()))
								.digHojasAsistenciaPorcentaje(resumen.get("n_mesas_digitalizadas_mm_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_digitalizadas_mm_porcentaje").toString()))
								.digResolucionesPorcentaje(resumen.get("n_resolucion_digitalizada_procesada") == null ? 0 : Double.parseDouble(resumen.get("n_resolucion_digitalizada_procesada").toString()))
								
								.txActasPorcentaje(resumen.get("n_transmisiones_procesadas") == null ? 0 : Double.parseDouble(resumen.get("n_transmisiones_procesadas").toString()))
								.txResolucionPorcentaje(resumen.get("n_resoluciones_transmitidas_procesadas") == null ? 0 : Double.parseDouble(resumen.get("n_resoluciones_transmitidas_procesadas").toString()))
								
								.omisosMmPorcentaje(resumen.get("n_mesas_registradas_mm_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_mm_porcentaje").toString()))
								.omisosVotantesPorcentaje(resumen.get("n_mesas_registradas_le_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_le_porcentaje").toString()))
								.omisosMmActaEscrutinioPorcentaje(resumen.get("n_mesas_registradas_me_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_me_porcentaje").toString()))
								.omisosPersonerosPorcentaje(resumen.get("n_mesas_registradas_pr_porcentaje") == null ? 0 : Double.parseDouble(resumen.get("n_mesas_registradas_pr_porcentaje").toString()))
								
								.totalMesas(resumen.get("n_total_mesas") == null ? 0 : Double.parseDouble(resumen.get("n_total_mesas").toString()))
								.resolucionesProcesadas(resumen.get("n_resolucion_procesadas") == null ? 0 : Double.parseDouble(resumen.get("n_resolucion_procesadas").toString()))
								
								.build();
					}).toList();
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		
		return null;
		
	}

	private List<DetalleResumenTotal> getDetalleResumenPorcentajes(List<DetalleResumenTotal> detalle) {
		return detalle
				.parallelStream()
				.map( cifras -> {
					Double mesasAInstalar = cifras.getMesasAInstalar();
					return DetalleResumenTotal
							.builder()
							.codigoCc(cifras.getCodigoCc())
							.centroComputo(cifras.getCentroComputo())
							.habilitado(cifras.getHabilitado())
							.mesasAInstalar(cifras.getMesasAInstalar())
							.mesasInstal(cifras.getMesasInstal())
							.mesasNoInstal(cifras.getMesasNoInstal())
							.actasSiniestradas(mesasAInstalar == 0 ? 0.00 : 100*(cifras.getActasSiniestradas()/mesasAInstalar))
							.actasExtraviadas(mesasAInstalar == 0 ? 0.00 : 100*(cifras.getActasExtraviadas()/mesasAInstalar))
							.actasProcesadas(mesasAInstalar == 0 ? 0.00 : 100*(cifras.getActasProcesadas()/mesasAInstalar))
							.actasContabilizadas(mesasAInstalar == 0 ? 0.00 : 100*(cifras.getActasContabilizadas()/mesasAInstalar))
							
							.digActas(cifras.getDigActasPorcentaje())
							.digListaElectores(cifras.getDigListaElectoresPorcentaje())
							.digHojasAsistencia(cifras.getDigHojasAsistenciaPorcentaje())
							.digResoluciones(cifras.getDigResolucionesPorcentaje())
							
							.txActas(cifras.getTxActasPorcentaje())
							.txResolucion(cifras.getTxResolucionPorcentaje())
							
							.omisosMm(cifras.getOmisosMmPorcentaje())
							.omisosVotantes(cifras.getOmisosVotantesPorcentaje())
							.omisosMmActaEscrutinio(cifras.getOmisosMmActaEscrutinioPorcentaje())
							.omisosPersoneros(cifras.getOmisosPersonerosPorcentaje())
							
							.build();
				})
				.toList();
	}

	private DetalleResumenTotal getTotalesResumenPorcentaje(DetalleResumenTotal totales) {
		Double totalMesasAinstalar = totales.getMesasAInstalar();
		
		return DetalleResumenTotal
				.builder()
				.codigoCc(null)
				.centroComputo("TOTAL")
				.mesasAInstalar(totalMesasAinstalar)
				.mesasInstal(totales.getMesasInstal())
				.mesasNoInstal(totales.getMesasNoInstal())
				.actasSiniestradas(totalMesasAinstalar == 0 ? 0.00 : 100*(totales.getActasSiniestradas()/totalMesasAinstalar))
				.actasExtraviadas(totalMesasAinstalar == 0 ? 0.00 : 100*(totales.getActasExtraviadas()/totalMesasAinstalar))
				.actasProcesadas(totalMesasAinstalar == 0 ? 0.00 : 100*(totales.getActasProcesadas()/totalMesasAinstalar))
				.actasContabilizadas(totalMesasAinstalar == 0 ? 0.00 : 100*(totales.getActasContabilizadas()/totalMesasAinstalar))

				.digActas(totalMesasAinstalar == 0 ? 0.00 : totales.getDigActasPorcentaje() / totalMesasAinstalar)
				.digListaElectores(totalMesasAinstalar == 0 ? 0.00 :  totales.getDigListaElectoresPorcentaje() / totalMesasAinstalar )
				.digHojasAsistencia(totalMesasAinstalar == 0 ? 0.00 :  totales.getDigHojasAsistenciaPorcentaje() / totalMesasAinstalar )
				.digResoluciones( totales.getResolucionesProcesadas() == 0 ? 0.00 : totales.getDigResolucionesPorcentaje() / totales.getResolucionesProcesadas() )
				
				.txActas(totalMesasAinstalar == 0 ? 0.00 :  totales.getTxActasPorcentaje() / totalMesasAinstalar )
				.txResolucion( totales.getResolucionesProcesadas() == 0 ? 0.00 : totales.getTxResolucionPorcentaje() / totales.getResolucionesProcesadas() )
				
				.omisosMm(totalMesasAinstalar == 0 ? 0.00 :  totales.getOmisosMmPorcentaje() / totalMesasAinstalar )
				.omisosVotantes(totalMesasAinstalar == 0 ? 0.00 :  totales.getOmisosVotantesPorcentaje() / totalMesasAinstalar )
				.omisosMmActaEscrutinio(totalMesasAinstalar == 0 ? 0.00 :  totales.getOmisosMmActaEscrutinioPorcentaje() / totalMesasAinstalar )
				.omisosPersoneros(totalMesasAinstalar == 0 ? 0.00 :  totales.getOmisosPersonerosPorcentaje() / totalMesasAinstalar)
				
				.build();
	}
}
