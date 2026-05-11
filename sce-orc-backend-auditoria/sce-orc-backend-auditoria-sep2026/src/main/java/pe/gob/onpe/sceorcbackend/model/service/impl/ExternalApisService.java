package pe.gob.onpe.sceorcbackend.model.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.model.dto.util.HashModelo;
import pe.gob.onpe.sceorcbackend.model.service.IExternalApiService;


@Service
public class ExternalApisService implements IExternalApiService {

    Logger logger = LoggerFactory.getLogger(ExternalApisService.class);

    @Value("${api.url.modelo}")
    private String url;

    private final RestTemplate restTemplate;

    public ExternalApisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public HashModelo modeloIntegrityHash() {
        try {
            return this.restTemplate.postForObject(this.url.concat("/models-integrity-hash"), null, HashModelo.class);
        } catch (Exception ex) {
            throw new GenericException(ex.getMessage());
        }

    }
}
