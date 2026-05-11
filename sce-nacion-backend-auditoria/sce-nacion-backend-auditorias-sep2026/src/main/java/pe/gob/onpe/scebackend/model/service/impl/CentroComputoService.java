package pe.gob.onpe.scebackend.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ParametroConexionFiltroDto;
import pe.gob.onpe.scebackend.model.dto.PingConexionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.service.ICentroComputoService;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Base64;
import java.util.Collections;

@Service
public class CentroComputoService implements ICentroComputoService {

	Logger logger = LoggerFactory.getLogger(CentroComputoService.class);
	
    private final CentroComputoRepository centroComputoRepository;
    
    private final RestTemplate restTemplate;
    
    @Value("${sce.cc.url-ping}")
	private String urlEndpoint;

    public CentroComputoService(
    		CentroComputoRepository centroComputoRepository,
    		RestTemplate restTemplate) {
        this.centroComputoRepository = centroComputoRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional("locationTransactionManager")
    public void save(CentroComputo centroComputo) {
        this.centroComputoRepository.save(centroComputo);
    }

    @Override
    @Transactional("locationTransactionManager")
    public void saveAll(List<CentroComputo> k) {
    	this.centroComputoRepository.saveAll(k);
    }

    @Override
    @Transactional("locationTransactionManager")
    public void deleteAll() {
    	this.centroComputoRepository.deleteAll();
    }

    @Override
    @Transactional("locationTransactionManager")
    public List<CentroComputo> findAll() {
        return this.centroComputoRepository.findAll();
    }
    
    @Override
    @Transactional("locationTransactionManager")
    public List<CentroComputo> findAll(ParametroConexionFiltroDto dto) {
        if (dto != null && dto.getIdCentroComputo() == null) {
            return centroComputoRepository.findByCentroComputoPadreIsNotNullOrderByCodigoAsc();
        } else if (dto != null && dto.getIdCentroComputo() != null) {
            return centroComputoRepository.findById(dto.getIdCentroComputo())
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional("locationTransactionManager")
    public CentroComputo getCentroComputoByPk(Long id) {
        try {
            Optional<CentroComputo> opt = this.centroComputoRepository.findById(id);
            return opt.orElse(null);
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }

    @Override
    @Transactional("locationTransactionManager")
    public CentroComputo getPadreNacion() {
        try {
            return this.centroComputoRepository.findByCentroComputoPadreIsNull();
        } catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
    }
    
    public String generarToken(Integer byteLength){
    	SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];  // tamaño del token
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

	@Override
	public boolean ping(PingConexionDto dto) {
		boolean exitoso = false;
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PingConexionDto> entity = new HttpEntity<>(dto, headers);
        String urlBase = String.format("%s://%s:%d", dto.getProtocolo(),
        		dto.getIp(), 
        		dto.getPuerto());
        String url = String.format("%s%s", 
        		urlBase, 
        		urlEndpoint);
        logger.info("url del endpoint que permite verificar la conexion al CC: {}", url);
        try {
	        ResponseEntity<GenericResponse> response = restTemplate.exchange(
	                url,
	                HttpMethod.POST,
	                entity,
	                GenericResponse.class
	        );
	        exitoso = response.getStatusCode() == HttpStatus.OK;
	        return exitoso;
        } catch (Exception e) {
        	logger.error("Error al enviar el  acta a CC", e);
            return exitoso;
        }
	}
}
