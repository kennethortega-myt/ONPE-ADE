package pe.gob.onpe.sceorcbackend.sasa.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.exception.SasaTimeoutException;
import pe.gob.onpe.sceorcbackend.model.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.SasaTokenService;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdUsuarioOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.CambiarContraseniaInputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.CambiarContraseniaOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginPerfilesOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginUsuarioOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.NuevaContraseniaInputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.NuevaContraseniaOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.RefreshTokenOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.service.SasaAuthService;
import pe.gob.onpe.sceorcbackend.sasa.service.SasaBaseService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Slf4j
@Service
public class SasaAuthServiceImpl extends SasaBaseService implements SasaAuthService {

    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final UsuarioRepository usuarioRepository;
    private final SasaTokenService sasaTokenService;

    @Value("${sasa.url}")
    private String url;

    @Value("${sasa.codigo}")
    private String codigo;

    @Value("${sasa.timeout-miliseconds}")
    private Long timeoutMiliseconds;

    public SasaAuthServiceImpl(
            @Qualifier("webClientSasa") WebClient webClient,
            RestTemplate restTemplate,
            UsuarioRepository usuarioRepository,
            SasaTokenService sasaTokenService) {
        super();
        this.webClient = webClient;
        this.restTemplate = restTemplate;
        this.usuarioRepository = usuarioRepository;
        this.sasaTokenService = sasaTokenService;
    }

    @Override
    public LoginDatosOutputDto accederSistema(LoginInputDto input) {
        LoginDatosOutputDto datos = null;

        input.setCodigo(codigo);
        input.setRecaptcha("xxxx");

        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<LoginInputDto> requestEntity = new HttpEntity<>(input,
                headers);
        try {
            ResponseEntity<LoginDatosOutputDto> responseLogin = restTemplate.exchange(url + "/usuario/loginsc",
                    HttpMethod.POST, requestEntity, LoginDatosOutputDto.class);
            datos = responseLogin.getBody();

            verificarYCambiarMensajeBloqueo(datos);

            // Guardando token SASA en el redis
            if (datos != null && datos.getResultado().equals(1) && datos.getDatos().getUsuario() != null) {
                this.sasaTokenService.addToken(datos.getDatos().getUsuario().getIdUsuario(),
                        datos.getDatos().getUsuario().getToken());
            }

        } catch (ResourceAccessException e) {
            // cuando el sasa no esta desplegado busca en el servicio de repaldo
            log.warn("SASA no disponible, usando accederSistemaInterno", e);
            datos = accederSistemaInterno(input);

        } catch (Exception e) {
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }
        return datos;
    }

    @Override
    public GenericResponse<String> refreshToken(TokenInfo tokenInfo) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<String> response = new GenericResponse<>();

