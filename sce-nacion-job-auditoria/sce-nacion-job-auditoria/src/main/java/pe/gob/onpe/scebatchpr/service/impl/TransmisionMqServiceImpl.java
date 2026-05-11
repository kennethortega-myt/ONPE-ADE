package pe.gob.onpe.scebatchpr.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pe.gob.onpe.scebatchpr.dto.ArchivoTransmisionRequest;
import pe.gob.onpe.scebatchpr.dto.TramaSceDto;
import pe.gob.onpe.scebatchpr.dto.VwPrEleccionExportDto;
import pe.gob.onpe.scebatchpr.entities.orc.TabPrTransmision;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrActa;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrEleccion;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrMesa;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrParticipacionCiudadana;
import pe.gob.onpe.scebatchpr.entities.orc.VwPrResumen;
import pe.gob.onpe.scebatchpr.mapper.IVwPrActaExportPrMapper;
import pe.gob.onpe.scebatchpr.mapper.IVwPrEleccionExportPrMapper;
import pe.gob.onpe.scebatchpr.mapper.IVwPrMesaExportPrMapper;
import pe.gob.onpe.scebatchpr.mapper.IVwPrParticipacionCiudadanaExportPrMapper;
import pe.gob.onpe.scebatchpr.mapper.IVwPrResumenExportPrMapper;
import pe.gob.onpe.scebatchpr.repository.orc.TabPrTransmisionRepository;
import pe.gob.onpe.scebatchpr.service.ArchivoTransmisionService;
import pe.gob.onpe.scebatchpr.service.MqTransmisionService;
import pe.gob.onpe.scebatchpr.service.TransmisionMqService;
import pe.gob.onpe.scebatchpr.utils.Constantes;

@Service
public class TransmisionMqServiceImpl implements TransmisionMqService {

	Logger logger = LoggerFactory.getLogger(TransmisionMqServiceImpl.class);
	
	private final TabPrTransmisionRepository tabPrTransmisionRepository;

	private final IVwPrParticipacionCiudadanaExportPrMapper mapperCiudadano;
	
	private final IVwPrMesaExportPrMapper mapperMesa;
	
	private final IVwPrEleccionExportPrMapper mapperEleccion;

	private final IVwPrResumenExportPrMapper mapperResumen;
	
	private final IVwPrActaExportPrMapper mapperActa;

	private final MqTransmisionService produce;
	
	private final ArchivoTransmisionService archivoTransmisionService;
	
	@Value("${data.pendientes.block.size:100}")
	private int blockSize;
	
	public TransmisionMqServiceImpl(
			TabPrTransmisionRepository tabPrTransmisionRepository,
			IVwPrParticipacionCiudadanaExportPrMapper mapperCiudadano,
			IVwPrMesaExportPrMapper mapperMesa,
			IVwPrEleccionExportPrMapper mapperEleccion,
			IVwPrResumenExportPrMapper mapperResumen,
			IVwPrActaExportPrMapper mapperActa,
			MqTransmisionService produce,
			ArchivoTransmisionService archivoTransmisionService
			){
		this.tabPrTransmisionRepository = tabPrTransmisionRepository;
		this.mapperCiudadano = mapperCiudadano;
		this.mapperMesa = mapperMesa;
		this.mapperEleccion = mapperEleccion;
		this.mapperResumen = mapperResumen;
		this.mapperActa = mapperActa;
		this.produce = produce;
		this.archivoTransmisionService = archivoTransmisionService;
	}
	
	@Override
	public void enviarTramaSce() throws IOException {
	    logger.info("**************INICIO DE LA TRANSMISION********************************");

	    try {
	    	PageRequest pageable = PageRequest.of(0, blockSize); 
	        List<TabPrTransmision> transmisiones = tabPrTransmisionRepository.listarPendientes(pageable);
	        logger.info("Total transmisiones: {}", transmisiones.size());

	        for (TabPrTransmision transmision : transmisiones) {
	            TramaSceDto trama = construirTrama(transmision);
	            procesarTransmisionPorVista(transmision, trama);
	            produce.productorData(List.of(trama));
	        }

	    } catch (InterruptedException e) {
	        logger.error("Exception: ", e);
	        Thread.currentThread().interrupt();
	    } finally {
	        logger.info("clear current tenantid");
	    }

	    logger.info("**************FIN DE LA TRANSMISION********************************");
	}

