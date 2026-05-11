package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasEstadoActaDto extends ReporteMesasBaseDto {

    private Long electoresHabiles;
    private Long idActa;
    private Integer idMesa;



}
