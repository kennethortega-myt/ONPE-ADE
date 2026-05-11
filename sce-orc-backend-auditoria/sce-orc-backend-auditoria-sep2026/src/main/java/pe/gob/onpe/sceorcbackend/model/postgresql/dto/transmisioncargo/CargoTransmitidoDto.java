package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CargoTransmitidoDto implements Serializable {
	
	private static final long serialVersionUID = 2178680147122671194L;
	private Long idTransmision;
    private Long idActa;
    private Integer estadoTransmitidoNacion;

}
