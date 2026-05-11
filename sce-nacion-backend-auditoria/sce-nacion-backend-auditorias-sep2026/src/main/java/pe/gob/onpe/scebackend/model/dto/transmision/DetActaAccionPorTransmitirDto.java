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
public class DetActaAccionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -3700697220975378111L;
	
	private Long idDetActaAccion;
	private String idCcDetActaAccion;
	private Long idActa;
	private String accion;
	private String tiempo;
	private Integer iteracion;
	private Integer orden;
	private String usuarioAccion;
	private String fechaAccion;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	
}
