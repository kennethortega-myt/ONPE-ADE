package pe.gob.onpe.scebackend.model.dto.request.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionRequestDto {
    private String esquema;
    private Integer idProcesoElectoral;
    private String usuario;
}
