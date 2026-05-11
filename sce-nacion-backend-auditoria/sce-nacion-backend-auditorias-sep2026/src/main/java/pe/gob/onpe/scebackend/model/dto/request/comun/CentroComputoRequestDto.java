package pe.gob.onpe.scebackend.model.dto.request.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentroComputoRequestDto {
    private String esquema;
    private Integer idEleccion;
    private String usuario;
    private Integer idAmbitoElectoral;
}
