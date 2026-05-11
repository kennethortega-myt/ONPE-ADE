package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EleccionDto {

  private Long id;
  private String codigo;
  private String nombre;

}
