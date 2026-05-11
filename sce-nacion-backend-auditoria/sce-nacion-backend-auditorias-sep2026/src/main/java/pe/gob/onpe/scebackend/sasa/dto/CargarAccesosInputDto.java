package pe.gob.onpe.scebackend.sasa.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
@Data
public class CargarAccesosInputDto {
	
	@NotNull(message = "El id del usuario no puede ser nulo")
	private Integer idUsuario;
	
	@NotNull(message = "El id del perfil no puede ser nulo")
	private Integer idPerfil;
}
