package pe.gob.onpe.sceorcbackend.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class CryptoGenerate {

    private CryptoGenerate() {

    }

    public static String hash(String valor) {
        return BCrypt.hashpw(valor, BCrypt.gensalt());
    }

    public static boolean match(String valor, String hash) {
        return BCrypt.checkpw(valor, hash);
    }
}
