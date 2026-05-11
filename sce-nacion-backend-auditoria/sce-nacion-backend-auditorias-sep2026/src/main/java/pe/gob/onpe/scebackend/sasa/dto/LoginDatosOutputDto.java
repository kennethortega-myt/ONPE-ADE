package pe.gob.onpe.scebackend.sasa.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDatosOutputDto {
	
	private Integer resultado;
	private String mensaje;
	private LoginOutputDto datos;
}
