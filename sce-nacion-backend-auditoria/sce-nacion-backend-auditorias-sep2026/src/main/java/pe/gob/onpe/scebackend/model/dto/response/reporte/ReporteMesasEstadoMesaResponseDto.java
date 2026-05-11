package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReporteMesasEstadoMesaResponseDto {
    private String nombreNivelUbigeoUno;
    private String nombreNivelUbigeoDos;
    private String nombreNivelUbigeoTres;
    private String totalMesasNivelUbigeoUno;
    private String totalMesasNivelUbigeoDos;
    private String totalMesasNivelUbigeoTres;
    private String totalElectoresNivelUbigeoUno;
    private String totalElectoresNivelUbigeoDos;
    private String totalElectoresNivelUbigeoTres;
    private String codigoLocalVotacion;
    private String nombreLocalVotacion;
    private String direccionLocalVotacion;
    private List<ReporteMesasEstadoMesaLocalVotacionResponseDto> localesVotacion;
}
