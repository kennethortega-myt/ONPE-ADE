package pe.gob.onpe.scebackend.security.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenInfo {
    private Integer idPerfil;
    private Integer userId;
    private String autority;
    private String subject;
    private String idSession;
    String codigoCentroComputo;
    String nombreUsuario;
    String nombreCentroComputo;
    String codigoAmbito;//codigo ambito electoral
    String nombreAmbito;//codigo nombre electoral
    String abrevProceso;
    String validator;
    List<String> scopes;
    String tokenPlano;
}
