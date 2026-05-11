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
public class PadronElectoralExportPrDto {

	private Long id;
	private Long idMesa;
	private String codigoMesa;
	private Integer idTipoDocumentoIdentidad;
	private String documentoIdentidad;
	
}
