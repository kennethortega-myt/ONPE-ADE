package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroEstadoActasOdpeDto {

  @Alphanumeric
  private String esquema;
  private Integer idProceso;
  private Integer idEleccion;
  private Integer idCentroComputo;
  private Integer idOdpe;
  private String proceso;
  private String eleccion;
  private String centroComputo;
  private String odpe;
  private String usuario;
}
