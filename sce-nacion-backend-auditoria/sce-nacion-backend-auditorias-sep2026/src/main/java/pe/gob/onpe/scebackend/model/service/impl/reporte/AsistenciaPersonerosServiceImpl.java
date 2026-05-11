package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AsistenciaPersonerosResponseDto;
import pe.gob.onpe.scebackend.model.orc.entities.PadronElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.AsistenciaPersonerosRepository;
import pe.gob.onpe.scebackend.model.orc.repository.PadronElectoralRepository;
import pe.gob.onpe.scebackend.model.service.AsistenciaPersonerosService;
import pe.gob.onpe.scebackend.model.service.IPadronElectoralService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@RequiredArgsConstructor
@Service
public class AsistenciaPersonerosServiceImpl implements AsistenciaPersonerosService{

	Logger logger = LoggerFactory.getLogger(AsistenciaPersonerosServiceImpl.class);
	
	private final AsistenciaPersonerosRepository asistenciaPersonerosRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;
    private final IPadronElectoralService   padronElectoralService;
	
	@Override
	public byte[] getReporteAsistenciaPersoneros(AsistenciaMiembroMesaRequestDto filtro) {
		try{
			Integer mesa = filtro.getMesa() == null ? null : Integer.parseInt(filtro.getMesa());
			List<AsistenciaPersonerosResponseDto> listaActas = this.asistenciaPersonerosRepository
								.listaAsistenciaPersoneros(filtro.getEsquema(),
										filtro.getIdEleccion(),
										filtro.getIdCentroComputo(),
										filtro.getUbigeo(),
										mesa)
								.stream()
								.map(this::llenarDatos)
                    .filter(distinctByKeys(AsistenciaPersonerosResponseDto::getNumEle, AsistenciaPersonerosResponseDto::getCodUbigeo)).toList();
			
			Map<String, Object> parametros = Funciones.getParametrosBaseReporte(
					this.getClass(), 
					utilSceService.getSinValorOficial(filtro.getIdProceso()), 
            		utilSceService.getVersionSistema(), 
            		filtro.getUsuario(), 
            		filtro.getProceso(),
            		"LISTA DE PERSONEROS");


			parametros.put("versionSuite", utilSceService.getVersionSistema());
			parametros.put("viewUsuario", filtro.getUsuario());

			if(filtro.getIdEleccion().equals(ConstantesComunes.ELECCION_REVOCATORIA)) {
            	parametros.put("tituloOrg", "AUTORIDADES EN CONSULTA");
            } else {
            	parametros.put("tituloOrg", "ORGANIZACIÓN POLÍTICA");
            }
			parametros.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);
			
        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        			"Se consultó el Reporte de Asistencia de Personeros",
        			ConstantesComunes.CC_NACION_DESCRIPCION, 
        			filtro.getCodigoCentroComputo(), 
        			ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, ConstantesComunes.REPORTE_ASISTENCIA_PERSONEROS, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
	}
	
	private AsistenciaPersonerosResponseDto llenarDatos(Map<String, Object> mapAsistencia) {
		Long nro = (Long) mapAsistencia.get("n_nro");
		
		return AsistenciaPersonerosResponseDto
				.builder()
				.codDescODPE((String)mapAsistencia.get("c_codigo_ambito_electoral"))
				.codDescCC((String)mapAsistencia.get("c_codigo_centro_computo"))
				.numMesa((String)mapAsistencia.get("c_mesa"))
				.descDepartamento((String)mapAsistencia.get("c_departamento"))
				.descDistrito((String)mapAsistencia.get("c_distrito"))
				.descProvincia((String)mapAsistencia.get("c_provincia"))
				.codUbigeo((String)mapAsistencia.get("c_codigo_ubigeo"))
				.eleHabil((Integer) mapAsistencia.get("n_total_electores_habiles"))
				.numEle((String)mapAsistencia.get("c_documento_identidad"))
				.votante(this.buscarinformacionVotante((String)mapAsistencia.get("c_apellido_nombre"), (String)mapAsistencia.get("c_documento_identidad")))
				.descAgrupol((String)mapAsistencia.get("c_organizacion_politica"))
				.numero(nro.toString())
				.build();
	}

    private String buscarinformacionVotante(String asistente, String numElec){
        try{
            String votante = asistente;
            if(asistente.contains("NO DISPONIBLE")){
                PadronElectoral padronElectoral = this.padronElectoralService.getPadronElectoral(numElec);
                if(Objects.nonNull(padronElectoral)){
                    votante = StringUtils.join(new String[]{padronElectoral.getApellidoPaterno(), padronElectoral.getApellidoMaterno(), padronElectoral.getNombres()}, " ");
                }
            }
            return votante;
        } catch (Exception e) {
            return asistente;
        }
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
