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
public class ArchivoResolucionTransmisionDto implements Serializable {

	private static final long serialVersionUID = 2464871835057838344L;
	private Long idResolucion;
	private String base64;
	private String guid;
	private String peso;
	private String extension;
	private String mimeType;
	
}
