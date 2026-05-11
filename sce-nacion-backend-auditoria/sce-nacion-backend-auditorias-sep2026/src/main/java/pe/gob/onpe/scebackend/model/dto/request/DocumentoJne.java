package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoJne {
    private String tipoDocumento;
    private String codigoEnvio;
    private String codigoTipoError;
    private String descripcionError;
    private String nroDocumento;
    private String fechaDocumento;
    private Integer idProcesoElectoral;
    private Integer idJuradoElectoral;
    private String rutaDocumento;
    private String observaciones;
}
