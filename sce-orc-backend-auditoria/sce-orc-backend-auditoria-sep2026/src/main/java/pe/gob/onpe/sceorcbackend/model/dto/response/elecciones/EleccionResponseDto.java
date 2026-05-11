package pe.gob.onpe.sceorcbackend.model.dto.response.elecciones;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EleccionResponseDto {
  private Long	id;
  private String cCodigo;
  private String cNombre;
  private String nActivo;
  private String rangoInicial;
  private String rangoFinal;
  private String digitoChequeoAE;
  private String digitoChequeoAIS;
  private String digitoCequeoError;
}
