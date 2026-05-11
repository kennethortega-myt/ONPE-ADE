package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.security.service.RSAEncryptionService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final RSAEncryptionService rsaEncryptionService;

    public AuthController(RSAEncryptionService rsaEncryptionService) {
        this.rsaEncryptionService = rsaEncryptionService;
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
            response.put("success", ConstantesComunes.TEXT_TRUE);
            logger.info("Clave pública RSA enviada exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error obteniendo clave pública RSA", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error obteniendo clave pública");
            errorResponse.put("success", ConstantesComunes.TEXT_FALSE);
            errorResponse.put("timestamp", Instant.now().toString());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
