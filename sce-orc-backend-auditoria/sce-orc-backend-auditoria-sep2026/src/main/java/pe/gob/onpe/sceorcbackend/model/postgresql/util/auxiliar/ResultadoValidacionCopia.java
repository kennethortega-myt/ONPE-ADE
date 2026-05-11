package pe.gob.onpe.sceorcbackend.model.postgresql.util.auxiliar;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;

@Data
@AllArgsConstructor
public class ResultadoValidacionCopia {

  private Eleccion eleccion;
  private String copia;
  private String digitoChequeo;
  private DetTipoEleccionDocumentoElectoral configuracion;
}
