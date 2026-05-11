package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.scebackend.model.dto.reportes.BarreraElectoralDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.BarreraElectoralRequestDto;
import pe.gob.onpe.scebackend.model.orc.repository.BarreraElectoralRepository;
import pe.gob.onpe.scebackend.model.service.BarreraElectoralService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;

import pe.gob.onpe.scebackend.repository.resultados.ResultadosActaRepository;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Log4j2
@RequiredArgsConstructor
@Service
public class BarreraElectoralServiceImpl implements BarreraElectoralService{

	Logger logger = LoggerFactory.getLogger(BarreraElectoralServiceImpl.class);

	private final BarreraElectoralRepository barreraElectoralRepository;
	
	private final ITabLogTransaccionalService logService;

	private final UtilSceService utilSceService;

	private final ResultadosActaRepository resultadosActaRepository;
	
	private static final String CAMPO_CODIGO_AGRUPACION = "c_agrupacion_politica";
	private static final String CAMPO_DESC_AGRUPACION = "c_descripcion_agrupacion_politica";
	private static final String CAMPO_PORCENTAJE_VOTOS_VALIDOS = "n_porcentaje_voto_valido";
	private static final String CAMPO_ESTADO_BARRERA = "c_estado_barrera";
	
	@Override
	public byte[] getReporteBarreraElectoral(BarreraElectoralRequestDto filtro) {
		try {
			List<BarreraElectoralDto> lista = listarBarreraElectoral(filtro);

			final String nombreReporte = nombreReporte(filtro.getIdEleccion());
			
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
			parametros.put("tituloSecundario", "REPORTE DE BARRERA ELECTORAL AL " + getPorcentajeAvance(filtro) +"% DE ACTAS CONTABILIZADAS");
			parametros.put("tipoEleccion", filtro.getEleccion());

			this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
					this.getClass().getSimpleName(), "Se consultó el Reporte de Barrera Electoral.", "",
					filtro.getDistritoElectoral(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);
			
		} catch (Exception e) {
			logger.error("Excepción en getReporteBarreraElectoral", e);
			return new byte[0];
		}
	}
	
	private List<BarreraElectoralDto> listarBarreraElectoral(BarreraElectoralRequestDto filtro) {
		
		if(Objects.equals(filtro.getIdEleccion(), ELECCION_PARLAMENTO_ANDINO) 
				|| Objects.equals(filtro.getIdEleccion(), ELECCION_DIPUTADOS) ) {
			
			List<Map<String, Object>> listaMap = this.barreraElectoralRepository
					.barreraElectoralParlamentoDiputados(
							filtro.getEsquema(), 
							filtro.getIdEleccion().toString(), 
							filtro.getDistritoElectoral(), 
							filtro.getAgrupacionPolitica());
			
			return getResponseBarreraParlamentoDiputados(listaMap);
			
		} 

		if(Objects.equals(filtro.getIdEleccion(), ELECCION_SENADORES_DISTRITO_MULTIPLE) 
				|| Objects.equals(filtro.getIdEleccion(), ELECCION_SENADORES_DISTRITO_UNICO) ) {
			List<Map<String, Object>> listaMap = this.barreraElectoralRepository
					.barreraElectoralSenadores(
							filtro.getEsquema(), 
							filtro.getDistritoElectoral(), 
							filtro.getAgrupacionPolitica());
			
			return getResponseBarreraSenadores(listaMap);
		}
		
		return Collections.emptyList();
				
	}
	
	private List<BarreraElectoralDto> getResponseBarreraParlamentoDiputados(List<Map<String, Object>> listaMap) {
		return listaMap.stream()
				.map(repo -> BarreraElectoralDto.builder()
						.codigoAgrupol(repo.get(CAMPO_CODIGO_AGRUPACION) == null ? "" : repo.get(CAMPO_CODIGO_AGRUPACION).toString())
						.descripcionAgrupol(repo.get(CAMPO_DESC_AGRUPACION) == null ? "" : repo.get(CAMPO_DESC_AGRUPACION).toString())
						.votosValidos(getCampoInteger(repo.get("n_votos_validos")))
						.porcentajeVotosValidos(repo.get(CAMPO_PORCENTAJE_VOTOS_VALIDOS) == null ? null : Double.valueOf(repo.get(CAMPO_PORCENTAJE_VOTOS_VALIDOS).toString()))
						.escanosObtenidos(getCampoInteger(repo.get("n_escanos_obtenidos")))
						.estadoValla(repo.get(CAMPO_ESTADO_BARRERA) == null ? "" : repo.get(CAMPO_ESTADO_BARRERA).toString())
						.totalMiembros(getCampoInteger(repo.get("n_total_miembros")))
						.build()
				).toList();
	}
	
