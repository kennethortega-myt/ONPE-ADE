package pe.gob.onpe.sceorcbackend.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class UploadResolucionRequest {

    @NotNull(message = "{campo.obligatorio}")
    private String numeroResolucion;

    @NotNull(message = "{campo.obligatorio}")
    @Min(value = 1, message = "{numeroPaginas.min}")
    @Max(value = 100, message = "{numeroPaginas.max}")
    private Integer numeroPaginas;

    private Long idResolucion;

    @NotNull(message = "{campo.obligatorio}")
    private MultipartFile file;

}

