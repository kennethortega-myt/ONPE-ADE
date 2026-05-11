package pe.gob.onpe.scebackend.model.vd.dto.login;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6161573056492231406L;

	private String token;
	private boolean success;
	private String message;
	private boolean awsEnabled;
	private String userData;
	
}
