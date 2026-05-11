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
public class DetOficioTransmistirDto implements Serializable {
	
	private static final long serialVersionUID = 5006168408206342461L;

	private String idCc;
	private CabActaFormatoPorTransmitirDto cabActaFormato;
	private String idActaCelesteCc;
	private Long idActa;
	private OficioPorTransmitirDto cabOficio;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
}
