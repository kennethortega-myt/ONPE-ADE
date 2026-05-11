package pe.gob.onpe.sceorcbackend.exception;

public class GenericException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String mensajeInterno;

  private String mensaje;

  public GenericException(String message) {
    super(message);
    this.setMensaje(message);
    this.setMensajeInterno(message);
  }

  public GenericException() {
    super();
  }

  public GenericException(String message, Throwable rootCause) {
    super(message, rootCause);
  }

  public GenericException(String mensaje, String mensajeInterno) {
    this.setMensaje(mensaje);
    this.setMensajeInterno(mensajeInterno);
  }

  public GenericException(Throwable rootCause) {
    super(rootCause);
  }

  public String getMensajeInterno() {
    return mensajeInterno;
  }

  public void setMensajeInterno(String mensajeInterno) {
    this.mensajeInterno = mensajeInterno;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }
}

