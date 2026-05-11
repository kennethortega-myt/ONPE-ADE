package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActa;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOpcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaPreferencial;
import java.util.List;

@Data
@AllArgsConstructor
public class ErroresActaWrapper {

  private List<DetActa> detActaListToErrores;
  private List<DetActaPreferencial> detActaPreferencialListToErrores;
  private List<DetActaOpcion> detActaOpcionListToErrores;
}
