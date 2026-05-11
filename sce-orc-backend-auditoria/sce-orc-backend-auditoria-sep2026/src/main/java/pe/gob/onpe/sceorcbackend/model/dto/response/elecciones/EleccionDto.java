package pe.gob.onpe.sceorcbackend.model.dto.response.elecciones;

import lombok.Data;
import java.io.Serializable;

@Data
public class EleccionDto implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4920000663143140807L;
	private String codigo;
    private String nombre;
    private String rangoInicial;
    private String rangoFinal;
    private String digitoChequeoAE;
    private String digitoChequeoAIS;
    private String digitoCequeoError;
}
