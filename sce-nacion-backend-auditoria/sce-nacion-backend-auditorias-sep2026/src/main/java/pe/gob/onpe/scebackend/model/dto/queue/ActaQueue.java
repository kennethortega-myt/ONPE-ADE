package pe.gob.onpe.scebackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActaQueue implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long idActa;
    private String estadoDigitalizacion;
    private String estadoActa;
    private String estadoComputo;
    private String estadoActaResolucion;
    private String estadoErrorMaterial;
    private String digitoChequeoEscrutinio;
    private Integer activo;
    private String audUsuarioCreacion;
    private Date audFechaCreacion;
    private String audUsuarioModificacion;
    private Date audFechaModificacion;
    private String accion;
    private List<DetActaQueue> actaQueueList;
}
