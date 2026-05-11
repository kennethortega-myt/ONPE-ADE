package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class VerificationObservationSectionDTO {
    private String token;

    private VerificationObservation count;
    private VerificationObservation install;
    private VerificationObservation vote;

    private Boolean nullityRequest;//solicitud de nulidad
    private Boolean noData;//acta sin datos
    //private Boolean incomplete;//acta incompleta
    private Long status;
}
