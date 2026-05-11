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
public class OficioPorTransmitirDto implements Serializable {


	private static final long serialVersionUID = 3825732178663800966L;
	
	private String idCc;
	private String nombreOficio;
	private Integer estado;
	private String estadoOficio;
	private Integer idCentroComputo;
	private Long idArchivo;
	private ArchivoTransmisionDto archivo;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
}
