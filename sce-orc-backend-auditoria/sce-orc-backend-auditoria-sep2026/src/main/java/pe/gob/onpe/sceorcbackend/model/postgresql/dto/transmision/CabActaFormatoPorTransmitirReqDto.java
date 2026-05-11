package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.io.Serializable;
import java.util.List;

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
public class CabActaFormatoPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = 4526939090186904354L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoTransmisionReqDto archivo;
	private Integer correlativo;
	private TabFormatoPorTransmitirReqDto tabFormato;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	private List<DetActaFormatoPorTransmitirReqDto> detalle;
	
}
