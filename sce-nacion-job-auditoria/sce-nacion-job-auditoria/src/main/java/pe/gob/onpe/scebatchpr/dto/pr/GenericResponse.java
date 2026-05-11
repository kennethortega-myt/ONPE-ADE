package pe.gob.onpe.scebatchpr.dto.pr;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;


@Data
public class GenericResponse<T> {
    private boolean success = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public GenericResponse() {
    }

    public GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GenericResponse(boolean success, String message,T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public GenericResponse(T data) {
        this.data = data;
    }
    
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

}