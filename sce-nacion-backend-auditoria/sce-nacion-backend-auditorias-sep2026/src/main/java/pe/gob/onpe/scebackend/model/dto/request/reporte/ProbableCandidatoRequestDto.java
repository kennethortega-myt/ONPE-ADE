package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProbableCandidatoRequestDto extends ReporteBaseRequestDto{
	
	private String distritoElectoral;
	private Integer cargo;
	private String agrupacionPolitica;
	private String proceso;
	
}
