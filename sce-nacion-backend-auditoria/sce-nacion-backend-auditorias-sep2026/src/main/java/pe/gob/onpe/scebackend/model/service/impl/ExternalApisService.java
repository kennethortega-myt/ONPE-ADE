package pe.gob.onpe.scebackend.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.AreaDTO;
import pe.gob.onpe.scebackend.model.dto.request.CoordenadasRequestDTO;
import pe.gob.onpe.scebackend.model.service.IExternalApiService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ExternalApisService implements IExternalApiService {

    Logger logger = LoggerFactory.getLogger(ExternalApisService.class);

    @Value("${coordenada.url}")
    private String url;

    private final RestTemplate restTemplate;

    public ExternalApisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<AreaDTO> computeRelativeCoordinates(CoordenadasRequestDTO request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CoordenadasRequestDTO> requestEntity = new HttpEntity<>(request, headers);

            AreaDTO[] areas = this.restTemplate.postForObject(this.url.concat("/compute-relative-coordinates"), requestEntity, AreaDTO[].class);
            // Validar nulos y convertir a lista
            List<AreaDTO> listaAreas = areas != null ? Arrays.asList(areas) : Collections.emptyList();

            // Ajustar negativos en cada área
            for (AreaDTO area : listaAreas) {
                if (area != null) {
                    area.ajustarNegativos();
                }
            }
            return listaAreas;
        } catch (Exception ex) {
            throw new GenericException(ex.getMessage());
        }

    }
}
