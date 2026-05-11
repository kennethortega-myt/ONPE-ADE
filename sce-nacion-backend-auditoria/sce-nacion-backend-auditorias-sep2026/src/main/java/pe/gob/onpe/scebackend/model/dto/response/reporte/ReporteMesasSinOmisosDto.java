package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasSinOmisosDto {
    private String codigoUbigeo;
    private String codigoODPE;
    private String codigoCentroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String numeroMesa;
    private Integer totalMesas;

}
