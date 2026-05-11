package pe.gob.onpe.scebackend.model.service.impl;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import pe.gob.onpe.scebackend.model.orc.entities.PuestaCeroPr;
import pe.gob.onpe.scebackend.model.orc.repository.PuestaCeroPrRepository;
import pe.gob.onpe.scebackend.model.puestocero.pr.dto.GenericResponse;
import pe.gob.onpe.scebackend.model.puestocero.pr.dto.TramaScePuestaCeroDto;
import pe.gob.onpe.scebackend.model.service.PuestaCeroPrService;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesPuestaCeroPr;

@Service
public class PuestaCeroPrServiceImpl implements PuestaCeroPrService {

	Logger logger = LogManager.getLogger(PuestaCeroPrServiceImpl.class);
	
	private static final String PATH_PUESTO_CERO = "trama-sce/puesta-cero";
	
	@Value("${sce.pr-admin.url}")
    private String baseUrlPr;
	
	@Value("${sce.pr-admin.pc.token}")
    private String token;

    private final RestTemplate clientExport;

	private final PuestaCeroPrRepository puestaCeroPrRepository;
	
	public PuestaCeroPrServiceImpl(
			RestTemplate clientExport,
			PuestaCeroPrRepository puestaCeroPrRepository){
		this.clientExport = clientExport;
		this.puestaCeroPrRepository = puestaCeroPrRepository;
	}
	
	@Override
	@Transactional(
			transactionManager = "locationTransactionManager", 
			rollbackFor = Exception.class)
	public boolean puestoCeroPr(String usuarioSce, Long id){
		boolean rpta = false;
		try{
			
			URI baseUri = URI.create(baseUrlPr);

			String url = UriComponentsBuilder
			    .fromUri(baseUri)
			    .path(PATH_PUESTO_CERO)
			    .queryParam("usuarioSce", usuarioSce)
			    .toUriString();

			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token);
		    headers.set(ConstantesComunes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		    
		    HttpEntity<Void> entity = new HttpEntity<>(headers);
			ResponseEntity<GenericResponse<TramaScePuestaCeroDto>> response = this.clientExport.exchange(
					url,
				    HttpMethod.GET,
				    entity,
				    new ParameterizedTypeReference<GenericResponse<TramaScePuestaCeroDto>>() {}
				);
			
			if (response.getStatusCode() == HttpStatus.OK) {
				GenericResponse<TramaScePuestaCeroDto> body = response.getBody();
				if(body!=null){
					logger.info("Se realizo correctamente la puesta cero, mensaje={}", body.getMessage());
			        logger.info("Se realizo correctamente la puesta cero, response={}", body.getData());
				}
		        this.registrarPuestaCeroPr(id, usuarioSce, true);
		        rpta = true;
		    }
		} catch (HttpClientErrorException ex) {
		    if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
		        String responseBody = ex.getResponseBodyAsString();
		        logger.info("Se realizo correctamente la puesta cero, response={}",responseBody);
		        this.registrarPuestaCeroPr(id, usuarioSce, false);
		    } else {
		    	logger.error("Se genero un error", ex);
		    	this.registrarPuestaCeroPr(id, usuarioSce, false);
		    }
		} catch (Exception ex) {
			logger.error("Error", ex);
			this.registrarPuestaCeroPr(id, usuarioSce, false);
		}
		return rpta;
	}

	public void registrarPuestaCeroPr(Long id, String usuarioSce,  boolean exitoso){
		PuestaCeroPr puestaCeroPr = null;
		if(id==null){
			Date fechaActual = DateUtil.getFechaActualPeruana();
			puestaCeroPr = new PuestaCeroPr();
			puestaCeroPr.setUsuarioCreacion(usuarioSce);
			puestaCeroPr.setFechaCreacion(fechaActual);
			puestaCeroPr.setUsuarioModificacion(usuarioSce);
			puestaCeroPr.setFechaModificacion(fechaActual);
			puestaCeroPr.setActivo(ConstantesPuestaCeroPr.ACTIVO);
		} else {
			Optional<PuestaCeroPr> opPuestaCeroPr = this.puestaCeroPrRepository.findById(id);
			if(opPuestaCeroPr.isPresent()){
				puestaCeroPr = opPuestaCeroPr.get();
			}
		}
		
		if(puestaCeroPr!=null){
			if(exitoso){
				puestaCeroPr.setIntento(ConstantesPuestaCeroPr.INTENTO_INICIAL);
				puestaCeroPr.setEstado(ConstantesPuestaCeroPr.EJECUTADO);
			} else {
				puestaCeroPr.setIntento(puestaCeroPr.getIntento()!=null ? (puestaCeroPr.getIntento()+1) : ConstantesPuestaCeroPr.PRIMER_INTENTO);
				puestaCeroPr.setEstado(ConstantesPuestaCeroPr.NO_EJECUTADO);
			}
			this.puestaCeroPrRepository.save(puestaCeroPr);
		}
	}
	
}
