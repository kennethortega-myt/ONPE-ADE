package pe.gob.onpe.sceorcbackend.model.dto.response.miembrosmesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class VerificationMmPaginaItem implements Serializable {

	@Serial
  private static final long serialVersionUID = -7782991789590636732L;
	private Integer pagina;
    private Long archivoObservacion;
    private String textoObservacionesUser;
    private List<VerificationMmSectionItem> secciones;
    private String tipoDenuncia;

  public VerificationMmPaginaItem() {
    super();
  }
}
