package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoEleccionRequest {

    private Integer id;
    private String nombre;
    private Integer activo;

}
