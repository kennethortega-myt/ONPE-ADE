package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import java.util.List;

import lombok.Data;

@Data
public class RechazarCcRequest {
	
	private Long idActa;
	private List<Long> idsResoluciones;
	
}
