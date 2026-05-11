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
public class MesaArchivoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -1830011381980639300L;
	
	private String idMesaArchivoCc;
	private Long idMesa;
	private Integer idDocumentoElectoral;
	private String tipoArchivo;
	private Integer pagina;
	private String descripcionObservacion;
	private Integer digitalizacion;
	private String estadoDigitalizacion;
	private String observacionDigitalizacion;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
	
}
