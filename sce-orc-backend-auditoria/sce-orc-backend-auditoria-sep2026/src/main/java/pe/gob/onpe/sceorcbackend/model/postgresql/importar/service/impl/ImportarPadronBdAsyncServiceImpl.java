package pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity.ImportPadronElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.exception.WebsocketBroadcastException;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportPadronElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportarPadronBdAsyncService;
import pe.gob.onpe.sceorcbackend.model.postgresql.importar.service.ImportPadronElectoralProgresoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.GestionarConstraintService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.BroadcastWebsocketServiceImpl;
import pe.gob.onpe.sceorcbackend.model.importar.dto.MaePadronDto;
import pe.gob.onpe.sceorcbackend.model.importar.dto.PaginaOptDto;
import pe.gob.onpe.sceorcbackend.model.importar.mapper.UtilMapper;
import pe.gob.onpe.sceorcbackend.utils.ConstantesProgresoPadron;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ImportarPadronBdAsyncServiceImpl implements ImportarPadronBdAsyncService {

	Logger logger = LoggerFactory.getLogger(ImportarPadronBdAsyncServiceImpl.class);

	public static final String BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS = "/topic/update-progress-with-details";

	private final ImportPadronElectoralService padronElectoralService;

	private final ImportPadronElectoralProgresoService padronElectoralProgresoService;

	private final RestTemplate clientExport;

	private final BroadcastWebsocketServiceImpl broadcastWebsocketService;

	private final GestionarConstraintService gestionarConstraintService;

	private final ObjectMapper objectMapper;

	public ImportarPadronBdAsyncServiceImpl(ImportPadronElectoralService padronElectoralRepository,
			ImportPadronElectoralProgresoService padronElectoralProgresoRepository, RestTemplate clientExport,
			BroadcastWebsocketServiceImpl broadcastWebsocketService,
			GestionarConstraintService gestionarConstraintService, ObjectMapper objectMapper) {
		this.padronElectoralService = padronElectoralRepository;
		this.padronElectoralProgresoService = padronElectoralProgresoRepository;
		this.clientExport = clientExport;
		this.broadcastWebsocketService = broadcastWebsocketService;
		this.gestionarConstraintService = gestionarConstraintService;
		this.objectMapper = objectMapper;
	}

	@Value("${sce.nacion.url}")
	private String urlNacion;

	private void reportProgress(float percent, String message, String estado) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("porcentaje", SceUtils.formatDecimal3Digitos(percent));
		payload.put("texto", message);
		payload.put("estado", estado);
		try {
			this.broadcastWebsocketService.broadcastProgressUpdate(BROADCAST_WS_PROGRESS_UPDATE_WITH_DETAILS,
					this.objectMapper.writeValueAsString(payload));
		} catch (JsonProcessingException e) {
			throw new WebsocketBroadcastException("Error al convertir payload a JSON para enviar por WebSocket", e);
		}
	}

	@Override
	public CompletableFuture<Void> migrar(String cc, String usuario, String proceso) {

		// truncate
		this.gestionarConstraintService.eliminarConstraintMiembroMesaSorteado();
		this.gestionarConstraintService.eliminarConstraintMiembroMesaCola();
		this.gestionarConstraintService.eliminarConstraintOmisoVotante();
		this.padronElectoralService.truncateTable();
		this.padronElectoralProgresoService.deleteAll();

		HttpHeaders headers = new HttpHeaders();
		headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
		headers.set(SceConstantes.TENANT_HEADER, proceso);
		headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		ResponseEntity<PaginaOptDto<MaePadronDto>> response;

		int tamanoPagina = 10000;
		Long lastId = 0L;
		int registrosProcesados = 0;
		PaginaOptDto<MaePadronDto> body = null;
		Integer totalRegistros = this.contar(cc, headers);
		
		logger.info("Total de registros {}", totalRegistros);

		do {
	
			response = this.importar(cc, tamanoPagina, lastId, headers);

			body = response.getBody();
			if (body == null) {
				throw new IllegalStateException("Respuesta nula desde servicio de exportación");
			}

			registrosProcesados += body.getData().size();

			this.reportProgress(
					(registrosProcesados * 100f) / totalRegistros, 
					"Migrando " + registrosProcesados + " registros de " +totalRegistros,
					ConstantesProgresoPadron.ESTADO_PROGRESO_CONTINUA);

			List<ImportPadronElectoral> list = body.getData().stream().map(UtilMapper::convertirPadron).toList();

			this.padronElectoralService.saveAll(list);

			lastId = body.getLastId();

		} while (body.isNext());

		logger.info("total de registros importados :{}", registrosProcesados);

		this.reportProgress(100f, "Finalizó la carga de datos", ConstantesProgresoPadron.ESTADO_PROGRESO_FINALIZA);

		logger.info("se inicia la creacion de los constraints");
		this.gestionarConstraintService.crearConstraintMiembroMesaSorteado();
		this.gestionarConstraintService.crearConstraintMiembroMesaCola();
		this.gestionarConstraintService.crearConstraintOmisoVotante();
		logger.info("se culmino la creacion de creacion de los constraints");

		return CompletableFuture.completedFuture(null);
	}
	
	private Integer contar(String cc, HttpHeaders headers){
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
		String urlCount = urlNacion + "padron-electoral/exportacion/orc/" + cc + "/count";

		ResponseEntity<Integer> countResponse = this.clientExport.exchange(
		        urlCount,
		        HttpMethod.GET,
		        httpEntity,
		        Integer.class
		);

		return countResponse.getBody();
	}
	
	private ResponseEntity<PaginaOptDto<MaePadronDto>> importar(String cc, int tamanoPagina, Long lastId, HttpHeaders headers){
		String url = urlNacion + "padron-electoral/exportacion/orc/opt/" + cc;
		UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance().uri(URI.create(url));
		urlBuilder.replaceQueryParam("tamanoPagina", tamanoPagina);
		urlBuilder.replaceQueryParam("lastId", lastId);
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
		return this.clientExport.exchange(
				urlBuilder.toUriString(), 
				HttpMethod.GET, 
				httpEntity,
				new ParameterizedTypeReference<PaginaOptDto<MaePadronDto>>() {
				});

	}
}
