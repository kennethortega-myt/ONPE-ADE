package pe.gob.onpe.sceorcbackend.model.dto;


import lombok.Data;

@Data
public class AvanceEstadoActaResumenDto {

    private Long porProcesar;
    private Long procesadas;
    private Long contabilizadas;

}
