package pe.gob.onpe.scebackend.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.model.dto.ConsultaResumenDto;
import pe.gob.onpe.scebackend.model.dto.ReporteResultadosDto;

@Getter
@Setter
public class ConsultaCifraRepartidoraResponseDto {
    ConsultaResumenDto consultaResumen;
    String porcentajeAvance;
    List<ReporteResultadosDto> listReporteResultados;

}
