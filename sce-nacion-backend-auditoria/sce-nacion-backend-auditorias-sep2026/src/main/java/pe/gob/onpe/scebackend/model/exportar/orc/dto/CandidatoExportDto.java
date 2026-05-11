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
public class CandidatoExportDto {

	private Integer id;
	private Long idDistritoElectoral;
	private Long idEleccion;
	private String codigoEleccion;
	private Long idAgrupacionPolitica;
	private Long idUbigeo;
	private Integer cargo;
	private String  documentoIdentidad;
	private String  apellidoPaterno;
	private String  apellidoMaterno;
	private String  nombres;
	private Integer sexo;
	private Integer estado;
	private Integer lista;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
	
}
