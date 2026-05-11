package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.scebackend.utils.anotation.Alphanumeric;

@Getter
@Setter
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

}