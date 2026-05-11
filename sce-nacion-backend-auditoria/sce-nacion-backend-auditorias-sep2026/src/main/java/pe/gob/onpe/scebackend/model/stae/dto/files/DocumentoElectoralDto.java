package pe.gob.onpe.scebackend.model.stae.dto.files;

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
public class DocumentoElectoralDto implements Serializable {

	private static final long serialVersionUID = 7148397761622764498L;
	
	private Integer tipoDocumentoElectoral;
	private String base64;
    private String guid;
    private String extension;
    private String peso;
    private String mimeType;
	
}