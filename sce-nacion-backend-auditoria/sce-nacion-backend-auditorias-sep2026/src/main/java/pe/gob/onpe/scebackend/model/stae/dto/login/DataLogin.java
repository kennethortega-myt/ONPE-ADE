package pe.gob.onpe.scebackend.model.stae.dto.login;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataLogin implements Serializable {

	private static final long serialVersionUID = 7312431029735659036L;
	private String token;
	private Integer estado;
	
}
