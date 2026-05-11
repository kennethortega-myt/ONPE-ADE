package pe.gob.onpe.scebackend.sasa.dto;

import lombok.Data;

@Data
public class BuscarPorIdUsuarioOutputDto {
    private Integer id;
    private Integer idUnidadOrganica;
    private String usuario;
    private BuscarPorIdPersonaOutputDto persona;
    private String claveTemporal;
    private Integer claveNueva;
    private Integer tipoAutenticacion;
    private Integer sesionUnica;
    private Integer sede;
    private Integer estado;
    private Integer bloqueado;
    private String fechaBloqueo;
}
