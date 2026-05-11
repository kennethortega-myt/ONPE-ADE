package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
public class ListaParticipantesRequestDto extends ReporteBaseRequestDto{

	@Alphanumeric
	private String ubigeo;
	@Alphanumeric
	private String mesa;
	private String proceso;
}
