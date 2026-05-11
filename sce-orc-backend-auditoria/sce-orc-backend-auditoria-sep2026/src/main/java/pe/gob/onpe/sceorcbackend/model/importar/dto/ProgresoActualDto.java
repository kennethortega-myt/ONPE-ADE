package pe.gob.onpe.sceorcbackend.model.importar.dto;


import java.util.List;

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
public class ProgresoActualDto {

	private Integer estado;
	private Double porcentaje;
	List<DetProgresoActualDto> detalles;
	
}
