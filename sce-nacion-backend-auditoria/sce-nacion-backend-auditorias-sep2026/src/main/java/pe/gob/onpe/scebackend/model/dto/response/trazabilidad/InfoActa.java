package pe.gob.onpe.scebackend.model.dto.response.trazabilidad;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InfoActa implements Serializable {
	@Serial
    private static final long serialVersionUID = -1147882051041578379L;
	private Long actaId;
    private String mesa;
    private String copia;
    private String digitoChequeo;
    private String eleccion;
    private String descripcionEstadoActual;
    private Long electoresHabiles;
    private Long totalVotantes;
    private String participacionCiudadana;
    private String departamento;
    private String provincia;
    private String distrito;
    private String localVotacion;
    private String centroPoblado;
}
