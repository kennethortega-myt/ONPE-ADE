package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.request.DetalleConfiguracionDocumentoElectoralBaseDTO;

@Data
public class DetalleConfiguracionDocumentoElectoralResponseDTO extends DetalleConfiguracionDocumentoElectoralBaseDTO {

    private Integer id;

    DetalleTipoEleccionDocumentoElectoralResponseDto detalleTipoEleccionDocumentoElectoral;

    DatosGeneralesResponseDto seccion;

    private Integer idColor;

    private String colorHex;
}
