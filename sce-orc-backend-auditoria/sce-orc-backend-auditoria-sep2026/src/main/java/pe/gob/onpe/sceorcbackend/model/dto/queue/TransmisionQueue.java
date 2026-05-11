package pe.gob.onpe.sceorcbackend.model.dto.queue;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;

@Getter
@Setter
@Builder
public class TransmisionQueue implements Serializable {

	private static final long serialVersionUID = 7548127148784070773L;
	
	Long idActa;
	String proceso; 
	TransmisionNacionEnum estadoEnum; 
	String usuario;
	
}
