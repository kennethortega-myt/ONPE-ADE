package pe.gob.onpe.sceorcbackend.model.dto.verification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActaResumenDto {
  private Long idActa;
  private String numeroMesa;
  private String numeroCopia;
  private String digitoChequeoEscrutinio;
  private String nombreEleccion;
  private String estadoActa;

  public ActaResumenDto(Long idActa, String numeroMesa, String numeroCopia,
                        String digitoChequeoEscrutinio, String nombreEleccion, String estadoActa) {
    this.idActa = idActa;
    this.numeroMesa = numeroMesa;
    this.numeroCopia = numeroCopia;
    this.digitoChequeoEscrutinio = digitoChequeoEscrutinio;
    this.nombreEleccion = nombreEleccion;
    this.estadoActa = estadoActa;
  }

}