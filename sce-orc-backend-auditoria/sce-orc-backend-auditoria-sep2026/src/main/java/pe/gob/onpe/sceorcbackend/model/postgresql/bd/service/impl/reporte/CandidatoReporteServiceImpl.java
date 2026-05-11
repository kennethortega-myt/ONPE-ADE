package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.CandidatoReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CandidatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.CandidatoReporteService;
import pe.gob.onpe.sceorcbackend.exception.DataNoFoundException;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidatoReporteServiceImpl implements CandidatoReporteService {

	Logger logger = LoggerFactory.getLogger(CandidatoReporteServiceImpl.class);
	
	private final CandidatoRepository candidatoRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	public CandidatoReporteServiceImpl(CandidatoRepository candidatoRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.candidatoRepository = candidatoRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}
	
	@Override
	public byte[] reporteCandidatosPorOrgPol(FiltroOrganizacionesPoliticasDto filtro) {
	
		byte[] pdf = null;
		
		try {
			TypedParameterValue idEleccion = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
			TypedParameterValue centroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());
			
			List<Map<String, Object>> candidatosMap = candidatoRepository
					.candidatosPorOrgPol(filtro.getSchema(), idEleccion, centroComputo);

			if (candidatosMap.isEmpty()) {
				throw new DataNoFoundException(ConstantesReportes.DATA_NO_ENCONTRADA);
			}

			 Map<Integer,List<CandidatoReporteDto>> candidatos = candidatosMap
					.parallelStream()
					.map( reporte -> {
						return CandidatoReporteDto
								.builder()
								.codigoEleccion(Integer.parseInt("" + reporte.get("c_tipo_eleccion")))
								.eleccion((String) reporte.get("c_nombre_eleccion"))
								.departamento((String) reporte.get("c_departamento"))
								.provincia((String) reporte.get("c_provincia"))
								.distrito((String) reporte.get("c_distrito"))
								.agrupacionPolitica((String) reporte.get("c_organicacion_politica"))
								.ubigeo((String) reporte.get("c_codigo_ubigeo"))
								.numeroOrden(Integer.parseInt("" + reporte.get("n_lista")))
								.cargo((String) reporte.get("c_cargo"))
								.apellidoPaterno((String) reporte.get("c_apellido_paterno"))
								.apellidoMaterno((String) reporte.get("c_apellido_materno"))
								.nombreCandidato((String) reporte.get("c_nombres"))
								.build();
					}).collect(Collectors.groupingBy(CandidatoReporteDto::getCodigoEleccion));
			
			 List<CandidatoReporteDto> candidatosList = new ArrayList<>();
			
			for (Map.Entry<Integer, List<CandidatoReporteDto>> can : candidatos.entrySet()) {
	            List<CandidatoReporteDto> otra = can.getValue();
	            candidatosList.addAll(otra);
	        }
			
			Map<String, Object> parametros = new java.util.HashMap<>();


			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());

			this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					this.getClass().getSimpleName(), "Se consultó el Reporte de Listado de Candidatos por Organización Política.", "" + candidatosMap.getFirst().get("c_codigo_centro_computo"), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

			if(filtro.getIdCentroComputo() == null) {
				parametros.put("centroComputo", ConstantesComunes.CC_NACION_DESCRIPCION);
			} else {
				parametros.put("centroComputo", candidatosMap.getFirst().get("c_codigo_centro_computo")
						+ " - " + candidatosMap.getFirst().get("c_descripcion_centro_computo") );
			}

			return Funciones.generarReporte(this.getClass(), candidatosList, ConstantesComunes.CANDIDATOS_ORG_POL, parametros);

		} catch (Exception e) {
            return pdf;
        }
		
	}

}
