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
public class LocalVotacionExportDto {

	private Long 	id;
	private Long 	idUbigeo; // foranea
	private String 	codigo;
	private String 	nombre;
	private String 	direccion;
	private String 	referencia;
	private String  centroPoblado;
	private Integer cantidadMesas;
	private Integer cantidadElectores;
	private Integer estado;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
	
}
