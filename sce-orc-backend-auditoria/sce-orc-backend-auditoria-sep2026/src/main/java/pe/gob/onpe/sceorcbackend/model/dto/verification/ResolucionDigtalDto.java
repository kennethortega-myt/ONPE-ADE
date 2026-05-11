package pe.gob.onpe.sceorcbackend.model.dto.verification;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResolucionDigtalDto {

  private String numeroResolucion;
  private Integer numeroPaginas;
  private MultipartFile file;
  private String detectedType;

}
