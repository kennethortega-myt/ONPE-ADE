package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.AutoridadesRevocadasResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IAutoridadesRevocadasRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutoridadesRevocadasService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
public class AutoridadesRevocadasServiceImpl implements AutoridadesRevocadasService{

	Logger logger = LoggerFactory.getLogger(AutoridadesRevocadasServiceImpl.class);
	
	private final IAutoridadesRevocadasRepository autoridadesRevocadasRepository;
	private final UtilSceService utilSceService;
	private final ITabLogService logService;
	
	public AutoridadesRevocadasServiceImpl(IAutoridadesRevocadasRepository autoridadesRevocadasRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.autoridadesRevocadasRepository = autoridadesRevocadasRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] getReporteAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro) {
		try{
        	String nombreReporte = "";
        	
			List<AutoridadesRevocadasResponseDto> listaActas = this.autoridadesRevocadasRepository.listaAutoridadesRevocadas(filtro);
			
			nombreReporte = ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.REPORTE_AUTORIDADES_REVOCADAS;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", this.utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());            
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo() );
            parametros.put("odpe", filtro.getOdpe() );
            parametros.put("tipoAutoridad", obtenerTipoAutoridad(filtro.getIdCargo()));
            parametros.put("eleccion", filtro.getEleccion());

        	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
        			this.getClass().getSimpleName(), "Se consultó el Reporte de Autoridades Revocadas.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
	}
	
	private String obtenerTipoAutoridad(Integer idCargo) {
	    if (idCargo == null) {
	        return "ALCALDES / REGIDORES";
	    }
	    if (idCargo == 18) {
	        return "REGIDORES";
	    }
	    return "ALCALDES";
	}

}
