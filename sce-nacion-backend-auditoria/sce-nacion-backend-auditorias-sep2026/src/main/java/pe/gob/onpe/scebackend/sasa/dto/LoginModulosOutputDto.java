package pe.gob.onpe.scebackend.sasa.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginModulosOutputDto {
	 private Integer idModulo;
	 private String nombre;
	 private String descripcion;
	 private String icono;
	 private String url;
}
