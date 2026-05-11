package pe.gob.onpe.scebackend.exeption;

public class AuthTransmisionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AuthTransmisionException(String message) {
        super(message);
    }

    public AuthTransmisionException(String message, Throwable cause) {
        super(message, cause);
    }
}