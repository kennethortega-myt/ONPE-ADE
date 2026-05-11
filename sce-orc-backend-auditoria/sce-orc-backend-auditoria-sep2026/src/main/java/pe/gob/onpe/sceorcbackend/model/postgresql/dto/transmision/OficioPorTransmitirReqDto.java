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
public class OficioPorTransmitirReqDto implements Serializable {


	private static final long serialVersionUID = -4096113512902241753L;
	
	private String idCc;
	private String nombreOficio;
	private Integer estado;
	private String estadoOficio;
	private Long idCentroComputo;
	private Long idArchivo;
	private ArchivoTransmisionReqDto archivo;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion;
	private String fechaModificacion;
	
	
}
