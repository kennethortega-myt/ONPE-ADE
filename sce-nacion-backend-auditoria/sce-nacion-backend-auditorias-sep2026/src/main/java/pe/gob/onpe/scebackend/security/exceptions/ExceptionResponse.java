package pe.gob.onpe.scebackend.security.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {
    String mensaje;
    String mensajeInteno;
    String requestedURI;
    List<FieldErrorModel> errorsField;
    int estado;
    String metodo;
    String clase;
    String lineaCodigoError;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    LocalDateTime timestamp;
    int resultado;
}