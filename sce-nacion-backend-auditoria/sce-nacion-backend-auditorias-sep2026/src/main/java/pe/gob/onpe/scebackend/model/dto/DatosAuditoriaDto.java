package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
//@SuperBuilder
public class DatosAuditoriaDto {
    private String 	audUsuarioCreacion;
    private String  audFechaCreacion;
    private String	audUsuarioModificacion;
    private String	audFechaModificacion;
}
