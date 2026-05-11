package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ListaParticipantesRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ConteoMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ListaParticipantesResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.ListaParticipantesRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ListaParticipantesService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ListaParticipantesServiceImpl implements ListaParticipantesService{

	Logger logger = LoggerFactory.getLogger(ListaParticipantesServiceImpl.class);
	
	private final ListaParticipantesRepository listaParticipantesRepository;
	
	private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	@Override
    public byte[] getReporteListaParticipantes(ListaParticipantesRequestDto filtro) {
        try{
        	String nombreReporte = "";
        	
			List<ListaParticipantesResponseDto> lista = listaParticipantes(filtro);
			
			nombreReporte = ConstantesComunes.REPORTE_LISTA_PARTICIPANTES;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("imagen", imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
            parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());            
            parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "LISTA DE PARTICIPANTES");

            try {
            	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
            			this.getClass().getSimpleName(), "Se consultó el Reporte de Lista de participantes.", filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            } catch (Exception e) {
            	logger.error("Error al registra log de Reporte de Lista de participantes: ", e);
            }

            return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }
	
	private List<ListaParticipantesResponseDto> listaParticipantes(ListaParticipantesRequestDto filtro) {
		Integer mesa = ( filtro.getMesa() == null || filtro.getMesa().isBlank() ) ? null : Integer.parseInt(filtro.getMesa());
		
		List<ListaParticipantesResponseDto> lista = this.listaParticipantesRepository
				.listarReporteListaParticipantes(filtro.getEsquema(),
						filtro.getIdEleccion(),
						filtro.getIdCentroComputo() == 0 ? null : filtro.getIdCentroComputo(),
						filtro.getUbigeo(),
						mesa)
				.stream()
				.map(this::llenarDatos).toList();

		Map<String, ConteoMesaResponseDto> conteosPorMesa = new LinkedHashMap<>();

		for (ListaParticipantesResponseDto item : lista) {
			String mesaKey = item.getNumMesa();

			if (!conteosPorMesa.containsKey(mesaKey)) {
				conteosPorMesa.put(mesaKey, new ConteoMesaResponseDto(0, 0));
			}

			ConteoMesaResponseDto conteo = conteosPorMesa.get(mesaKey);
			if ("PARTICIPÓ".equalsIgnoreCase(item.getEstado())) {
				conteo.participaron++;
			} else if ("OMISO".equalsIgnoreCase(item.getEstado())) {
				conteo.omiso++;
			}
		}

		for (ListaParticipantesResponseDto item : lista) {
			ConteoMesaResponseDto conteo = conteosPorMesa.get(item.getNumMesa());
			if (conteo != null) {
				item.setPartiparon(conteo.participaron);
				item.setOmitieron(conteo.omiso);
			}
		}
		
		return lista;
	}
	
	private ListaParticipantesResponseDto llenarDatos(Map<String, Object> mapParticipante) {
		Long nro = (Long) mapParticipante.get("n_nro");
		
		return ListaParticipantesResponseDto
				.builder()
				.numFila(nro.intValue())
				.codDescODPE((String) mapParticipante.get("c_codigo_ambito_electoral"))
				.codDescCC((String) mapParticipante.get("c_codigo_centro_computo"))
				.numMesa((String) mapParticipante.get("c_mesa"))
				.descDepartamento((String) mapParticipante.get("c_departamento"))
				.descDistrito((String) mapParticipante.get("c_distrito"))
				.descProvincia((String) mapParticipante.get("c_provincia"))
				.codUbigeo((String) mapParticipante.get("c_codigo_ubigeo"))
				.eleHabil((Integer) mapParticipante.get("n_total_electores_habiles"))
				.numEle((String) mapParticipante.get("c_documento_identidad"))
				.votante((String) mapParticipante.get("c_apellido_nombre"))
				.estado((String) mapParticipante.get("c_asistencia"))
				.partiparon(0)
				.omitieron(0)
				.build();
	}
}
