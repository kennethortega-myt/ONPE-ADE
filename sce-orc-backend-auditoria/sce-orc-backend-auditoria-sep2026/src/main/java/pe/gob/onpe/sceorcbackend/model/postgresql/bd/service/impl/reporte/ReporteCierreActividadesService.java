package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteCierreReaperturaCentroComputoDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteHistoricoCierreReaperturaDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteHistoricoCierreReaperturaRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.ReporteCierreActividadesRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteCierreActividadesService;
import static pe.gob.onpe.sceorcbackend.utils.ConstantesComunes.*;

import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Log
public class ReporteCierreActividadesService implements IReporteCierreActividadesService {

	private static final String PARAMETRO_IMAGEN = "imagen";
	private static final String PARAMETRO_TITULO_REPORTE = "tituloRep";
    private final ReporteCierreActividadesRepository reporteCierreActividadesRepository;
	private final UtilSceService utilSceService;
	private final ITabLogService logService;

    private final MaeProcesoElectoralService procesoElectoralService;

	@Override
    public byte[] reporteHistoricoCierreReapertura(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException {
        List<ReporteHistoricoCierreReaperturaDto> lista = null;
        Map<String, Object> parametrosReporte = new HashMap<>();
        String nombreReporte = "";

        InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE);
        parametrosReporte.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);

        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + NOMBRE_LOGO_ONPE);
        parametrosReporte.put(PARAMETRO_IMAGEN, imagen);

        parametrosReporte.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());

        lista = this.reporteCierreActividadesRepository
        			.listarReporteHistoricoCierreReapertura(filtro.getEsquema(), 
        								filtro.getIdCentroComputo().intValue())
        			.stream()
        			.map(this::llenarDatosHistorico).toList();
        
        nombreReporte = REPORTE_HISTORICO_CIERRE_REAPERTURA;
        parametrosReporte.put(PARAMETRO_TITULO_REPORTE, "REPORTE HISTÓRICO DE CIERRES Y REANUDACIONES DE ACTIVIDADES");

        mapearCamposCabeceraReporteHistorico(filtro, parametrosReporte, lista);

        this.logService.registrarLog(filtro.getUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            this.getClass().getName(),
            TransactionalLogUtil.crearMensajeLog("Reporte Histórico de Cierres y Reanudaciones"),
            filtro.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }
  
  	private ReporteHistoricoCierreReaperturaDto llenarDatosHistorico(Map<String, Object> mapHistorico){
	        ReporteHistoricoCierreReaperturaDto dto = new ReporteHistoricoCierreReaperturaDto();
	        dto.setCodigoCentroComputo((String) mapHistorico.get("c_codigo_centro_computo"));
	        dto.setFechaCierre((Date) mapHistorico.get("d_fecha_cierre"));
	        dto.setUsuarioCierre((String) mapHistorico.get("c_usuario_cierre"));
	        dto.setMotivoCierre((String) mapHistorico.get("c_motivo_cierre"));
	        dto.setFechaReapertura((Date) mapHistorico.get("d_fecha_reapertura"));
	        dto.setUsuarioReapertura((String) mapHistorico.get("c_usuario_reapertura"));
	        String nombresCompletos = (String) mapHistorico.get("c_apellidos_nombres");
	        dto.setApellidosNombresCierre(nombresCompletos);
	        dto.setApellidosNombresReanudacion(nombresCompletos);
	        dto.setMotivo((String) mapHistorico.get("c_motivo_cierre"));
	        dto.setFechaReanudacion((Date) mapHistorico.get("d_fecha_reapertura"));
	        dto.setUsuarioReanudacion((String) mapHistorico.get("c_usuario_reapertura"));
	        
	        return dto;
	    }

    private void mapearCamposCabeceraReporteHistorico(ReporteHistoricoCierreReaperturaRequestDto filtro,
                                                      Map<String, Object> parametrosReporte, List<ReporteHistoricoCierreReaperturaDto> lista) {

        ProcesoElectoral procesoElectoral = this.procesoElectoralService.findByActivo();
        parametrosReporte.put("proceso", procesoElectoral.getNombre());
        parametrosReporte.put("usuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
        parametrosReporte.put("tipoEstado", "HISTÓRICO");
        parametrosReporte.put("p_centroComputo", filtro.getCentroComputo() != null ? filtro.getCentroComputo() : "TODOS");
        parametrosReporte.put("p_mensaje", "");
        parametrosReporte.put("p_estado", "TODOS");
        parametrosReporte.put("p_reporte", "HistoricoCierreReapertura");

        parametrosReporte.put("tituloGeneral", procesoElectoral.getNombre());
        parametrosReporte.put(PARAMETRO_TITULO_REPORTE, "REPORTE HISTÓRICO DE CIERRES Y REANUDACIONES");

        parametrosReporte.put("viewUsuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
        parametrosReporte.put("versionSuite", utilSceService.getVersionSistema());
        parametrosReporte.put("viewSrvDB", "SERVIDOR_BD");
        parametrosReporte.put("viewEstacion", obtenerEstacionTrabajo());
    }

    private String obtenerNombreCentroComputo(ReporteHistoricoCierreReaperturaRequestDto filtro) {
        String centroComputo = filtro.getCentroComputo();
        if (centroComputo != null && centroComputo.contains(" - ")) {
            return centroComputo.split(" - ", 2)[1].trim();
        }
        return filtro.getCodigoCentroComputo() != null ? filtro.getCodigoCentroComputo() : "CENTRO DE CÓMPUTO";
    }
    
    private String obtenerEstacionTrabajo() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "ESTACION_TRABAJO";
        }
    }


    @Override
    public byte[] reporteCierreActividades(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException {
        List<ReporteCierreReaperturaCentroComputoDto> lista = null;
        Map<String, Object> parametrosReporte = new HashMap<>();
        String nombreReporte = "";

        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + NOMBRE_LOGO_ONPE);
        parametrosReporte.put(PARAMETRO_IMAGEN, imagen);

        InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + "pixel_transparente.png");
        parametrosReporte.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);

        parametrosReporte.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());

        lista = this.reporteCierreActividadesRepository
        			.listarReporteCierreActividades(filtro.getEsquema(), filtro.getIdCentroComputo().intValue())
        			.stream()
        			.map(this::llenarDatosCierre).toList();
        
        nombreReporte = REPORTE_CIERRE_ACTIVIDADES;

        mapearCamposCabeceraReporteCierre(filtro, parametrosReporte, lista);

        this.logService.registrarLog(filtro.getUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            this.getClass().getName(),
            TransactionalLogUtil.crearMensajeLog("Reporte Cierre de Actividades"),
            filtro.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }
    
    private ReporteCierreReaperturaCentroComputoDto llenarDatosCierre(Map<String, Object> mapDatos) {
	  ReporteCierreReaperturaCentroComputoDto dto = new ReporteCierreReaperturaCentroComputoDto();
	  dto.setNunmeroEleccion((Integer) mapDatos.get("n_eleccion"));
	  dto.setCodigoCentroComputo((String) mapDatos.get("c_codigo_centro_computo"));
	  dto.setNumeroMesasInstalar(mapDatos.get("n_mesas_a_instalar") == null ? 0 : ((Long) mapDatos.get("n_mesas_a_instalar")).intValue());
	  dto.setNumeroActasProcesadas(mapDatos.get("n_digit_actas_procesadas") == null ? 0 : ((Long) mapDatos.get("n_digit_actas_procesadas")).intValue());
	  dto.setNunmeroActasComputadas(mapDatos.get("n_digit_actas_computadas") == null ? 0 : ((Long) mapDatos.get("n_digit_actas_computadas")).intValue());
	  dto.setNumeroControlCalidadActas(mapDatos.get("n_digitaliz_control_calidad_actas") == null ? 0 : ((Long) mapDatos.get("n_digitaliz_control_calidad_actas")).intValue());
	  dto.setNumeroResoluciones( mapDatos.get("n_digitaliz_control_calidad_resoluciones") == null ? 0 : ((Long) mapDatos.get("n_digitaliz_control_calidad_resoluciones")).intValue());
	  dto.setNumeroOmisosElectores(mapDatos.get("n_omisos_electores") == null ? 0 : ((BigDecimal) mapDatos.get("n_omisos_electores")).intValue());
	  dto.setNumeroOmisosMm(mapDatos.get("n_omisos_mm") == null ? 0 : ((BigDecimal) mapDatos.get("n_omisos_mm")).intValue());
	  dto.setNunmeroOmisosMe(mapDatos.get("n_registro_me") == null ? 0 : ((BigDecimal) mapDatos.get("n_registro_me")).intValue());
	  dto.setNumeroOmisosPer(mapDatos.get("n_registro_per") == null ? 0 : ((BigDecimal) mapDatos.get("n_registro_per")).intValue());
	  dto.setNombreEleccion((String) mapDatos.get("c_nombre_eleccion"));
	  dto.setNombreCentro((String) mapDatos.get("c_nombre_centro_computo"));
	  
	  return dto;
	}

    private void mapearCamposCabeceraReporteCierre(ReporteHistoricoCierreReaperturaRequestDto filtro,
                                                      Map<String, Object> parametrosReporte, List<ReporteCierreReaperturaCentroComputoDto> lista) {

        ProcesoElectoral procesoElectoral = this.procesoElectoralService.findByActivo();
        parametrosReporte.put("proceso", procesoElectoral.getNombre());
        parametrosReporte.put("usuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
        parametrosReporte.put("tipoEstado", "HISTÓRICO");
        parametrosReporte.put("p_centroComputo", filtro.getCentroComputo() != null ? filtro.getCentroComputo() : "TODOS");
        parametrosReporte.put("p_mensaje", "");
        parametrosReporte.put("p_estado", "TODOS");
        parametrosReporte.put("p_reporte", "HistoricoCierreReapertura");

        parametrosReporte.put("tituloGeneral", procesoElectoral.getNombre());
        parametrosReporte.put(PARAMETRO_TITULO_REPORTE, "CIERRE DE ACTIVIDADES");

        parametrosReporte.put("viewUsuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
        parametrosReporte.put("versionSuite", utilSceService.getVersionSistema());
        parametrosReporte.put("viewSrvDB", "SERVIDOR_BD");
        parametrosReporte.put("viewEstacion", obtenerEstacionTrabajo());
        parametrosReporte.put("numeroReporte", filtro.getCorrelativo());
    }

    @Override
    public byte[] reporteReaperturaActividades(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException {
        List<ReporteCierreReaperturaCentroComputoDto> lista = null;
        Map<String, Object> parametrosReporte = new HashMap<>();
        String nombreReporte = "";

        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + NOMBRE_LOGO_ONPE);
        parametrosReporte.put(PARAMETRO_IMAGEN, imagen);

        InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(
            PATH_IMAGE_COMMON + "pixel_transparente.png");
        parametrosReporte.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);

        parametrosReporte.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());

        lista = this.reporteCierreActividadesRepository
        		.listarReporteReaperturaActividades(filtro.getEsquema(), filtro.getIdCentroComputo().intValue())
        		.stream()
        		.map(this::llenarDatosCierre).toList();
        
        nombreReporte = REPORTE_REAPERTURA_ACTIVIDADES;

        mapearCamposCabeceraReporteReapertura(filtro, parametrosReporte, lista);

        this.logService.registrarLog(filtro.getUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            this.getClass().getName(),
            TransactionalLogUtil.crearMensajeLog("Reporte Reapertura de Actividades"),
            filtro.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }

    private void mapearCamposCabeceraReporteReapertura(ReporteHistoricoCierreReaperturaRequestDto filtro,
                                                       Map<String, Object> parametrosReporte,
                                                       List<ReporteCierreReaperturaCentroComputoDto> lista) {
        ProcesoElectoral procesoElectoral = this.procesoElectoralService.findByActivo();
        parametrosReporte.put("proceso", procesoElectoral.getNombre());
        parametrosReporte.put("usuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");

        parametrosReporte.put("tituloGeneral",procesoElectoral.getNombre());
        parametrosReporte.put(PARAMETRO_TITULO_REPORTE, "REAPERTURA DE ACTIVIDADES");
        parametrosReporte.put("tituloReporte", "REAPERTURA DE ACTIVIDADES");

        parametrosReporte.put("codigoCC", filtro.getCodigoCentroComputo() != null ? filtro.getCodigoCentroComputo() : "");
        parametrosReporte.put("nombreCC", obtenerNombreCentroComputo(filtro));

        parametrosReporte.put("viewUsuario", filtro.getUsuario() != null ? filtro.getUsuario() : "");
        parametrosReporte.put("versionSuite", utilSceService.getVersionSistema());
        parametrosReporte.put("viewSrvDB", "SERVIDOR_BD");
        parametrosReporte.put("viewEstacion", obtenerEstacionTrabajo());
    }
}
