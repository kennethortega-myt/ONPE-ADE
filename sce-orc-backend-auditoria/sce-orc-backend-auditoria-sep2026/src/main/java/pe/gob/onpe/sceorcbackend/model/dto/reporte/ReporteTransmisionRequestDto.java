package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteTransmisionRequestDto extends ReporteBaseRequestDto{

	private String proceso;
	private String centroComputo;
}
