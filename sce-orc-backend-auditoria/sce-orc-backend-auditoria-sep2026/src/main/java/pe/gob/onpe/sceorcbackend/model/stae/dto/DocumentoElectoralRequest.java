package pe.gob.onpe.sceorcbackend.model.stae.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DocumentoElectoralRequest {

	private String codigoMesa;
	private Integer idEleccion;
	private List<DocumentoElectoralDto> documentos;
	
}
