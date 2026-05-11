package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchivoDto {

    private Long idCentroComputo;
    private String proceso;
    private Long id;
    private String guid;
    private String nombre;
    private String nombreOriginal;
    private String formato;
    private String peso;
    private String ruta;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
