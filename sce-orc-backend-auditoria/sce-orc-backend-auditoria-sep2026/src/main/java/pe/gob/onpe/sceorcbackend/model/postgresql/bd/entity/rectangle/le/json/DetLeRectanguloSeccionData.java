package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class DetLeRectanguloSeccionData implements Serializable {

	@Serial
    private static final long serialVersionUID = 4577719691907858764L;

	@JsonProperty("orden")
    private Integer orden;

    @JsonProperty("archivo_seccion")
    private Long archivoSeccion;

    @JsonProperty("huella")
    private Boolean huella;

    @JsonProperty("firma")
    private Boolean firma;

    @JsonProperty("no_voto")
    private Boolean noVoto;


}
