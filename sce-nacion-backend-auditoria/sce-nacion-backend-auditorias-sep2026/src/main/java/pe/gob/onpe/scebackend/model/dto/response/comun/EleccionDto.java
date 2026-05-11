package pe.gob.onpe.scebackend.model.dto.response.comun;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EleccionDto {
    private Integer id;
    private String codigo;
    private String nombre;
    private String nombreVista;
    private Integer preferencial;

}
