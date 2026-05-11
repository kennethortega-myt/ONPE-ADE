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
public class DetOficioTransmistirReqDto implements Serializable  {

	private static final long serialVersionUID = 7090036014746016892L;
	
	private String idCc;
	private CabActaFormatoPorTransmitirReqDto cabActaFormato;
	private String idActaCelesteCc;
	private Long idActa;
	private OficioPorTransmitirReqDto cabOficio;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
}
