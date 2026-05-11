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
public class OficioPorTransmitirDto implements Serializable {
	
	private static final long serialVersionUID = -5131598828985955690L;
	private String idCc;
	private String nombreOficio;
	private Integer estado;
	private String estadoOficio;
	private Long idCentroComputo;
	private Long idArchivo;
	private ArchivoTransmisionDto archivo;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;

}
