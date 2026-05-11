package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CambiarContraseniaOutputDto {
    private Integer success;
    private String mensaje;
}
