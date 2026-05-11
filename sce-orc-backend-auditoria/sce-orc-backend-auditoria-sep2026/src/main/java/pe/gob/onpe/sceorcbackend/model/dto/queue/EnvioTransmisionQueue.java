package pe.gob.onpe.sceorcbackend.model.dto.queue;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EnvioTransmisionQueue implements Serializable {
	
	private static final long serialVersionUID = -8756747412971259179L;

	Long idTransmision;
	boolean exitoso;
	String proceso;
	String usuario;
	
	
}
