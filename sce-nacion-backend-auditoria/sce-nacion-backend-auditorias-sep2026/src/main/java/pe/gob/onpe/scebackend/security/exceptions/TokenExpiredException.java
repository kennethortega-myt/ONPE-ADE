package pe.gob.onpe.scebackend.security.exceptions;



public class TokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = 378518966805472467L;

	public TokenExpiredException(String message) {
        super(message);
    }
}
