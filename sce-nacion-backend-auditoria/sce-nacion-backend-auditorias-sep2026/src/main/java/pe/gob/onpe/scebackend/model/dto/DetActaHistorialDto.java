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
public class DetActaHistorialDto {

	private Long	id;
	private Long	correlativo;
	private Long	idCentroComputo;
	private String 	proceso;
	private Long	idCabActa;
	private Long	idAgrupacionPolitica;
	private Long	posicion;
	private Long	votos;
	private String  estadoErrorMaterial;
	private String 	ilegible;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
