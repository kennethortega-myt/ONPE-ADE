package pe.gob.onpe.sceorcbackend.sasa.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

public abstract class SasaBaseService {

    /**
     * SASA DEVUELVE LOS CODIGOS 403 O 405 CUANDO EL TOKEN EXPIRO O EL TOKEN ES
     * INVALIDO
     */
    protected boolean tokenSasaIsExpired(HttpStatusCode status) {
        return ConstantesComunes.SASA_TOKEN_INVALIDO_O_EXPIRADO_HTTP_STATUS.contains(status);
    }

    protected HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    protected void buildResourceAccessExceptionResponse(GenericResponse<?> response) {
        response.setSuccess(false);
        response.setData(null);
        response.setMessage(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE);
    }

}
