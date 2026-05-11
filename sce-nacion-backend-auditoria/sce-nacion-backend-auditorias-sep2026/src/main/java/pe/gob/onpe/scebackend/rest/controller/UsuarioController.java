package pe.gob.onpe.scebackend.rest.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.scebackend.model.dto.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.scebackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.scebackend.model.service.UsuarioService;
import pe.gob.onpe.scebackend.model.service.impl.TokenBlacklistService;
import pe.gob.onpe.scebackend.security.dto.*;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.security.service.TabRefreshTokenService;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.Map;


@RestController
@RequestMapping("/usuario")
public class UsuarioController extends BaseController{

    Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final TokenUtilService tokenUtilService;

    private final UsuarioService usuarioService;
    private final TabRefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    
    private static final String HEADER_USERNAME = "usr";

    private final pe.gob.onpe.scebackend.sasa.service.UsuarioServicio usuarioServicioSasa;

    public UsuarioController(TokenDecoder tokenDecoder, TokenUtilService tokenUtilService,
                             TabRefreshTokenService refreshTokenService,
                             UsuarioService usuarioService,
                             pe.gob.onpe.scebackend.sasa.service.UsuarioServicio usuarioServicioSasa,
                             TokenBlacklistService tokenBlacklistService) {
        super(tokenDecoder);
        this.tokenUtilService = tokenUtilService;
        this.refreshTokenService = refreshTokenService;
        this.usuarioService = usuarioService;
        this.usuarioServicioSasa = usuarioServicioSasa;
        this.tokenBlacklistService = tokenBlacklistService;
    }


    @PostMapping("/cerrar-sesion")
    public ResponseEntity<GenericResponse<String>> cerrarSesion(
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        GenericResponse<String> genericResponse = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            this.usuarioServicioSasa.cerrarSesionActivaSasa(
                    tokenInfo.getNombreUsuario(),
                    tokenInfo.getTokenPlano()
            );

            long timeExpired = this.tokenUtilService.getTimeExpiredSeconds(authorization);
            this.tokenBlacklistService.addToBlacklist(tokenInfo.getTokenPlano(), timeExpired);
            this.tokenBlacklistService.removeFromRedis(tokenInfo.getNombreUsuario());

            String refreshToken = this.tokenBlacklistService.getActiveRefreshToken(tokenInfo.getNombreUsuario());
            long timeExpiredRt = this.tokenBlacklistService.getExpireRefreshToken(tokenInfo.getNombreUsuario());
            if (refreshToken != null && timeExpiredRt > 0) {
                this.tokenBlacklistService.addToBlacklist(refreshToken, timeExpiredRt);
                this.tokenBlacklistService.removeRefreshTokenFromRedis(tokenInfo.getNombreUsuario());
            }

            genericResponse = this.usuarioService.cerrarSession(tokenInfo.getNombreUsuario());
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/cerrar-sesion-activa")
    public ResponseEntity<GenericResponse<String>> cerrarSesionActiva(
            @RequestBody Map<String, String> body) {

        GenericResponse<String> genericResponse = new GenericResponse<>();
        try {
            String usuario = body.get("usuario");
            String token = tokenBlacklistService.getActiveToken(usuario);

            this.usuarioServicioSasa.cerrarSesionActivaSasa(
                    usuario,
                    token
            );

            if (token != null) {
                long ttl = tokenBlacklistService.getExpireToken(usuario);
                if (ttl > 0) {
                    tokenBlacklistService.addToBlacklist(token, ttl);
                }
                // eliminar token activo del usuario en Redis
                tokenBlacklistService.removeFromRedis(usuario);
            }

            String refreshToken = this.tokenBlacklistService.getActiveRefreshToken(usuario);
            long timeExpiredRt = this.tokenBlacklistService.getExpireRefreshToken(usuario);
            if (refreshToken != null && timeExpiredRt > 0) {
                this.tokenBlacklistService.addToBlacklist(refreshToken, timeExpiredRt);
                this.tokenBlacklistService.removeRefreshTokenFromRedis(usuario);
            }

            genericResponse = this.usuarioService.cerrarSession(usuario);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
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
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            ExpireTokenDTO expireTokenDTO = new ExpireTokenDTO();
            expireTokenDTO.setExpired(true);
            expireTokenDTO.setTimeLeft("00:00:00");
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(expireTokenDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }


    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/refreshToken")
    public ResponseEntity<GenericResponse<JwtResponseDTO>> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(refreshToken);
            GenericResponse<JwtResponseDTO> genericResponse = this.refreshTokenService.getNewToken(tokenInfo);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            GenericResponse<JwtResponseDTO> genericResponse = new GenericResponse<>();
            genericResponse.setSuccess(false);
            genericResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/actualizar-contrasenia")
    public ResponseEntity<GenericResponse<Boolean>> actualizarContrasenia(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto) {

        try {
            LoginUserHeader user = getUserLogin(authorization);
            GenericResponse<Boolean> genericResponse = this.usuarioServicioSasa.actualizarContrasenia(actualizarNuevaClaveInputDto,user.getUsuario());
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("En este momento no se puede realizar el cambio de contraseña.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
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
            var genericResponse = this.usuarioServicioSasa.nuevaContraseniaSasa(tokenInfo, nuevaClaveInputDto);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("En este momento no se puede realizar el cambio de contraseña.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }
}
