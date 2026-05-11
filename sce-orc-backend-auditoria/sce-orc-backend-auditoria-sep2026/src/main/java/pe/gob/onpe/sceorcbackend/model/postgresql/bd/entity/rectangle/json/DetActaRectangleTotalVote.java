package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DetActaRectangleTotalVote implements Serializable {



	@JsonProperty("file")
    private Long archivo;

    @JsonProperty("predicted")
    private String prediccion;

}
