package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class VerificationObservationItem {
    private String fileId;
    private String userValue;
    private String systemValue;
    private Boolean nullityRequest;
}
