package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransmisionDto implements Serializable {

	private static final long serialVersionUID = -2317484411979274389L;
	
	private Long idTransmision;
	private String centroComputo;
	private String acronimoProceso;
	private ActaPorTransmitirDto actaTransmitida;
	private Integer estadoTransmitidoNacion;
	private String accion;
	private String subAccion;
	private String fechaTransmision;
	private String fechaRegistro;
	private String usuarioTransmision;

}
