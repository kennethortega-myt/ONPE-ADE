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
public class DetActaAccionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -9050008622781938022L;
	
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
