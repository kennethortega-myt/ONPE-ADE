package pe.gob.onpe.scebackend.model.dto.request;

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
