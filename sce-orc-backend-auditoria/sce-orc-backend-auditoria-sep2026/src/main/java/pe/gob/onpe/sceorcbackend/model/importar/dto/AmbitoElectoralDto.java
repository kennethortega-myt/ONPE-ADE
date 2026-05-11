package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbitoElectoralDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idPadre;
    private String nombre;
    private String codigo;
    private Integer tipoAmbitoElectoral;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