	private List<BarreraElectoralDto> getResponseBarreraSenadores(List<Map<String, Object>> listaMap) {
		return listaMap.stream()
				.map(repo -> BarreraElectoralDto.builder()
						.codigoAgrupol(repo.get(CAMPO_CODIGO_AGRUPACION).toString())
						.descripcionAgrupol(repo.get(CAMPO_DESC_AGRUPACION).toString())
						.votosValidosUnico(getCampoInteger(repo.get("n_votos_validos_unico")))
						.votosValidosMultiple(getCampoInteger(repo.get("n_votos_validos_multiple")))
						.votosValidos(getCampoInteger(repo.get("n_votos_validos_nacion")))
						.porcentajeVotosValidos(repo.get(CAMPO_PORCENTAJE_VOTOS_VALIDOS) == null ? null : Double.valueOf(repo.get(CAMPO_PORCENTAJE_VOTOS_VALIDOS).toString()))
						.miembrosUnico(getCampoInteger(repo.get("n_total_miembros_unico")))
						.miembrosLima(getCampoInteger(repo.get("n_total_miembros_lima")))
						.miembrosOtros(getCampoInteger(repo.get("n_total_miembros_otros")))
						.totalMiembros(getCampoInteger(repo.get("n_total_miembros_nacion")))
						.estadoValla(repo.get("c_estado_barrera") == null ? "" : repo.get("c_estado_barrera").toString())
						.build()
				).toList();
	}
	
	private Integer getCampoInteger(Object campo) {
		return campo == null ? null : Integer.parseInt(campo.toString());
	}

	private Double getPorcentajeAvance(BarreraElectoralRequestDto filtro) {
		double porcentajeAvance = 0.000;

		TypedParameterValue<Integer> idEleccion = new TypedParameterValue<>(StandardBasicTypes.INTEGER, filtro.getIdEleccion());
		TypedParameterValue<Integer> centroComputo = new TypedParameterValue<>(StandardBasicTypes.INTEGER, 0);
		TypedParameterValue<Integer> ambito = new TypedParameterValue<>(StandardBasicTypes.INTEGER, 0);
		TypedParameterValue<String> ubigeo = new TypedParameterValue<>(StandardBasicTypes.STRING, "000000");

		List<Map<String, Object>> resultadoMapList = resultadosActaRepository.resultadosActasContabilizadasResumidoPreferencial(
				filtro.getEsquema(), idEleccion, ambito, centroComputo, ubigeo);

		if(resultadoMapList == null || resultadoMapList.isEmpty()) return porcentajeAvance;

		Map<String, Object> resumenMap = resultadoMapList.getFirst();
		Integer mesasAinstalar = Integer.parseInt(resumenMap.get("n_mesas_a_instalar").toString());
		Integer actasContabilizadasNormal = Integer.parseInt(resumenMap.get("n_estado_contabilizada_normal").toString());
		Integer actasAnuladas = Integer.parseInt( resumenMap.get("n_estado_contabilizada_anulada").toString());
		Integer actasNoInstaladas = Integer.parseInt(resumenMap.get("n_estado_no_instalada").toString());

		if(mesasAinstalar.compareTo(0) != 0) {
			porcentajeAvance = Math.round(((actasContabilizadasNormal + actasAnuladas + actasNoInstaladas) * 100.000 / mesasAinstalar) * 1000.0) / 1000.0;
		}

		return porcentajeAvance;
	}
	
	private String nombreReporte(Integer idEleccion) {
		String nombreReporte = "";
		
		if(Objects.equals(idEleccion, ELECCION_PARLAMENTO_ANDINO)) {
			nombreReporte = BARRERA_ELECTORAL_PARLAMENTO_JRXML;
		} else if(Objects.equals(idEleccion, ELECCION_DIPUTADOS)) {
			nombreReporte = BARRERA_ELECTORAL_DIPUTADOS_JRXML;
		} else if(Objects.equals(idEleccion, ELECCION_SENADORES_DISTRITO_MULTIPLE)) {
			nombreReporte = BARRERA_ELECTORAL_SENADORES_JRXML;
		} else if(Objects.equals(idEleccion, ELECCION_SENADORES_DISTRITO_UNICO)) {
			nombreReporte = BARRERA_ELECTORAL_SENADORES_JRXML;
		}
		
		return nombreReporte;
	}
	
}
