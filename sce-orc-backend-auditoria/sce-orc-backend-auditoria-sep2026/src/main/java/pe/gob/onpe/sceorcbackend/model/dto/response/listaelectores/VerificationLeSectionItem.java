package pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class VerificationLeSectionItem implements Serializable {

  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -5283152875951259788L;
  private Long idPadron;
  private Long archivoSeccion;
  private Integer orden;
  private Boolean huella;
  private Boolean firma;
  private Boolean noVoto;

  private String dni;
  private String nombres;
  private String apellidoPaterno;
  private String apellidoMaterno;

  private String asistioUser;//no asistio
  private String asistioAutomatico;//no asistio

  public VerificationLeSectionItem() {
    super();
  }

}
