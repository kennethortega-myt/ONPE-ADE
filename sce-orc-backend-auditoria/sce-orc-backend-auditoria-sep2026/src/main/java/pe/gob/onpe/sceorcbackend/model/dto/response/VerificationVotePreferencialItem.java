package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationVotePreferencialItem {
    private Integer position;
    private String fileId;
    private String systemValue;
    private String userValue;


}
