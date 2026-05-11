package pe.gob.onpe.scebackend.model.dto.transmision;

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
public class ActaTransmitidaDto implements Serializable {
	
	private static final long serialVersionUID = 6617609046871223772L;
	
	private Long idTransmision;
	private Long idActa;
	private Integer estadoTransmitidoNacion;
	
}
