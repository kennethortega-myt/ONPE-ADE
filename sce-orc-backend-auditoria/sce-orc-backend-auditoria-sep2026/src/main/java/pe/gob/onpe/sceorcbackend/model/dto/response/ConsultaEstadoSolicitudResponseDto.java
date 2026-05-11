package pe.gob.onpe.sceorcbackend.model.dto.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaEstadoSolicitudResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean existeSolicitud;
    private String mensaje;
}
