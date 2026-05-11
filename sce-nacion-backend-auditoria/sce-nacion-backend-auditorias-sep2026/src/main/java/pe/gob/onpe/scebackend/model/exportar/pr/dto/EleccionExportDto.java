package pe.gob.onpe.scebackend.model.exportar.pr.dto;

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
public class EleccionExportDto {

	private Long	id;
	private Long	idProcesoElectoral;
	private String	nombreVista;
	private Integer principal;
	private String  codigo;
	private String	nombre;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
