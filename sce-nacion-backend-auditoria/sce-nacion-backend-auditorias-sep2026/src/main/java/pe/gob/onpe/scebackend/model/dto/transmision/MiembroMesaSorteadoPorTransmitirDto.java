package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiembroMesaSorteadoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -9113417818452295177L;
	
	private Long idMesa;
	private Long idMiembroMesaSorteado;
	private Integer asistenciaManual;
	private Integer asistenciaAutomatico;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	private List<OmisoMiembroMesaPorTransmitirDto> omisosMiembroMesa;
	
}
