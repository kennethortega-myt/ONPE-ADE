package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteProcedePagoRequestDto extends BaseRequestDto {
    private Integer tipoReporte;
    private Integer tipoReporteActorElectoral;
}
