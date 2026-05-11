package pe.gob.onpe.scebackend.model.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutorizacionReprocesarActaResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -1934867961916561312L;

    private boolean autorizado;
    private boolean solicitudGenerada;
    private String mensaje;
    private String idAutorizacion;
    private Boolean fromCentroComputo;
}
