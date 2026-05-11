package pe.gob.onpe.sceorcbackend.exception.utils;

public class ArchivoProcesamientoException extends RuntimeException {

	private static final long serialVersionUID = -1374390339035205933L;

	public ArchivoProcesamientoException(String message) {
        super(message);
    }

    public ArchivoProcesamientoException(String message, Throwable cause) {
        super(message, cause);
    }
}