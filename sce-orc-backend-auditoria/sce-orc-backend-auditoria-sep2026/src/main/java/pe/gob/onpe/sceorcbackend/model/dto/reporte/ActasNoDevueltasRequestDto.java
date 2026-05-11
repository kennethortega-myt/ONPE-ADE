package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ActasNoDevueltasRequestDto extends ReporteBaseRequestDto{

	@Alphanumeric
	private String ubigeo;
	private Integer tipoImpresion;
	private String proceso;
	private String centroComputo;
	private String odpe;
}
