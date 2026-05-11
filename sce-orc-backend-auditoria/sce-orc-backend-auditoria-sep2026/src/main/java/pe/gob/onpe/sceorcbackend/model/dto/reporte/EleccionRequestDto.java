package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionRequestDto {
    private String esquema;
    private Integer idProcesoElectoral;
    private String usuario;
}
