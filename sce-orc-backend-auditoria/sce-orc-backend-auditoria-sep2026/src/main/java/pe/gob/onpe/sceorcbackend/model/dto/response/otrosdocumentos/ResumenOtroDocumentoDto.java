package pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ResumenOtroDocumentoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5736956486L;
    private long numeroTotalDocumentos;
    private long mumeroDocumentosAsociados;
    private long numeroDocumentosPendientesDeAsociar;
    private long numeroDocumentosAnulados;
    private List<OtroDocumentoDto> otroDocumentoDtoList;
}
