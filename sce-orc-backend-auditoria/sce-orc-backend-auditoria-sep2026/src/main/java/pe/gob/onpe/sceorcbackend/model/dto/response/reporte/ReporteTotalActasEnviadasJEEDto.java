package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteTotalActasEnviadasJEEDto{
    private Integer idEleccion;
    private Short totalActasPorLocal;
    private Short totalActasPorCentroComputo;
    private Short acta01;
    private Short acta02;
    private Short acta03;
    private Short acta04;
    private Short acta05;
    private Short acta06;
    private Short acta07;
    private Short acta08;
    private Short acta09;
    private Short acta10;
    private Short acta11;
    private Short totalActasEnvioJEE;
    private double porcentajeActasEnvioJEE;
    private String codigoAmbitoElectoral;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
}