package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroResumenTotalDto {
  @Alphanumeric
  private String esquema;
  private Integer idProceso;
  private Integer idEleccion;
  private String  codigoEleccion;
  private Integer idCentroComputo;
  private Integer idOdpe;
  private Integer habilitado;
  private Integer tipoReporte;
  private String proceso;
  private String eleccion;
  @Alphanumeric
  private String usuario;
  private String centroComputo;
  private String estado;

}
