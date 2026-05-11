package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.reportes.DigitalizacionResolucionDto;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.DigitalizacionResolucionProjection;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.DigitalizacionResolucionRepository;
import pe.gob.onpe.scebackend.model.service.DigitalizacionResolucionService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class DigitalizacionResolucionServiceImpl implements DigitalizacionResolucionService{

	Logger logger = LoggerFactory.getLogger(DigitalizacionResolucionServiceImpl.class);
	
	private final DigitalizacionResolucionRepository digitalizacionResolucionRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogTransaccionalService logService;
	
	public DigitalizacionResolucionServiceImpl(DigitalizacionResolucionRepository digitalizacionResolucionRepository,
			UtilSceService utilSceService, ITabLogTransaccionalService logService) {
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
							.comparing(DigitalizacionResolucionProjection::getCodCCompu)
							.thenComparing(DigitalizacionResolucionProjection::getEstadoDigital)
			);
		
			nombreReporte = ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_RESOLUCION;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "AVANCE DE DIGITALIZACIÓN DE RESOLUCIONES");
			parametros.put("nombreCortoEleccion", filtro.getNombreEleccion());
			parametros.put("departamento", filtro.getDepartamento());
			parametros.put("provincia", filtro.getProvincia());
			parametros.put("distrito", filtro.getDistrito());

			this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(),
            			"Se consultó el Reporte de avance de digitalización de resoluciones",
            			ConstantesComunes.CC_NACION_DESCRIPCION, "C56000", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
            			ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            
            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
			return new byte[0];
        }
	}

}
