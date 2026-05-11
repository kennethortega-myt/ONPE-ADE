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
public class UbigeoExportDto {

	private Long 	id;
	private Long 	idPadre;
	private Long 	idAmbitoElectoral;
	private Integer idDistritoElectoral;
	private Long	idCentroComputo;
	private Integer region;
	private String  departamento;
	private String  provincia;
	private String  distrito;
	private String 	codigo; // ubigeo
	private String	nombre;
	private Integer	tipoAmbitoGeografico;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
}
