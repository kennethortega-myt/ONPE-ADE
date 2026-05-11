package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;


@Data
public class FiltroPuestaCeroDTO {
    private String esquema;
    private String usuario;
    private String acronimo;
    private Integer procesoId;
    private String nombre;
    private Long idPuestaCeroStae;
    private Long idPuestaCeroVd;
}
