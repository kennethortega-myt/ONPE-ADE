package pe.gob.onpe.sceorcbackend.model.stae.exception;

public class ActaStaeNoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = -2056820999633175704L;

	public ActaStaeNoEncontradaException(String message) {
        super(message);
    }

    public ActaStaeNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}