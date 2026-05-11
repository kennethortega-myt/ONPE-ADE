package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.util.List;
import java.util.Map;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.model.dto.reportes.AvanceDigitalizacionActasReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroAvanceDigitalizacionDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ActasRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ReporteAvanceDigitalizacionService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class ReporteAvanceDigitalizacionServiceImpl implements ReporteAvanceDigitalizacionService{

	Logger logger = LoggerFactory.getLogger(ReporteAvanceDigitalizacionServiceImpl.class);
	
	private final ActasRepository actasRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;
	
	public ReporteAvanceDigitalizacionServiceImpl(ActasRepository actasRepository, ITabLogTransaccionalService logService,
			UtilSceService utilSceService) {
		this.actasRepository = actasRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
	}
	
	@Override
	public byte[] reporteAvanceDigitalizacion(FiltroAvanceDigitalizacionDto filtro) {
		byte[] pdf = null;
		String messageSobreCeleste = "";
		
		try {
			TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
			TypedParameterValue centroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getCentroComputo());
			TypedParameterValue ubigeo = new TypedParameterValue(StandardBasicTypes.STRING, filtro.getUbigeo());

			List<Map<String, Object>> actasMap;
			if(!filtro.isSobreCeleste()) {
				actasMap = actasRepository
						.avanceDigitalizacion(filtro.getEsquema(), idEleccion, centroComputo, ubigeo);
			} else {
				actasMap = actasRepository
						.avanceDigitalizacionSobreCeleste(filtro.getEsquema(), idEleccion, centroComputo, ubigeo);
			}
			
			List<AvanceDigitalizacionActasReporteDto> actasLista = actasMap
					.parallelStream()
					.map( reporte -> AvanceDigitalizacionActasReporteDto
									.builder()
									.nombreEleccion((String) reporte.get("c_nombre_eleccion"))
									.departamento((String) reporte.get("c_departamento"))
									.provincia((String) reporte.get("c_provincia"))
									.distrito((String) reporte.get("c_distrito"))
									.codigoUbigeo((String) reporte.get("c_codigo_ubigeo"))
									.centroComputo((String) reporte.get("c_nombre_centro_computo"))
									.codigoCc((String) reporte.get("c_codigo_centro_computo"))
									.estadoDigitalizacion((String) reporte.get("c_estado_digitalizacion"))
									.mesa((String) reporte.get("c_mesa"))
									.build()
							).toList();
			
			Map<String, Object> parametros = Funciones.getParametrosBaseReporte(
					this.getClass(), 
					utilSceService.getSinValorOficial(filtro.getIdProceso()), 
            		utilSceService.getVersionSistema(), 
            		filtro.getUsuario(), 
            		filtro.getProceso(),
            		ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_ACTAS);

			if(filtro.isSobreCeleste()){
				parametros.put("sobre", "SOBRE CELESTE");
				messageSobreCeleste = " con sobre celeste";
			}

        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        			"Se consultó el Reporte de avance de Digitalización" + messageSobreCeleste,
        			ConstantesComunes.CC_NACION_DESCRIPCION, centroComputo.toString(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            return Funciones.generarReporte(this.getClass(), actasLista, ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_ACTAS, parametros);
            
		} catch (Exception e) {
			logger.error(e.getMessage());
            return pdf;
        }
	}

	
	
}
