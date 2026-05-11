package pe.gob.onpe.sceorcbackend.model.dto.stae;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DetActaStae implements Serializable {
   
	private static final long serialVersionUID = 8729993794829545327L;
	
	private String codigoAgrupacionPolitica;
    private Integer posicionAgrupacionPolitica;
    private Integer estadoAgrupacionPolitica;
    private String dniPersonero;
    private String estadoErrorMaterial;
    private Long votos;
    private List<DetActaPreferencialStae> detalleActaPreferencial;

}
