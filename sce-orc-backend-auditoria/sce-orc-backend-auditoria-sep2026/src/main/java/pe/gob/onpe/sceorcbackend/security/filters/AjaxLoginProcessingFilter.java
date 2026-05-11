package pe.gob.onpe.sceorcbackend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
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
import pe.gob.onpe.sceorcbackend.security.dto.LoginRequest;
import pe.gob.onpe.sceorcbackend.security.exceptions.AuthMethodNotSupportedException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.gob.onpe.sceorcbackend.security.service.RSAEncryptionService;

import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter   {


    private static final Logger LOGGER = LoggerFactory.getLogger(AjaxLoginProcessingFilter.class);

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    private final ObjectMapper objectMapper;

    @Setter
    private RSAEncryptionService rsaEncryptionService;


    public AjaxLoginProcessingFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
                                     AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
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


        if (StringUtils.isBlank(loginRequest.getUsername()) ||
                StringUtils.isBlank(loginRequest.getPassword())) {
            throw new AuthenticationServiceException("Username or Password or DNI not provided");
        }

        try {
            if (rsaEncryptionService != null) {
                LOGGER.info("Desencriptando contraseña con RSA para usuario: {}", loginRequest.getUsername());
                String decryptedPassword = rsaEncryptionService.decryptPassword(loginRequest.getPassword());
                loginRequest.setPassword(decryptedPassword);
                LOGGER.info("Contraseña desencriptada exitosamente para usuario: {}", loginRequest.getUsername());
            } else {
                LOGGER.warn("RSAEncryptionService no está disponible, usando contraseña sin desencriptar.");
            }
        } catch (Exception e) {
            LOGGER.error("Error desencriptando contraseña para usuario: {}", loginRequest.getUsername(), e);
            throw new AuthenticationServiceException("Error en desencriptación de contraseña");
        }


        String ipAddress = null;
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
        loginRequest.getAdicional().put("ipAddress", ipAddress);
        LOGGER.info("IP capturada en filtro de login: {} para usuario: {}", ipAddress, loginRequest.getUsername());


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest);

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

}