package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoridadesRevocadasRequestDto extends ReporteBaseRequestDto{

	private Integer idCargo;
	private String proceso;
	private String centroComputo;
	private String odpe;
}
