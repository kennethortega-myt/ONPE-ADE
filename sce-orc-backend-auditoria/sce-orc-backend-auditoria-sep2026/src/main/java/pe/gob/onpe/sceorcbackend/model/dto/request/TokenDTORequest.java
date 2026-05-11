package pe.gob.onpe.sceorcbackend.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class TokenDTORequest {

    @JsonProperty("user_service")
    @Expose
    private String userService;

    private String password;
}
