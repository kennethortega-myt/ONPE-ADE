package pe.gob.onpe.sceorcbackend.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.DataNoFoundException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.ExpireTokenDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.usuarios.JwtResponseDTO;
import pe.gob.onpe.sceorcbackend.model.dto.response.usuario.UsuarioDetailResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.usuario.UsuarioResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.usuario.UsuarioSasaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.usuario.UsuarioUpdateRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.TokenBlacklistService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario.UsuarioFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;
import pe.gob.onpe.sceorcbackend.rest.controller.reporte.BaseController;
import pe.gob.onpe.sceorcbackend.sasa.service.SasaAuthService;
import pe.gob.onpe.sceorcbackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.security.service.TabRefreshTokenService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController extends BaseController {
  Logger logger = LoggerFactory.getLogger(UsuarioController.class);
  private final TokenUtilService tokenUtilService;
  private final UsuarioService usuarioService;
  private final SasaAuthService sasaAuthService;
  private final TabRefreshTokenService refreshTokenService;
  private final UsuarioServicio usuarioServicioSasa;
  private final TokenBlacklistService tokenBlacklistService;
  private final ParametroService parametroService;

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
	@PostMapping("/cerrar-sesion")
	public ResponseEntity<GenericResponse<String>> cerrarSesion(
			@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

		GenericResponse<String> genericResponse = new GenericResponse<>();
		try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      var parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_EJECUCION_PUESTA_CERO);
      if (parametroDto != null && parametroDto.getValor() != null
          && parametroDto.getValor().toString().equals(ConstantesComunes.TEXT_TRUE)) {
        throw new InsufficientAuthenticationException(
            "El sistema está realizando una puesta a cero. Por favor, intente unos minutos.");
      }      
			long timeExpired = this.tokenUtilService.getTimeExpiredSeconds(authorization);
			this.tokenBlacklistService.addToBlacklist(tokenInfo.getTokenPlano(), timeExpired, ConstantesComunes.SESION_CERRADA_POR_USUARIO);
            this.tokenBlacklistService.removeFromRedis(tokenInfo.getNombreUsuario());

      String refreshToken = this.tokenBlacklistService.getActiveRefreshToken(tokenInfo.getNombreUsuario());
      long timeExpiredRt = this.tokenBlacklistService.getExpireRefreshToken(tokenInfo.getNombreUsuario());
      if (refreshToken != null && timeExpiredRt > 0) {
        this.tokenBlacklistService.addToBlacklist(refreshToken, timeExpiredRt, ConstantesComunes.SESION_CERRADA_POR_USUARIO);
        this.tokenBlacklistService.removeRefreshTokenFromRedis(tokenInfo.getNombreUsuario());
      }

            genericResponse = this.usuarioService.cerrarSession(tokenInfo.getNombreUsuario());
			return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			genericResponse.setData(null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
		}
	}

  @PreAuthorize("permitAll()")
  @PostMapping("/cerrar-sesion-activa")
  public ResponseEntity<GenericResponse<String>> cerrarSesionActiva(@RequestBody Map<String, String> body) {
    GenericResponse<String> genericResponse = new GenericResponse<>();
    try {
      var parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_EJECUCION_PUESTA_CERO);
      if (parametroDto != null && parametroDto.getValor() != null
          && parametroDto.getValor().toString().equals(ConstantesComunes.TEXT_TRUE)) {
        throw new InsufficientAuthenticationException(
            "El sistema está realizando una puesta a cero. Por favor, intente ingresar en unos minutos.");
      }
      String usuario = body.get("usuario");
      String token = tokenBlacklistService.getActiveToken(usuario);
      String refreshToken = tokenBlacklistService.getActiveRefreshToken(usuario);

      if (token != null) {
        long ttl = tokenBlacklistService.getExpireToken(usuario);
        if (ttl > 0) {
          tokenBlacklistService.addToBlacklist(token, ttl, ConstantesComunes.SESION_CERRADA_POR_OTRO_DISPOSITIVO);
        }
        // eliminar token activo del usuario en Redis
        tokenBlacklistService.removeFromRedis(usuario);
      }

      if (refreshToken != null) {
        long ttl = tokenBlacklistService.getExpireRefreshToken(usuario);
        if (ttl > 0) {
          tokenBlacklistService.addToBlacklist(refreshToken, ttl, ConstantesComunes.SESION_CERRADA_POR_OTRO_DISPOSITIVO);
        }
        // eliminar token activo del usuario en Redis
        tokenBlacklistService.removeRefreshTokenFromRedis(usuario);
      }

      genericResponse = this.usuarioService.cerrarSession(usuario);
      return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    } catch (Exception e) {
      logger.warn("Error al cerrar sesión activa", e);
      genericResponse.setMessage(e.getMessage());
      genericResponse.setData(null);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @PostMapping("/validar-token")
  public ResponseEntity<GenericResponse<ExpireTokenDTO>> cerrarSesion2(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

    GenericResponse<ExpireTokenDTO> genericResponse = new GenericResponse<>();
    try {
      String timeExpired = this.tokenUtilService.getTimeExpired(authorization);
      ExpireTokenDTO expireTokenDTO = new ExpireTokenDTO();
      expireTokenDTO.setExpired(false);
      expireTokenDTO.setTimeLeft(timeExpired);
      genericResponse.setData(expireTokenDTO);
      return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    } catch (Exception e) {
      ExpireTokenDTO expireTokenDTO = new ExpireTokenDTO();
      expireTokenDTO.setExpired(true);
      expireTokenDTO.setTimeLeft("00:00:00");
      genericResponse.setSuccess(false);
      genericResponse.setMessage(e.getMessage());
      genericResponse.setData(expireTokenDTO);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @PostMapping("/refreshToken")
  public ResponseEntity<GenericResponse<JwtResponseDTO>> refreshToken(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {

    GenericResponse<JwtResponseDTO> genericResponse = null;
    TokenInfo tokenInfo = this.tokenUtilService.getInfo(refreshToken);

    try {
      genericResponse = this.refreshTokenService.getNewToken(tokenInfo);
    } catch (Exception e) {
      genericResponse = new GenericResponse<>();
      genericResponse.setSuccess(false);
      genericResponse.setMessage(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }

    try {
      var response = this.sasaAuthService.refreshToken(tokenInfo);
      if (!response.isSuccess()) {
        logger.error(response.getMessage());
      }
    } catch (Exception e) {
      logger.error(ConstantesComunes.SASA_SERVICIO_NO_DISPONIBLE, e);
    }
    return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/perfiles")
  public ResponseEntity<GenericResponse<List<DetCatalogoEstructuraDTO>>> listPerfiles() {
    return ResponseEntity.ok(
        new GenericResponse<List<DetCatalogoEstructuraDTO>>(this.usuarioService.listPerfilesCentroComputo()));
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/tipos-documento")
  public ResponseEntity<GenericResponse<List<DetCatalogoEstructuraDTO>>> listTiposDocumento() {
    return ResponseEntity.ok(
        new GenericResponse<List<DetCatalogoEstructuraDTO>>(this.usuarioService.listTiposDocumento()));
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping
  public ResponseEntity<GenericResponse<SearchFilterResponse<UsuarioResponseDto>>> listPaginated(
    @RequestParam(required = true) Integer size,
    @RequestParam(required = true) Integer page,
    @RequestParam(name = "acronimo_proceso", required = false) String acronimoProceso,
    @RequestParam(name = "centro_computo", required = false) String centroComputo,
    @RequestParam(name = "apellido_paterno", required = false) String apellidoPaterno,
    @RequestParam(name = "apellido_materno", required = false) String apellidoMaterno,
    @RequestParam(required = false) String documento,
    @RequestParam(required = false) String nombres,
    @RequestParam(required = false) String perfil,
    @RequestParam(required = false) String usuario,
    @RequestParam(required = false) Integer personaAsignada,
    @RequestParam(required = false) Integer desincronizado
  ) {
    GenericResponse<SearchFilterResponse<UsuarioResponseDto>> response = new GenericResponse<>();

    UsuarioFilter filter = UsuarioFilter.builder()
      .acronimoProceso(acronimoProceso)
      .centroComputo(centroComputo)
      .apellidoPaterno(apellidoPaterno)
      .apellidoMaterno(apellidoMaterno)
      .documento(documento)
      .nombres(nombres)
      .perfil(perfil)
      .usuario(usuario)
      .personaAsignada(personaAsignada)
      .desincronizadoSaza(desincronizado)
      .build();

    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.usuarioService.listPaginatedAll(filter, page, size));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/{username}")
  public ResponseEntity<GenericResponse<UsuarioDetailResponseDto>> getUsuario(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @PathVariable String username) {
    GenericResponse<UsuarioDetailResponseDto> response = new GenericResponse<>();

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

    Usuario usuario = null;
    UsuarioResponseDto usuarioResponse = null;
    try {
      usuario = this.usuarioService.findByUsername(username);
      if (usuario == null) {
        response.setSuccess(Boolean.FALSE);
        response.setData(null);
        response.setMessage("Registro no encontrado");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }
      usuarioResponse = this.usuarioService.convertirUsuarioDto(usuario);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    UsuarioSasaResponseDto usuarioSasa = null;
    try {
      var sasaResponse = this.usuarioServicioSasa.buscarPorId(tokenInfo, usuario.getIdUsuario());
      if (sasaResponse.isSuccess()) {
        usuarioSasa = UsuarioSasaResponseDto
            .builder()
            .id(sasaResponse.getData().getData().getId())
            .usuario(sasaResponse.getData().getData().getUsuario())
            .estado(sasaResponse.getData().getData().getEstado())
            .bloqueado(sasaResponse.getData().getData().getBloqueado())
            .fechaBloqueo(sasaResponse.getData().getData().getFechaBloqueo())
            .persona(sasaResponse.getData().getData().getPersona())
            .build();
      } else {
        response.setMessage(sasaResponse.getMessage());
      }
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    response.setSuccess(Boolean.TRUE);
    response.setData(UsuarioDetailResponseDto.builder()
        .usuario(usuarioResponse)
        .usuarioSasa(usuarioSasa)
        .build());
    return ResponseEntity.ok(response);
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/{username}/desbloquear")
  public ResponseEntity<GenericResponse<UsuarioDetailResponseDto>> desbloquearUsuarioSasa(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @PathVariable String username) {
    GenericResponse<UsuarioDetailResponseDto> response = new GenericResponse<>();

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

    Usuario usuario = null;
    UsuarioResponseDto usuarioResponse = null;
    try {
      usuario = this.usuarioService.findByUsername(username);
      if (usuario == null) {
        response.setSuccess(Boolean.FALSE);
        response.setData(null);
        response.setMessage("Registro no encontrado");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }
      usuarioResponse = this.usuarioService.convertirUsuarioDto(usuario);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    UsuarioSasaResponseDto usuarioSasa = null;
    try {
      var sasaResponse = this.usuarioServicioSasa.buscarPorId(tokenInfo, usuario.getIdUsuario());
      if (!sasaResponse.isSuccess()) {
        response.setSuccess(Boolean.FALSE);
        response.setData(null);
        response.setMessage("Servicio SASA no disponible");
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
      }

      var sasaDesbloqueoResponse = this.sasaAuthService.desbloquearUsuario(tokenInfo,
          sasaResponse.getData().getData());

      if (sasaDesbloqueoResponse.isSuccess()) {
        usuarioSasa = UsuarioSasaResponseDto
            .builder()
            .id(sasaDesbloqueoResponse.getData().getData().getId())
            .usuario(sasaDesbloqueoResponse.getData().getData().getUsuario())
            .estado(sasaDesbloqueoResponse.getData().getData().getEstado())
            .bloqueado(sasaDesbloqueoResponse.getData().getData().getBloqueado())
            .fechaBloqueo(sasaDesbloqueoResponse.getData().getData().getFechaBloqueo())
            .persona(sasaDesbloqueoResponse.getData().getData().getPersona())
            .build();

        response.setSuccess(Boolean.TRUE);
        response.setData(UsuarioDetailResponseDto.builder()
            .usuario(usuarioResponse)
            .usuarioSasa(usuarioSasa)
            .build());
        response.setMessage(sasaDesbloqueoResponse.getMessage());
      } else {
        response.setSuccess(Boolean.FALSE);
        response.setData(null);
        response.setMessage(sasaDesbloqueoResponse.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/{username}/restablecer-contrasenia")
  public ResponseEntity<GenericResponse<String>> restablecerContrasenia(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @PathVariable String username) {
    GenericResponse<String> response = new GenericResponse<>();
    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    try {
      var usuario = this.usuarioService.findByUsername(username);
      // Validando que usuario tenga persona asignada / correo
      if (usuario.getPersonaAsignada().equals(0) || usuario.getCorreo().isEmpty()) {
        response.setSuccess(Boolean.FALSE);
        String message = "Error de validación";
        if (usuario.getPersonaAsignada().equals(0)) {
          message = "Verifique que el usuario tenga una persona asignada";
        } else if (usuario.getCorreo().isEmpty()) {
          message = "Verifique que el usuario tenga un correo electrónico válido";
        }
        response.setMessage(message);
        ResponseEntity.badRequest().body(response);
      }
      var restablecerRes = this.usuarioServicioSasa.restablecerContrasenia(tokenInfo,
          usuario.getIdUsuario());
      response.setSuccess(restablecerRes.isSuccess());
      response.setMessage(restablecerRes.getMessage());
      if (restablecerRes.isSuccess()) {
        var res = this.usuarioServicioSasa.buscarPorId(tokenInfo, usuario.getIdUsuario());
        response.setData(res.getData().getData().getClaveTemporal());
        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.badRequest().body(response);
      }
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/sesion-activa")
  public ResponseEntity<GenericResponse<SearchFilterResponse<UsuarioResponseDto>>> listSesionActivaPaginated(
      @RequestParam(name = "size") Integer size, @RequestParam(name = "page") Integer page,
      @RequestParam(name = "usuario") String usuario) {
    GenericResponse<SearchFilterResponse<UsuarioResponseDto>> response = new GenericResponse<>();
    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.usuarioService.listPaginted(usuario, page, size));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PutMapping("/{username}")
  public ResponseEntity<GenericResponse<UsuarioResponseDto>> update(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @PathVariable String username,
      @RequestBody @Valid UsuarioUpdateRequestDto request) {
    GenericResponse<UsuarioResponseDto> response = new GenericResponse<>();

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

    var usuario = this.usuarioService.findByUsername(username);
    if (usuario == null) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage("Usuario no encontrado");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    UsuarioResponseDto usuarioResponse = null;
    try {
      usuarioResponse = this.usuarioService.update(usuario, request, tokenInfo);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setData(null);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    response.setSuccess(Boolean.TRUE);
    response.setData(usuarioResponse);
    response.setMessage("Usuario actualizado");

    try {
      var actualizarUsuarioRes = this.usuarioServicioSasa.actualizarUsuario(tokenInfo, usuario, request);
      if (!actualizarUsuarioRes.isSuccess()) {
        response.setSuccess(Boolean.FALSE);
        response.setMessage("Registro actualizado en el SCE, SASA no disponible, sincronizar en unos minutos.");
        this.usuarioService.updateDesincronizado(usuario.getId(), Boolean.TRUE);
      }
    } catch (Exception e) {
      this.usuarioService.updateDesincronizado(usuario.getId(), Boolean.TRUE);
      response.setMessage("Error SASA: " + e.getMessage());
    }

    return ResponseEntity.ok(response);
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/{username}/sincronizar")
  public ResponseEntity<GenericResponse<UsuarioResponseDto>> sincronizarUsuario(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @PathVariable String username) {
    GenericResponse<UsuarioResponseDto> response = new GenericResponse<>();
    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    var usuario = this.usuarioService.findByUsername(username);
    if (usuario == null) {
      response.setSuccess(Boolean.FALSE);
      response.setData(this.usuarioService.convertirUsuarioDto(usuario));
      response.setMessage("Usuario no encontrado");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    UsuarioUpdateRequestDto dto = UsuarioUpdateRequestDto.builder()
        .tipoDocumento(usuario.getTipoDocumento())
        .documento(usuario.getDocumento())
        .apellidoPaterno(usuario.getApellidoPaterno())
        .apellidoMaterno(usuario.getApellidoMaterno())
        .nombres(usuario.getNombres())
        .correo(usuario.getCorreo())
        .activo(usuario.getActivo())
        .build();
    try {
      var actualizarUsuarioRes = this.usuarioServicioSasa.actualizarUsuario(tokenInfo, usuario, dto);
      if(actualizarUsuarioRes.isSuccess()){
        response.setSuccess(true);
        response.setData(null);
        response.setMessage("Usuario sincronizado");
        this.usuarioService.updateDesincronizado(usuario.getId(), Boolean.FALSE);
      } else {
        response.setSuccess(false);
        response.setData(null);
        response.setMessage(actualizarUsuarioRes.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      response.setSuccess(false);
      response.setData(null);
      response.setMessage("Error SASA: " + e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return ResponseEntity.ok(response);
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/reset-sesion")
  public ResponseEntity<GenericResponse<Boolean>> resetSesion(@RequestParam(name = "usuario") String usuario,
          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

    TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
    this.usuarioService.resetSesionActiva(usuario, tokenInfo);
    return ResponseHelperException.createSuccessResponse("Se cerró la sesión del usuario seleccionado.");
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @PostMapping("/actualizar-contrasenia")
  public ResponseEntity<GenericResponse<Boolean>> actualizarContrasenia(
          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
          @Valid @RequestBody ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto) {
    try {
      LoginUserHeader user = getUserLogin(authorization);
      GenericResponse<Boolean> genericResponse = this.sasaAuthService.actualizarContrasenia(actualizarNuevaClaveInputDto,user.getUsuario());
      return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    } catch (Exception e) {
      GenericResponse<Boolean> genericResponse = new GenericResponse<>();
      genericResponse.setSuccess(Boolean.FALSE);
      genericResponse.setMessage("En este momento no se puede realizar el cambio de contraseña.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @PostMapping("/nueva-contrasenia")
  public ResponseEntity<GenericResponse<Boolean>> nuevaContrasenia(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @Valid @RequestBody NuevaClaveInputDto nuevaClaveInputDto) {

    if (!nuevaClaveInputDto.getClaveNueva().equals(nuevaClaveInputDto.getConfirmaClaveNueva())) {
      GenericResponse<Boolean> genericResponse = new GenericResponse<>();
      genericResponse.setSuccess(Boolean.FALSE);
      genericResponse.setMessage("Las contraseñas no coinciden");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      var genericResponse = this.sasaAuthService.nuevaContraseniaSasa(tokenInfo, nuevaClaveInputDto);
      return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    } catch (Exception e) {
      GenericResponse<Boolean> genericResponse = new GenericResponse<>();
      genericResponse.setSuccess(Boolean.FALSE);
      genericResponse.setMessage("En este momento no se puede realizar el cambio de contraseña.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
    }
  }

  @PreAuthorize(RoleAutority.ACCESO_TOTAL)
  @GetMapping("/validar-sesion-activa")
  public ResponseEntity<Boolean> validarSesionActiva(
          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      return ResponseEntity.status(HttpStatus.OK).body(this.usuarioService.validarSessionActiva(tokenInfo.getNombreUsuario()));
    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }

    @GetMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReporteUsuarioPdf( @RequestParam(required = true) Integer size,
                                                                                  @RequestParam(required = true) Integer page,
                                                                                  @RequestParam(name = "proceso", required = false) String acronimoProceso,
                                                                                  @RequestParam(name = "centroComputo", required = false) String centroComputo,
                                                                                  @RequestParam(name = "apellido_paterno", required = false) String apellidoPaterno,
                                                                                  @RequestParam(name = "apellido_materno", required = false) String apellidoMaterno,
                                                                                  @RequestParam(required = false) String documento,
                                                                                  @RequestParam(required = false) String nombres,
                                                                                  @RequestParam(required = false) String perfil,
                                                                                  @RequestParam(required = false) String usuario,
                                                                                  @RequestParam(required = false) Integer personaAsignada,
                                                                                  @RequestParam(required = false) Integer desincronizado,
                                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        try {
            LoginUserHeader user = getUserLogin(authorization);
            UsuarioFilter filter = UsuarioFilter.builder()
                    .acronimoProceso(acronimoProceso)
                    .centroComputo(centroComputo)
                    .apellidoPaterno(apellidoPaterno)
                    .apellidoMaterno(apellidoMaterno)
                    .documento(documento)
                    .nombres(nombres)
                    .perfil(perfil)
                    .usuario(usuario)
                    .personaAsignada(personaAsignada)
                    .desincronizadoSaza(desincronizado)
                    .build();

            byte[] resultado = this.usuarioService.getReporteUsuariosPdf(filter,page,size, user.getUsuario());
            return getPdfResponse(resultado);
        } catch (DataNoFoundException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }


}
