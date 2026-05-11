package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.Data;

@Data
public class RefreshTokenOutputDto {
  private Integer resultado;
  private String mensaje;
  private String token;
}
