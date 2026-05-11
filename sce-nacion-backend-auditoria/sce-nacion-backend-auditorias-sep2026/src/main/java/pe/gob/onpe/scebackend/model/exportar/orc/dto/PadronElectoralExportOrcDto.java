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
public class PadronElectoralExportOrcDto {

	private Long id;
	private Long idMesa;
	private String codigoMesa;
	private Integer idTipoDocumentoIdentidad;
	private String documentoIdentidad;
	private String nombres;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private Long orden;
	private String ubigeo;
	private String ubigeoReniec;
	private Integer sexo;
	private Integer vd;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
