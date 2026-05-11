package pe.gob.onpe.sceorcbackend.model.importar.dto;

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
public class DetProgresoActualDto {

	private String mensaje;
	private double porcentaje;
	
}
