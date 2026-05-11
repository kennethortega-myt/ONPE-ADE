package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleVoteItem;

@Data
@Builder
public class DataPaso3Response {
		
	private DetActaRectangleVoteItem imagenesAgrupol;
	private DetActaRectangleVoteItem imagenesPreferencial;
	private Integer numeroColumnasPref;
	private List<DetActaCcResponse> detActaAgrupol;
	private List<List<DetActaCcResponse>> detActaPreferenciales;
	private Long cvas;
	private boolean sinFirmas;
	private boolean solicitudNulidad;
	
}