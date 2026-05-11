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
public class OmisoMiembroMesaPorTransmitirDto implements Serializable {
	
	private static final long serialVersionUID = -8045510558884155297L;
	
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
