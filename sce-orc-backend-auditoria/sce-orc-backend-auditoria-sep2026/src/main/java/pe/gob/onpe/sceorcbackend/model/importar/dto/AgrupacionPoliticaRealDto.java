package pe.gob.onpe.sceorcbackend.model.importar.dto;



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
public class AgrupacionPoliticaRealDto {

	private Long   	id;
	private String  codigo;
	private String  descripcion;
	private Long	tipoAgrupacionPolitica;
	private Integer estado;
	private String  ubigeoMaximo;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
