package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

@Data
public class CargaDataRequestDTO {

    private Integer idProceso;
    private String nombreEsquemaPrincipal;
    private String nombreEsquemaBdOnpe;
    private String nombreDbLink;
    private String usuario;
}
