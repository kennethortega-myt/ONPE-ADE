package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class DetActaRectangleVoteFooterItem implements Serializable {

	@JsonProperty("item")
    private String item;

    @JsonProperty("total_votos_0")
    private DetActaRectangleTotalVote totalVotos0;

    @JsonProperty("total_votos_1")
    private DetActaRectangleTotalVote totalVotos1;

    @JsonProperty("total_votos_2")
    private DetActaRectangleTotalVote totalVotos2;

    @JsonProperty("total_votos_3")
    private DetActaRectangleTotalVote totalVotos3;

    @JsonProperty("total_votos_4")
    private DetActaRectangleTotalVote totalVotos4;

    @JsonProperty("total_votos_5")
    private DetActaRectangleTotalVote totalVotos5;


}
