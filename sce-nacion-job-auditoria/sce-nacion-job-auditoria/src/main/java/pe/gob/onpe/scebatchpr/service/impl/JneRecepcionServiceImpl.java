package pe.gob.onpe.scebatchpr.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebatchpr.entities.orc.ProcesoElectoral;
import pe.gob.onpe.scebatchpr.properties.SceNacionProperties;
import pe.gob.onpe.scebatchpr.repository.orc.ProcesoElectoralRepository;
import pe.gob.onpe.scebatchpr.service.JneRecepcionService;

@Service
@Slf4j
@RequiredArgsConstructor
public class JneRecepcionServiceImpl implements JneRecepcionService {

    private final SceNacionProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ProcesoElectoralRepository procesoElectoralRepository;

    private static final String FIELD_ID_SESSION = "idSession";
    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String FIELD_TOKEN = "token";

    @Override
    public boolean reenviarPendientes() {

        ProcesoElectoral cp = procesoElectoralRepository.findByActivo(1);

        if (cp == null) {
            throw new IllegalStateException("Proceso Electoral activo no encontrado");
        }

        String acronimo = cp.getAcronimo();
        Map<String, String> tokenData = obtenerToken(acronimo);

        String token = tokenData.get(FIELD_TOKEN);
        String idSession = tokenData.get(FIELD_ID_SESSION);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.add(FIELD_ID_SESSION, idSession);
        headers.add(TENANT_HEADER, acronimo);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(properties.getEndpointUrl(), HttpMethod.POST, request,
                String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    private Map<String, String> obtenerToken(String proceso) {
        String idSession = UUID.randomUUID().toString();

        Map<String, Object> body = Map.of("clientId", properties.getClientId(), "clientSecret",
                properties.getClientSecret(), "username", "sce-job", "autoridad", "JNE", "userId", 1, "perfilId", 91,
                "acronimoProceso", proceso, "codigoCentroComputo", "", "nombreCentroComputo", "", FIELD_ID_SESSION,
                idSession);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(properties.getEndpointClientTokenUrl(), HttpMethod.POST,
                request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            String token = json.get("access_token").asText();

            return Map.of(FIELD_TOKEN, token, FIELD_ID_SESSION, idSession);
        } catch (Exception e) {
            log.error("Error interno al obtenerToken ", e);
            throw new RuntimeException("Error parseando token", e);
        }
    }

}
