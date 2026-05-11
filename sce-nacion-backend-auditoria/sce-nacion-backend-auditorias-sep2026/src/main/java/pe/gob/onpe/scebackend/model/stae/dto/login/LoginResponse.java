package pe.gob.onpe.scebackend.model.stae.dto.login;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse implements Serializable {

	private static final long serialVersionUID = -4938215727137476735L;
	
	private boolean success;
	private String message;
	private DataLogin data;
	
}
