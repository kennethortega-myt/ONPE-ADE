package pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification;

import lombok.Data;

@Data
public class VerificationActaDTO {
    private String token;
    private String estadoActa;
    private String solucionTecnologica;
    private VerificationSignSectionDTO signSection;
    private VerificationVoteSectionDTO voteSection;
    private VerificationVotePreferencialSectionDTO votePreferencialSection;
    private VerificationObservationSectionDTO observationSection;
    private VerificationDatetimeSectionDTO dateSectionResponse;
    private String propOrdenVerificacionActas;//vendrá el valor 1 0 2 para ver si el json vino del modelo o de lo normal
}
