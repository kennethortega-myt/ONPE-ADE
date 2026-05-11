package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetActaFormatoQueue implements Serializable {
	@Serial
  private static final long serialVersionUID = -7806277698788876316L;
	private ActaQueue actaQueue;
    private Integer correlativo;
    private Integer activo;
    private String audUsuarioCreacion;
    private Date audFechaCreacion;
    private String audUsuarioModificacion;
    private Date audFechaModificacion;
    private String accion;
}
