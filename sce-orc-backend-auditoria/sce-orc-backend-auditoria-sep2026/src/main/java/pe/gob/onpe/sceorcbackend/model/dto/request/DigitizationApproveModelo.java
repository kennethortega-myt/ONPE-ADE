package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class DigitizationApproveModelo {
    private Long actaId;
    private Long fileId1;
    private Long fileId2;
    private String estado;
    private String usuario;
    private String codigoCc;
    private String abrevProceso;

}
