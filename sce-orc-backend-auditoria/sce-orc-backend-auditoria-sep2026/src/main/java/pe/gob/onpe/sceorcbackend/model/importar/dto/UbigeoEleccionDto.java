package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbigeoEleccionDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idUbigeo;
    private Long idEleccion;
    private String codigoEleccion;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
