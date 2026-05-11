package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteOmisosDto {
    private String codigoUbigeo;
    private String codigoCentroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
    private Integer totalMesas;
    private Integer totalElectores;
    private Integer totalMesasRegistradas;
    private Integer totalOmisos;
}
