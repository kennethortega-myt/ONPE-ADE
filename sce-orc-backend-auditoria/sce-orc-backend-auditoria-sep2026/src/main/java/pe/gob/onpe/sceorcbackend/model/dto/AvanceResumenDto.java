package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class AvanceResumenDto {

    private AvanceResumenVotoDto validados;
    private AvanceResumenVotoDto blancos;
    private AvanceResumenVotoDto nulos;
    private AvanceResumenVotoDto emitidos;

}
