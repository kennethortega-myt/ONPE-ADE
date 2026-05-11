package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class DigitizationRejectMesaRequest {
    private Long actaId;
    private int type;
    private Long fileId;
    private String comments;
}
