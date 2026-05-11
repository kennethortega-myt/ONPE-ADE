package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class VerificationDatetimeSectionDTO {
    private String token;
    private VerificationDatetimeItem start;
    private VerificationDatetimeItem end;
    private VerificationDatetimeTotal total;
}


