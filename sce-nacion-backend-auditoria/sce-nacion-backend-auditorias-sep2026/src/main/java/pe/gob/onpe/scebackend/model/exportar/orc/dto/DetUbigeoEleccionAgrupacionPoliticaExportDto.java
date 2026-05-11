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
public class DetUbigeoEleccionAgrupacionPoliticaExportDto {

	private Long	id;
	private Long    idDetUbigeoEleccion; // foranea
	private Long    idAgrupacionPolitica; // foranea
	private Integer posicion;
	private Integer estado;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
