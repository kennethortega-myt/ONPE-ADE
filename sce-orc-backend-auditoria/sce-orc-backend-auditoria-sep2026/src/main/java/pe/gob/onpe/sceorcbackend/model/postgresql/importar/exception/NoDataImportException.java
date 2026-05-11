package pe.gob.onpe.sceorcbackend.model.postgresql.importar.exception;

public class NoDataImportException extends Exception {

	private static final long serialVersionUID = -3543341337038644508L;

	public NoDataImportException(String message) {
        super(message);
    }

    public NoDataImportException(String message, Throwable cause) {
        super(message, cause);
    }
    
}