package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class VerificationVoteRevocatoriaItem {
    private Integer position;
    private Long fileId;
    private String systemValue;
    private String userValue;
}
