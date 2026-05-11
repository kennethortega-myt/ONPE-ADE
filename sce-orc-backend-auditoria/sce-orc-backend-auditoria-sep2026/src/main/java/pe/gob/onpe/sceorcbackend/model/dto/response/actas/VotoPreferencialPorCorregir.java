package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;

@Data
public class VotoPreferencialPorCorregir implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7345995292457322723L;
	private Integer lista;
    private Long idDetActaPreferencial;
    private String primeraDigitacion;
    private String segundaDigitacion;
    private String terceraDigitacion;

}
