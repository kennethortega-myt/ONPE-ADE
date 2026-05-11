package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class FiltroUbigeoLocalVotacionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4422892170975749416L;
    private Long idUbigeo;
}
