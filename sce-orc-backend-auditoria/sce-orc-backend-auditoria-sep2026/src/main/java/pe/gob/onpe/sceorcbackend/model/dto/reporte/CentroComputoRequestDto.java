package pe.gob.onpe.sceorcbackend.model.dto.reporte;

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
