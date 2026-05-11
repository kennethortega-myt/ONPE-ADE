package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AdminDocumentoElectoralDto {

    private String nombreDoc;
    private String descCorta;
    private List<PropiedadDocumentoElectoralDto> documentos;
    private Integer visible;

}
