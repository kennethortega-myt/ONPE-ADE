package pe.gob.onpe.sceorcbackend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.TokenBlacklistService;
import pe.gob.onpe.sceorcbackend.security.dto.UserContext;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.sceorcbackend.security.utils.Util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AjaxAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
	private final ObjectMapper mapper;
    private final JwtTokenFactory tokenFactory;
    private final TokenBlacklistService  tokenBlacklistService;

    public AjaxAwareAuthenticationSuccessHandler(final ObjectMapper mapper, final JwtTokenFactory tokenFactory, final  TokenBlacklistService tokenBlacklistService) {
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

        long expirationSeconds = tokenFactory.getAccessTokenExpirationInSeconds();
        long expirationRefreshTokenSeconds = tokenFactory.getRefreshTokenExpirationInSeconds();

        this.tokenBlacklistService.addTokenToRedis(userContext.getUsernameSinEncriptar(), accessToken, expirationSeconds);
        this.tokenBlacklistService.addRefreshTokenToRedis(userContext.getUsernameSinEncriptar(), refreshToken, expirationRefreshTokenSeconds);

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

}