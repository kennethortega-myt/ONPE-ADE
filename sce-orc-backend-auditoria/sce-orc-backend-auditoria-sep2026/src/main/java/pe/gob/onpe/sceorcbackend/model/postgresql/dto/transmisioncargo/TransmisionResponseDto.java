package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransmisionResponseDto implements Serializable {

	private static final long serialVersionUID = -2783035048190840934L;
	
	private boolean success;
    private String message;
    private List<CargoTransmitidoDto> cargosTransmitidos;
    
    public static CargoTransmitidoDto getObject(String jsonString) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, CargoTransmitidoDto.class);
	}
    
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
	
}
