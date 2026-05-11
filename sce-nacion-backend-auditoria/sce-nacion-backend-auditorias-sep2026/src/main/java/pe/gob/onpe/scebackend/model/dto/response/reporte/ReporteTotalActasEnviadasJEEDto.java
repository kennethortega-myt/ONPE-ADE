package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteTotalActasEnviadasJEEDto extends ReporteBaseDto {
    private Integer idEleccion;
    private Long totalActasPorLocal;
    private Long totalActasPorCentroComputo;
    private Long acta01;
    private Long acta02;
    private Long acta03;
    private Long acta04;
    private Long acta05;
    private Long acta06;
    private Long acta07;
    private Long acta08;
    private Long acta09;
    private Long acta10;
    private Long acta11;
    private Long totalActasEnvioJEE;
    private double porcentajeActasEnvioJEE;
}
