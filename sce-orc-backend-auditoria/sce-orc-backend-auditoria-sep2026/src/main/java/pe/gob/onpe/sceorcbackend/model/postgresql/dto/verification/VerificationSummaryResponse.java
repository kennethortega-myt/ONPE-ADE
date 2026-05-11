package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class VerificationSummaryResponse {
    private VerificationSummarySectionCount signs;
    private VerificationSummarySectionCount votes;
    private VerificationSummarySectionCount observations;
    private VerificationSummarySectionCount datetime;
}
