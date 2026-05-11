package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoreoListActaItemMongo {

  private Long actaId;
  private String acta;
  private String mesa;
  private String estado;
  private String fecha;
  private String imagenEscrutinio;
  private String imagenInstalacion;
}
