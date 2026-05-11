package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class VerificationSaveObservationRequest {
    private String token;
    private VerificationObservationItem count;
    private VerificationObservationItem install;
    private VerificationObservationItem vote;
    private Boolean nullityRequest;//solicitud de nulidad
    private Boolean noData;//acta sin datos
    private Long status;
}
