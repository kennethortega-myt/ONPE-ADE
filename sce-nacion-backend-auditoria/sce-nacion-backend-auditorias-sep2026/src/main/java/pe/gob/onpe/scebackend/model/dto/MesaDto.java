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
public class MesaDto {

	private Long	id;
	private Long	idLocalVotacion; // foranea
	private String  codigo;
	private Integer cantidadElectoresHabiles;
	private Integer cantidadElectoresHabilesExtranjeros;
	private Integer discapacidad;
	private Long	solucionTecnologica;
	private String 	estadoMesa;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
