package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

@Data
public class ProcesoElectoralOtherRequestDTO {

    private String esquema;
    private Integer idEleccion;
    private String usuario;

}
