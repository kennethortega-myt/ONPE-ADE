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
public class DetActaFormatoDto implements Serializable {

	private static final long serialVersionUID = -2666798509932364716L;
	
	private Long id;
	private String idCc;
	private Long idActa;
	private CabActaFormatoDto cabActaFormato;
	private Integer activo;
	private String usuarioCreacion;
	private String fechaCreacion;
	private String usuarioModificacion;
	private String fechaModificacion;
	
}
