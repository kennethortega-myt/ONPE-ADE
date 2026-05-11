package pe.gob.onpe.sceorcbackend.model.importar.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetParametroDto {

	private Long id;
	private Long idParametro;
	private String nombre;
	private String valor;
	private Integer tipoDato;
	private String descripcion;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
