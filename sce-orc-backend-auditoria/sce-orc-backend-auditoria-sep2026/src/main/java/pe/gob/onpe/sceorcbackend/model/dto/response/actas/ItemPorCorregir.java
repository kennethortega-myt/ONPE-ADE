package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;

@Data
public class ItemPorCorregir implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3075369904210359889L;
	private String detalle;
    private String organizacionPolitica;
    private String primeraDigitacion;
    private String segundaDigitacion;
    private String terceraDigitacion;
}
