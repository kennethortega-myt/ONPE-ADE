package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaAccionPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = 5161056256409515966L;
	
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
