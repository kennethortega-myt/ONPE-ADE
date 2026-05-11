package pe.gob.onpe.scebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccesoPcResponse {
    private Long id;

    private String fechaAccesoPc;

    private String usuarioAccesoPc;

    private String ipAccesoPc;

    private String nombre;

    private String apellidoPaterno;

    private String apellidoMaterno;
}
