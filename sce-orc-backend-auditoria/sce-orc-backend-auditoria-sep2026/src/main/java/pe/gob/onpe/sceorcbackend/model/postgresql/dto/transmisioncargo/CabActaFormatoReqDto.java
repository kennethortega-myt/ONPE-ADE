package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

import java.io.Serializable;

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
public class CabActaFormatoReqDto implements Serializable {

	private static final long serialVersionUID = -8643510301720304148L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoCargoTransmisionReqDto archivo;
	private FormatoReqDto formato;
	private Integer correlativo;
	private Integer activo;
	private String usuarioCreacion;
	private String fechaCreacion;
	private String usuarioModificacion;
	private String fechaModificacion;
	
}
