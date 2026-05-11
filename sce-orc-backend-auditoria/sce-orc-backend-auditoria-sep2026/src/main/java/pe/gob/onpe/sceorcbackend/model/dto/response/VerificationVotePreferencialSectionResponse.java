package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class VerificationVotePreferencialSectionResponse {
    private String token;
    private Integer cantidadEscanios;
    private List<VerificationVotePreferenciaRowItem> items;
}
