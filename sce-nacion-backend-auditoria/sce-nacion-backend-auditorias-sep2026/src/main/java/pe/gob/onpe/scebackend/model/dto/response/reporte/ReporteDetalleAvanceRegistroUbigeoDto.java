package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteDetalleAvanceRegistroUbigeoDto {
    private String codigoAmbitoElectoral;
    private String codigoCentroComputo;
    private String codigoUbigeo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String procesada;
    private String mesa;
    private Integer totalMesasProcesadas;
    private Integer totalMesas;
    private Integer totalMesaSinProcesar;
    private Double porcentajeAvance;
}
