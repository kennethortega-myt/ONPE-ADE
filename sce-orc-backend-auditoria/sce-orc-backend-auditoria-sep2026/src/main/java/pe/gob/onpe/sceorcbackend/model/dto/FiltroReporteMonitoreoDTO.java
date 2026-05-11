package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class FiltroReporteMonitoreoDTO {
    private Long idUbigeo;
    private Long idLocalVotacion;
    private String mesa;
    private String idEleccion;
}
