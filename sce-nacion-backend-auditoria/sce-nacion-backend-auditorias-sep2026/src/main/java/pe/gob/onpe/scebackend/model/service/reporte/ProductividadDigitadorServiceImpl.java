package pe.gob.onpe.scebackend.model.service.reporte;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ProductividadDigitadorRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ProductividadDigitadorResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.UsuarioDigitadorResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ProductividadDigitadorRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class ProductividadDigitadorServiceImpl implements ProductividadDigitadorService {

	Logger logger = LoggerFactory.getLogger(ProductividadDigitadorServiceImpl.class);

	private final ProductividadDigitadorRepository productividadDigitadorRepository;

	private final UtilSceService utilSceService;
	
	private final ITabLogTransaccionalService logService;
	
	@Override
	public byte[] getReporteProductividadDigitador(ProductividadDigitadorRequestDto filtro) {
		try {
			List<ProductividadDigitadorResponseDto> lista = this.productividadDigitadorRepository
					.listarReporteProductividadDigitador(filtro.getEsquema(),
							filtro.getIdEleccion(),
							filtro.getIdCentroComputo(), 
							filtro.getUsuarioDigitador())
					.stream()
					.map(this::llenarDatos)
					.toList();
			
			final String nombreReporte = PRODUCTIVIDAD_DIGITADOR_JRXML;
			Map<String, Object> parametros = new HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON + "onpe.jpg");

			parametros.put("url_imagen", imagen);			
			parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
			parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
			parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
			parametros.put("tituloPrincipal", filtro.getProceso());
			parametros.put("tituloSecundario", "PRODUCTIVIDAD POR DIGITADOR");
			parametros.put("descCompu", filtro.getCentroComputo());
			parametros.put("tituloEleccionSimple", filtro.getEleccion());

			this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
					this.getClass().getSimpleName(), "Se consultó el Reporte de Productividad por Digitador",
					filtro.getCodigoAmbitoElectoral(), filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);
			
		} catch (Exception e) {
			logger.error("Excepción en getReporteProductividadDigitador", e);
			return new byte[0];
		}
		
	}
	
	@Override
	public List<UsuarioDigitadorResponseDto> obtenerListaUsuariosDigitador(String esquema, Integer idCentroComputo) {
		List<Map<String, Object>> listaMap = this.productividadDigitadorRepository
							.listarUsuariosDigitador(esquema, idCentroComputo == 0 ? null : idCentroComputo);
		
		return listaMap
				.stream()
				.map( map -> UsuarioDigitadorResponseDto
								.builder()
								.codigoUsuario( (String) map.get("c_usuario") )
								.documentoIdentidad( (String) map.get("c_documento_identidad") )
								.nombres( (String) map.get("c_nombres") )
								.apellidoPaterno( (String) map.get("c_apellido_paterno") )
								.apellidoMaterno( (String) map.get("c_apellido_materno") )
								.build())
				.toList();
	}
	
	private ProductividadDigitadorResponseDto llenarDatos(Map<String, Object> mapProductividad) {

		return ProductividadDigitadorResponseDto
				.builder()
				.codigoCentroComputo((String) mapProductividad.get("c_codigo_centro_computo"))
				.usuario((String) mapProductividad.get("c_usuario_accion"))
				.nombres((String) mapProductividad.get("c_apellidos_nombres"))
				.totalDigitaciones((Long) mapProductividad.get("n_total_digitaciones"))
				.tiempoTotal((String) mapProductividad.get("n_tiempo_total"))
				.build();
	}

}
