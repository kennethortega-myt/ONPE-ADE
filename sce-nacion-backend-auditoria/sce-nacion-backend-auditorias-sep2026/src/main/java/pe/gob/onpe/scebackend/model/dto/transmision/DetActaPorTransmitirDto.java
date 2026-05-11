package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;
import java.util.List;

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
public class DetActaPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -2924357182602790695L;
	
	private String idActaDetalle;
	private Long idActa;
	private Long idAgrupacionPolitica;
	private Long posicion;
	private Long votos;
	private Long votosAutomatico;
	private Long votosManual1;
	private Long votosManual2;
	private String estadoErrorMaterial;
	private String ilegible;
	private String ilegiblev2;
	private String ilegiblev1;
	private Integer activo;
	private List<DetActaPreferencialPorTransmitirDto> detActaPreferencial;
	private List<DetActaOpcionPorTransmitirDto> detActaOpcion;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	private Integer estado;
	
}
