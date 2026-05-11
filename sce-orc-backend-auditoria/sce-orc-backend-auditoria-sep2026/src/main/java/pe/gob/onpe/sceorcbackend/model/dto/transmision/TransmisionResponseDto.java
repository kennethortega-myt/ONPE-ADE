package pe.gob.onpe.sceorcbackend.model.dto.transmision;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransmisionResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 7249684515942055039L;

    private boolean exitoso;
	private Long idActa;
	private Long idTransmision;
	private Integer estado;
    private String mensaje;
    private String correlationId;
    
    public static TransmisionResponseDto getObject(String jsonString) throws  JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, TransmisionResponseDto.class);
	}
    
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }


}
