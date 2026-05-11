package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;

import lombok.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArchivoTransmisionDto implements Serializable {

    private static final long serialVersionUID = 3147655482960116271L;

    private String base64;
    private String guid;
    private String extension;
    private String peso;
    private String mimeType;


}
