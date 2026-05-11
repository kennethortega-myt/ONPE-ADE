package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
@Data
public class AmbitoElectoralRequestDto {
    private String esquema;
    private Integer idCentroComputo;
    private String usuario;
    private Integer idEleccion;
}
