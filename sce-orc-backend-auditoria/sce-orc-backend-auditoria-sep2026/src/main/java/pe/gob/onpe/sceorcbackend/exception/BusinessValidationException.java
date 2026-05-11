package pe.gob.onpe.sceorcbackend.exception;

public class BusinessValidationException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
