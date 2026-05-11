package pe.gob.onpe.scebackend.security.dto;

import lombok.Data;

@Data
public class ExpireTokenDTO {
    private boolean isExpired;
    private String timeLeft;
    private String validator;
}
