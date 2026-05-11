package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idProcesoElectoral;
    private String codigo;
    private Integer principal;
    private Integer preferencial;
    private String nombre;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
    private String nombreVista;
}
