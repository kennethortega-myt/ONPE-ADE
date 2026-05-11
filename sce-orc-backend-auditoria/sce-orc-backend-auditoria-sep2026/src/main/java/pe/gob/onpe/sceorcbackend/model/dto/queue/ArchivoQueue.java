package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoQueue implements Serializable {

	  @Serial
    private static final long serialVersionUID = -250671477691362600L;
	  private String guid;
    private String nombre;
    private String nombreOriginal;
    private String formato;
    private String peso;
    private String ruta;
    private Integer activo;
    private String audUsuarioCreacion;
    private Date audFechaCreacion;
    private String audUsuarioModificacion;
    private Date audFechaModificacion;
    private String accion;
    private byte[] file;

}
