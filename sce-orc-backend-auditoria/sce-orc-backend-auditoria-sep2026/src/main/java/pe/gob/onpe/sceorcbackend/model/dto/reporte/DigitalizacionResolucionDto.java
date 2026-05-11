package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class DigitalizacionResolucionDto extends ReporteBaseRequestDto{

	@Alphanumeric
	private String ubigeo;
	private String proceso;
	private String centroComputo;
	private String odpe;
	@Alphanumeric
	private String nombreEleccion;
	@Alphanumeric
	private String departamento;
	@Alphanumeric
	private String provincia;
	@Alphanumeric
	private String distrito;
}
