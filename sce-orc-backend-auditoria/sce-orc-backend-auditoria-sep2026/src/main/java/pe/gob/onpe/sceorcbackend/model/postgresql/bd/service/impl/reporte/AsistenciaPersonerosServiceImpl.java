package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.AsistenciaPersonerosResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.AsistenciaPersonerosRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AsistenciaPersonerosService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class AsistenciaPersonerosServiceImpl implements AsistenciaPersonerosService{

	Logger logger = LoggerFactory.getLogger(AsistenciaPersonerosServiceImpl.class);
	
	private final AsistenciaPersonerosRepository asistenciaPersonerosRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	@Override
	public byte[] getReporteAsistenciaPersoneros(AsistenciaMiembroMesaRequestDto filtro) {
		try{
        	String nombreReporte = "";
        	Integer mesa = filtro.getMesa() == null ? null : Integer.parseInt(filtro.getMesa());
        	
			List<AsistenciaPersonerosResponseDto> listaActas = this.asistenciaPersonerosRepository
					.listaAsistenciaPersoneros(filtro.getEsquema(),
							filtro.getIdEleccion(),
							filtro.getIdCentroComputo(),
							filtro.getUbigeo(),
							mesa)
					.stream()
					.map(this::llenarDatos)
                    .filter(distinctByKeys(AsistenciaPersonerosResponseDto::getNumEle, AsistenciaPersonerosResponseDto::getCodUbigeo))
                    .toList();
			
			nombreReporte = ConstantesComunes.REPORTE_ASISTENCIA_PERSONEROS;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("imagen", imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL,utilSceService.getSinValorOficial());
            parametros.put("versionSuite", utilSceService.getVersionSistema());            
            parametros.put("viewUsuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "LISTA DE PERSONEROS");
            
            if(filtro.getIdEleccion().equals(ConstantesComunes.ELECCION_CPR)) {
            	parametros.put("tituloOrg", "AUTORIDADES EN CONSULTA");
            } else {
            	parametros.put("tituloOrg", "ORGANIZACIÓN POLÍTICA");
            }

        	this.logService.registrarLog(filtro.getUsuario(),
					Thread.currentThread().getStackTrace()[1].getMethodName(),
        			this.getClass().getSimpleName(), "Se consultó el Reporte de Asistencia de Personeros.",
					filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
	}
	
	private AsistenciaPersonerosResponseDto llenarDatos(Map<String, Object> mapAsistencia) {
		Long nro = (Long) mapAsistencia.get("n_nro");
		
		return AsistenciaPersonerosResponseDto
				.builder()
				.codDescODPE((String) mapAsistencia.get("c_codigo_ambito_electoral"))
				.codDescCC((String) mapAsistencia.get("c_codigo_centro_computo"))
				.numMesa((String) mapAsistencia.get("c_mesa"))
				.descDepartamento((String) mapAsistencia.get("c_departamento"))
				.descDistrito((String) mapAsistencia.get("c_distrito"))
				.descProvincia((String) mapAsistencia.get("c_provincia"))
				.codUbigeo((String) mapAsistencia.get("c_codigo_ubigeo"))
				.eleHabil((Integer) mapAsistencia.get("n_total_electores_habiles"))
				.numEle((String) mapAsistencia.get("c_documento_identidad"))
				.votante((String) mapAsistencia.get("c_apellido_nombre"))
				.descAgrupol((String) mapAsistencia.get("c_organizacion_politica"))
				.numero(nro.toString())
				.build();
	}
	
	private static <T> Predicate<T> distinctByKeys(
            Function<? super T, ?> key1,
            Function<? super T, ?> key2
    ) {
        Set<List<?>> seen = Collections.synchronizedSet(new HashSet<>());
        return t -> {
            List<?> compositeKey = List.of(key1.apply(t), key2.apply(t));
            return seen.add(compositeKey);
        };
    }


}
