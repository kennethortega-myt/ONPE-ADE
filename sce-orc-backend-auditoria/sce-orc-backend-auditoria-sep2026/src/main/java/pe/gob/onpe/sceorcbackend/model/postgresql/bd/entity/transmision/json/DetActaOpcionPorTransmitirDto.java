package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetActaOpcionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = 8800267755494120349L;
	
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
