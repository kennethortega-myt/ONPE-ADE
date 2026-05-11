package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json;



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
public class CabActaFormatoDto implements Serializable {

	private static final long serialVersionUID = -7523552676403383258L;
	
	private Long id;
	private String idCc;
	private Long idArchivo;
	private ArchivoCargoTransmisionDto archivo;
	private FormatoDto formato;
	private Integer correlativo;
	private Integer activo;
	private String usuarioCreacion;
	private String fechaCreacion;
	private String usuarioModificacion;
	private String fechaModificacion;
	
}
