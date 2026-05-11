package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrecisionAsistAutomaDigitacionResumenDto {
    private String nombreEleccion;
    private String codigoCentroComputo;
    private String nombreCentroComputo;
    private Integer actasConVotoAutomatico;
    private Integer actasSinVotoAutomatico;
    private Integer actasUnaDigitacion;
    private Integer actasDosDigitacion;
    private Integer actasPendientes;
    private Integer actasExtraviadas;
    private Integer actasCorregidas;
    private Integer totalActas;
}
