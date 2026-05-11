package pe.gob.onpe.scebackend.security.config;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.model.service.impl.TokenBlacklistService;
import pe.gob.onpe.scebackend.security.AjaxAuthenticationProvider;
import pe.gob.onpe.scebackend.security.RestAuthenticationEntryPoint;
import pe.gob.onpe.scebackend.security.SkipPathRequestMatcher;
import pe.gob.onpe.scebackend.security.TokenExtractor;
import pe.gob.onpe.scebackend.security.filters.AjaxLoginProcessingFilter;
import pe.gob.onpe.scebackend.security.filters.CORSFilter;
import pe.gob.onpe.scebackend.security.filters.PlainTextLoginProcessingFilter;
import pe.gob.onpe.scebackend.security.jwt.JwtAuthenticationProvider;
import pe.gob.onpe.scebackend.security.jwt.JwtConstant;
import pe.gob.onpe.scebackend.security.jwt.JwtTokenAuthenticationProcessingFilter;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.security.service.RSAEncryptionService;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String AUTHENTICATION_EXTERNAL_URL = "/api/auth/external-login";
    public static final String API_ROOT_URL = "/**";


    // ← CAMPOS FINAL PARA INMUTABILIDAD
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final AjaxAuthenticationProvider ajaxAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final TokenExtractor tokenExtractor;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenUtilService tokenUtilService;
    private final RSAEncryptionService rsaEncryptionService;

    // ← CONSTRUCTOR PARA INYECCIÓN DE DEPENDENCIAS
    public WebSecurityConfig(
            RestAuthenticationEntryPoint authenticationEntryPoint,
            AjaxAuthenticationProvider ajaxAuthenticationProvider,
            JwtAuthenticationProvider jwtAuthenticationProvider,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            TokenExtractor tokenExtractor,
            ObjectMapper objectMapper,
            TokenBlacklistService tokenBlacklistService,
            TokenUtilService tokenUtilService,
            RSAEncryptionService rsaEncryptionService) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.ajaxAuthenticationProvider = ajaxAuthenticationProvider;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.tokenExtractor = tokenExtractor;
        this.objectMapper = objectMapper;
        this.tokenBlacklistService = tokenBlacklistService;
        this.tokenUtilService = tokenUtilService;
        this.rsaEncryptionService = rsaEncryptionService; 
    }



    @Autowired
    public void registerProviders(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(ajaxAuthenticationProvider)
            .authenticationProvider(jwtAuthenticationProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(AuthenticationManager authManager) {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(
            AUTHENTICATION_URL,
            successHandler,
            failureHandler,
            objectMapper
        );
        filter.setAuthenticationManager(authManager);
        filter.setRsaEncryptionService(rsaEncryptionService);
        return filter;
    }

    private PlainTextLoginProcessingFilter buildExternalLoginProcessingFilter(AuthenticationManager authManager) {
        PlainTextLoginProcessingFilter filter = new PlainTextLoginProcessingFilter(
            AUTHENTICATION_EXTERNAL_URL,
            successHandler,
            failureHandler,
            objectMapper
        );
        filter.setAuthenticationManager(authManager);
        return filter;
    }

    private JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(
        List<String> pathsToSkip, String pattern, AuthenticationManager authManager) {

        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(
            failureHandler,
            tokenExtractor,
            matcher,
            tokenBlacklistService,
            tokenUtilService
        );
        filter.setAuthenticationManager(authManager);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF deshabilitado intencionalmente: la autenticación con JWT es sin estado (stateless) y no utiliza cookies
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(JwtConstant.getWhitelistPaths()).permitAll()
                .requestMatchers(API_ROOT_URL).authenticated()
                .anyRequest().denyAll()
            )

            // Filtro de CORS
            .addFilterBefore(new CORSFilter(), UsernamePasswordAuthenticationFilter.class)

            // Filtro de login AJAX (con RSA)
            .addFilterBefore(buildAjaxLoginProcessingFilter(authManager), UsernamePasswordAuthenticationFilter.class)

            // Filtro de login externo (sin RSA)
            .addFilterBefore(buildExternalLoginProcessingFilter(authManager), UsernamePasswordAuthenticationFilter.class)

            // Filtro de autenticación JWT
            .addFilterBefore(
                buildJwtTokenAuthenticationProcessingFilter(
                        List.of(JwtConstant.getWhitelistPaths()),
                        API_ROOT_URL,
                        authManager),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/resources/**"
        );
    }
}
