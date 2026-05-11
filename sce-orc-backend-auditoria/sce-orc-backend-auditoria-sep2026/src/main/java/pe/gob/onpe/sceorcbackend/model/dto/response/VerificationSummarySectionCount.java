package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationSummarySectionCount {
    private Integer pending = 0;
    private Integer approved = 0;
    private Integer rejected = 0;
}
