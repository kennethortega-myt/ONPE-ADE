package pe.gob.onpe.scebackend.model.dto.response;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutorizacionNacionResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -213040931081436217L;
    private boolean autorizado;
    private boolean solicitudGenerada;
    private String mensaje;
    private String idAutorizacion;
    private Boolean fromCentroComputo;
}
