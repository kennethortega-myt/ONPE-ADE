package pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.OmisoVotante;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VerificationLe implements Serializable {
  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -6677477403992904458L;
  private Long mesaId;
  private String type;
  private String mesa;
  private String estadoMesa;
  private Integer electoresHabiles;
  private Integer electoresAusentes;
  private Integer electoresOmisos;
  private String localVotacion;
  private String ubigeo;
  private String departamento;
  private String provincia;
  private String distrito;
  private List<VerificationLePaginaItem> paginas;
  private List<OmisoVotante> data;
  private String estadoLe;

  public VerificationLe() {
    super();
  }

}
