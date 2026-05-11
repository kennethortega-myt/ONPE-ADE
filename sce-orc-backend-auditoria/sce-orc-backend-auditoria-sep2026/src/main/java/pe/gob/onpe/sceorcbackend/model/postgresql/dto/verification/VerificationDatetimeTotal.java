package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class VerificationDatetimeTotal {
    private Long fileId;
    private Long fileIdNumber;
    private Long fileIdNumberEscrutinio;
    private String textSystemValue;
    private String textUserValue;
    private String numberSystemValue;
    private String numberUserValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngTextoUrl;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngNumberUrl;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePngNumberEscrutinioUrl;

}
