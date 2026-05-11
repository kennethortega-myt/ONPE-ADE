package pe.gob.onpe.scebackend.model.stae.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaeCcResponse<T> {

	private boolean success;
    private String message;
    private T data;
	
}
