package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OmisoMiembroMesaPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -5341566039300639474L;
	
	private Long idOmisoMiembroMesa;
	private String idOmisoMiembroMesaCc;
	private Long idMiembroMesaSorteado;
	private Long idMesa;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
}
