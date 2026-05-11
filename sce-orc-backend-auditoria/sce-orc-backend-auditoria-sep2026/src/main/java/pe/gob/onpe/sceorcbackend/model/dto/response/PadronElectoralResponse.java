package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PadronElectoralResponse {
    private Long id;
    private String documentoIdentidad;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String codigoMesa;
    private Integer mesaId;
    private Integer orden;
}
