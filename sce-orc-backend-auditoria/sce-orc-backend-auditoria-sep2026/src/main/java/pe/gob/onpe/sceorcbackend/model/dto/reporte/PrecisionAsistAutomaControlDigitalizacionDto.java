package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrecisionAsistAutomaControlDigitalizacionDto {
    private String nombreEleccion;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private Integer actasAprobadasAutomatica;
    private Integer actasAprobadasManual;
    private Integer actasNoReconocidas;
    private Integer actasPendientes;
    private Integer actasExtraviadas;
    private Integer totalActas;
    private Integer sumaActasAproAutoManual;
    private Double porcentajeAprobadasAutomatica;
    private Double porcentajeAprobadasManual;
    private Double porcentajeNoReconocidas;
    private Double porcentajePendientes;
    private Double porcentajeExtraviadas;
    private Double sumaPorcentajeAproAutoManual;

    public void sumaActaAproAutoManual(){
        this.setSumaActasAproAutoManual(this.actasAprobadasAutomatica + this.actasAprobadasManual);
        this.setSumaPorcentajeAproAutoManual(this.porcentajeAprobadasAutomatica + this.porcentajeAprobadasManual);
    }
}