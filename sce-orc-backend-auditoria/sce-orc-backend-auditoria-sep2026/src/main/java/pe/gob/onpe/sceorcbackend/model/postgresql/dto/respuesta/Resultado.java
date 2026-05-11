package pe.gob.onpe.sceorcbackend.model.postgresql.dto.respuesta;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resultado {
    private boolean success;
    private String mensaje;

}
