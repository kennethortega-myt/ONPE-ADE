package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbigeoDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private Long idPadre;
    private Long idAmbitoElectoral;
    private Integer idDistritoElectoral;
    private String departamento;
    private String provincia;
    private String distrito;
    private String codigo; // ubigeo
    private String nombre;
    private Integer tipoAmbitoGeografico;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
}
