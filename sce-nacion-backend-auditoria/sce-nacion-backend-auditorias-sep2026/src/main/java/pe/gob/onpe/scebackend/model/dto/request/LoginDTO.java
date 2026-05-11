package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

@Data
public class LoginDTO {
    
    private String usuario;
    private String clave;
    
    private String token;
}
