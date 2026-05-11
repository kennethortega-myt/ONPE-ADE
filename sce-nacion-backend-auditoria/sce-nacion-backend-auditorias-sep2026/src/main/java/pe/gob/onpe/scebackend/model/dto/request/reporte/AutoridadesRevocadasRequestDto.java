package pe.gob.onpe.scebackend.model.dto.request.reporte;

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
