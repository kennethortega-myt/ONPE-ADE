package pe.gob.onpe.scebackend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReparteCurulesRequestDto {    
    @NotEmpty(message = "El tipo de elección no puede estar vacío.")
    String codEleccion;
    @NotEmpty(message = "El distrito electoral no puede estar vacío.")
    String codDistritoElectoral;
    @NotEmpty(message = "El código de usuario no puede estar vacío.")
    String codigoUsuario;
    @NotEmpty(message = "El nombre del PC no puede estar vacío.")
    String nombrePc;
    Integer forzarCalculo;
}
