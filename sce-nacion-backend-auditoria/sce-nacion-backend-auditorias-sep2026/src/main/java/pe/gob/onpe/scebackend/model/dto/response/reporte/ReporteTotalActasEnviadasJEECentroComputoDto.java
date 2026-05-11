package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteTotalActasEnviadasJEECentroComputoDto extends ReporteBaseDto {
    private Integer idEleccion;
    private Short totalActasPorLocal;
    private Short voto01;
    private Short voto02;
    private Short voto03;
    private Short voto04;
    private Short voto05;
    private Short voto06;
    private Short voto07;
    private Short voto08;
    private Short voto09;
    private Short voto10;
    private Short voto11;
    private Short totalActasEnvioJEE;
    private Short porcentajeActasEnvioJEE;
}
