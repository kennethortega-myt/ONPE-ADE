package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoElectoralDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private String nombre;
    private String acronimo;
    private String fechaConvocatoria;
    private Long tipoAmbitoElectoral;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
