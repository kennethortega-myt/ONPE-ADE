package pe.gob.onpe.scebackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AplicacionUsuariosResponseDto {

    private List<AplicacionUsuariosoOutputDto>lista;
    private String mensaje;
    private Integer resultado;
}
