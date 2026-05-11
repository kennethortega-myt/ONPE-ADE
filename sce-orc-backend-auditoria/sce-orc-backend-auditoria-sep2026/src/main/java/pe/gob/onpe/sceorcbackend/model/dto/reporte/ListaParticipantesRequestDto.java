package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ListaParticipantesRequestDto extends ReporteBaseRequestDto{
	@Alphanumeric
	private String ubigeo;
	@Alphanumeric
	private String mesa;
	private String proceso;
}

