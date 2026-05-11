package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;
import java.util.List;

@Data
public class VerificationVoteSectionDTO {
    private String token;
    private Integer cantidadVotosPreferenciales;
    private boolean autorizado;
    private List<VerificationVoteItem> items;
    private Long status;
}
