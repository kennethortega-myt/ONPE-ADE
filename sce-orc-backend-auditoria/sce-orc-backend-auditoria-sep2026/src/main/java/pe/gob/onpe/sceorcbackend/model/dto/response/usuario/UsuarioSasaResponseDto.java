package pe.gob.onpe.sceorcbackend.model.dto.response.usuario;

import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdPersonaOutputDto;

@Data
@Builder
public class UsuarioSasaResponseDto {
    private Integer id;
    private String usuario;
    private Integer estado;
    private Integer bloqueado;
    private String fechaBloqueo;
    private BuscarPorIdPersonaOutputDto persona;
}
