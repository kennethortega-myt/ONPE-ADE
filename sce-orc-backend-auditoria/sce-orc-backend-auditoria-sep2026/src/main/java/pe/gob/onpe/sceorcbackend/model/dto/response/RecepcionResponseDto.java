package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecepcionResponseDto {
    private boolean exitoso;
    private String mensaje;
}
