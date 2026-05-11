package pe.gob.onpe.scebackend.sasa.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginPerfilesOutputDto {
	 private Integer idPerfil;
	 private String nombre;
	 private String descripcion;
	 private String abreviatura;
}
