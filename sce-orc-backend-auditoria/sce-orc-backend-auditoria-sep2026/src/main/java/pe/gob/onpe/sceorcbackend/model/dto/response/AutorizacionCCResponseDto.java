package pe.gob.onpe.sceorcbackend.model.dto.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutorizacionCCResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -213040931081436217L;
    private boolean autorizado;
    private boolean solicitudGenerada;
    private String mensaje;
    private String idAutorizacion;
}
