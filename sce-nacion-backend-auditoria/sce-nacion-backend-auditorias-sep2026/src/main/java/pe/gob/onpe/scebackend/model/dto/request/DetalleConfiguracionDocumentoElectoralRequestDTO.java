package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

@Data
public class DetalleConfiguracionDocumentoElectoralRequestDTO extends DetalleConfiguracionDocumentoElectoralBaseDTO {

    private Integer id;
    
    DetalleTipoEleccionDocumentoElectoralRequestDto detalleTipoEleccionDocumentoElectoral;

    DatosGeneralesRequestDto seccion;

}
