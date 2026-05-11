package pe.gob.onpe.scebackend.model.dto.request.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmbitoElectoralRequestDto {
    private String esquema;
    private Integer idCentroComputo;
    private String usuario;
    private Integer idEleccion;
}
