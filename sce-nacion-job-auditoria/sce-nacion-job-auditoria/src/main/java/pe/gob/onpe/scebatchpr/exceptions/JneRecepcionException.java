package pe.gob.onpe.scebatchpr.exceptions;

public class JneRecepcionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JneRecepcionException(String message) {
        super(message);
    }

    public JneRecepcionException(String message, Throwable cause) {
        super(message, cause);
    }
}
