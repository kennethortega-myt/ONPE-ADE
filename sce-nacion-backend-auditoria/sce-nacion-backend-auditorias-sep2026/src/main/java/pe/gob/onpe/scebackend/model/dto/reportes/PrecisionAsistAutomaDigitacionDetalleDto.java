package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
public class PrecisionAsistAutomaDigitacionDetalleDto {
    private Integer idEeleccion;
    private Integer centroComputoId;
    private String centroComputoCodigo;
    private String centroComputoNombre;
    private String acta;
    private Integer coincideVotos;
    private Integer noCoincideVotos;
    private Integer totalVotos;
    private Integer coincidePreferencial;
    private Integer noCoincidePreferencial;
    private Integer totalPreferencial;
    private Integer coincideCvas;
    private Double presicionActa;

    public void calculaPrecision(boolean presidencial) {
        double precision;
        if(presidencial){
            precision = ((double)(this.coincideVotos + this.coincideCvas + 1) / (this.totalVotos + 1 + 1)) * 100;
        } else {
            precision = ((double)(this.coincideVotos + this.coincidePreferencial + this.coincideCvas + 1) / (this.totalVotos + this.totalPreferencial + 1 + 1)) * 100;
        }
        this.presicionActa = BigDecimal.valueOf(precision) .setScale(3, RoundingMode.HALF_UP) .doubleValue();
    }
}
