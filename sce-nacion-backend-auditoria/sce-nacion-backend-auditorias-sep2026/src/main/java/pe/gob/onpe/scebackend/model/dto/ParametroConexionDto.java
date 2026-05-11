package pe.gob.onpe.scebackend.model.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametroConexionDto {

	private Long idCentroComputo;
	private Integer puerto;
	private String  ip;
	private String protocolo;
	private boolean activar;
	
}
