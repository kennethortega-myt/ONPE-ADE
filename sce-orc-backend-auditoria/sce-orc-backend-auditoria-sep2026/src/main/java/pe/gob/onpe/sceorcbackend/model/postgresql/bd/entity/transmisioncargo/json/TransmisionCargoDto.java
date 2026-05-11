package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransmisionCargoDto implements Serializable {

	private static final long serialVersionUID = 3411791651020188532L;
	
	private Long idTransmision;
	private String centroComputo;
	private String acronimoProceso;
	private CargoPorTransmitirDto cargo;
	private Integer estadoTransmitidoNacion;
	private String accion;
	private String fechaTransmision;
	private String fechaRegistro;
	private String usuarioTransmision;
	

}
