package pe.gob.onpe.sceorcbackend.model.dto.request;

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
public class AutorizacionCCRequestDto {
    private String usuario;
    private String tipoAutorizacion;
    private String tipoDocumento;
    private Long idDocumento;
}
