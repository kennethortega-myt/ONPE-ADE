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
public class MesaArchivoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -3448983685330141646L;
	
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
