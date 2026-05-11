package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json;



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
public class ArchivoCargoTransmisionDto implements Serializable {

	private static final long serialVersionUID = 4178712750545794989L;
	
	private String base64;
    private String guid;
    private String extension;
    private String peso;
    private String mimeType;
	
}
