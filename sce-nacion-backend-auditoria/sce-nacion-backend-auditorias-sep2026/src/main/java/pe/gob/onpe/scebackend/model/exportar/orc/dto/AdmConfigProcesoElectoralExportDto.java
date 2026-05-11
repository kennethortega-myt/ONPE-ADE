package pe.gob.onpe.scebackend.model.exportar.orc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AdmConfigProcesoElectoralExportDto {

	private Long 	id;
	private String 	nombre;
	private String 	acronimo;
	private String 	conexionEsquemaPrincipal;
	private String 	conexionEsquemaBdOnpe;
	private String 	fechaConvocatoria;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
