package pe.gob.onpe.scebackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionUsuariosoOutputDto {

    private Integer idAplicacionUsuario;
    private Integer tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String estado;
    private Integer idUsuario;
    private String usuario;
    private Integer personaAsignada;
    private String codigo1;
    private Integer codigo2;
    private Integer idPerfil;
    private String nombrePerfil;
    private String abreviaturaPerfil;
    private String correo;
    private String codCentroComputo;
    private String nombreCentroComputo;
}
