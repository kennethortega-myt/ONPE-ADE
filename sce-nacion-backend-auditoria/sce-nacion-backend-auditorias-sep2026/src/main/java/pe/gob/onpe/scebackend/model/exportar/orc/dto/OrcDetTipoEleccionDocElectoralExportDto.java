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
public class OrcDetTipoEleccionDocElectoralExportDto {

	private Long id;
	private Long idProcesoElectoral;
	private Long idEleccion;
	private Long idDocumentoElectoral;
	private String 	rangoInicial;
    private String 	rangoFinal;
    private String 	digitoChequeo;
    private String 	digitoError;
	private Long correlativo;
	private Integer requerido;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
