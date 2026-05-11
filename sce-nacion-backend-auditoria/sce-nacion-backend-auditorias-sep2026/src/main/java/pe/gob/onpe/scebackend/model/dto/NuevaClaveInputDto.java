package pe.gob.onpe.scebackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NuevaClaveInputDto {
    @NotBlank(message = "La clave nueva es obligatorio")
    @Size(max = 20, message = "La clave nueva no debe superar los 20 caracteres")
    private String claveNueva;

    @NotBlank(message = "Confirmar la clave nueva es obligatorio")
    @Size(max = 20, message = "La confirmación de clave no debe superar los 20 caracteres")
    private String confirmaClaveNueva;
}
