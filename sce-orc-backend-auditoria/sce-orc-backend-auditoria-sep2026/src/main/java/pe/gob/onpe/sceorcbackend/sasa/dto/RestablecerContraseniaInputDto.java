package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestablecerContraseniaInputDto {
    private String codigoAplicacion;
    private Integer id;
}
