package pe.gob.onpe.scebatchpr.dto;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class TramaSceResponse {

	private boolean success;
    private String message;
	private TramaVistaResponse data;
	
	public static TramaSceResponse getObject(String jsonString) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, TramaSceResponse.class);
	}
	
}
