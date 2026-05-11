package pe.gob.onpe.scebackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionUsuarioRequestDto {

    private String codigoAplicacion;
    private String acronimoProceso;
}
