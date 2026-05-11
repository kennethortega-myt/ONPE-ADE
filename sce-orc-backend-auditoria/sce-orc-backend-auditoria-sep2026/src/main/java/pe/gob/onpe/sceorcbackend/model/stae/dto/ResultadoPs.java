package pe.gob.onpe.sceorcbackend.model.stae.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResultadoPs {

	private Integer poResultado;
	private String poMensaje;
	private String poEstadoActa;
	private String poEstadoActaResolucion;
	private String poEstadoComputo;
	private String poEstadoErrorMaterial;
	private Integer poTipoTransmision;
}
