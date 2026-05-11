package pe.gob.onpe.scebackend.security.dto;



import lombok.Data;

import java.util.List;

@Data
public class GenericResponse<T> {
    private boolean success = true;
    private String message = "";
    private T data;
    private List<Long> actasId;

    public GenericResponse() {
    }

    public GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public GenericResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public GenericResponse(boolean success, String message, T data, List<Long> longList) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.actasId = longList;
    }

    public GenericResponse(T data) {
        this.data = data;
    }

}
