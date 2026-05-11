package pe.gob.onpe.scebackend.utils.verificaversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilitario {

    private Utilitario() {

    }

    private static final Logger logger = LoggerFactory.getLogger(Utilitario.class);

    public static String hashSHA256(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ConstantesComunes.ALGORITHM_SHA_256);
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            return String.format("%064x", number); // 64-character zero-padded hex
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not found: {}", e.getMessage());
        }
        return null;
    }

    public static String getFileHashSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ConstantesComunes.ALGORITHM_SHA_256);

        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
