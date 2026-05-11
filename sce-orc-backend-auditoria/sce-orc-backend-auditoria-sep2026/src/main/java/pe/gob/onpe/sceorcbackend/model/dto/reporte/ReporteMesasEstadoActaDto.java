package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasEstadoActaDto extends ReporteMesasBaseDto {

    private Long electoresHabiles;
    private Long idActa;
    private Integer idMesa;



}
