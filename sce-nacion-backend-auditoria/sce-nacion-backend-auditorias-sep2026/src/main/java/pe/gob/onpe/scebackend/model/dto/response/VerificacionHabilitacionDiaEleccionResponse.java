package pe.gob.onpe.scebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificacionHabilitacionDiaEleccionResponse {

	private String acronimo;
	private String fechaConvocatoria;
	private String formatoFechaConvocatoria;
	
}
