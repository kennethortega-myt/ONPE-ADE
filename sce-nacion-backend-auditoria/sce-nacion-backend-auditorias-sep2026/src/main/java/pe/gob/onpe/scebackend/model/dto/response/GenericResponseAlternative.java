package pe.gob.onpe.scebackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponseAlternative<T> {
    private boolean success;
    private String message;
    private T data;
    private String exceptionCode;
    private LocalDateTime timestamp;
}
