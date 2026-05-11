package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationDatetimeItem {
    private String fileId;
    private String systemValue;
    private String userValue;
    private String nextText;
}
