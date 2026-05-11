package pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DetOtroDocumentoDto implements Serializable {
    private Long idDetOtroDocumento;
    private Integer idMesa;
    private String numeroMesa;
    private String abrevTipoDocumento;
    private String abrevTipoPerdida;
    private String descTipoPerdida;
    private String descTipoDocumento;
    private Integer activo;
}