	@Override
	public void enviarArchivos() throws JsonProcessingException, InterruptedException {
		logger.info("********************INICIO DE LA TRANSMISION DE IMAGENES********************");
		
		try {
			
			List<ArchivoTransmisionRequest> requests = this.archivoTransmisionService.listarArchivosPendientes();

	        if (requests == null || requests.isEmpty()) {
	            logger.info("No hay imagenes para transmitir");
	            return;
	        }
			 
	        for (ArchivoTransmisionRequest request : requests) {
	            if (tieneArchivosParaEnviar(request)) {
	                produce.productorArchivos(request);
	            } else {
	                logger.info("No se generaron archivos para enviar para el acta={}", request.getIdActa());
	            }
	        }
			
		} catch(InterruptedException e) {
			logger.error("Exception: ",e);
			Thread.currentThread().interrupt(); // <- muy importante
		} finally {
			logger.info("********************FIN DE LA TRANSMISION DE IMAGENES**********************");
		}
	}
	
	private TramaSceDto construirTrama(TabPrTransmision transmision) {
	    TramaSceDto trama = new TramaSceDto();
	    trama.setIdTransferencia(transmision.getId());
	    trama.setIdActa(transmision.getIdActa());
	    trama.setVista(transmision.getNombreVista());
	    trama.setUsuario(Constantes.USUARIO_JOB);
	    return trama;
	}
	
	private boolean tieneArchivosParaEnviar(ArchivoTransmisionRequest request) {
	    return request.getArchivos() != null
	        && !request.getArchivos().isEmpty();
	}
	
	private void procesarTransmisionPorVista(TabPrTransmision transmision, TramaSceDto trama) throws JsonProcessingException {
	    String vista = transmision.getNombreVista();
	    String tramaJson = transmision.getTrama();

	    switch (vista) {
	        case "vw_pr_eleccion_distrital", "vw_pr_parlamento_andino",
	             "vw_pr_diputados", "vw_pr_senadores_distrito_multiple", "vw_pr_senadores_distrito_unico",
	             "vw_pr_presidente_y_vicepresidentes", "vw_pr_revocatoria_distrital" -> {
	            List<VwPrEleccion> elecciones = new ObjectMapper().readValue(tramaJson, new TypeReference<>() {});
	            if (elecciones != null) {
	                List<VwPrEleccionExportDto> dto = elecciones.stream().map(mapperEleccion::toDto).toList();
	                asignarTramaEleccion(trama, vista, dto);
	            }
	        }
	        case "vw_pr_resumen" -> {
	            List<VwPrResumen> resumenes = new ObjectMapper().readValue(tramaJson, new TypeReference<>() {});
	            if (resumenes != null) {
	                trama.setTramaResumen(resumenes.stream().map(mapperResumen::toDto).toList());
	            }
	        }
	        case "vw_pr_participacion_ciudadana" -> {
	            List<VwPrParticipacionCiudadana> ciudadanos = new ObjectMapper().readValue(tramaJson, new TypeReference<>() {});
	            if (ciudadanos != null) {
	                trama.setTramaParticipacion(ciudadanos.stream().map(mapperCiudadano::toDto).toList());
	            }
	        }
	        case "vw_pr_acta" -> {
	            List<VwPrActa> actas = new ObjectMapper().readValue(tramaJson, new TypeReference<>() {});
	            if (actas != null) {
	                trama.setTramaActa(actas.stream().map(mapperActa::toDto).toList());
	            }
	        }
	        case "vw_pr_mesa" -> {
	            List<VwPrMesa> mesas = new ObjectMapper().readValue(tramaJson, new TypeReference<>() {});
	            if (mesas != null) {
	                trama.setTramaMesa(mesas.stream().map(mapperMesa::toDto).toList());
	            }
	        }
	        default -> logger.warn("Vista no reconocida: {}", vista);
	    }
	}

	private void asignarTramaEleccion(TramaSceDto trama, String vista, List<VwPrEleccionExportDto> dto) {
	    switch (vista) {
	        case "vw_pr_eleccion_distrital" -> trama.setTramaEleccion(dto);
	        case "vw_pr_parlamento_andino" -> trama.setTramaParlamento(dto);
	        case "vw_pr_diputados" -> trama.setTramaDiputados(dto);
	        case "vw_pr_senadores_distrito_multiple" -> trama.setTramaSenadoresDistritoElectoralMultiple(dto);
	        case "vw_pr_senadores_distrito_unico" -> trama.setTramaSenadoresDistritoNacionalUnico(dto);
	        case "vw_pr_presidente_y_vicepresidentes" -> trama.setTramaPresidenciales(dto);
	        case "vw_pr_revocatoria_distrital" -> trama.setTramaRevocatoriaDistrital(dto);
	        default -> throw new IllegalArgumentException("Vista no soportada");
	    }
	}
	

}
