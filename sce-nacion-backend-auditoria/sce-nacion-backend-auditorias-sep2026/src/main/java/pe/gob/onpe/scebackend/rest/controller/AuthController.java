package pe.gob.onpe.scebackend.rest.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import pe.gob.onpe.scebackend.model.extranjero.dto.request.ClientTokenRequest;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.scebackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.scebackend.security.service.RSAEncryptionService;
import pe.gob.onpe.scebackend.security.utils.CryptoUtils;
import pe.gob.onpe.scebackend.security.utils.Util;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final RSAEncryptionService rsaEncryptionService;
    
    private final PasswordEncoder passwordEncoder;
    
    private final JwtTokenFactory tokenFactory;
    
    @Value("${auth.client.id}")
    private String clientId;

    @Value("${auth.client.secret}")
    private String clientSecret;

    public AuthController(
            RSAEncryptionService rsaEncryptionService,
            JwtTokenFactory tokenFactory) {
        this.rsaEncryptionService = rsaEncryptionService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.tokenFactory = tokenFactory;
    }


    /**
     * Endpoint público para obtener la clave pública RSA
     * No requiere autenticación - está en JwtConstant.API_LIBRES
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        try {
            logger.info("Solicitud de clave pública RSA recibida");
            String publicKey = rsaEncryptionService.getPublicKeyAsString();
            Map<String, String> response = new HashMap<>();
            response.put("publicKey", publicKey);
            response.put("timestamp", Instant.now().toString());
            response.put("success", "true");
            logger.info("Clave pública RSA enviada exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo clave pública RSA", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error obteniendo clave pública");
            errorResponse.put("success", "false");
            errorResponse.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/client-token")
    public ResponseEntity<Map<String, String>> generarTokenServices(
            @RequestBody ClientTokenRequest request,
            HttpServletRequest servletRequest) {
        
        if (!request.getClientId().equals(clientId)) {
            logger.warn("client_id no registrado: {}", request.getClientId());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        if (!passwordEncoder.matches(request.getClientSecret(), clientSecret)) {
            logger.warn("client_secret inválido para {}", request.getClientId());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        
        if (request.getUsername() == null || request.getAutoridad() == null) {
            logger.warn("Campos obligatorios faltantes en el request.");
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan datos requeridos"));
        }
        
        Util util = new Util();
        JwtAuthentication jwt = util.infoPC(servletRequest);
        jwt.setUsername(CryptoUtils.getHash(request.getUsername(), "SHA1"));
        String infoPcEncryp = util.encryJson(jwt);

        
        UserContext userContext = UserContext.create(
                CryptoUtils.getHash(request.getUsername(), "SHA1"),
                request.getUsername(),
                List.of(new SimpleGrantedAuthority(request.getAutoridad()))
            );

        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        
        userContext.setUsernameSinEncriptar(request.getUsername());
        userContext.setUserId(request.getUserId());
        userContext.setPerfilId(request.getPerfilId());
        userContext.setAcronimoProceso(request.getAcronimoProceso());
        userContext.setCodigoCentroComputo(request.getCodigoCentroComputo());
        userContext.setNombreCentroComputo(request.getNombreCentroComputo());
        userContext.setClaveNueva(0);
        userContext.setIdSession(enc.encode(request.getIdSession()));

        String token = tokenFactory.createAccessJwtToken(userContext, infoPcEncryp);

        return ResponseEntity.ok(Map.of(
            "access_token", token,
            "idSession", request.getIdSession()
        ));
    }
}
