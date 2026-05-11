package pe.gob.onpe.scebackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParametroConexionList {

	private Long idCentroComputo;
	private String codigoCc;
	private String nombre;
	private String  estado;
	private Integer activo;
	private Boolean esActivo;
	private Integer puerto;
	private String  ip;
	private String  protocolo;
	private String  usuarioModificacion;
	private String  fechaModificacion;
	
}