        try {
            ResponseEntity<RefreshTokenOutputDto> res = webClient.post()
                    .uri(url + "/usuario/refreshtoken")
                    .headers(headers -> headers.addAll(this.authHeaders(optSasaToken.get())))
                    .retrieve()
                    .toEntity(RefreshTokenOutputDto.class)
                    .timeout(Duration.ofMillis(timeoutMiliseconds))
                    .onErrorMap(TimeoutException.class, e -> new SasaTimeoutException("TIMEOUT SASA", e))
                    .block();

            if (res.getStatusCode() == HttpStatus.OK) {
                if (res.getBody().getResultado().equals(1)) {
                    response.setSuccess(true);
                    response.setData(res.getBody().getToken());
                    response.setMessage(res.getBody().getMensaje());
                } else {
                    response.setSuccess(false);
                    response.setData(null);
                    response.setMessage(res.getBody().getMensaje());
                }
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Error SASA refreshToken");
            }
        } catch (WebClientRequestException | SasaTimeoutException e) {
            this.buildResourceAccessExceptionResponse(response);
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e);
        } catch (WebClientResponseException e) {
            if (this.tokenSasaIsExpired(e.getStatusCode())) {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Error SASA");
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error SASA refreshToken");
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        if (response.isSuccess()) {
            this.sasaTokenService.addToken(tokenInfo.getUserId(), response.getData());
        }

        return response;
    }

    @Override
    public GenericResponse<BuscarPorIdOutputDto> desbloquearUsuario(TokenInfo tokenInfo,
            BuscarPorIdUsuarioOutputDto usuarioData) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<BuscarPorIdOutputDto> response = new GenericResponse<>();

        usuarioData.setBloqueado(0);
        usuarioData.setFechaBloqueo(null);

        HttpEntity<BuscarPorIdUsuarioOutputDto> requestEntity = new HttpEntity<>(usuarioData,
                this.authHeaders(optSasaToken.get()));

        try {
            ResponseEntity<BuscarPorIdOutputDto> res = restTemplate.exchange(
                    url + "/usuario/actualizar",
                    HttpMethod.POST,
                    requestEntity,
                    BuscarPorIdOutputDto.class);

            if (res.getStatusCode() == HttpStatus.OK) {
                if (res.getBody().getSuccess().booleanValue()) {
                    response.setSuccess(true);
                    response.setData(res.getBody());
                    response.setMessage(res.getBody().getTitulo());
                } else {
                    response.setSuccess(false);
                    response.setData(null);
                    response.setMessage(res.getBody().getTitulo());
                }
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Error SASA");
            }

        } catch (ResourceAccessException e) {
            this.buildResourceAccessExceptionResponse(response);
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e);
        } catch (HttpClientErrorException e) {
            if (this.tokenSasaIsExpired(e.getStatusCode())) {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Error al actualizar datos SASA");
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error al actualizar datos SASA");
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        return response;
    }

    @Override
    public GenericResponse<Boolean> actualizarContrasenia(ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto,
            String usuario) {
        GenericResponse<Boolean> response = new GenericResponse<>();
        LoginDatosOutputDto u;
        try {
            validarContraseniaFuerte(actualizarNuevaClaveInputDto.getClaveNueva());
            LoginInputDto login = new LoginInputDto();
            login.setUsuario(usuario);
            login.setClave(actualizarNuevaClaveInputDto.getClaveActual());
            login.setCodigo(codigo);
            u = accederSistema(login);
        } catch (IllegalArgumentException e) {
            log.error("Validación de contraseña fallida: {}", e.getMessage());
            response.setMessage(e.getMessage());
            return response;
        } catch (Exception e) {
            log.error("Error al cambiar contrasenia {}", e.getMessage());
            response.setMessage("Error al cambiar contraseña.");
            return response;
        }

        var errorOutput = getLoginDatosOutputDtoError(u);
        if (errorOutput.isPresent()) {
            response.setMessage(errorOutput.get());
            return response;
        }

        CambiarContraseniaInputDto body = new CambiarContraseniaInputDto();
        body.setClave(actualizarNuevaClaveInputDto.getClaveNueva());

        HttpEntity<CambiarContraseniaInputDto> requestEntity = new HttpEntity<>(body,
                this.authHeaders(u.getDatos().getUsuario().getToken()));
        CambiarContraseniaOutputDto data = null;
        try {
            ResponseEntity<CambiarContraseniaOutputDto> responseLogin = restTemplate.exchange(
                    url + "/usuario/actualizar-nueva-clave",
                    HttpMethod.POST,
                    requestEntity,
                    CambiarContraseniaOutputDto.class);
            data = responseLogin.getBody();
        } catch (ResourceAccessException e) {
            // cuando el sasa no esta desplegado para cambio de contrasenia, se busca en el
            // servicio de repaldo
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e);
        } catch (Exception e) {
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }

        response.setData(data != null && data.getSuccess() == 1);
        if (response.getData().equals(Boolean.TRUE)) {
            response.setMessage("Usuario actualizado con éxito.");
            try {
                actualizarContraseniaInterno(usuario, actualizarNuevaClaveInputDto.getClaveNueva());
            } catch (Exception e) {
                log.error("Error al actualizar la contraseña interna {}", e.getMessage());
            }
        } else {
            response.setMessage("No se pudo actualizar la contraseña.");
        }
        return response;
    }

    @Override
    public GenericResponse<Boolean> nuevaContraseniaSasa(TokenInfo tokenInfo,
            NuevaClaveInputDto actualizarNuevaClaveInputDto) {
        final var optSasaToken = this.sasaTokenService.getToken(tokenInfo.getUserId());
        if (optSasaToken.isEmpty()) {
            throw new IllegalArgumentException(ConstantesComunes.SASA_TOKEN_NO_EXISTENTE);
        }

        GenericResponse<Boolean> response = new GenericResponse<>();
        response.setSuccess(false);
        response.setData(false);

        var body = NuevaContraseniaInputDto.builder()
                .contrasenia(actualizarNuevaClaveInputDto.getClaveNueva())
                .confirmaContrasenia(actualizarNuevaClaveInputDto.getClaveNueva())
                .build();

        try {
            validarContraseniaFuerte(actualizarNuevaClaveInputDto.getClaveNueva());
            ResponseEntity<NuevaContraseniaOutputDto> res = webClient.post()
                    .uri(url + "/usuario/nueva-contrasenia-sasa")
                    .headers(headers -> headers.addAll(this.authHeaders(optSasaToken.get())))
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(NuevaContraseniaOutputDto.class)
                    .timeout(Duration.ofMillis(timeoutMiliseconds))
                    .onErrorMap(TimeoutException.class, e -> new SasaTimeoutException("TIMEOUT SASA", e))
                    .block();

            if (res.getStatusCode() == HttpStatus.OK) {
                if (Boolean.TRUE.equals(res.getBody().getSuccess())) {
                    response.setSuccess(true);
                    response.setData(true);
                    response.setMessage(res.getBody().getMessage());
                } else {
                    response.setMessage(res.getBody().getMessage());
                }
            } else {
                response.setMessage("Error SASA nuevaContraseniaSasa");
            }
        } catch (WebClientRequestException | SasaTimeoutException e) {
            this.buildResourceAccessExceptionResponse(response);
            log.warn(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e);
        } catch (WebClientResponseException e) {
            if (this.tokenSasaIsExpired(e.getStatusCode())) {
                response.setMessage(ConstantesComunes.SASA_TOKEN_EXPIRADO_MENSAJE);
            } else {
                response.setMessage("Error SASA nuevaContraseniaSasa");
                log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
            }
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        } catch (Exception e) {
            response.setMessage("Error SASA nueva-contrasenia-sasa");
            log.error(ConstantesComunes.MENSAJE_LOGGER_ERROR, e);
        }
        return response;
    }

    private LoginDatosOutputDto accederSistemaInterno(LoginInputDto input) {
        LoginDatosOutputDto login = new LoginDatosOutputDto();
        login.setDatos(null);
        login.setResultado(0);
        Usuario usuario = this.usuarioRepository.findByUsuario(input.getUsuario());
        if (usuario == null) {
            login.setMensaje("El usuario no se encuentra registrado.");
        } else if (usuario.getPerfil() == null) {
            login.setMensaje("El usuario no dispone de un perfil para acceder al sistema.");
        } else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(input.getClave(), usuario.getCodigo1())) {
                login.setResultado(1);
                LoginOutputDto loginOutputDto = new LoginOutputDto();
                LoginUsuarioOutputDto loginUsuarioOutputDto = new LoginUsuarioOutputDto();
                loginUsuarioOutputDto.setIdUsuario(usuario.getIdUsuario());
                loginUsuarioOutputDto.setAcronimoProceso(usuario.getAcronimoProceso());
                loginUsuarioOutputDto.setNombreCentroComputo(usuario.getNombreCentroComputo());
                loginUsuarioOutputDto.setCodigoCentroComputo(usuario.getCentroComputo());
                loginUsuarioOutputDto.setClaveNueva(0);
                loginUsuarioOutputDto.setNombres(usuario.getNombres());
                loginUsuarioOutputDto.setPersonaAsignada(usuario.getPersonaAsignada());
                loginUsuarioOutputDto.setToken("CANTPROVIDETOKEN");

                List<LoginPerfilesOutputDto> loginPerfilesOutputDtos = new ArrayList<>();
                // aqui normalmente se obtiene un arreglo de sasa pero segun lo indicando para
                // autogenerados solo es 1 perfil
                LoginPerfilesOutputDto loginPerfilesOutputDto = new LoginPerfilesOutputDto();
                loginPerfilesOutputDto.setAbreviatura(usuario.getPerfil());
                loginPerfilesOutputDto.setIdPerfil(usuario.getIdPerfil());
                loginPerfilesOutputDtos.add(loginPerfilesOutputDto);

                loginOutputDto.setPerfiles(loginPerfilesOutputDtos);
                loginOutputDto.setUsuario(loginUsuarioOutputDto);
                login.setDatos(loginOutputDto);
                login.setMensaje("La operación se ejecutó correctamente");
            } else {
                login.setMensaje("La contraseña ingresada no es correcta.");
            }
        }
        return login;
    }

    private CambiarContraseniaOutputDto actualizarContraseniaInterno(String username, String nuevaClave) {
        CambiarContraseniaOutputDto contraseniaOutputDto = new CambiarContraseniaOutputDto();
        contraseniaOutputDto.setSuccess(-1);
        contraseniaOutputDto.setMensaje("No se puede realizar el cambio de contraseña.");
        Usuario usuarioActualizarContrasenia = this.usuarioRepository.findByUsuario(username);
        if (usuarioActualizarContrasenia == null) {
            contraseniaOutputDto.setMensaje("El usuario no se encuentra registrado.");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String nuevaClaveEnconde = passwordEncoder.encode(nuevaClave);
            usuarioActualizarContrasenia.setCodigo1(nuevaClaveEnconde);
            this.usuarioRepository.save(usuarioActualizarContrasenia);
            contraseniaOutputDto.setSuccess(1);
            contraseniaOutputDto.setMensaje("Actualización exitosa");
        }
        return contraseniaOutputDto;
    }

    private void validarContraseniaFuerte(String claveNueva) throws IllegalArgumentException {

        if (claveNueva == null || claveNueva.isEmpty() || claveNueva.length() < 8 || claveNueva.length() > 20) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 20 caracteres");
        }

        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?]).+$";
        if (!claveNueva.matches(pattern)) {
            throw new IllegalArgumentException(
                    "La contraseña debe contener mayúscula, minúscula, número y carácter especial");
        }
    }

    private Optional<String> getLoginDatosOutputDtoError(LoginDatosOutputDto u) {
        if ((u.getResultado() > 0 &&
                u.getDatos() != null &&
                u.getDatos().getUsuario() != null &&
                u.getDatos().getUsuario().getClaveNueva() == 1 &&
                u.getDatos().getUsuario().getToken() != null)) {
            return Optional.empty();
        }
        String error;
        if (u.getDatos().getUsuario().getToken() == null) {
            error = ConstantesComunes.SASA_ERROR_CAMBIO_CONSTRASENIA;
        } else if (u.getResultado() > 0 &&
                u.getDatos() != null &&
                u.getDatos().getUsuario() != null &&
                u.getDatos().getUsuario().getClaveNueva() < 1) {
            error = "El usuario no requiere cambio de contraseña.";
        } else if (u.getResultado() == -1) {
            error = "La contraseña actual ingresada no es correcta.";
        } else if (u.getMensaje() != null) {
            error = u.getMensaje();
        } else {
            error = ConstantesComunes.SASA_ERROR_CAMBIO_CONSTRASENIA;
        }
        return Optional.of(error);
    }

    private void verificarYCambiarMensajeBloqueo(LoginDatosOutputDto datos) {
        if (datos != null
                && datos.getResultado() == -1
                && datos.getMensaje() != null
                && datos.getMensaje().toLowerCase().contains("bloquead")) {
            datos.setMensaje(
                    "Su cuenta ha sido bloqueada por superar el número máximo de intentos fallidos. Comuníquese con su administrador.");
        }
    }

}
