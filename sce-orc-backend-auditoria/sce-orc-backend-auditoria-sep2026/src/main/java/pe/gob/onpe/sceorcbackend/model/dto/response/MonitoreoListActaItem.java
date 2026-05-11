package pe.gob.onpe.sceorcbackend.model.dto.response;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class MonitoreoListActaItem {
  private Long actaId;
  private String grupoActa;
  private String acta;
  private String mesa;
  private String estado;
  private String fecha;
  private Integer verActa;
  private Integer cantidadTransmitidasFaltantes;
  private String imagenEscrutinio;
  private String imagenInstalacion;
  private String imagenSufragio;
  private String imagenInstalacionSufragio;
}
