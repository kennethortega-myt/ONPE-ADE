package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasEstadoDigitacionDto extends ReporteMesasBaseDto {

	private Long totalMesasMesa;
    private Long totalElectoresMesa;
}
