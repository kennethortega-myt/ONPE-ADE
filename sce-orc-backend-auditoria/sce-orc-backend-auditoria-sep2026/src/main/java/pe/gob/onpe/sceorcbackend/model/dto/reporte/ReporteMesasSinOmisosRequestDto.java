package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReporteMesasSinOmisosRequestDto extends ReporteRequestBaseDto {

    @NotNull(message = "Tipo de Reporte es requerido")
    private Integer tipoReporte;

}
