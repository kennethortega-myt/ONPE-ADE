package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

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
public class DetActaFormatoReqDto {

	private Long id;
	private String idCc;
	private Long idActa;
	private CabActaFormatoReqDto cabActaFormato;
	private Integer activo;
	private String usuarioCreacion;
	private String fechaCreacion;
	private String usuarioModificacion;
	private String fechaModificacion;
	
}
