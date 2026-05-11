package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AgrupacionPoliticaDto {

    private Long id;
    private Long idCentroComputo;
    private String proceso;
    private String codigo;
    private String descripcion;
    private Long tipoAgrupacionPolitica;
    private Integer estado;
    private String ubigeoMaximo;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
