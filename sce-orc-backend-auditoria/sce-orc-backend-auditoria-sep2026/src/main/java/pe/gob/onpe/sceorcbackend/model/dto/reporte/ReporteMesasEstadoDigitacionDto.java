package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ReporteMesasEstadoDigitacionDto extends ReporteMesasBaseDto {

	private Long totalMesasMesa;
    private Long totalElectoresMesa;
    
}
