package pe.gob.onpe.sceorcbackend.security.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH)
    LocalDateTime timestamp;
    int resultado;
}