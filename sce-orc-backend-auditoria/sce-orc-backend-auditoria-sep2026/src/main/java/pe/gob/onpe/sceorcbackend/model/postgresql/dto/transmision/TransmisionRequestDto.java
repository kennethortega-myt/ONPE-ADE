package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import lombok.*;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransmisionRequestDto implements Serializable {

    private static final long serialVersionUID = -2409518664250913884L;

    private Long idActa;
    private String proceso;
    private List<TransmisionReqDto> actasTransmitidas;
    
    public static TransmisionRequestDto getObject(String jsonString) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, TransmisionRequestDto.class);
	}
	
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
	}

}
