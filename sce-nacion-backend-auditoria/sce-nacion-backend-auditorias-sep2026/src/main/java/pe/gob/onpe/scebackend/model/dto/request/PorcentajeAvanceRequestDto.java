package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PorcentajeAvanceRequestDto {
    private String esquema;
    private String tipoEleccion;
    private String distritoElectoral;
}
