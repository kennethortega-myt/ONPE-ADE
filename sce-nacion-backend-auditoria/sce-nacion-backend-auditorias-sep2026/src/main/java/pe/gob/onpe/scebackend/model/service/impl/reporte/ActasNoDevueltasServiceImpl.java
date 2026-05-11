package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ActasNoDevueltasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ActasNoDevueltasResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ErrorMaterialResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.ActasNoDevueltasRepository;
import pe.gob.onpe.scebackend.model.service.ActasNoDevueltasService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Log4j2
@RequiredArgsConstructor
@Service
public class ActasNoDevueltasServiceImpl implements ActasNoDevueltasService{

	Logger logger = LoggerFactory.getLogger(ActasNoDevueltasServiceImpl.class);
	
	private final ActasNoDevueltasRepository actasNoDevueltasRepository;
	
    private final ITabLogTransaccionalService logService;
    
    private final UtilSceService utilSceService;
	
  @Override
  public byte[] getReporteActasNoDevueltas(ActasNoDevueltasRequestDto filtro) {
    try {
      List<ActasNoDevueltasResponseDto> listaActas;
      AtomicInteger index = new AtomicInteger();
      
      if(ELECCION_CPR.equals(filtro.getIdEleccion())) {    	  
    	  listaActas = this.actasNoDevueltasRepository
    			  				.listarReporteActasNoDevueltasCPR(filtro.getEsquema(),
																filtro.getIdEleccion(),
																filtro.getIdCentroComputo(),
																filtro.getUbigeo(),
																filtro.getTipoImpresion())
								.stream()
								.map(mapa -> llenarDatosCpr(mapa, index.incrementAndGet())).toList();
      } else {
    	  listaActas = this.actasNoDevueltasRepository
			  				.listarReporteActasNoDevueltas(filtro.getEsquema(),
														filtro.getIdEleccion(),
														filtro.getIdCentroComputo(),
														filtro.getUbigeo(),
														filtro.getTipoImpresion())
							.stream()
							.map(mapa -> llenarDatos(mapa, index.incrementAndGet())).toList();
      }

      final String nombreReporte = filtro.getTipoImpresion().compareTo(1) == 0 ? REPORTE_ACTAS_NO_DEVUELTAS_TODAS : REPORTE_ACTAS_NO_DEVUELTAS_ACTAS;
      Map<String, Object> parametros = new HashMap<>();

      InputStream imagen = this.getClass().getClassLoader()
          .getResourceAsStream(PATH_IMAGE_COMMON_NAC + "onpe.jpg");
      InputStream pixelTransparente = this.getClass().getClassLoader()
          .getResourceAsStream(PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");

      parametros.put("logo_onpe", imagen);
      parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
      parametros.put("SVOF", utilSceService.getSinValorOficial(filtro.getIdProceso()));
      parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
      parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
      parametros.put(REPORT_PARAM_TITULO, filtro.getProceso());
      parametros.put(REPORT_PARAM_TITULO_REPORTE, "ACTAS NO DEVUELTAS POR EL JEE");
      parametros.put("desCC", filtro.getCentroComputo());
      parametros.put("descOdpe", filtro.getOdpe());

      try {
        InputStream subreportJrxml = this.getClass().getClassLoader()
            .getResourceAsStream(PATH_REPORT_JRXML + "/TiposErrorMaterial.jrxml");

        if (subreportJrxml != null) {
          JasperReport subreportCompilado = JasperCompileManager.compileReport(subreportJrxml);
          parametros.put("SUBREPORT_TIPOS_ERROR", subreportCompilado);
          logger.info("Subreporte TiposErrorMaterial compilado exitosamente en memoria");
        } else {
          logger.error("No se encontró el archivo TiposErrorMaterial.jrxml");
        }
      } catch (Exception e) {
        logger.error("Error compilando subreporte TiposErrorMaterial", e);
      }
      
      List<ErrorMaterialResponseDto> listaErrorMaterial = this.actasNoDevueltasRepository
    		  													.listarErrorMaterial(filtro.getEsquema())
    		  													.stream()
    		  													.map(this::llenarDatosErrorMaterial).toList();
																
      parametros.put("listaErrorMaterial", new JRBeanCollectionDataSource(listaErrorMaterial));

      try {
        this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
            this.getClass().getSimpleName(), "Se consultó el Reporte de actas no devueltas",
            "", filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO,
            LOG_TRANSACCIONES_ACCION);
      } catch (Exception e) {
        logger.error("Error al registrar log de Reporte de actas no devueltas: ", e);
      }

      return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);
    } catch(Exception e) {
      logger.error("Excepción en getReporteActasNoDevueltas", e);
      return new byte[0];
    }
  }
  
  private ActasNoDevueltasResponseDto llenarDatos(Map<String, Object> mapActa, int i) {
  	return ActasNoDevueltasResponseDto
  			.builder()
  			.numFila("" + (i))
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
  			.numFila("" + (i))
  			.acta((String) mapActa.get("c_acta"))
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
