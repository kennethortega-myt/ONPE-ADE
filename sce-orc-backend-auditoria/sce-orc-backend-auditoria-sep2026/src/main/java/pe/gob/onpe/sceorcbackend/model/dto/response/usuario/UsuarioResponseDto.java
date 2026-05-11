package pe.gob.onpe.sceorcbackend.model.dto.response.usuario;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDto {
    private Long id;
    private String usuario;
    private Integer tipoDocumento;
    private String documento;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombres;
    private String correo;
    private String perfil;
    private String nombrePerfil;
    private String acronimoProceso;
    private String centroComputo;
    private String nombreCentroComputo;
    private Integer sesionActiva;
    private Integer actasAsignadas;
    private Integer actasAtendidas;
    private Integer activo;
    private String usuarioCreacion;
    private Date fechaCreacion;
    private String usuarioModificacion;
    private Date fechaModificacion;
    private Boolean desincronizadoSasa;
    private Integer personaAsignada;
}
