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
import pe.gob.onpe.scebackend.model.dto.request.reporte.SistemasAutomatizadosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.SistemasAutomatizadosResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.SistemasAutomatizadosRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.SistemasAutomatizadosService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class SistemasAutomatizadosServiceImpl implements SistemasAutomatizadosService {

	Logger logger = LoggerFactory.getLogger(SistemasAutomatizadosServiceImpl.class);

	private final SistemasAutomatizadosRepository sistemasAutomatizadosRepository;

	private final ITabLogTransaccionalService logService;

	private final UtilSceService utilSceService;

	@Override
	public byte[] getReporteSistemasAutomatizados(SistemasAutomatizadosRequestDto filtro) {
		try {
			List<SistemasAutomatizadosResponseDto> lista = this.sistemasAutomatizadosRepository
					.listarReporteSistemasAutomatizados(filtro.getEsquema(), 
							filtro.getIdEleccion(),
							filtro.getIdAmbitoElectoral(), 
							filtro.getIdCentroComputo(), 
							filtro.getUbigeo(),
							filtro.getEstado())
					.stream()
					.map(this::llenarDatos)
					.toList();

			final String nombreReporte = SISTEMAS_AUTOMATIZADOS_JRXML;
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
			parametros.put("tituloPrincipal", filtro.getProceso());
			parametros.put("tituloSecundario", "SISTEMAS AUTOMATIZADOS");
			parametros.put("descCompu", filtro.getCentroComputo());
			parametros.put("descOdpe", filtro.getOdpe());
			parametros.put("tituloEleccionSimple", filtro.getEleccion());

			this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
						this.getClass().getSimpleName(), "Se consultó el Reporte de Sistemas Automatizados.", "",
						filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);
			
		} catch (Exception e) {
			logger.error("Excepción en getReporteSistemasAutomatizados", e);
			return new byte[0];
		}

	}

	private SistemasAutomatizadosResponseDto llenarDatos(Map<String, Object> mapSistemas) {

		return SistemasAutomatizadosResponseDto
				.builder()
				.eleccion((String) mapSistemas.get("c_eleccion"))
				.descOdpe((String) mapSistemas.get("c_ambito_electoral"))
				.descCC((String) mapSistemas.get("c_centro_computo"))
				.departamento((String) mapSistemas.get("c_departamento"))
				.provincia((String) mapSistemas.get("c_provincia")).distrito((String) mapSistemas.get("c_distrito"))
				.ubigeo((String) mapSistemas.get("c_ubigeo")).numeMesa((String) mapSistemas.get("c_mesa"))
				.estado((String) mapSistemas.get("c_estado_stae"))
				.solucion((String) mapSistemas.get("c_solucion_tecnologica")).build();
	}

}

