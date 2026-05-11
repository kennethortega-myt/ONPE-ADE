package pe.gob.onpe.sceorcbackend.model.dto.request.usuario;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequestDto {
    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer tipoDocumento;

    @NotEmpty(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 20, message = "El documento debe tener entre 8 y 20 dígitos")
    @Pattern(regexp = "^[0-9]+$", message = "El número de documento solo puede contener números")
    private String documento;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El apellido paterno solo puede contener letras y espacios, en mayúscula")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Size(max = 100, message = "El apellido paterno no puede exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El apellido materno solo puede contener letras y espacios, en mayúscula")
    private String apellidoMaterno;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras y espacios, en mayúscula")
    private String nombres;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    @Email(message = "Correo con formato inválido")
    private String correo;

    @Min(value = 0, message = "El campo activo solo soporta los valores 1 y 0")
    @Max(value = 1, message = "El campo activo solo soporta los valores 1 y 0")
    @NotNull(message = "El campo activo es obligatorio")
    private Integer activo;
}
