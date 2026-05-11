package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTransmisionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.ITransmisionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ReporteTransmisionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
public class ReporteTransmisionServiceImpl implements ReporteTransmisionService{

	Logger logger = LoggerFactory.getLogger(ReporteTransmisionServiceImpl.class);
	
	private final ITransmisionRepository transmisionRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public ReporteTransmisionServiceImpl(ITransmisionRepository transmisionRepository,
			UtilSceService utilSceService, ITabLogService logService ) {
		this.transmisionRepository = transmisionRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] getReporteTransmision(ReporteTransmisionRequestDto filtro) {
		try{
        	String nombreReporte = "";
        	
			List<ReporteTransmisionResponseDto> listaActas = this.transmisionRepository.listarTransmision(filtro);
			
			
			nombreReporte = ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.REPORTE_TRANSMISION;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloSecundario", "REPORTE DE TRANSMISIÓN");   
            parametros.put("nombreCc", filtro.getCentroComputo());

            try {
            	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
            			this.getClass().getSimpleName(), "Se consultó el Reporte de Transmisión.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            } catch (Exception e) {
            	logger.error("Error al registra log de Reporte de Transmisión: ", e);
            }
            
            return Funciones.generarReporteSinValidacionDTD(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
	}

}
