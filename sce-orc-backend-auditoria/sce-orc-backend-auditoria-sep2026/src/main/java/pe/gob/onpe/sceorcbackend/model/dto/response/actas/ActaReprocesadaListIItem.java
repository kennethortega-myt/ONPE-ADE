package pe.gob.onpe.sceorcbackend.model.dto.response.actas;

import lombok.Data;
import java.io.Serializable;
@Data
public class ActaReprocesadaListIItem implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3714775228005968885L;
	private Long actaId;
    private String mesa;
    private String copia;
    private String digitoChequeo;
    private String eleccion;
    private String estadoActa;
    private Long autorizacionId;
    private String procesar;
}
