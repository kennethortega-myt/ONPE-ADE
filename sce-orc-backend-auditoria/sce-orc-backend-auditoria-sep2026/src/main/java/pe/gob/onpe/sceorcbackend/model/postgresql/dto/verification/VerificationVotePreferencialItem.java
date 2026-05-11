package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class VerificationVotePreferencialItem {
    private Integer position;
    private Long fileId;
    private Integer estado;
    private String systemValue;
    private String userValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngUrl;
}
