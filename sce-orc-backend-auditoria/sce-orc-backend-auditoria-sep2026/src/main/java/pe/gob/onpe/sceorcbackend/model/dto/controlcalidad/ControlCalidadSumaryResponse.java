package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ControlCalidadSumaryResponse {

	private Integer pendiente;
	private Integer validado;
}
