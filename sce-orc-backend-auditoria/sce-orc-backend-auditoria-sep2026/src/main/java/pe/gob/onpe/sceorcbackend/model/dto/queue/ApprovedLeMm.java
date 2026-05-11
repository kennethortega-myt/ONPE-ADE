package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.Data;

@Data
public class ApprovedLeMm {
    private Long mesaId;
    private String abrevDocumento;
    private String codUsuario;
    private String codCentroComputo;
}
