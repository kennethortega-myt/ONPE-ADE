package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UbigeoDto {

	private Long 	id;
	private Long 	idPadre;
	private Long 	idAmbitoElectoral;
	private Long	idCentroComputo;
	private String 	codigo; // ubigeo
	private String	nombre;
	private Integer	tipoAmbitoGeografico;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;

	private List<LocalVotacionDto> localesVotacion;
}
