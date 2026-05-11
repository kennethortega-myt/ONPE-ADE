package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.Data;

@Data
public class ApprovedActa {
    private Long actaId;
    private Long fileId1;
    private Long fileId2;
    private String codUsuario;
    private String codCentroComputo;
}
