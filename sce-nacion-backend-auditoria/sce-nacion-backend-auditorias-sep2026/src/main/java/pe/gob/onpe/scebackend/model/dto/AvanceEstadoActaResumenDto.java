package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class AvanceEstadoActaResumenDto {

    private Integer porProcesar;
    private Integer procesadas;
    private Integer contabilizadas;

}