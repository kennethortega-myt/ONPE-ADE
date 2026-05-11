package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.AsistenciaMmEscrutinioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AsistenciaMmEscrutinioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.AsistenciaMmEscrutinioProjection;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsistenciaMmEscrutinioServiceImpl implements AsistenciaMmEscrutinioService{

	Logger logger = LoggerFactory.getLogger(AsistenciaMmEscrutinioServiceImpl.class);
	
	private final AsistenciaMmEscrutinioRepository asistenciaMmEscrutinioRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public AsistenciaMmEscrutinioServiceImpl(AsistenciaMmEscrutinioRepository asistenciaMmEscrutinioRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.asistenciaMmEscrutinioRepository = asistenciaMmEscrutinioRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}

	@Override
	public byte[] getReporteAsistenciaMmEscrutinio(AsistenciaMiembroMesaRequestDto filtro) {
		try {
			Integer mesa = null;
			if (filtro.getMesa() != null && !filtro.getMesa().trim().isEmpty()) {
				mesa = Integer.parseInt(filtro.getMesa());
			}

			List<AsistenciaMmEscrutinioProjection> listaActas = this.asistenciaMmEscrutinioRepository.listaAsistenciaMmEscrutinio(
					filtro.getEsquema(), filtro.getIdEleccion(), filtro.getIdCentroComputo(), filtro.getUbigeo(), mesa);

			if (listaActas == null || listaActas.isEmpty()) {
				return new byte[0];
			}

			String nombreReporte = ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.REPORTE_ASISTENCIA_MM_ESCRUTINIO;
			InputStream reportStream = this.getClass().getClassLoader().getResourceAsStream(nombreReporte);

			if (reportStream == null) {
				return new byte[0];
			}
			JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
			Map<String, Object> parametros = new HashMap<>();
			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
			parametros.put("imagen", imagen);

			parametros.put("tituloGeneral", filtro.getProceso() != null ? filtro.getProceso() : "PROCESO ELECTORAL");
			parametros.put("viewUsuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
			parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
			parametros.put("versionSuite", utilSceService.getVersionSistema());
			parametros.put("tituloRep", "LISTA DE MIEMBROS DE MESA SEGÚN ACTA DE ESCRUTINIO");
			parametros.put("pixeltransparente", null);

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaActas);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
			
			this.logService.registrarLog(
					filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					this.getClass().getSimpleName(), "Se consultó el Reporte de Asistencia miembros de mesa escrutinio.", filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return JasperExportManager.exportReportToPdf(jasperPrint);

		} catch(Exception e) {
			return new byte[0];
		}
	}
}
