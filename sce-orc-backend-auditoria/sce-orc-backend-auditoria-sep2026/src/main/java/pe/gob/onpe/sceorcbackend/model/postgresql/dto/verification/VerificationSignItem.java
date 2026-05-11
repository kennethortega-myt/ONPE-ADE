package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class VerificationSignItem {
    private Long fileId;
    private String systemStatus;
    private String userStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngUrl;
}
