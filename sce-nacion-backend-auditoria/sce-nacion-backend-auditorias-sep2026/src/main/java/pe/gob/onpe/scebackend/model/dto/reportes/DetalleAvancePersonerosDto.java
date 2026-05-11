package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleAvancePersonerosDto {
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
