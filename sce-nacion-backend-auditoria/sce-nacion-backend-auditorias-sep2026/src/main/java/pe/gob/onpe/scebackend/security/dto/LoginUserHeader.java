package pe.gob.onpe.scebackend.security.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginUserHeader {

    private String usuario;
    private List<String> permisos;
    private String perfil;
}
