package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ActasNoDevueltasRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ActasNoDevueltasResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ErrorMaterialResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.ActasNoDevueltasRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActasNoDevueltasService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;

import static pe.gob.onpe.sceorcbackend.utils.ConstantesComunes.*;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class ActasNoDevueltasServiceImpl implements ActasNoDevueltasService{

	Logger logger = LoggerFactory.getLogger(ActasNoDevueltasServiceImpl.class);
	
	private final ActasNoDevueltasRepository actasNoDevueltasRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	@Override
	public byte[] getReporteActasNoDevueltas(ActasNoDevueltasRequestDto filtro) {
	    try {
	        List<ActasNoDevueltasResponseDto> listaActas = obtenerActas(filtro);
	        String nombreReporte = obtenerNombreReporte(filtro);

	        Map<String, Object> parametros = construirParametros(filtro);
	        cargarSubreporteTiposError(parametros);
	        cargarErrorMaterial(parametros, filtro);

	        registrarLogReporte(filtro);

	        return Funciones.generarReporte(
	                this.getClass(),
	                listaActas,
	                nombreReporte,
	                parametros
	        );

	    } catch (Exception e) {
	        logger.error("Error generando reporte de actas no devueltas", e);
	        return new byte[0];
	    }
	}

	private List<ActasNoDevueltasResponseDto> obtenerActas(ActasNoDevueltasRequestDto filtro) {
	    AtomicInteger index = new AtomicInteger();

	    if (ELECCION_CPR.equals(filtro.getIdEleccion())) {
	        return actasNoDevueltasRepository
	                .listarReporteActasNoDevueltasCPR(
	                        filtro.getEsquema(),
	                        filtro.getIdEleccion(),
	                        filtro.getIdCentroComputo(),
	                        filtro.getUbigeo(),
	                        filtro.getTipoImpresion()
	                )
	                .stream()
	                .map(mapa -> llenarDatosCpr(mapa, index.incrementAndGet()))
	                .toList();
	    }

	    return actasNoDevueltasRepository
	            .listarReporteActasNoDevueltas(
	                    filtro.getEsquema(),
	                    filtro.getIdEleccion(),
	                    filtro.getIdCentroComputo(),
	                    filtro.getUbigeo(),
	                    filtro.getTipoImpresion()
	            )
	            .stream()
	            .map(mapa -> llenarDatos(mapa, index.incrementAndGet()))
	            .toList();
	}
	private String obtenerNombreReporte(ActasNoDevueltasRequestDto filtro) {
	    return filtro.getTipoImpresion() == 1
	            ? REPORTE_ACTAS_NO_DEVUELTAS_TODAS
	            : REPORTE_ACTAS_NO_DEVUELTAS_ACTAS;
	}

	private Map<String, Object> construirParametros(ActasNoDevueltasRequestDto filtro) {
	    Map<String, Object> params = new HashMap<>();

	    InputStream imagen = getClass()
	            .getClassLoader()
	            .getResourceAsStream(PATH_IMAGE_COMMON + "onpe.jpg");

	    params.put("logo_onpe", imagen);
	    params.put("SVOF", utilSceService.getSinValorOficial());
	    params.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
	    params.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
	    params.put(REPORT_PARAM_TITULO, filtro.getProceso());
	    params.put(REPORT_PARAM_TITULO_REPORTE, "ACTAS NO DEVUELTAS POR EL JEE");
	    params.put("desCC", filtro.getCentroComputo());
	    params.put("descOdpe", filtro.getOdpe());
	    params.put(REPORT_PARAM_SUBREPORT_DIR, PATH_REPORT_JRXML + File.separator);

	    return params;
	}

	
	private void cargarSubreporteTiposError(Map<String, Object> parametros) {
	    try {
	        InputStream jrxml = getClass()
	                .getClassLoader()
	                .getResourceAsStream(PATH_REPORT_JRXML + "/TiposErrorMaterial.jrxml");

	        if (jrxml == null) {
	            logger.error("No se encontró TiposErrorMaterial.jrxml");
	            return;
	        }

	        JasperReport subreport = JasperCompileManager.compileReport(jrxml);
	        parametros.put("SUBREPORT_TIPOS_ERROR", subreport);

	    } catch (Exception e) {
	        logger.error("Error compilando subreporte TiposErrorMaterial", e);
	    }
	}

	private void cargarErrorMaterial(Map<String, Object> parametros, ActasNoDevueltasRequestDto filtro) {

		List<ErrorMaterialResponseDto> lista = actasNoDevueltasRepository.listarErrorMaterial(filtro.getEsquema())
				.stream().map(this::llenarDatosErrorMaterial).toList();

		parametros.put(REPORT_PARAM_LISTA_ERROR_MATERIAL, new JRBeanCollectionDataSource(lista));
	}
	
	private void registrarLogReporte(ActasNoDevueltasRequestDto filtro) {
	    logService.registrarLog(
	            filtro.getUsuario(),
	            Thread.currentThread().getStackTrace()[1].getMethodName(),
	            getClass().getSimpleName(),
	            "Se consultó el Reporte de actas no devueltas.",
	            filtro.getCentroComputo(),
	            LOG_TRANSACCIONES_AUTORIZACION_NO,
	            LOG_TRANSACCIONES_ACCION
	    );
	}

	private ActasNoDevueltasResponseDto llenarDatos(Map<String, Object> mapActa, int i) {
    	return ActasNoDevueltasResponseDto
    			.builder()
    			.numFila("" + i)
				.lote("")
    			.acta((String) mapActa.get("c_acta"))
    			.nomTipoEleccion((String) mapActa.get("c_nombre_eleccion"))
    			.errorMaterial((String) mapActa.get("c_error_material"))
    			.tipoErrorM((String) mapActa.get("c_tipo_error_material"))
    			.votosImpugnados((String) mapActa.get("c_impugnada"))
    			.ilegibilidad((String) mapActa.get("c_ilegible"))
    			.tipoIlegible((String) mapActa.get("c_tipo_ilegible"))
    			.detalleIlegible((String) mapActa.get("c_det_ilegible"))
    			.actasIncompletas((String) mapActa.get("c_incompleta"))
    			.solNulidad((String) mapActa.get("c_nulidad"))
    			.actaSinDatos((String) mapActa.get("c_sin_datos"))
    			.actaSinFirma((String) mapActa.get("c_sin_firma"))
    			.observacion((String) mapActa.get("c_observacion"))
    			.centroComputo((String) mapActa.get("c_nombre_centro_computo"))
    			.odpe((String) mapActa.get("c_nombre_ambito_electoral"))
    			.build();
    }
    
    private ActasNoDevueltasResponseDto llenarDatosCpr(Map<String, Object> mapActa, int i) {
    	return ActasNoDevueltasResponseDto
    			.builder()
    			.numFila("" + i)
    			.acta((String) mapActa.get("c_acta"))
    			.lote((String) mapActa.get("c_correlativo"))
    			.autoridad((String) mapActa.get("c_consulta"))
    			.nomTipoEleccion((String) mapActa.get("c_nombre_eleccion"))
    			.errorMaterial((String) mapActa.get("c_error_material"))
    			.tipoErrorM((String) mapActa.get("c_tipo_error_material"))
    			.votosImpugnados((String) mapActa.get("c_impugnada"))
    			.ilegibilidad((String) mapActa.get("c_ilegible"))
    			.actasIncompletas((String) mapActa.get("c_incompleta"))
    			.solNulidad((String) mapActa.get("c_nulidad"))
    			.actaSinDatos((String) mapActa.get("c_sin_datos"))
    			.actaSinFirma((String) mapActa.get("c_sin_firma"))
    			.centroComputo((String) mapActa.get("c_nombre_centro_computo"))
    			.odpe((String) mapActa.get("c_nombre_ambito_electoral"))
    			.correlativo((String) mapActa.get("c_correlativo"))
    			.build();
    }
    
    private ErrorMaterialResponseDto llenarDatosErrorMaterial(Map<String, Object> mapErrorMat) {
  	  return ErrorMaterialResponseDto
  		.builder()
  		.codigo((String) mapErrorMat.get("c_codigo"))
  		.nombre((String) mapErrorMat.get("c_descripcion"))
  		.build();
    }
}
