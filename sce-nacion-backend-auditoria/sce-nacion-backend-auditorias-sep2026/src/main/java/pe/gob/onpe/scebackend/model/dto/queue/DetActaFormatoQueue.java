package pe.gob.onpe.scebackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetActaFormatoQueue implements Serializable {

    @Serial
    private static final long serialVersionUID = 2405172041950251807L;
    private ActaQueue actaQueue;
    private Integer correlativo;
    private Integer activo;
    private String audUsuarioCreacion;
    private Date audFechaCreacion;
    private String audUsuarioModificacion;
    private Date audFechaModificacion;
    private String accion;
}
