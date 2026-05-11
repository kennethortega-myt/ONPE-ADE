package pe.gob.onpe.scebackend.model.dto.transmision;



import java.io.Serializable;
import java.util.List;

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
public class CabActaFormatoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = 6859958570491493122L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoTransmisionDto archivo;
	private Integer correlativo;
	private TabFormatoPorTransmitirDto tabFormato;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	private List<DetActaFormatoPorTransmitirDto> detalle;
	
}
