package pe.gob.onpe.scebackend.exeption;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String mensajeInterno;

  private final String mensaje;

  public GenericException(String mensaje) {
    super();
    this.mensajeInterno = mensaje;
    this.mensaje = mensaje;
  }

  public GenericException(String mensajeInterno, String mensaje) {
    super();
      this.mensajeInterno = mensajeInterno;
      this.mensaje = mensaje;
  }

  public GenericException(String message, Throwable rootCause, String mensajeInterno, String mensaje) {
    super(message, rootCause);
      this.mensajeInterno = mensajeInterno;
      this.mensaje = mensaje;
  }

  public GenericException(Throwable rootCause, String mensajeInterno, String mensaje) {
    super(rootCause);
      this.mensajeInterno = mensajeInterno;
      this.mensaje = mensaje;
  }


}
