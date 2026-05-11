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
public class TabFormatoPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = 5888161213263928188L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoTransmisionReqDto archivo;
	private Integer tipoFormato;
	private Integer correlativo;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
}
