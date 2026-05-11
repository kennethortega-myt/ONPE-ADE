package pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoMiembroMesa;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VerificationMm implements Serializable {
  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -4588592979482809001L;
  private Long mesaId;
  private String type;
  private String mesa;
  private String estadoMesa;
  private Integer electoresHabiles;
  private Integer electoresOmisos;
  private Integer electoresAusentes;
  private String localVotacion;
  private String ubigeo;
  private String departamento;
  private String provincia;
  private String distrito;
  private List<VerificationMmPaginaItem> paginas;
  private List<OmisoMiembroMesa> data;

  public VerificationMm() {
    super();
  }

}
