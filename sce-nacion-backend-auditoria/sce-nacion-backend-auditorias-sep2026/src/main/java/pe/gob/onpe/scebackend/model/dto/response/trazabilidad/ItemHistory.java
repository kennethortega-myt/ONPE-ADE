package pe.gob.onpe.scebackend.model.dto.response.trazabilidad;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class ItemHistory implements Serializable {
	@Serial
    private static final long serialVersionUID = 285657247389746564L;
	private Long id;
    private String descripcionEstado;
    private String codEstadoActa;
    private String fecha;
    private String detalle;
    private String fechaInicioFin;
    private Integer activo;
}
