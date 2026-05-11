package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

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
public class ArchivoCargoTransmisionReqDto implements Serializable {

	private static final long serialVersionUID = 4121590056283671544L;
	
	private String base64;
    private String guid;
    private String extension;
    private String peso;
    private String mimeType;
	
}
