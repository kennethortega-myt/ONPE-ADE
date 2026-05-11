package pe.gob.onpe.scebackend.model.dto.response.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteMesasObservacionesDto {
    private String codigoAmbitoElectoral;
    private String codigoCentroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
    private String codigoUbigeo;
    private String mesa;
    private String nombreDocumento;
    private String pagina;
    private String descripcionObservacion;
}
