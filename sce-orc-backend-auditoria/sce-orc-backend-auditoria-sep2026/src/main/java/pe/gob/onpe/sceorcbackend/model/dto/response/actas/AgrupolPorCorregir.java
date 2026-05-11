package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class AgrupolPorCorregir implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2879578084644070543L;
	private Integer nro;
    private Long idDetActa;
    private String organizacionPolitica;
    private String primeraDigitacion;
    private String segundaDigitacion;
    private String terceraDigitacion;
    private List<VotoPreferencialPorCorregir> votosPreferenciales;
    private List<VotoOpcionPorCorregir> votosOpciones;
}
