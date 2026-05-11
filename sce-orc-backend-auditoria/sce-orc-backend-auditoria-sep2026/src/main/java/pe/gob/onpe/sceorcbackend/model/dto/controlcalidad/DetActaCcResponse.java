package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetActaCcResponse {
	private Long	id;
	private Long   	idAgrupacion;
	private Long	posicion;
	private Long	votos;
	private Integer	estado;
}
