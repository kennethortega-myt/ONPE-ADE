package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;
import java.util.List;

@Data
public class VerificationVotePreferencialSectionDTO {
    private String token;
    private Integer cantidadEscanios;
    private List<VerificationVotePreferenciaRowItem> items;
}
