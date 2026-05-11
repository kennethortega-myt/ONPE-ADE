package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroAvanceEstadoActaDto {

    private Integer idProceso;
    private Integer idEleccion;
    private Integer idCentroComputo;
    @Alphanumeric
    private String schema;
    @Alphanumeric
    private String usuario;
    private Integer estado;
    private String estadoDescripcion;
    private String centroComputo;

}
