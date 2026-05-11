package pe.gob.onpe.scebackend.model.service.impl;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.exeption.DataNoFoundException;
import pe.gob.onpe.scebackend.model.dto.CandidatoReporteDto;
import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.scebackend.model.orc.repository.CandidatoRepository;
import pe.gob.onpe.scebackend.model.service.CandidatoReporteService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CandidatoReporteServiceImpl implements CandidatoReporteService{

	Logger logger = LoggerFactory.getLogger(CandidatoReporteServiceImpl.class);
	
	private final CandidatoRepository candidatoRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;
	
	public CandidatoReporteServiceImpl(CandidatoRepository candidatoRepository, ITabLogTransaccionalService logService, UtilSceService utilSceService) {
		this.candidatoRepository = candidatoRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
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
					.map(reporte -> CandidatoReporteDto
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
							.build())
					.collect(Collectors.groupingBy(CandidatoReporteDto::getCodigoEleccion));
			
			 List<CandidatoReporteDto> candidatosList = new ArrayList<>();
			
			for (Map.Entry<Integer, List<CandidatoReporteDto>> can : candidatos.entrySet()) {
	            List<CandidatoReporteDto> otra = can.getValue();
	            candidatosList.addAll(otra);
	        }
			
			Map<String, Object> parametros = new java.util.HashMap<>();


			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            
			this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
					this.getClass().getSimpleName(), "Se consultó el Reporte de Listado de Candidatos por Organización Política.",
					"", "" + candidatosMap.getFirst().get("c_codigo_centro_computo"), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

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
