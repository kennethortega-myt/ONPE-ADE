package pe.gob.onpe.sceorcbackend.model.dto.transmision;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ActaTransmitidaDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1037597398593470812L;

    private Long idTransmision;
    private Long idActa;
    private Integer estadoTransmitidoNacion;


}
