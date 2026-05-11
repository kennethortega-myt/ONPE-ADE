package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransmisionResponseDto implements Serializable {

	private static final long serialVersionUID = -1934867961916561312L;
	
	private boolean exitoso;
	private Long idActa;
	private Long idTransmision;
	private Integer estado;
    private String mensaje;
    private String correlationId;

    public static TransmisionResponseDto getObject(String jsonString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, TransmisionResponseDto.class);
	}
	
	public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
	}

}
