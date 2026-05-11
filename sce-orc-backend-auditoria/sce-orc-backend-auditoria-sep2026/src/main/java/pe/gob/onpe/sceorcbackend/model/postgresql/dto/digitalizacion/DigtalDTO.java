package pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion;

import lombok.Data;
import java.io.Serializable;

@Data
public class DigtalDTO implements Serializable {
    private Long idActa;
    private Long idArchivo;
    private Long tipoArchivo;

    private Long idArchivoAis;
    private Long tipoArchivoAis;
    private boolean isCompleto;
    private boolean isRedigitalizado;

}
