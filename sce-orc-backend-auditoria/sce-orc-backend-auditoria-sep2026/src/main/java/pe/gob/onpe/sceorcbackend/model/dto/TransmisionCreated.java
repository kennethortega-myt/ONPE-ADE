package pe.gob.onpe.sceorcbackend.model.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransmisionCreated {

	private Long idTransmision;
	private String proceso;
	private String usuario;
	private boolean success;
	
}
