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
public class PersoneroPorTransmitirDto implements Serializable {
	
	private static final long serialVersionUID = -5307313206451712655L;
	
	private Long idPersonero;
	private Long idMesa;
	private Long idAgrupacionPolitica;
	private String documentoIdentidad;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String nombres;
	private Integer activo;
	private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
	
	
}
