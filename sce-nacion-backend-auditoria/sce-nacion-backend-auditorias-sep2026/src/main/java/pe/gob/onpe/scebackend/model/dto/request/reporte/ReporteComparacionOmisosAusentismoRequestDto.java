package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReporteComparacionOmisosAusentismoRequestDto extends ReporteProcedePagoRequestDto {
    private Integer idAmbito;
}
