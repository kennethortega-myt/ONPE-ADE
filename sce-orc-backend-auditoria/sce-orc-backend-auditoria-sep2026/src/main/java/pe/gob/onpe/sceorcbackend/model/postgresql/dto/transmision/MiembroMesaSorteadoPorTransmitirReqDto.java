package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

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
public class MiembroMesaSorteadoPorTransmitirReqDto implements Serializable  {

	private static final long serialVersionUID = -3366680364250408043L;
	
	private Long idMesa;
	private Long idMiembroMesaSorteado;
	private Integer asistenciaManual;
	private Integer asistenciaAutomatico;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	private List<OmisoMiembroMesaPorTransmitirReqDto> omisosMiembroMesa;
}
