package pe.gob.onpe.scebackend.model.dto.request.reporte;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteActaRequestDto {
    private String esquema;
    private String usuario;
    private Integer idEleccion;
    private Integer idCentroComputo;
    private Integer idAmbitoElectoral;
    private String mesa;
    private String ubigeo;
}
