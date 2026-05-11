package pe.gob.onpe.scebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConexionResponseDto {
    private String message;
    private String credential;
    private boolean success;

}
