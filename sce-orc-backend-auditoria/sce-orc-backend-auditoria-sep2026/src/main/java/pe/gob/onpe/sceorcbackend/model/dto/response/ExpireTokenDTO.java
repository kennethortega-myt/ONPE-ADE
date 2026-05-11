package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Data;

@Data
public class ExpireTokenDTO {
    private boolean isExpired;
    private String timeLeft;
    private String validator;
}
