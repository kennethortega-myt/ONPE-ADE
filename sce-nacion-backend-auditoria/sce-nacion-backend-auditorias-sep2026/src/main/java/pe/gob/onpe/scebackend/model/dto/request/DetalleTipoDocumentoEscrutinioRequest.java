package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;
import pe.gob.onpe.scebackend.model.dto.response.DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO;

import java.util.List;

@Data
public class DetalleTipoDocumentoEscrutinioRequest {
    List<DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO> detalles;
}
