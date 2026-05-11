package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;

@Data
public class TokenDTOResponse {

    private boolean success;

    private String message;

    private String token;
}
