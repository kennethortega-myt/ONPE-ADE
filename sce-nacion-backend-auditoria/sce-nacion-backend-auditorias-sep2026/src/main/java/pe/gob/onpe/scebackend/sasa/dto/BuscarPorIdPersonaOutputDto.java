package pe.gob.onpe.scebackend.sasa.dto;

import lombok.Data;

@Data
public class BuscarPorIdPersonaOutputDto {
    private Integer id;
    private String nombres;
    private String numeroDocumento;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Integer tipoDocumento;
    private String correo;
    private Integer estado;
}
