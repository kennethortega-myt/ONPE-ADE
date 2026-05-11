package pe.gob.onpe.sceorcbackend.exception;

public class DuplicadoException extends RuntimeException {
  public DuplicadoException(String mensaje) {
    super(mensaje);
  }
}