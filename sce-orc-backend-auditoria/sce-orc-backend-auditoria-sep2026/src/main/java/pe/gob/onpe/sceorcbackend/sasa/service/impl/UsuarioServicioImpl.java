package pe.gob.onpe.sceorcbackend.sasa.service.impl;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.exception.SasaTimeoutException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.usuario.UsuarioUpdateRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.SasaTokenService;
import pe.gob.onpe.sceorcbackend.sasa.dto.*;
import pe.gob.onpe.sceorcbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServicioImpl implements UsuarioServicio {

    @Qualifier("webClientSasa")
    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final SasaTokenService sasaTokenService;

    @Value("${sasa.url}")
    private String url;

    @Value("${sasa.codigo}")
    private String codigo;

    @Value("${sasa.timeout-miliseconds}")
    private Long timeoutMiliseconds;

    @Override
    public CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input, String token) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", token);

            HttpEntity<CargarAccesosInputDto> requestEntity = new HttpEntity<>(input, headers);

            ResponseEntity<CargarAccesoDatosOutputDto> responseLogin = restTemplate.exchange(url + "/usuario/cargar-accesos",
                    HttpMethod.POST, requestEntity, CargarAccesoDatosOutputDto.class);

            return responseLogin.getBody();
        } catch (Exception e) {
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR,e.getMessage());
            return null;
        }
    }

    @Override
    public GenericResponse<BuscarPorIdOutputDto> buscarPorId(TokenInfo tokenInfo, Integer usuarioId) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<BuscarPorIdOutputDto> response = new GenericResponse<>();

        BuscarPorIdInputDto body = BuscarPorIdInputDto.builder()
                .id(usuarioId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(optSasaToken.get());

        try {
            ResponseEntity<BuscarPorIdOutputDto> res = webClient.post()
                    .uri(url + "/usuario/buscar-por-id")
                    .headers(h -> h.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(BuscarPorIdOutputDto.class)
                    .timeout(Duration.ofMillis(timeoutMiliseconds))
                    .onErrorMap(TimeoutException.class, SasaTimeoutException::new)
                    .block();
            if (res.getStatusCode() == HttpStatus.OK) {
                response.setSuccess(true);
                response.setData(res.getBody());
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Error SASA");
            }
        } catch (WebClientRequestException | SasaTimeoutException e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE);
            // SASA no desplegado
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e.getMessage());
        } catch (WebClientResponseException e) {
            if (this.validarTokenSasa(e.getStatusCode())) {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        return response;
    }

    @Override
    public GenericResponse<Boolean> actualizarUsuario(TokenInfo tokenInfo, Usuario usuario,
            UsuarioUpdateRequestDto usuarioDto) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<Boolean> response = new GenericResponse<>();

        var buscarPorIdResponse = this.buscarPorId(tokenInfo, usuario.getIdUsuario());
        if (!buscarPorIdResponse.isSuccess()) {
            response.setSuccess(false);
            response.setData(false);
            response.setMessage(buscarPorIdResponse.getMessage());
            return response;
        }

        BuscarPorIdUsuarioOutputDto body = buscarPorIdResponse.getData().getData();
        body.getPersona().setTipoDocumento(usuarioDto.getTipoDocumento());
        body.getPersona().setNumeroDocumento(usuarioDto.getDocumento());
        body.getPersona().setApellidoPaterno(usuarioDto.getApellidoPaterno());
        body.getPersona().setApellidoMaterno(usuarioDto.getApellidoMaterno());
        body.getPersona().setNombres(usuarioDto.getNombres());
        body.getPersona().setCorreo(usuarioDto.getCorreo());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(optSasaToken.get());

        ActualizarAutogeneradoOutputDto data = null;

        try {
            ResponseEntity<ActualizarAutogeneradoOutputDto> res = webClient.post()
                    .uri(url + "/usuario/actualizar-autogenerado")
                    .headers(h -> h.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(ActualizarAutogeneradoOutputDto.class)
                    .timeout(Duration.ofMillis(timeoutMiliseconds))
                    .onErrorMap(TimeoutException.class, SasaTimeoutException::new)
                    .block();
            data = res.getBody();

            if (res.getStatusCode() == HttpStatus.OK) {
                if (data.getSuccess().equals(Boolean.TRUE)) {
                    response.setSuccess(data.getSuccess());
                    response.setData(data.getSuccess());
                    response.setMessage(data.getMessage());
                } else {
                    // Error al actualizar (Nro Documento, etc)
                    response.setSuccess(false);
                    response.setData(false);
                    response.setMessage(res.getBody().getMessage());
                }
            } else {
                response.setSuccess(false);
                response.setData(false);
                response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
            }

        } catch (WebClientRequestException | SasaTimeoutException e) {
            response.setSuccess(false);
            response.setData(false);
            response.setMessage(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE);
            // SASA no desplegado
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e.getMessage());
        } catch (WebClientResponseException e) {
            if (this.validarTokenSasa(e.getStatusCode())) {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setSuccess(false);
                response.setData(false);
                response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(false);
            response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        return response;
    }

    @Override
    public GenericResponse<Boolean> restablecerContrasenia(TokenInfo tokenInfo, Integer usuarioId) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<Boolean> response = new GenericResponse<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(optSasaToken.get());

        RestablecerContraseniaInputDto body = RestablecerContraseniaInputDto.builder()
                .codigoAplicacion(this.codigo)
                .id(usuarioId)
                .build();
        try {
            ResponseEntity<RestablecerContraseniaOutputDto> res = webClient.post()
                    .uri(url + "/usuario/restablecer-contrasenia")
                    .headers(h -> h.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(RestablecerContraseniaOutputDto.class)
                    .timeout(Duration.ofMillis(timeoutMiliseconds))
                    .onErrorMap(TimeoutException.class, SasaTimeoutException::new)
                    .block();
            var data = res.getBody();
            if (res.getStatusCode() == HttpStatus.OK) {
                if (data.getSuccess().equals(Boolean.TRUE)) {
                    response.setSuccess(data.getSuccess());
                    response.setData(data.getSuccess());
                    response.setMessage(data.getMessage());
                } else {
                    response.setSuccess(false);
                    response.setData(false);
                    response.setMessage(res.getBody().getMessage());
                }
            }
        } catch (WebClientRequestException | SasaTimeoutException e) {
            response.setSuccess(false);
            response.setData(false);
            response.setMessage(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE);
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e.getMessage());
        } catch (WebClientResponseException e) {
            if (this.validarTokenSasa(e.getStatusCode())) {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setSuccess(false);
                response.setData(false);
                response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(false);
            response.setMessage(ConstantesComunes.SASA_ERROR_ACTUALIZAR_DATOS);
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        return response;
    }

    /**
     * SASA DEVUELVE LOS CODIGOS 403 O 405 CUANDO EL TOKEN EXPIRO O EL TOKEN ES
     * INVALIDO
     */
    private boolean validarTokenSasa(HttpStatusCode status) {
        return ConstantesComunes.SASA_TOKEN_INVALIDO_O_EXPIRADO_HTTP_STATUS.contains(status);
    }

}