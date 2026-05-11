package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ListaParticipantesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.ConteoMesaResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ListaParticipantesResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.ListaParticipantesRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.ListaParticipantesService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@RequiredArgsConstructor
@Service
public class ListaParticipantesServiceImpl implements ListaParticipantesService{

	Logger logger = LoggerFactory.getLogger(ListaParticipantesServiceImpl.class);
	
	private final ListaParticipantesRepository listaParticipantesRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;
	
	@Override
    public byte[] getReporteListaParticipantes(ListaParticipantesRequestDto filtro) {
        try{
			List<ListaParticipantesResponseDto> lista = listaParticipantes(filtro);
            
			Map<String, Object> parametros = Funciones.getParametrosBaseReporte(
            		this.getClass(), 
            		utilSceService.getSinValorOficial(filtro.getIdProceso()), 
            		utilSceService.getVersionSistema(), 
            		filtro.getUsuario(), 
            		filtro.getProceso(),
            		"LISTA DE PARTICIPANTES");

        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        			"Se consultó el Reporte de Lista de Participantes",
        			ConstantesComunes.CC_NACION_DESCRIPCION, 
        			filtro.getCodigoCentroComputo(), 
        			ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
        	parametros.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);
        	
            return Funciones.generarReporte(
            		this.getClass(), 
            		lista, 
            		ConstantesComunes.REPORTE_LISTA_PARTICIPANTES, 
            		parametros);

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
				conteo.incrementarParticipacion();
			} else if ("OMISO".equalsIgnoreCase(item.getEstado())) {
				conteo.incrementarOmiso();
			}
		}
		
		for (ListaParticipantesResponseDto item : lista) {
			ConteoMesaResponseDto conteo = conteosPorMesa.get(item.getNumMesa());
			if (conteo != null) {
				item.setPartiparon(conteo.getParticiparon());
				item.setOmitieron(conteo.getOmiso());
			}
		}
		return lista;
	}
	
	private ListaParticipantesResponseDto llenarDatos(Map<String, Object> mapParticipante) {
		Long nro = (Long) mapParticipante.get("n_nro");
    	return ListaParticipantesResponseDto
    			.builder()
    			.numFila( nro.intValue() )
    			.codDescODPE((String) mapParticipante.get("c_codigo_ambito_electoral") )
    			.codDescCC( (String) mapParticipante.get("c_codigo_centro_computo") )
    			.numMesa( (String) mapParticipante.get("c_mesa") )
    			.descDepartamento( (String) mapParticipante.get("c_departamento") )
    			.descDistrito( (String) mapParticipante.get("c_distrito") )
    			.descProvincia( (String) mapParticipante.get("c_provincia") )
    			.codUbigeo( (String) mapParticipante.get("c_codigo_ubigeo") )
    			.eleHabil((Integer) mapParticipante.get("n_total_electores_habiles") )
    			.numEle( (String) mapParticipante.get("c_documento_identidad") )
    			.votante( (String) mapParticipante.get("c_apellido_nombre") )
    			.estado( (String) mapParticipante.get("c_asistencia") )
    			.build();
    }
	
}
