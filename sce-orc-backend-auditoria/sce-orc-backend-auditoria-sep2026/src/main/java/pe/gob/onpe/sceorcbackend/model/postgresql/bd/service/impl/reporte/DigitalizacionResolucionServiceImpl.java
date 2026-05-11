package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DigitalizacionResolucionDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.DigitalizacionResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.DigitalizacionResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.projection.DigitalizacionResolucionProjection;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
public class DigitalizacionResolucionServiceImpl implements DigitalizacionResolucionService{
	Logger logger = LoggerFactory.getLogger(DigitalizacionResolucionServiceImpl.class);
	
	private final DigitalizacionResolucionRepository digitalizacionResolucionRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public DigitalizacionResolucionServiceImpl(DigitalizacionResolucionRepository digitalizacionResolucionRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.digitalizacionResolucionRepository = digitalizacionResolucionRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] getReporteDigitalizacionResolucion(DigitalizacionResolucionDto filtro) {
		try{
        	String nombreReporte = "";
			List<DigitalizacionResolucionProjection> listaActas = this.digitalizacionResolucionRepository.listarReporteAvanceDigitalizacionResolucion(
					filtro.getEsquema(),
					filtro.getIdEleccion(),
					filtro.getIdCentroComputo(),
					filtro.getUbigeo());

			listaActas.sort(
					Comparator
							.comparing(DigitalizacionResolucionProjection::getCodiDesOdpe)
							.thenComparing(DigitalizacionResolucionProjection::getEstadoDigital)
			);
		
			nombreReporte = ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_RESOLUCION;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "AVANCE DE DIGITALIZACIÓN DE RESOLUCIONES");
			parametros.put("nombreCortoEleccion", filtro.getNombreEleccion());
			parametros.put("departamento", filtro.getDepartamento());
			parametros.put("provincia", filtro.getProvincia());
			parametros.put("distrito", filtro.getDistrito());

			String codigoCc = filtro.getCentroComputo().split(" - ")[0].trim();

            	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(),
            			"Se consultó el Reporte de avance de digitalización de resoluciones",
						codigoCc, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
            			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
	}
}
