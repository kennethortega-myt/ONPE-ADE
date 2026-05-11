package pe.gob.onpe.scebackend.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.scebackend.model.service.impl.TokenBlacklistService;
import pe.gob.onpe.scebackend.security.TokenExtractor;
import pe.gob.onpe.scebackend.security.config.WebSecurityConfig;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;
import pe.gob.onpe.scebackend.security.utils.Util;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenAuthenticationProcessingFilter.class);
    private final AuthenticationFailureHandler failureHandler;
    private final TokenExtractor tokenExtractor;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenUtilService tokenUtilService;


    public JwtTokenAuthenticationProcessingFilter(AuthenticationFailureHandler failureHandler,
                                                  TokenExtractor tokenExtractor, 
                                                  RequestMatcher matcher,
                                                  TokenBlacklistService tokenBlacklistService,
                                                  TokenUtilService tokenUtilService) {
        super(matcher);
        this.failureHandler = failureHandler;
        this.tokenExtractor = tokenExtractor;
        this.tokenBlacklistService = tokenBlacklistService;
        this.tokenUtilService = tokenUtilService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String tokenPayload = request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME);
        RawAccessJwtToken token = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
    	
    	String authHeader = request.getHeader("Authorization");
    	String idSession = request.getHeader(SceConstantes.HEADER_IDSESSION);
    	
    	if(idSession==null){
    		throw new IOException("El IdSession es requerido");  
	    }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IOException("No se encontró un token Bearer en el request");
        }

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authHeader);
        String idSessionHash = tokenInfo.getIdSession();

        LOGGER.info("se da inicio la validacion al token");
        Util util = new Util();
        JwtAuthentication jwt = util.infoPC(request);
        UserContext u = (UserContext) authResult.getPrincipal();
        jwt.setUsername(u.getUsername());
        
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        boolean ok = enc.matches(idSession, idSessionHash);
        
        if (!ok){
            throw new IOException("Invalid JWT Token - idSession not match");
        }
        
        if (!jwt.equals(util.dencryString(u.getValidator()))){
            throw new IOException("Invalid JWT Token");
        }
        
        Boolean isBlacklisted = tokenBlacklistService.isBlacklisted(tokenInfo.getTokenPlano());
        
        if (isBlacklisted != null && isBlacklisted) {
        	throw new IOException("Invalid JWT Token by black list");
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        request.setAttribute("login", u.getName());
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        LOGGER.warn("Falló autenticación JWT para {}: {}", request.getRequestURI(), failed.getMessage());
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}