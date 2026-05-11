package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRecepcionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteRecepcionResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IRecepcionRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ReporteRecepcionService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class ReporteRecepcionServiceImpl implements ReporteRecepcionService{

	Logger logger = LoggerFactory.getLogger(ReporteRecepcionServiceImpl.class);
	
	private final IRecepcionRepository recepcionRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogTransaccionalService logService;
	
	public ReporteRecepcionServiceImpl(IRecepcionRepository recepcionRepository,
			UtilSceService utilSceService, ITabLogTransaccionalService logService ) {
		this.recepcionRepository = recepcionRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	@Transactional("locationTransactionManager")
	public byte[] getReporteRecepcion(ReporteRecepcionRequestDto filtro) {
		try{
        	String nombreReporte = ConstantesComunes.REPORTE_RECEPCION;
        	
			List<ReporteRecepcionResponseDto> listaActas = this.recepcionRepository.listarRecepcion(filtro);
            Map<String, Object> parametros = new java.util.HashMap<>();
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloSecundario", "REPORTE DE RECEPCIÓN");   
            parametros.put("nombreCc", filtro.getCentroComputo());

            this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de Recepción.",
                    "", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
	}
}
