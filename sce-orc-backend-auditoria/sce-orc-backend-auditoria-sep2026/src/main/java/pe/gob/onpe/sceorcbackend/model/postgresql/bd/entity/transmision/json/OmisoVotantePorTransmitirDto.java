package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;

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
public class OmisoVotantePorTransmitirDto implements Serializable  {
	
	private static final long serialVersionUID = 7089799133825154480L;

	private Long idMesa;
	private Long idPadronElectoral;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
}
