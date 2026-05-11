package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.AutoridadReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CandidatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.AutoridadReporteService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

@Service
public class AutoridadReporteServiceImpl implements AutoridadReporteService {

	Logger logger = LoggerFactory.getLogger(AutoridadReporteServiceImpl.class);
	
	private final CandidatoRepository candidatoRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public AutoridadReporteServiceImpl(CandidatoRepository candidatoRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.candidatoRepository = candidatoRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] reporteAutoridadesEnConsulta(FiltroOrganizacionesPoliticasDto filtro) {
	
		byte[] pdf = null;
		
		try {
			TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
			TypedParameterValue centroComputo = new TypedParameterValue(StandardBasicTypes.STRING, filtro.getCentroComputo());
			
			List<Map<String, Object>> autoridadesMap = candidatoRepository
					.autoridadesEnConsulta(filtro.getSchema(), idEleccion, centroComputo);
			
			List<AutoridadReporteDto> lista = autoridadesMap
					.parallelStream()
					.map( reporte ->
						 AutoridadReporteDto
								.builder()
								.codigoEleccion(Integer.parseInt("" + reporte.get("c_tipo_eleccion")))
								.eleccion((String) reporte.get("c_nombre_eleccion"))
								.departamento((String) reporte.get("c_departamento"))
								.provincia((String) reporte.get("c_provincia"))
								.distrito((String) reporte.get("c_distrito"))
								.ubigeo((String) reporte.get("c_codigo_ubigeo"))
								.cargo((String) reporte.get("c_cargo"))
								.apellidoPaterno((String) reporte.get("c_apellido_paterno"))
								.apellidoMaterno((String) reporte.get("c_apellido_materno"))
								.nombreCandidato((String) reporte.get("c_nombres"))
								.build()
					).toList();
			
			Map<String, Object> parametros = new java.util.HashMap<>();


			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCcDescripcion());
            
            String nombreReporte = ConstantesComunes.AUTORIDADES_EN_CONSULTA;
            
        	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
        			this.getClass().getSimpleName(), "Se consultó el Reporte de Autoridades en Consulta.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);

		} catch (Exception e) {
			logger.error("Error al generar el reporte de autoridades en consulta", e);
            return pdf;
        }
		
	}
}
