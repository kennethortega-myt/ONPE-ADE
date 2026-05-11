package pe.gob.onpe.sceorcbackend.model.dto.response.usuario;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDetailResponseDto {
    private UsuarioResponseDto usuario;
    private UsuarioSasaResponseDto usuarioSasa;
}
