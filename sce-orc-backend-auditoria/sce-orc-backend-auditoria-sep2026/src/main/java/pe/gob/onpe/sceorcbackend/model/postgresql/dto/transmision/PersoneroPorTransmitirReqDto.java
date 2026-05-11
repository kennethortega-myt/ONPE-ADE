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
public class PersoneroPorTransmitirReqDto implements Serializable {
	
	private static final long serialVersionUID = 3757248067427988623L;
	
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
