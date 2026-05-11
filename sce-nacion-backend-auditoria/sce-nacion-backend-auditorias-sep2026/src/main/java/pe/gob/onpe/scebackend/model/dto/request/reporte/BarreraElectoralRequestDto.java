package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarreraElectoralRequestDto extends ReporteBaseRequestDto{

	private String distritoElectoral;
	private String agrupacionPolitica;
	private String proceso;
}
