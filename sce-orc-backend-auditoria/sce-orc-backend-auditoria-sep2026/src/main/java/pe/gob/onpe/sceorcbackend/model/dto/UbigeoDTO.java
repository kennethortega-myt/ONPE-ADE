package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UbigeoDTO {

  private String ubigeo;

  private String distrito;

  private String provincia;

  private String departamento;
}
