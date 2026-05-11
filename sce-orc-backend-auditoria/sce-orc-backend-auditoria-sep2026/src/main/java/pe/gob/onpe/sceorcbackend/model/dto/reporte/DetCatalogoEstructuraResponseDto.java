package pe.gob.onpe.sceorcbackend.model.dto.reporte;

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
