package pe.gob.onpe.scebackend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.model.service.IAccesoPcService;
import pe.gob.onpe.scebackend.model.service.impl.TokenBlacklistService;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.scebackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.scebackend.security.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper mapper;
    private final JwtTokenFactory tokenFactory;
    public final TokenBlacklistService tokenBlacklistService;

    @Autowired
    private IAccesoPcService accesoPcService;

    @Autowired
    public AjaxAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory,
                                                 final TokenBlacklistService tokenBlacklistService) {
        this.mapper = mapper;
        this.tokenFactory = tokenFactory;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserContext userContext = (UserContext) authentication.getPrincipal();

        Util util = new Util();
        JwtAuthentication jwt = util.infoPC(request);

        jwt.setUsername(userContext.getUsername());

        String infoPcEncryp = util.encryJson(jwt);

        String accessToken = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp);
        String refreshToken = tokenFactory.createRefreshToken(userContext, infoPcEncryp);

        log.info("sesionUnica={}", userContext.getSesionUnica());
        if (userContext.getSesionUnica() != null && userContext.getSesionUnica().equals(1)) {
			long expirationSeconds = tokenFactory.getAccessTokenExpirationInSeconds();
			long expirationRefreshTokenSeconds = tokenFactory.getRefreshTokenExpirationInSeconds();

			this.tokenBlacklistService.addTokenToRedis(userContext.getUsernameSinEncriptar(), accessToken, expirationSeconds);
			this.tokenBlacklistService.addRefreshTokenToRedis(userContext.getUsernameSinEncriptar(), refreshToken, expirationRefreshTokenSeconds);
		}

        String username = userContext.getUsernameSinEncriptar();
        String ipAddress = obtenerIpAddress(request);

        registrarAccesoSiEsPrimerLogin(username, ipAddress);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", accessToken);
        tokenMap.put("refreshToken", refreshToken);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), tokenMap);

        clearAuthenticationAttributes(request);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    private void registrarAccesoSiEsPrimerLogin(String username, String ipAddress) {
        try {
            if (ipAddress == null || ipAddress.trim().isEmpty()) {
                return;
            }

            if(esIpLoopBack(ipAddress)){
                return;
            }

            boolean esPrimerLogin = accesoPcService.esPrimerLogin(ipAddress);

            if (esPrimerLogin) {
                accesoPcService.registrarAcceso(username, ipAddress);
            }
        } catch (Exception e) {

        }

    }

    private String obtenerIpAddress(HttpServletRequest request) {

        String ipAddress = "";
        ipAddress = request.getHeader("X-Real-IP");
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            ipAddress = request.getHeader("X-FORWARDED-FOR");
        }
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    private boolean esIpLoopBack(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        try {
            return java.net.InetAddress.getByName(ipAddress.trim()).isLoopbackAddress();
        } catch (Exception e) {
            return false;
        }
    }

}