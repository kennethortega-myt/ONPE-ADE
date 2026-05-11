package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationDatetimeSectionResponse {
    private String token;

    private VerificationDatetimeItem start;
    private VerificationDatetimeItem end;
    private VerificationDatetimeTotal total;
}


