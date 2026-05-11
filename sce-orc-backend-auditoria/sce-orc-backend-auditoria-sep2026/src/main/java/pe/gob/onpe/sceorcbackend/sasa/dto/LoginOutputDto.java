package pe.gob.onpe.sceorcbackend.sasa.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOutputDto {

    private List<LoginPerfilesOutputDto> perfiles;
    private LoginUsuarioOutputDto usuario;
}
