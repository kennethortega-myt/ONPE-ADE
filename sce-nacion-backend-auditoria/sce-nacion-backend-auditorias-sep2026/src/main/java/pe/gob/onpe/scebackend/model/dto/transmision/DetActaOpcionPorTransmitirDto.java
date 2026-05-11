package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetActaOpcionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -5364116447474659752L;
	
	private Integer idDetActaOpcion;
	private Long idActaDetalle;
	private String idDetActaOpcionCc;
	private Long posicion;
	private Long votos;
	private Long votosAutomatico;
	private Long votosManual1;
	private Long votosManual2;
	private String estadoErrorMaterial;
	private String ilegible;
	private String ilegibleAutomatico;
	private String ilegiblev1;
	private String ilegiblev2;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	
	
}
