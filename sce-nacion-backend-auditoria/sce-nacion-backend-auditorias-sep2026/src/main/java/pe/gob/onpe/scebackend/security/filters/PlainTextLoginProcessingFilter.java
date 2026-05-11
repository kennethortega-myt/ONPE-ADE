package pe.gob.onpe.scebackend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import pe.gob.onpe.scebackend.security.dto.LoginRequest;
import pe.gob.onpe.scebackend.security.exceptions.AuthMethodNotSupportedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Filtro de autenticación para login con contraseña en texto plano (sin encriptación RSA)
 * Similar a AjaxLoginProcessingFilter pero sin desencriptación
 */
public class PlainTextLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlainTextLoginProcessingFilter.class);

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;

    public PlainTextLoginProcessingFilter(String defaultProcessUrl, 
                                         AuthenticationSuccessHandler successHandler,
                                         AuthenticationFailureHandler failureHandler, 
                                         ObjectMapper mapper) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Authentication method not supported. Request method: {}", request.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        loginRequest.getAdicional().put("ipAddress", request.getRemoteAddr());
        loginRequest.getAdicional().put("hostName", request.getRemoteHost());
        loginRequest.getAdicional().put("ipOrginal", request.getHeader("X-Forwarded-For"));
        loginRequest.getAdicional().put("userAgente", request.getHeader("User-Agent"));

        if (StringUtils.isBlank(loginRequest.getUsername()) ||
                StringUtils.isBlank(loginRequest.getPassword())) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }

        LOGGER.info("Autenticación con texto plano para usuario: {}", loginRequest.getUsername());
        // NO se desencripta la contraseña, se usa tal cual viene

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), 
            loginRequest
        );

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                          FilterChain chain, Authentication authResult) 
            throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException failed) 
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
