package pe.gob.onpe.scebackend.model.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosGeneralesRequestDto {
	
	private Integer id;

	@Pattern(
			regexp = "^[a-zA-Z0-9\\- ]+$",
			message = "La descripción solo puede contener letras, números y guiones"
	)
	private String nombre;
	private Integer activo;
	private String usuario;


}
