package pe.gob.onpe.sceorcbackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbiEleccionAgrupolRequestDto {
    private String esquema;
    private Integer idEleccion;
    private String ubigeo;
    private String mesa;
}
