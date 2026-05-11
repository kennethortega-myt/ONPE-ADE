package pe.gob.onpe.sceorcbackend.model.dto.response.listaelectores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VerificationLePaginaItem implements Serializable {

  /**
   *
   */
  @Serial
  private static final long serialVersionUID = -2061592308546165441L;
  private Integer pagina;
  private Long archivoObservacion;
  private String textoObservacionesUser;
  private List<VerificationLeSectionItem> secciones;
  private boolean existeObservacion;
  private String tipoDenuncia;

  public VerificationLePaginaItem() {
    super();
  }
}
