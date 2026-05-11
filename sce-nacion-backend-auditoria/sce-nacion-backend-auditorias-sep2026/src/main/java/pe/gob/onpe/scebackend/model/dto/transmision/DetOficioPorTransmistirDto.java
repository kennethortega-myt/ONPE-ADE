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
public class DetOficioPorTransmistirDto implements Serializable {

	private static final long serialVersionUID = -5004850007452413535L;
	
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
