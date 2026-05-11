package pe.gob.onpe.scebackend.sasa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginInputDto {
	@NotBlank(message = "El usuario no puede ser nulo ni estar en blanco")
	@NotNull(message = "El usuario no puede ser nulo")
	private String usuario;
	@NotBlank(message = "La Contraseña no puede ser nulo ni estar en blanco")
	@NotNull(message = "La Contraseña no puede ser nulo")
	private String clave;
	
	@NotBlank(message = "El captcha no puede ser nulo ni estar en blanco")
	@NotNull(message = "El captcha no puede ser nulo")
	private String recaptcha;	

	@NotBlank(message = "El código no puede ser nulo ni estar en blanco")
	@NotNull(message = "El código no puede ser nulo")
	private String codigo;	

}
