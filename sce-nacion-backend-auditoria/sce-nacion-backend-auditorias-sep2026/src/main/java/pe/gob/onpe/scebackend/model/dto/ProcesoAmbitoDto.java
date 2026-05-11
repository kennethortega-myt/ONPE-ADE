package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class ProcesoAmbitoDto {
    private Long idProceso;
    private String nombreProceso;
    private Integer idTipoAmbito;
    private String nombreTipoAmbito;
}
