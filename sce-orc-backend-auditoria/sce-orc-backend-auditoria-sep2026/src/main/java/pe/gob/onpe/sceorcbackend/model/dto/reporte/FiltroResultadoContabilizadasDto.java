package pe.gob.onpe.sceorcbackend.model.dto.reporte;

import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.anotation.Alphanumeric;

@Data
public class FiltroResultadoContabilizadasDto {
  @Alphanumeric
  private String esquema;
  private Integer idProceso;
  private Integer idEleccion;
  private Integer idCentroComputo;
  private Integer idOdpe;
  @Alphanumeric
  private String ubigeo;
  private String proceso;
  private String eleccion;
  @Alphanumeric
  private String usuario;
  private Integer tipoReporte;
  private String cc;
  @Alphanumeric
  private String codigoEleccion;
  @Alphanumeric
  private String acronimo;
  private String centroComputo;
  private String odpe;
  private String distritoElectoral;
  private String nombreUbigeoUno;
  private String nombreUbigeoDos;
  private String nombreUbigeoTres;

}
