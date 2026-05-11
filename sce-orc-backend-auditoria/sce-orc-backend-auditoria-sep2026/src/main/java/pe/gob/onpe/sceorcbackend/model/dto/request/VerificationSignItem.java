package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Data;

@Data
public class VerificationSignItem {

    private String fileId;
    private String systemStatus;
    private String userStatus;
}
