package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PadronElectoralBusquedaDto {
    private String dni;
    private Integer numeroMesa;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
}
