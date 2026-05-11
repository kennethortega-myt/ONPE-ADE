package pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class VerificationMmSectionItem implements Serializable {

  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -5182351991852896833L;
  private Long idPadron;
  private Long idMiembroMesaSorteado;
  private Long archivoSeccion;
  private Integer cargo;
  private Boolean firma;
  private String dni;
  private String nombres;
  private String apellidoPaterno;
  private String apellidoMaterno;
  private String asistioUser;//no asistio
  private String asistioAutomatico;//no asistio
  private Integer estado;

  public VerificationMmSectionItem() {
    super();
  }

}
