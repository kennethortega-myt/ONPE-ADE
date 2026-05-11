package pe.gob.onpe.sceorcbackend.rest.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionDataService;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ActaElectoralRequestDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ActaElectoralResponse;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.DocumentoElectoralRequest;
import pe.gob.onpe.sceorcbackend.model.stae.dto.MesaElectoresRequestDto;
import pe.gob.onpe.sceorcbackend.model.stae.dto.ResultadoPs;
import pe.gob.onpe.sceorcbackend.model.stae.exception.ActaStaeNoEncontradaException;
import pe.gob.onpe.sceorcbackend.model.stae.service.StaeService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@RequestMapping("/stae/")
@Controller
public class StaeController {

	Logger logger = LoggerFactory.getLogger(StaeController.class);
	
	private static final String ACRONIMO_PROCESO = "acronimoproceso";

	private final StaeService staeService;
	
	private final ActaTransmisionDataService transmisionData;
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	@Value("${desarrollo.integracion}")
	private boolean desarrolloIntegracion;

	public StaeController(
			StaeService staeService,
			ActaTransmisionDataService transmisionData) {
		this.staeService = staeService;
		this.transmisionData = transmisionData;
	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/acta")
	public ResponseEntity<GenericResponse> insertarActa(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestHeader("user") String usuario,
			@RequestHeader("codigocc") String cc,
			@RequestHeader(ACRONIMO_PROCESO) String acronimoProceso,
			@RequestBody ActaElectoralRequestDto actaDto) {

		String acta = actaDto.getNumeroActa() +
				Optional.ofNullable(actaDto.getNumeroCopiaEscrutinio())
						.orElse(ConstantesComunes.VACIO);

		logger.info("Recibio el acta {}, para el CC {}.",acta, cc);
		
		GenericResponse<ActaElectoralResponse> genericResponse = new GenericResponse<>();
		boolean autenticacion = this.staeService.validarTokenStae(authorization, cc);
		logger.info("La autenticacion para insertar una acta de STAE/VD en CC fue {}", autenticacion ? "EXITOSA": "ERRONEA");
		if (autenticacion) {

			try {
				
				List<DocumentoElectoralDto> archivos = actaDto.getArchivos();
				
				actaDto.setArchivos(null);
				String jsonString = this.getJsonString(actaDto);
				
				Optional<Acta> actaOp = this.staeService.getActa(actaDto);
				
				if(!actaOp.isPresent()){
					throw new ActaStaeNoEncontradaException("Acta no encontrada");
				}
				
				logger.info("Se iniciara la insercion del acta {}", actaOp.get().getId());
				
				ResultadoPs resultadopc = this.staeService.insertActaStae(
						schema, 
						desarrolloIntegracion,
						jsonString, 
						usuario);
				
				logger.info("Se finalizo la insercion del acta {}", actaOp.get().getId());
				
				logger.info("Se iniciara la insercion de los documentos electorales del acta {}", actaOp.get().getId());
				
				this.staeService.guardarDocumentosElectorales(actaOp.get().getId(), archivos, usuario);
				
				logger.info("Se finalizara la insercion de los documentos electorales del acta {}", actaOp.get().getId());
				
				logger.info("Se iniciara la insercion en la tabla transmision del acta {}", actaOp.get().getId());
				
				this.transmisionData.guardarTransmisionStae(
						actaOp.get().getId(), 
						TransmisionNacionEnum.INITIAL_TRANSMISION,
						acronimoProceso, 
						usuario);
				
				logger.info("Se finalizo la insercion en la tabla transmision del acta {}", actaOp.get().getId());
				
				this.staeService.sendProcessActaStae(
						actaOp.get(),
						usuario,
						cc
						);
				
				if (resultadopc.getPoResultado().equals(SceConstantes.ACTIVO)) {
					ActaElectoralResponse response = new ActaElectoralResponse();
					response.setEstadoActa(resultadopc.getPoEstadoActa());
					response.setEstadoCompu(resultadopc.getPoEstadoComputo());
					response.setEstadoActaResolucion(resultadopc.getPoEstadoActaResolucion());
					response.setEstadoErrorAritmetico(resultadopc.getPoEstadoErrorMaterial());
					response.setTipoTransmision(resultadopc.getPoTipoTransmision());
					genericResponse.setSuccess(Boolean.TRUE);
					genericResponse.setMessage(resultadopc.getPoMensaje());
					genericResponse.setData(response);
					return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
				} else {
					genericResponse.setSuccess(Boolean.FALSE);
					genericResponse.setMessage(resultadopc.getPoMensaje());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
				}
			}
			catch (ActaStaeNoEncontradaException e) {
				logger.error("Error en recibir el acta stae/vd", e);
				genericResponse.setSuccess(Boolean.FALSE);
				genericResponse.setMessage(e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
			}
			catch (Exception e) {
				logger.error("Error en recibir el acta stae/vd", e);
				genericResponse.setSuccess(Boolean.FALSE);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
			}
			

		} else {
			genericResponse.setMessage("Token Inválido");
			genericResponse.setSuccess(Boolean.FALSE);
			return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
					.body(genericResponse);
		}

	}

	@SuppressWarnings("rawtypes")
	@PostMapping("/lista-electores")
	public ResponseEntity<GenericResponse> insertarLe(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestHeader("user") String usuario,
			@RequestHeader("codigocc") String cc,
			@RequestBody MesaElectoresRequestDto leDto) {

		GenericResponse genericResponse = new GenericResponse();
		boolean autenticacion = this.staeService.validarTokenStae(authorization, cc);
		logger.info("La autenticacion para insertar una lista de electores STAE/VD en CC fue {}", autenticacion ? "EXITOSA": "ERRONEA");
		if (autenticacion) {
			try {
				String jsonString = this.getJsonString(leDto);
				ResultadoPs resultadopc = this.staeService.insertListaElectoresStae(
						schema, 
						desarrolloIntegracion,
						jsonString,
						usuario);
				genericResponse.setMessage(resultadopc.getPoMensaje());
				if (resultadopc.getPoResultado().equals(SceConstantes.ACTIVO)) {
					genericResponse.setSuccess(Boolean.TRUE);
					return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
				} else {
					genericResponse.setSuccess(Boolean.FALSE);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
				}
			} catch (Exception e) {
				logger.error("Error en recibir la lista de electores stae/vd", e);
				genericResponse.setSuccess(Boolean.FALSE);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
			}

		} else {
			genericResponse.setMessage("Token Inválido");
			genericResponse.setSuccess(Boolean.FALSE);
			return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
					.body(genericResponse);
		}

	}
	
	@SuppressWarnings("rawtypes")
	@PostMapping("/documentos-electorales")
	public ResponseEntity<GenericResponse> insertarDocumentosElectorales(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@RequestBody DocumentoElectoralRequest request) {

		GenericResponse genericResponse = new GenericResponse();
		logger.info("Se recibieron los archivos stae/vd");
		
		try {
			staeService.guardarDocumentosElectorales(request, authorization);
			genericResponse.setSuccess(Boolean.TRUE);
			return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
		} catch (Exception e) {
			logger.error("Error en recibir los archivos stae/vd", e);
			genericResponse.setSuccess(Boolean.FALSE);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
		}
		

	}
	
	
	
	private String getJsonString(Object jsonDto) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
	    return mapper.writeValueAsString(jsonDto);
	}

}
