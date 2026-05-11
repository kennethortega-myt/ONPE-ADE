package pe.gob.onpe.scebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarResolucionResponseDto {
    private boolean todasExitosas;
    private int resolucionesExitosas;
    private int resolucionesFallidas;
    private String mensajeCompleto;
    private String mensajeResumen;
}
