package pe.gob.onpe.scebackend.exeption;

public class CreacionDirectorioException extends RuntimeException {

	private static final long serialVersionUID = 1566887563543107698L;
	
	public CreacionDirectorioException(String errorMessage) {
		super(errorMessage);
	}

}
