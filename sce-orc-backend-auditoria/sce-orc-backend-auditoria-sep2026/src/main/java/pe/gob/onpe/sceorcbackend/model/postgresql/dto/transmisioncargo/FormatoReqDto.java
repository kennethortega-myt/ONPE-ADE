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
public class FormatoReqDto implements Serializable {

	private static final long serialVersionUID = -1537671533092187971L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoCargoTransmisionReqDto archivo;
	private Integer correlativo;
	private Integer tipoFormato;
	private Integer activo;
	private String usuarioCreacion;
	private String fechaCreacion;
	private String usuarioModificacion;
	private String fechaModificacion;
	
}
