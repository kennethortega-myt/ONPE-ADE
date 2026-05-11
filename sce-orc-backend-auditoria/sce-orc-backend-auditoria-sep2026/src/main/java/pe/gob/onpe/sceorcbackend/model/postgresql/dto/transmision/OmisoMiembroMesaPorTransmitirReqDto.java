package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.io.Serializable;

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
public class OmisoMiembroMesaPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = -1510491213429403292L;
	private Long idOmisoMiembroMesa;
	private String idOmisoMiembroMesaCc;
	private Long IdMiembroMesaSorteado;
	private Long idMesa;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;

}
