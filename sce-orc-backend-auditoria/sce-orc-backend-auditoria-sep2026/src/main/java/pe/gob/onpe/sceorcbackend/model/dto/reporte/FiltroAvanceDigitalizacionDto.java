package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroAvanceDigitalizacionDto {
  @Alphanumeric
  private String esquema;
  private Integer idProceso;
  private Integer idEleccion;
  private Integer centroComputo;
  @Alphanumeric
  private String ubigeo;
  private String proceso;
  @Alphanumeric
  private String usuario;
  private String cc;
  private boolean sobreCeleste;
}
