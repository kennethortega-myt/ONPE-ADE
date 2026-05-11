package pe.gob.onpe.scebatchpr.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ArchivoTransmitidoDto implements Serializable {
	
	private static final long serialVersionUID = -7257633117434142586L;
	private String guid;
	private boolean transmitido;

}
