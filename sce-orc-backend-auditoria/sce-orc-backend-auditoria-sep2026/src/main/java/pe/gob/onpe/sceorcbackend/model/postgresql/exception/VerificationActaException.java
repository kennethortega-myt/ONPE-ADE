package pe.gob.onpe.sceorcbackend.model.postgresql.exception;

public class VerificationActaException extends Exception {

	private static final long serialVersionUID = 1073108518479109993L;

	public VerificationActaException(String message) {
        super(message);
    }

    public VerificationActaException(String message, Throwable cause) {
        super(message, cause);
    }
}