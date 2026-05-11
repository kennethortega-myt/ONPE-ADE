package pe.gob.onpe.sceorcbackend.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametroDto {

  private Long id;

  private String parametro;

  private Integer activo;

  private List<DetParametroDto> detalles;

  private Object valor;

  private String usuario;
}
