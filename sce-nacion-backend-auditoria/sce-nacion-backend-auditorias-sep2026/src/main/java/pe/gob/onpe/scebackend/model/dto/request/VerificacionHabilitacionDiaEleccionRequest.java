package pe.gob.onpe.scebackend.model.dto.request;

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
public class VerificacionHabilitacionDiaEleccionRequest {

	private String acronimo;
	private String formatoFechaConvocatoria;
	
}
