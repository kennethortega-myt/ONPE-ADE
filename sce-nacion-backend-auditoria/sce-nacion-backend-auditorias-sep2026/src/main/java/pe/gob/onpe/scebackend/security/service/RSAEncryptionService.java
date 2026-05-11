package pe.gob.onpe.scebackend.security.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.exeption.InternalServerErrorException;
import javax.crypto.Cipher;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RSAEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(RSAEncryptionService.class);
    private KeyPair keyPair;

    @Value("${security.rsa.private-key}")
    private String privateKey;

    @Value("${security.rsa.public-key}")
    private String publicKey;

    @PostConstruct
    public void init() {
        loadKeyPair();
    }

    private void loadKeyPair() {
        try {
            String privateKeyPEM = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            String publicKeyPEM = publicKey
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);

            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.keyPair = new KeyPair(keyFactory.generatePublic(publicSpec), keyFactory.generatePrivate(privateSpec));
            logger.info("RSA Key Pair cargado exitosamente");
        } catch (Exception e) {
            throw new InternalServerErrorException("Error cargando claves RSA");
        }
    }

    public String getPublicKeyAsString() {
        try {
            PublicKey publicKey = keyPair.getPublic();
            byte[] publicKeyBytes = publicKey.getEncoded();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKeyBytes);

            // Formatear como PEM
            return "-----BEGIN PUBLIC KEY-----\n" +
                    publicKeyString.replaceAll("(.{64})", "$1\n") +
                    "\n-----END PUBLIC KEY-----";
        } catch (Exception e) {
            throw new InternalServerErrorException("Error obteniendo clave pública");
        }
    }

    public String decryptPassword(String encryptedPassword) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new InternalServerErrorException("Error desencriptando contraseña");
        }
    }
}
