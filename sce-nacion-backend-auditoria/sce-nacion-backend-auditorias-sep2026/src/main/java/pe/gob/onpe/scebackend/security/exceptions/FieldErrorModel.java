package pe.gob.onpe.scebackend.security.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldErrorModel {
    String fieldName;
    String fieldError;
}
