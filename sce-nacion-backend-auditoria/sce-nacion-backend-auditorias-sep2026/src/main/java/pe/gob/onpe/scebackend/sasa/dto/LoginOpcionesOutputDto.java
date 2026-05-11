package pe.gob.onpe.scebackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOpcionesOutputDto {
	private Integer idModulo; 
	private Integer idOpcion;
	 private String nombre;
	 private String descripcion;
	 private String url;
	 private String icono;
}
