package pe.gob.onpe.sceorcbackend.model.dto.puestacero;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class AutorizacionDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1662582442791663580L;
  private String id;
  private Long numero;
  private String detalle;
  private String estado;
  private String descripcionEstado;
  private String fechaHora;


}
