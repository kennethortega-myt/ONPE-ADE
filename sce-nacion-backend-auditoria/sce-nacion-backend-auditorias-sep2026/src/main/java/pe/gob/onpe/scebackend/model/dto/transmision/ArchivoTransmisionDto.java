package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArchivoTransmisionDto implements Serializable {
	 
	private static final long serialVersionUID = 3147655482960116271L;
	 
	private String base64;
	private String guid;
	private String peso;
	private String extension;
	private String mimeType;


}
