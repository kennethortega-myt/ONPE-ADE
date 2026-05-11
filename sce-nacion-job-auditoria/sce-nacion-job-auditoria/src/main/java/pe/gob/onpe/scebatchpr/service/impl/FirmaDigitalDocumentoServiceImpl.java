package pe.gob.onpe.scebatchpr.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebatchpr.exceptions.FirmaDigitalException;
import pe.gob.onpe.scebatchpr.properties.FirmaDigitalDocConfigProperties;
import pe.gob.onpe.scebatchpr.properties.FirmaDigitalDocParamProperties;
import pe.gob.onpe.scebatchpr.service.FirmaDigitalDocumentoService;

@Service
public class FirmaDigitalDocumentoServiceImpl implements FirmaDigitalDocumentoService {

	Logger logger = LogManager.getLogger(FirmaDigitalDocumentoServiceImpl.class);

	private final FirmaDigitalDocParamProperties properties;
	
	private final FirmaDigitalDocConfigProperties config;

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;
	
	public FirmaDigitalDocumentoServiceImpl(
			FirmaDigitalDocParamProperties properties,
			FirmaDigitalDocConfigProperties config,
			ObjectMapper objectMapper,
			RestTemplate restTemplate
			){
		this.properties = properties;
		this.config = config;
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}
	
	@Override
	public InputStream firmarArchivo(String pathPdf) throws JsonProcessingException {

		logger.info("Se inicia el firmado del documento con los siguientes parametros");
		InputStream inputStream = null;
		Resource resource = new ClassPathResource("firmaDigital/firmaDigital.png");

		FileSystemResource archivo = new FileSystemResource(pathPdf);
		logger.info("ruta a firmar: {}", pathPdf);

        String paramJson = objectMapper.writeValueAsString(properties);
        logger.info("paramJson: {}", paramJson);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("credential", config.getCredential());
        form.add("document", archivo);
        form.add("stamp", resource);
        form.add("param", paramJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(form, headers);

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
        		config.getUrl(),
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
        	byte[] archivoFirmado= responseEntity.getBody();
            inputStream = new ByteArrayInputStream(archivoFirmado);
        } else {
        	throw new FirmaDigitalException("Ocurrió un error inesperado en el servicio para firmar documentos");
        }
		return inputStream;
	}

}
