package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetActaPreferencialDto {

    private Long id;
    private Long detActaId;
    private Integer distritoElectoralId;
    private Integer posicion;
    private Integer lista;
    private Long votos;
    private Long votosAutomatico;
    private Long votosManual1;
    private Long votosManual2;
    private String estadoErrorMaterial;
    private String ilegible;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
