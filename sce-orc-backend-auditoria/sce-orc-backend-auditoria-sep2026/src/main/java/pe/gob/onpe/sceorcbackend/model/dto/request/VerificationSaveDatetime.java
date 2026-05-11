package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.dto.response.VerificationDatetimeItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.VerificationDatetimeTotal;

@Data
public class VerificationSaveDatetime {


    private String token;
    private Long status;
    private VerificationDatetimeItem start;
    private VerificationDatetimeItem end;
    private VerificationDatetimeTotal total;
}
