package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteRelacionPuestaCeroRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteRelacionPuestoCeroDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.ReporteRelacionPuestaCeroRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteRelacionPuestaCeroService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
@Log
public class ReporteRelacionPuestaCeroService implements IReporteRelacionPuestaCeroService{

	@Autowired
    private ReporteRelacionPuestaCeroRepository reporteRelacionPuestaCeroRepository;
	@Autowired
	private UtilSceService utilSceService;
	@Autowired
	private ITabLogService logService;

    @Override
    public byte[] reporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro) throws JRException {
        List<ReporteRelacionPuestoCeroDto> lista = null;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("imagen", imagen);
        parametrosReporte.put("sinValorOficial", utilSceService.getSinValorOficial());

        lista = this.reporteRelacionPuestaCeroRepository
						    			.listarReporteRelacionPuestaCero(filtro.getEsquema(),
						    					filtro.getIdCentroComputo() == 0 ? null :  filtro.getCodigoCentroComputo(),
												filtro.getIdEstado())
						.stream()
						.map(this::llenarDatos).toList();
        
        nombreReporte = ConstantesComunes.REPORTE_RELACION_PUESTA_CERO;
        parametrosReporte.put("tituloReporte", "RELACIÓN DE PUESTA A CERO POR CENTRO DE CÓMPUTO");
        mapearCamposCabeceraReporte(filtro,parametrosReporte, lista);

        return Funciones.generarReporte(this.getClass(),lista,nombreReporte,parametrosReporte);
    }

    @Override
    public List<ReporteRelacionPuestoCeroDto> consultaReporteRelacionPuestaCero(ReporteRelacionPuestaCeroRequestDto filtro) {
        List<ReporteRelacionPuestoCeroDto> lista = this.reporteRelacionPuestaCeroRepository
													    			.listarReporteRelacionPuestaCero(filtro.getEsquema(),
													    					filtro.getIdCentroComputo() == 0 ? null :  filtro.getCodigoCentroComputo(),
																			filtro.getIdEstado())
													.stream()
													.map(this::llenarDatos).toList();

        try {
        	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
        			this.getClass().getSimpleName(), "Se consultó el Reporte de Relación puesta a cero.", filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        } catch (Exception e) {
        	log.info("Error al registra log de Reporte de Relación puesta a cero: ");
        }
        
        return lista;
    }
    
    private ReporteRelacionPuestoCeroDto llenarDatos(Map<String, Object> mapPc) {
        ReporteRelacionPuestoCeroDto reporte = new ReporteRelacionPuestoCeroDto();
        reporte.setCodigoCentroComputo((String) mapPc.get("c_codigo"));
        reporte.setNombreCentroComputo((String) mapPc.get("c_nombre"));
        reporte.setFechaHoraPuestaCeroCC((String) mapPc.get("d_fecha_envio"));
        reporte.setFechaHoraRecepcionCC((String) mapPc.get("d_fecha_recepcion"));
        reporte.setEstado(Long.valueOf( (Integer) mapPc.get("completo")));

        return reporte;
    }

    private void mapearCamposCabeceraReporte(
            ReporteRelacionPuestaCeroRequestDto filtro, Map<String, Object> parametrosReporte, List<? extends ReporteRelacionPuestoCeroDto> lista) {
        
        if(filtro.getIdEstado()==null) {
            parametrosReporte.put("p_estado",ConstantesReportes.TODOS);
        } else {
            parametrosReporte.put("p_estado",filtro.getEstado());
        }

        parametrosReporte.put("p_centroComputo", filtro.getCodigoCentroComputo() + " - " + filtro.getCentroComputo() );
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("proceso", filtro.getProceso());

        parametrosReporte.put("reporte", "FALTA PONER DATOS");
        parametrosReporte.put("version", utilSceService.getVersionSistema());

    }
}
