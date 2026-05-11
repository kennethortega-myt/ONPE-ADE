package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class LocalVotacionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 803038864560082030L;
    private Long id;
    private String nombre;
    private Integer estado;

}
