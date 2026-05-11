package pe.gob.onpe.scebackend.model.stae.dto.files;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchivoCopyDto {

	private Long   	id;
	private String 	guid;
	private String 	nombre;
	private String 	nombreOriginal;
	private String 	formato;
	private String 	peso;
	private String 	ruta;
	private String 	codigoCc;
	private Date fechaCreacion;
	private String usuarioCreacion;
	private String  pathAbsolute;
	private Integer tipoDocumentoElectoral;
	
}
