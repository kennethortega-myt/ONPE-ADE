package pe.gob.onpe.scebackend.model.vd.dto.pc;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuestaCeroVdResponse implements Serializable {

	private static final long serialVersionUID = 496182891537134991L;
	
	private boolean success;
	private String message;
	private DataPcDto data;
	
}
