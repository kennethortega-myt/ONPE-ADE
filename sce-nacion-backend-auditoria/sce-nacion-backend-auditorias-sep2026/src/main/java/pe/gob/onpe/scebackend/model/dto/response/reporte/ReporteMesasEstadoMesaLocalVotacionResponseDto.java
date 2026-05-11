package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReporteMesasEstadoMesaLocalVotacionResponseDto {
    private String codigoLocalVotacion;
    private String nombreLocalVotacion;
    private String direccionLocalVotacion;
    private String totalMesasLocalVotacion;
    private String totalElectoresLocalVotacion;
    private List<ReporteMesasEstadoMesaLocalVotacionMesasResponseDto> mesas;
}
