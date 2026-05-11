package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class VerificationVotePreferenciaRowItem {
    private Integer position;
    private String tokenPosition;
    private List<VerificationVotePreferencialItem> items;
}
