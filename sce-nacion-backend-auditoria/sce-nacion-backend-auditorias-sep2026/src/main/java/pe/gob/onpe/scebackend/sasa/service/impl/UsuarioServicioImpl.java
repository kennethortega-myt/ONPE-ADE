package pe.gob.onpe.scebackend.sasa.service.impl;


import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.scebackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.scebackend.model.dto.NuevaContraseniaOutputDto;
import pe.gob.onpe.scebackend.sasa.dto.*;
import pe.gob.onpe.scebackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.service.SasaTokenService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServicioImpl implements UsuarioServicio {

	public static final String APPLICATION_JSON = "application/json";
	public static final String CONTENT_TYPE = "Content-Type";

	@Value("${sasa.url}")
	private String url;

	@Value("${sasa.codigo}")
	private String codigo;

	private final RestTemplate restTemplate;

	private final SasaTokenService sasaTokenService;

    @Override
	public LoginDatosOutputDto accederSistema(LoginInputDto input) throws GenericException {
				LoginDatosOutputDto datos = null;
		
				input.setCodigo(codigo);
				input.setRecaptcha("xxxx");
				
				HttpHeaders headers = new HttpHeaders();
				headers.set(CONTENT_TYPE, APPLICATION_JSON);
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				HttpEntity<LoginInputDto> requestEntity = new HttpEntity<>(input,
						headers);
				
				try {
						ResponseEntity<LoginDatosOutputDto> responseLogin = restTemplate.exchange(url +"/usuario/loginsc",
						HttpMethod.POST, requestEntity, LoginDatosOutputDto.class);
						datos = responseLogin.getBody();
                        verificarYCambiarMensajeBloqueo(datos);

						// Guardando token SASA en el redis
						if (datos != null && datos.getResultado().equals(1) && datos.getDatos().getUsuario() != null) {
							this.sasaTokenService.addToken(datos.getDatos().getUsuario().getIdUsuario(),
								datos.getDatos().getUsuario().getToken());
						}
						
				} 
				catch (HttpClientErrorException.Unauthorized e) {
					log.info("Error 401: No autorizado. Verifica tus credenciales o usuario bloqueado.");
					String responseBody = e.getResponseBodyAsString();
					log.info("Response: {}",responseBody);
				    try {
				    	ObjectMapper objectMapper = new ObjectMapper();
				    	var errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);
				    	if(errorResponse.getMensaje().equals("Usuario bloqueado.")) {
				    		LoginDatosOutputDto response = new LoginDatosOutputDto();
					    	response.setMensaje("Usuario bloqueado");	    	
					    	return response;
				    	} else {
				    		return null;
				    	}
				    } catch (Exception ex) {
				        log.error("Error al exceder al sistema", ex);
				    }
				}
				catch(Exception e) {
					log.error("Error al exceder al sistema", e);
				}
				
			return datos;
		
	}
		
	@Override
	public CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input,String token) throws GenericException {
	try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.set(CONTENT_TYPE, APPLICATION_JSON);
			headers.set("Authorization",  token);
			
			HttpEntity<CargarAccesosInputDto> requestEntity = new HttpEntity<>(input,headers);

			ResponseEntity<CargarAccesoDatosOutputDto> responseLogin = restTemplate.exchange(url +"/usuario/cargar-accesos",
					HttpMethod.POST, requestEntity, CargarAccesoDatosOutputDto.class);

			return  responseLogin.getBody();
		} catch (Exception e) {
			throw new GenericException(e.getMessage());
		
		}
	}

	@Override
	public BuscarPorIdOutputDto buscarPorId(String tokenSasa, Integer usuarioId) {
		BuscarPorIdInputDto body = BuscarPorIdInputDto.builder()
                .id(usuarioId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenSasa);

		HttpEntity<BuscarPorIdInputDto> requestEntity = new HttpEntity<>(body, headers);

		try {

			ResponseEntity<BuscarPorIdOutputDto> response = restTemplate.exchange(
					url + "/usuario/buscar-por-id",
					HttpMethod.POST, requestEntity, BuscarPorIdOutputDto.class);

			return  response.getBody();
		} catch (Exception e) {
			log.error("Error al buscar por id en SASA para usuarioId: {}", usuarioId);
			return null;
		}
	}

    @Override
    public void cerrarSesionActivaSasa(String usuario, String token) throws GenericException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(CONTENT_TYPE, APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);

            CerrarSesionSasaInputDto body = new CerrarSesionSasaInputDto();
            body.setUsuario(usuario);
            body.setCodigo(codigo);

            HttpEntity<CerrarSesionSasaInputDto> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    url + "/usuario/cerrar-sesion-activa",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info("Sesión cerrada en SASA para usuario: {}", usuario);

        } catch (Exception e) {
            log.error("Error al cerrar sesión en SASA para usuario: {}",usuario,e);
        }
    }


    @Override
	public AplicacionUsuariosResponseDto listAplicacionUsuarios(String codAplicacion, String codProceso, String token) throws GenericException {

		final String urlV1 = "/aplicacion-usuario/listar-usuarios-por-aplicacion-proceso";
		final String urlV2 = "/aplicacion-usuario/listar-usuarios-por-aplicacion-proceso-v2";
		HttpHeaders headers = new HttpHeaders();
		headers.set(CONTENT_TYPE, APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		AplicacionUsuarioRequestDto apUsuario = new AplicacionUsuarioRequestDto();
		apUsuario.setCodigoAplicacion(codAplicacion);
		apUsuario.setAcronimoProceso(codProceso);
		HttpEntity<AplicacionUsuarioRequestDto> requestEntity = new HttpEntity<>(apUsuario,headers);

		try {

			ResponseEntity<AplicacionUsuariosResponseDto> response = restTemplate.exchange(url +urlV2,
					HttpMethod.POST, requestEntity, AplicacionUsuariosResponseDto.class);

			return  response.getBody();
		} catch (Exception e) {
			try {
				ResponseEntity<AplicacionUsuariosResponseDto> response = restTemplate.exchange(url +urlV1,
						HttpMethod.POST, requestEntity, AplicacionUsuariosResponseDto.class);
				return  response.getBody();
			}catch (Exception e2) {
				throw new GenericException(e2.getMessage());
			}
		}
	}

	@Override
	public GenericResponse<Boolean> actualizarContrasenia(ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto, String usuario) {
		GenericResponse<Boolean> response = new GenericResponse<>();
		try {
            validarContraseniaFuerte(actualizarNuevaClaveInputDto.getClaveNueva());
			LoginInputDto login = new LoginInputDto();
			login.setUsuario(usuario);
			login.setClave(actualizarNuevaClaveInputDto.getClaveActual());
			login.setCodigo(codigo);
			LoginDatosOutputDto u;
			u = accederSistema(login);

			if (u.getResultado() > 0 &&
					u.getDatos() != null &&
					u.getDatos().getUsuario() != null &&
					u.getDatos().getUsuario().getClaveNueva() == 1 &&
					u.getDatos().getUsuario().getToken() != null) {

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setBearerAuth(u.getDatos().getUsuario().getToken());

				CambiarContraseniaInputDto body = new CambiarContraseniaInputDto();
				body.setClave(actualizarNuevaClaveInputDto.getClaveNueva());

				HttpEntity<CambiarContraseniaInputDto> requestEntity = new HttpEntity<>(body, headers);

				ResponseEntity<CambiarContraseniaOutputDto> responseLogin = restTemplate.exchange(
						url + "/usuario/actualizar-nueva-clave",
						HttpMethod.POST,
						requestEntity,
						CambiarContraseniaOutputDto.class);

				CambiarContraseniaOutputDto data = responseLogin.getBody();

				response.setData(data != null && data.getSuccess() == 1);
				if (response.getData()) {
					response.setMessage("Usuario actualizado con éxito.");
				}else{
					response.setMessage("No se pudo actualizar la contraseña.");
				}
			} else if (u.getResultado() > 0 &&
					u.getDatos() != null &&
					u.getDatos().getUsuario() != null &&
					u.getDatos().getUsuario().getClaveNueva() < 1) {
				response.setMessage("El usuario no requiere cambio de contraseña.");
			} else if (u.getResultado() == -1){
				response.setMessage("La contraseña actual ingresada no es correcta.");
			}else if(u.getMensaje()!=null){
				response.setMessage(u.getMensaje());
			}else{
				response.setMessage("No se puede realizar el cambio de contraseña.");
			}
		} catch (IllegalArgumentException e){
            log.error("Validación de contraseña fallida: {}", e.getMessage());
            response.setMessage(e.getMessage());
        }catch (Exception e) {
			log.error("Error al cambiar contrasenia {}", e.getMessage());
			response.setMessage("Error al cambiar contraseña.");
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(optSasaToken.get());

            HttpEntity<NuevaContraseniaInputDto> requestEntity = new HttpEntity<>(body, headers);

            validarContraseniaFuerte(actualizarNuevaClaveInputDto.getClaveNueva());
            ResponseEntity<NuevaContraseniaOutputDto> res = restTemplate.exchange(
                    url + "/usuario/nueva-contrasenia-sasa",
                    HttpMethod.POST,
                    requestEntity,
                    NuevaContraseniaOutputDto.class);

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
        } catch (ResourceAccessException e) {
            response.setMessage(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE);
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
                log.error("Error: {}", e);
            }
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            log.error("Validación de contraseña fallida: {}", e);
        } catch (Exception e) {
            log.error("Error al cambiar contrasenia {}", e.getMessage());
            response.setMessage("Error al cambiar contraseña.");
        }

        return response;
    }

    private void validarContraseniaFuerte(String claveNueva) throws IllegalArgumentException {

        if (claveNueva == null || claveNueva.isEmpty() || claveNueva.length() < 8 || claveNueva.length() > 20){
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 20 caracteres");
        }

        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?]).+$";
        if (!claveNueva.matches(pattern)) {
            throw new IllegalArgumentException(
                    "La contraseña debe contener mayúscula, minúscula, número y carácter especial"
            );
        }
    }

    private void verificarYCambiarMensajeBloqueo(LoginDatosOutputDto datos){
        if(datos != null
                && datos.getResultado() == -1
                && datos.getMensaje() != null
                && datos.getMensaje().toLowerCase().contains("bloquead")){
            datos.setMensaje("Su cuenta ha sido bloqueada por superar el número máximo de intentos fallidos. Comuníquese con su administrador.");
        }
    }

    /**
     * SASA DEVUELVE LOS CODIGOS 403 O 405 CUANDO EL TOKEN EXPIRO O EL TOKEN ES
     * INVALIDO
     */
    private boolean tokenSasaIsExpired(HttpStatusCode status) {
        return ConstantesComunes.SASA_TOKEN_INVALIDO_O_EXPIRADO_HTTP_STATUS.contains(status);
    }
}
