package pe.gob.onpe.scebatchpr.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ArchivoTransmisionDto implements Serializable {
	 
	private static final long serialVersionUID = 8570465072037724405L;
	private String base64;
	private String guid;
	private String peso;
	private String extension;
	private String mimeType;
	private Integer tipoArchivo;

}
