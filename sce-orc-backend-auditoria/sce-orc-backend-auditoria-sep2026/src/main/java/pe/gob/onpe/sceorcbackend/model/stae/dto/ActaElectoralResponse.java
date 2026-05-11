package pe.gob.onpe.sceorcbackend.model.stae.dto;

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
