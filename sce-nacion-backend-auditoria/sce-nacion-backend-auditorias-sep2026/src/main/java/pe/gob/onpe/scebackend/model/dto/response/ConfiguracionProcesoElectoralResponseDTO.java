package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ConfiguracionProcesoElectoralResponseDTO {
    private Integer id;
    private String nombre;
    private String acronimo;
    private byte[] logo;
    private String nombreDbLink;
    private String nombreEsquemaPrincipal;
    private String nombreEsquemaBdOnpe;
    private Date fechaConvocatoria;
    private Integer activo;
    private Integer vigente;
    private String usuario;
    private List<DatosGeneralesResponseDto> tipoEleccion;
    private Integer etapa;
    private Boolean isEditar;
    private boolean existePrincipal;
}
