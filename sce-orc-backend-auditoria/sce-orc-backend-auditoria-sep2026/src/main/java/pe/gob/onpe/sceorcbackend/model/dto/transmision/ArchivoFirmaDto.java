package pe.gob.onpe.sceorcbackend.model.dto.transmision;

import lombok.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.ArchivoTransmisionDto;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ArchivoFirmaDto {

    private Long idActa;
    private Integer tipoArchivo;
    private ArchivoTransmisionDto archivo;
}
