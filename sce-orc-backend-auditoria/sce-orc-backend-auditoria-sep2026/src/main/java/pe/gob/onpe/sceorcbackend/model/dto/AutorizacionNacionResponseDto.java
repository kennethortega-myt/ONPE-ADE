package pe.gob.onpe.sceorcbackend.model.dto;

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
    private Boolean autorizado;
    private Boolean solicitudGenerada;
    private String mensaje;
    private String idAutorizacion;
    private Boolean fromCentroComputo;
}
