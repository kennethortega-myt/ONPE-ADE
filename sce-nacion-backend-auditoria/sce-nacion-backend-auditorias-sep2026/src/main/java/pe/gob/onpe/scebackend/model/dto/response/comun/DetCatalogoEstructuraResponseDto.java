package pe.gob.onpe.scebackend.model.dto.response.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetCatalogoEstructuraResponseDto {
    private Integer idCodigo;
    private String codigo;
    private String nombre;
    private String idEstado;
}
