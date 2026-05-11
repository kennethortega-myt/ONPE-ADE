package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetActaDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idCabActa;
    private Long idAgrupacionPolitica;
    private Long posicion;
    private Long votos;
    private String estadoErrorMaterial;
    private String ilegible;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
