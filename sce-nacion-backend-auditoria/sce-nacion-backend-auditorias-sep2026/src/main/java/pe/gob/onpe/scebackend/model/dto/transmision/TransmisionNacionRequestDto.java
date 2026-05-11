package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransmisionNacionRequestDto implements Serializable {

	private static final long serialVersionUID = -6912858067442970314L;
	
	private String proceso;
	private List<TransmisionDto> actasTransmitidas;
	
	public static TransmisionNacionRequestDto getObject(String jsonString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, TransmisionNacionRequestDto.class);
	}
	
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
	}
	
}
