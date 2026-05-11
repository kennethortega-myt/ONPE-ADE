package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenTotalPorCCReporteFiltroDto {

    private String centroComputo;
    private String odpe;
    private String estado;
    private String tipoEleccion;
}
