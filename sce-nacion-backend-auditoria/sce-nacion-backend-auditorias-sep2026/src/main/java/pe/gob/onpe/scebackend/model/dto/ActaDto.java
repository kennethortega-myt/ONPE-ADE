package pe.gob.onpe.scebackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActaDto {

  private Long id;

  private String estadoActa;

  private String estadoCc;

  private String estadoActaResolucion;

  private String estadoDigitalizacion;

  private String estadoErrorMaterial;

  private Long eleccionId;

}
