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
public class DetActaOpcionExportDto {

	private Integer	id;
	private String	idDetActaOpcionCc;
	private Long    idDetActa;
	private Long	posicion;
	private Long	votos;
	private Long    votosAutomatico;
	private Long    votosManual1;
	private Long    votosManual2;
	private String  estadoErrorMaterial;
	private String 	ilegible;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
