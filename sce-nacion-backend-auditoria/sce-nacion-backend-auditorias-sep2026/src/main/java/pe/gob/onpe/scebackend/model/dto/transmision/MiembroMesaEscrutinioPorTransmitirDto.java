package pe.gob.onpe.scebackend.model.dto.transmision;

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
public class MiembroMesaEscrutinioPorTransmitirDto implements Serializable {
	 
	private static final long serialVersionUID = -1395092310072439984L;
	
	private Long idMesa;
	private Long idMiembroMesaEscrutinio;
	private String idMiembroMesaEscrutinioCc;
	private String documentoIdentidadPresidente;
	private String documentoIdentidadSecretario;
	private String documentoIdentidadTercerMiembro;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;

}
