package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AplicarActaBean implements Serializable {
 
	private static final long serialVersionUID = 541240763504199722L;
	private String idResolucion;
    private Long actaId;
    private String mesa;
    private String copia;
    private String codigoEleccion;
    private String codigoProceso;
    private boolean siguiente;
}
