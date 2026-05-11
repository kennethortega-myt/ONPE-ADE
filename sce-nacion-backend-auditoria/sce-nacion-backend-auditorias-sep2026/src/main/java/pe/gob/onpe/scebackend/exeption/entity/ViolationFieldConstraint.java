package pe.gob.onpe.scebackend.exeption.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ViolationFieldConstraint {
    private String campo;
    private String mensaje;
}
