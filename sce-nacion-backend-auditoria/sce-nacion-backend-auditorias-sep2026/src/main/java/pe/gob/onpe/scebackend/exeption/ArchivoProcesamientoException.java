package pe.gob.onpe.scebackend.exeption;

public class ArchivoProcesamientoException extends RuntimeException{
    public ArchivoProcesamientoException(String message) {
        super(message);
    }

    public ArchivoProcesamientoException(String message, Throwable cause) {
        super(message, cause);
    }
}
