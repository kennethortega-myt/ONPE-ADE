package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetCatalogoEstructuraDto {

    private Long id;
    private Long idCatalogo;
    private String columna;
    private String nombre;
    private Integer codigoI;
    private String codigoS;
    private Long orden;
    private String tipo;
    private String informacionAdicional;
    private Integer obligatorio;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
