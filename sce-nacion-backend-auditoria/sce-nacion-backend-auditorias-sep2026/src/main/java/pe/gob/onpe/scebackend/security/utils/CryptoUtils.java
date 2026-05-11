package pe.gob.onpe.scebackend.security.utils;

import lombok.extern.log4j.Log4j2;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Cipher;

@Log4j2
public class CryptoUtils {

    Cipher ecipher;
    Cipher dcipher;

    byte[] salt = {(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35,
            (byte) 0xE3, (byte) 0x03};

    public static CryptoUtils getInstance(String key) {
        return new CryptoUtils(key);
    }

    private BufferedBlockCipher cipher;
    private KeyParameter key;

    // Initialize the cryptographic engine.
    // The key array should be at least 8 bytes long.
    public CryptoUtils(byte[] key) {
        cipher = new PaddedBlockCipher(new CBCBlockCipher(new BlowfishEngine()));
        this.key = new KeyParameter(key);
    }

    // Initialize the cryptographic engine.
    // The string should be at least 8 chars long.
    public CryptoUtils(String key) {
        this(key.getBytes());
    }

    // Private routine that does the gritty work.
    private byte[] callCipher(byte[] data) throws Exception {
        int size
                = cipher.getOutputSize(data.length);
        byte[] result = new byte[size];
        int olen = cipher.processBytes(data, 0, data.length, result, 0);
        olen += cipher.doFinal(result, olen);

        if (olen < size) {
            byte[] tmp = new byte[olen];
            System.arraycopy(result, 0, tmp, 0, olen);
            result = tmp;
        }

        return result;
    }

    public String encrypt(String data) throws Exception {

        String result = null;

        if (data != null) {
            cipher.init(true, key);
            byte[] b = callCipher(data.getBytes());
            result = new String(Base64.encode(b));
        } else {
            return new String(new byte[0]);
        }
        return result;
    }

    public String encrypt(byte[] data) throws Exception {

        String result = null;

        if (data.length != 0) {
            cipher.init(true, key);
            byte[] b = callCipher(data);
            result = new String(Base64.encode(b));
        } else {
            return new String(new byte[0]);
        }
        return result;
    }

    public String decrypt(String data) throws Exception {

        String result = null;

        if (data != null) {
            cipher.init(false, key);
            byte[] decode = Base64.decode(data);
            byte[] b = callCipher(decode);
            result = new String(b);
        } else {
            return new String(new byte[0]);
        }
        return result;
    }

    public String decrypt(byte[] data) throws Exception {

        String result = null;

        if (data.length != 0) {
            cipher.init(false, key);
            byte[] decode = Base64.decode(data);
            byte[] b = callCipher(decode);
            result = new String(b);
        } else {
            return new String(new byte[0]);
        }
        return result;
    }

    public static String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}