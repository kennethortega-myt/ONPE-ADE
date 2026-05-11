package pe.gob.onpe.scebackend.model.exportar.orc.dto;


import java.io.Serializable;

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
public class DetDistritoElectoralEleccionExportDto implements Serializable {

	private static final long serialVersionUID = -3129451250081655020L;
	
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
