package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NuevaContraseniaInputDto {
    private String contrasenia;
    private String confirmaContrasenia;
}
