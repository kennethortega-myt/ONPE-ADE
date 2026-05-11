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
public class AdmDetCatalogoEstructuraExportDto {

	private Long	id;
	private Long	idCatalogo;
	private String  columna;
	private String  nombre;
	private Integer	codigoI;
	private String  codigoS;
	private Long	orden;
	private String 	tipo;
	private String  informacionAdicional;
	private Integer obligatorio; 	
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
