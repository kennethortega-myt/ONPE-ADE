package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmSeccionDto {

	private Integer id;
	private String nombre;
	private String abreviatura;
	private Integer activo;
	private Integer orientacion;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
    
    

}
