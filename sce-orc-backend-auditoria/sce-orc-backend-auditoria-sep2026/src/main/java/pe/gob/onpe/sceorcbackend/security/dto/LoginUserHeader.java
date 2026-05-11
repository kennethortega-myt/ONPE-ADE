package pe.gob.onpe.sceorcbackend.security.dto;

import java.util.List;

import lombok.Data;

@Data
public class LoginUserHeader {

  private String usuario;
  private List<String> permisos;
  private String perfil;
}