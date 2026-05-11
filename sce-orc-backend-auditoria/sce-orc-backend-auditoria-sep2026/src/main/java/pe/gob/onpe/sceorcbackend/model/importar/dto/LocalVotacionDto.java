package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalVotacionDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idUbigeo;
    private String codigo;
    private String nombre;
    private String direccion;
    private String referencia;
    private String centroPoblado;
    private Integer cantidadMesas;
    private Integer cantidadElectores;
    private Integer estado;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;


}
