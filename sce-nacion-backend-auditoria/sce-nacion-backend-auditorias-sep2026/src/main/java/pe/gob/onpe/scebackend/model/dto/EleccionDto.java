package pe.gob.onpe.scebackend.model.dto;

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
public class EleccionDto {

	private Long	id;
	private Long	idProcesoElectoral; // foranea
	private String  codigo;
	private String	nombre;
	private Integer activo;
	private Integer principal;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
