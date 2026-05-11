package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenInfo {
    private Integer idPerfil;
    private String idSession;
    private Integer userId;
    private String autority;
    private String subject;
    String codigoCentroComputo;
    String nombreUsuario;
    String nombreCentroComputo;
    String codigoAmbito;//codigo ambito electoral
    String nombreAmbito;//codigo nombre electoral
    String abrevProceso;
    String validator;
    List<String> scopes;
    String descOdpe;
    Long idCentroComputo;
    Long idProceso;
    String tokenPlano;
}
