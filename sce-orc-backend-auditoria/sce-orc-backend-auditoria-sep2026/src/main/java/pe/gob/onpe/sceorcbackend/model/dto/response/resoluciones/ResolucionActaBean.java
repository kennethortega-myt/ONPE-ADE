package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResolucionActaBean implements Serializable {

  private static final long serialVersionUID = 65123752894453889L;

  private ActaBean actaAntes;
  private ActaBean actaDespues;


}
