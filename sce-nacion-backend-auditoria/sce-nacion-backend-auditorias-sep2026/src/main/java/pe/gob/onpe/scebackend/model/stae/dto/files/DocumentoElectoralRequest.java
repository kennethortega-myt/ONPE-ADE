package pe.gob.onpe.scebackend.model.stae.dto.files;


import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoElectoralRequest implements Serializable {

	private static final long serialVersionUID = 9020631933540159539L;
	
	private String codigoMesa;
	private Integer idEleccion;
	private List<DocumentoElectoralDto> documentos;
	
}
