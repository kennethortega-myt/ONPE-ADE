package pe.gob.onpe.scebackend.model.stae.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaElectoralResponse {

	private String estadoActa;
	private String estadoActaResolucion;
	private String estadoCompu;
	private String estadoErrorAritmetico;
	private Integer tipoTransmision;
	
}
