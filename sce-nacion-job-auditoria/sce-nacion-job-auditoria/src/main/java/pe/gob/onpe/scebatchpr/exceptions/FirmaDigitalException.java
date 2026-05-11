package pe.gob.onpe.scebatchpr.exceptions;

public class FirmaDigitalException extends RuntimeException {

	private static final long serialVersionUID = 296216873235794963L;

	public FirmaDigitalException(String errorMessage) {
		super(errorMessage);
	}
	
	public FirmaDigitalException(String message, Throwable cause){
		super(message, cause);
	}
	
}
