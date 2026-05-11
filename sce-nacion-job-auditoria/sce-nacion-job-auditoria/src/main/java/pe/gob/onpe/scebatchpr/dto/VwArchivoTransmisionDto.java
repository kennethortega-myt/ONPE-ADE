package pe.gob.onpe.scebatchpr.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VwArchivoTransmisionDto implements Serializable {


	private static final long serialVersionUID = 8709997079171651379L;
	
	private Long idActa;
	private Long idArchivo;
	private String nombreOriginal;
	private String ruta;
    private String guid;
    private String formato;
    private String peso;
    private Integer documentoElectoral;
	
}
