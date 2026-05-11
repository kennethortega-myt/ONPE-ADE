package pe.gob.onpe.scebackend.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsolidarVotosAgrupacionRequestDto {    

    @NotEmpty(message = "El tipo de elección no puede estar vacío.")
    private String codEleccion;

    @NotEmpty(message = "El código de distrito electoral no puede estar vacío.")
    private String codDistritoElectoral;

    @NotEmpty(message = "El código de usuario no puede estar vacío.")
    private String codigoUsuario;
    
    private String nombrePc;

}
