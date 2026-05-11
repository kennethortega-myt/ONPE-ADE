package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class VerificationObservation {
    private Long fileId;
    private String systemValue;
    private String userValue;
    private boolean nullityRequest;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngUrl;
}
