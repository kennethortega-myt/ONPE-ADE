package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;

@Data
public class GenericResponse {

    private boolean success;
    private String message;
    private Object data;
}
