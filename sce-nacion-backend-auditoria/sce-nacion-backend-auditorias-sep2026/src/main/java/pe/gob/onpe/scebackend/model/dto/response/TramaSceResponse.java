package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;
import pe.gob.onpe.scebackend.ext.pr.dto.TramaVistaResponse;

@Data
public class TramaSceResponse {

	private boolean success;
    private String message;
	private TramaVistaResponse data;
}
