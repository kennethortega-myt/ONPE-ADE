package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AutorizacionNacionRequestDto {
    private String usuario;
    private String cc;
    private String tipoAutorizacion;
    private String tipoDocumento;
    private Long idDocumento;
}
