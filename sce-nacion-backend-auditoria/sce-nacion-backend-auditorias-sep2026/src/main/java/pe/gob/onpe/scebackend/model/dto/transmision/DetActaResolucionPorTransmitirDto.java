package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaResolucionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = 2862564640766166564L;

	private String id;
	private Long idActa;
	private String estadoActa;
	private Integer correlativo;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	private ResolucionPorTransmitirDto resolucionDto;
	
}
