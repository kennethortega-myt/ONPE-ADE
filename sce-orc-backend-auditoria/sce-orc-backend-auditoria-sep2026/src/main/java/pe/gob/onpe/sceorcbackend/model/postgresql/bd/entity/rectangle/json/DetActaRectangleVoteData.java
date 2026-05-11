package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class DetActaRectangleVoteData implements Serializable {
	
	@JsonProperty("headers")
    private List<String> headers;
    @JsonProperty("body")
    private List<DetActaRectangleVoteItem> body;
    @JsonProperty("footer")
    private List<DetActaRectangleVoteFooterItem> footer;


}
