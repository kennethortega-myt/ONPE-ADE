package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.scebackend.model.dto.OrganizacionPoliticaReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.ProbableCandidatoElecto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ProbableCandidatoRequestDto;
import pe.gob.onpe.scebackend.model.orc.repository.AgrupacionPoliticaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ProbablesCandidatosElectosRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ProbablesCandidatosElectosService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class ProbablesCandidatosElectosServiceImpl implements ProbablesCandidatosElectosService {

	Logger logger = LoggerFactory.getLogger(ProbablesCandidatosElectosServiceImpl.class);

	private final ProbablesCandidatosElectosRepository probablesCandidatosElectosRepository;
	
	private final AgrupacionPoliticaRepository agrupacionPoliticaRepository;

	private final ITabLogTransaccionalService logService;

	private final UtilSceService utilSceService;
	
	public List<OrganizacionPoliticaReporteDto> listarAgrupolPorDE(ProbableCandidatoRequestDto filtro){
		
		List<Map<String, Object>> listaMap = this.agrupacionPoliticaRepository.listarAgrupolPorDistritoElectoral(
					filtro.getEsquema(), 
					""+filtro.getIdEleccion(), 
					filtro.getDistritoElectoral());
		
		return listaMap
				.stream()
				.map( map -> OrganizacionPoliticaReporteDto
							.builder()
							.codigoAgrupol( (String) map.get("c_codigo") )
							.agrupacionPolitica( (String) map.get("c_nombre") )
							.build()
				).toList();
	}

	public List<ProbableCandidatoElecto> listarProbablesCandidatosElectos(ProbableCandidatoRequestDto filtro) {

		List<Map<String, Object>> listaMap = this.probablesCandidatosElectosRepository.listarReporteProbablesCandidatosElectos(
				filtro.getEsquema(), filtro.getIdEleccion().toString(), filtro.getDistritoElectoral(),
				filtro.getAgrupacionPolitica(), filtro.getCargo());
		
        this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Probables Candidatos Electos.", "",
                filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

		return listaMap.stream()
				.map(candidato -> ProbableCandidatoElecto.builder()
						.distritoElecDesc(candidato.get("c_descripcion_distrito_electoral").toString())
						.agrupolDesc(candidato.get("c_descripcion_agrupacion_politica").toString())
						.votosAgrupol(candidato.get("n_votos_agrupacion_politica").toString())
						.escanosObtenidos(candidato.get("n_escanos_obtenido").toString())
						.nombreElecto((String)candidato.get("c_nombre_candidato_electo"))
						.numeroDni(candidato.get("c_numero_dni").toString())
						.votosValidos(candidato.get("n_votos_validos").toString())
						.ordenCandidato(candidato.get("n_orden_candidato").toString())
						.ordenObtenido(candidato.get("n_orden_obtenido").toString())
						.estadoCandidato(candidato.get("c_estado_candidato").toString())
						.observacion(candidato.get("c_observacion").toString())
						.cargoCandidato(candidato.get("c_cargo_candidato").toString()).build())
				.toList();
	}

	@Override
	public byte[] getReporteProbablesCandidatos(ProbableCandidatoRequestDto filtro) {
		try {
			List<ProbableCandidatoElecto> lista = listarProbablesCandidatosElectos(filtro);

			final String nombreReporte = PROBABLES_CANDIDATOS_JRXML;
			Map<String, Object> parametros = new HashMap<>();

			InputStream imagen = this.getClass().getClassLoader()
					.getResourceAsStream(PATH_IMAGE_COMMON_NAC + "onpe.jpg");
			InputStream pixelTransparente = this.getClass().getClassLoader()
					.getResourceAsStream(PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");

			parametros.put("url_imagen", imagen);
			parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
			parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
			parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
			parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
			parametros.put("tituloGeneral", filtro.getProceso());
			parametros.put("tituloSecundario", "PROBABLES CANDIDATOS ELECTOS");
			parametros.put("tipoEleccion", filtro.getEleccion());

			this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
					this.getClass().getSimpleName(), "Se consultó el Reporte de Probables Candidatos Electos.", "",
					filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);
			
		} catch (Exception e) {
			logger.error("Excepción en getReporteProbablesCandidatos", e);
			return new byte[0];
		}
	}
}
