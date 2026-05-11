package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ResolucionPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -7012681363492793182L;
	private String id;
	private String idCc;
	private ArchivoTransmisionDto archivoResolucion;
	private Integer procedencia;
	private String fechaResolucion;
	private String numeroExpediente;
	private String numeroResolucion;
	private Integer tipoResolucion;
	private String estadoResolucion;
	private String estadoDigitalizacion;
	private String observacionDigitalizacion;
	private Integer numeroPaginas;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	
}
