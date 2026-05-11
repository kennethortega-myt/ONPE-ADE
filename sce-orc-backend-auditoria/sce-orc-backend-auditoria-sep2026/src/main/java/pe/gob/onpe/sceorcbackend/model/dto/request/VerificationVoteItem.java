package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class VerificationVoteItem {
    private Integer position;
    private String positionToken;
    private String fileId;
    private String systemValue;
    private String userValue;
    private String filePng;
    private VerificationVotePreferencialItem[] votoPreferencial;

}
