package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;

@Data
public class VotoOpcionPorCorregir implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6506408380200695498L;
	private Long posicion;
    private Integer idDetActaOpcion;
    private String primeraDigitacion;
    private String segundaDigitacion;
    private String terceraDigitacion;
}
