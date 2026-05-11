package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationObservationSectionResponse {
    private String token;

    private VerificationObservation count;
    private VerificationObservation install;
    private VerificationObservation vote;
    private Boolean nullityRequest;//solicitud de nulidad
    private Boolean noData;//acta sin datos

}
