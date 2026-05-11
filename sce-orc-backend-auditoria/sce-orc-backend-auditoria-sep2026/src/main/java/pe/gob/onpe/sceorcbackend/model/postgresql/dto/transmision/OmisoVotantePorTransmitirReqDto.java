package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

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
public class OmisoVotantePorTransmitirReqDto implements Serializable  {

	private static final long serialVersionUID = 1091287783936143548L;
	
	private Long idMesa;
	private Long idPadronElectoral;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
}
