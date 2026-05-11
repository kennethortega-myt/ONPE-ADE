package pe.gob.onpe.sceorcbackend.model.postgresql.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ArchivosRectanguloDto {
  private Long fileId;
  private String systemValue;
  private String userValue;
  private boolean nullityRequest;
  @JsonIgnore
  private String filePngUrl;

}
