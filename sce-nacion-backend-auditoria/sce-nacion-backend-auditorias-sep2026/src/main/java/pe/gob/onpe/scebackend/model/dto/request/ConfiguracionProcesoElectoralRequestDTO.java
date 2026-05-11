package pe.gob.onpe.scebackend.model.dto.request;

import lombok.Data;

import java.util.Date;


@Data
public class ConfiguracionProcesoElectoralRequestDTO {

    private Integer id;
    private String nombre;
    private String acronimo;
    private byte[] logo;
    private String nombreEsquemaPrincipal;
    private String nombreEsquemaBdOnpe;
    private String nombreDbLink;
    private Date fechaConvocatoria;
    private Integer activo;
    private String usuario;
    private Integer vigente;
}
