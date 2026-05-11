package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentroComputoDto {

    private Long id;
    private String proceso;
    private Long idPadre;
    private String codigo;
    private String nombre;
    private String  protocolBackendCc;
    private String ipBackendCc;
    private String apiTokenBackedCc;
    private Integer puertoBackedCc;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
