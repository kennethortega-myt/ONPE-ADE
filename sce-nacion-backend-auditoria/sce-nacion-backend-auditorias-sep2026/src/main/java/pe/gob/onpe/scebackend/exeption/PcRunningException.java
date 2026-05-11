package pe.gob.onpe.scebackend.exeption;

public class PcRunningException extends RuntimeException {

    
	private static final long serialVersionUID = -3383202952440404132L;

	public PcRunningException(String message) {
        super(message);
    }

    public PcRunningException(String message, Throwable cause) {
        super(message, cause);
    }
}