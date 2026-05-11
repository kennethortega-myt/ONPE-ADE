package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetDistritoElectoralEleccionDto {

	private Long id;
	private Long idEleccion;
	private Integer idDistritoElectoral;
	private Integer cantidadCurules;
	private Integer cantidadCandidatos;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	
}
