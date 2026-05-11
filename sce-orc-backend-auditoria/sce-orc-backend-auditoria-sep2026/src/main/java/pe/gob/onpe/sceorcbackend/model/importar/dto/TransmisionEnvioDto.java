package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransmisionEnvioDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private String tramaDato;
    private String tramaImagen;
    private Integer estado;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
