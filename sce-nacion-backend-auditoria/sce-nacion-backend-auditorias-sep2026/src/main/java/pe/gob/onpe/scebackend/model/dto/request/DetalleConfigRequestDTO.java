package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DetalleConfigRequestDTO {

    List<DetalleConfiguracionDocumentoElectoralRequestDTO> detalles;
    DocumentoElectoralRequestDto documento;


}
