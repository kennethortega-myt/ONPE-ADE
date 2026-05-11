package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

import java.util.Date;

@Data
public class FiltroActasDigitalizadasDto {
  @Alphanumeric
  private String esquema;
  private Integer idProceso;
  private Integer idEleccion;
  private Integer idCentroComputo;
  private Integer idOdpe;
  private Date fechaInicial;
  private Date fechaFin;
  private String proceso;
  private String eleccion;
  private String centroComputo;
  private String odpe;
  @Alphanumeric
  private String usuario;
}
