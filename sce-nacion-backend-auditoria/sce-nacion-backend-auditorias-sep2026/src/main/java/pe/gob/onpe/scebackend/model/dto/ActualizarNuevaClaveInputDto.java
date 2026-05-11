package pe.gob.onpe.scebackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarNuevaClaveInputDto {
    @NotBlank(message = "La clave nueva es obligatorio")
    @NotNull(message = "La clave nueva es obligatorio")
    @Size(max = 20, message = "La clave nueva no debe superar los 20 caracteres")
    @Pattern(regexp = "^[^/*]+$", message = "La clave nueva no puede contener los caracteres '/' o '*'")
    private String claveNueva;
    @NotBlank(message = "La clave actual es obligatorio")
    @NotNull(message = "La clave actual es obligatorio")
    @Size(max = 20, message = "La clave actual no debe superar los 20 caracteres")
    private String claveActual;
}