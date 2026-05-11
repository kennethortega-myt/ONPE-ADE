package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidarUsuarioReaperturaResponse {
    private String mensaje;
    private boolean mismoUsuario;
}
