package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class VerificationDatetimeTotal {
    private String fileId;
    private String fileIdNumber;
    private String textSystemValue;
    private String textUserValue;
    private String numberSystemValue;
    private String numberUserValue;
}
